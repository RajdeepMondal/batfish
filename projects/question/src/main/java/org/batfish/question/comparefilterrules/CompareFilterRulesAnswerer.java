package org.batfish.question.comparefilterrules;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import org.batfish.common.Answerer;
import org.batfish.common.BatfishException;
import org.batfish.common.NetworkSnapshot;
import org.batfish.common.bdd.BDDPacket;
import org.batfish.common.bdd.BDDSourceManager;
import org.batfish.common.bdd.IpAccessListToBdd;
import org.batfish.common.bdd.IpAccessListToBddImpl;
import org.batfish.common.bdd.PermitAndDenyBdds;
import org.batfish.common.plugin.IBatfish;
import org.batfish.datamodel.IpAccessList;
import org.batfish.datamodel.AclLine;
import org.batfish.datamodel.Configuration;
import org.batfish.datamodel.IpSpace;
import org.batfish.datamodel.answers.AnswerElement;
import org.batfish.datamodel.answers.Schema;
import org.batfish.datamodel.table.ColumnMetadata;
import org.batfish.datamodel.table.Row;
import org.batfish.datamodel.table.TableAnswerElement;
import org.batfish.datamodel.table.TableMetadata;
import org.batfish.question.comparefilters.FilterDifference;
import org.batfish.specifier.LocationSpecifier;
import org.batfish.specifier.SpecifierContext;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.stream.Stream;

import static org.batfish.common.bdd.PermitAndDenyBdds.takeDifferentActions;
import static org.batfish.question.FilterQuestionUtils.differentialBDDSourceManager;
import static org.batfish.question.FilterQuestionUtils.getSpecifiedFilters;
import static org.batfish.question.comparefilters.CompareFiltersAnswerer.toRow;

/** An answerer for {@link CompareFilterRulesQuestion}. */
public class CompareFilterRulesAnswerer extends Answerer {
    private static final String COL_NODE = "Node";
    private static final String COL_FILTER_NAME = "Filter_Name";
    private static final String COL_CURRENT_ACTION = "Line_Action";
    private static final String COL_CURRENT_LINE = "Line_Index";
    private static final String COL_CURRENT_NAME = "Line_Content";
    private static final String COL_REFERENCE_LINE = "Reference_Line_Index";
    private static final String COL_REFERENCE_NAME = "Reference_Line_Content";

    private final CompareFilterRulesQuestion _question;

    public CompareFilterRulesAnswerer(CompareFilterRulesQuestion question, IBatfish batfish) {
        super(question, batfish);
        _question = question;
    }

    public AnswerElement answer(NetworkSnapshot snapshot){
        SpecifierContext currentContext = _batfish.specifierContext(_batfish.getSnapshot());
//        Configuration currentConfig = currentContext.getConfigs().get(_question.getNodes());
//        Map<String, IpAccessList> currentAcls = currentConfig.getIpAccessLists();

//        System.out.println(currentAcls);
        // Retrieve the desired ACLs
        Multimap<String, String> currentFilters =
                getSpecifiedFilters(
                        currentContext,
                        _question.getNodeSpecifier(),
                        _question.getFilterSpecifier(),
                        false
                );

        BDDPacket bddPacket = new BDDPacket();
        Multiset<Row> rows =
                currentFilters.entries().stream()
                        .flatMap(
                                entry ->
                                        compareFilterRules(
                                                entry.getKey(),
                                                entry.getValue(),
                                                bddPacket,
                                                currentContext))
                        .map(filterDifference -> toRow(filterDifference, currentContext, currentContext))
                        .collect(ImmutableMultiset.toImmutableMultiset());

        TableAnswerElement table = new TableAnswerElement(metadata());
        table.postProcessAnswer(_question, rows);
        return table;
    }

    private static Stream<FilterDifference> compareFilterRules(
            String hostname,
            String filtername,
            BDDPacket bddPacket,
            SpecifierContext currentContext
    ){
        Configuration currentConfig = currentContext.getConfigs().get(hostname);
        Map<String, IpAccessList> currentAcls = currentConfig.getIpAccessLists();
        Map<String, IpSpace> currentIpSpaces = currentConfig.getIpSpaces();
        IpAccessList currentAcl = currentAcls.get(filtername);

        BDDSourceManager srcMgr =
                differentialBDDSourceManager(
                        bddPacket,
                        currentContext,
                        currentContext,
                        currentConfig,
                        currentConfig,
                        currentAcls.keySet(),
                        LocationSpecifier.ALL_LOCATIONS
                );

        // for currentACL, dissociate it into its constituent rules and convert each rule into a new ACL.

        // Step 1: retrieve all the ACL lines
        List<AclLine> aclLines = currentAcl.getLines();
        String sourceName = currentAcl.getSourceName();
        String sourceType = currentAcl.getSourceType();

        Map<String, IpAccessList> accessListEntries = new HashMap<String, IpAccessList>();

        // Step 2: Make new ACLs
        for(int  i=0;i<aclLines.size();i++){
            IpAccessList acl_new = IpAccessList.builder().
                    setName(filtername+"_"+Integer.toString(i)).
                    setLines(ImmutableList.of(aclLines.get(i))).
                    setSourceName(sourceName+"_"+Integer.toString(i)).
                    setSourceType(sourceType).build();

            accessListEntries.put(
                    filtername + "_" + Integer.toString(i),
                    acl_new
            );
        }

        // We shall now create a BDD representation for each new ACL
        // Each BDD representation is a list containing two PermitAndDenyBDDs
        // The first element  in the list is stored only
        // The second element corresponds to the default ACL action of deny all
        // We are not interested in that
        List<PermitAndDenyBdds> currentBdds = new ArrayList<>();
        IpAccessListToBdd currentToBdd =
                new IpAccessListToBddImpl(bddPacket, srcMgr, accessListEntries, currentIpSpaces);

        for(int i=0;i<aclLines.size();i++){
            String fname = filtername + "_" + i;
            List<PermitAndDenyBdds> currBdd = currentToBdd.reachAndMatchLines(accessListEntries.get(fname));
            currentBdds.add(currBdd.get(0));
        }

        // Take all pairs and compare them
        List<FilterDifference> filterDifferences = new ArrayList<>();

        for(int i=0;i<currentBdds.size();i++){
            for(int j=i+1;j<currentBdds.size();j++){
                if(takeDifferentActions(currentBdds.get(i), currentBdds.get(j))){
                    filterDifferences.add(new FilterDifference(hostname, filtername, i, j));
                }
            }
        }
        return filterDifferences.stream();
    }

    public AnswerElement answerDiff(NetworkSnapshot snapshot, NetworkSnapshot reference) {
        throw new BatfishException(
                String.format("%s cannot be run in differential mode.", _question)
        );
    }

    private static TableMetadata metadata() {
        List<ColumnMetadata> columnMetadata =
                ImmutableList.of(
                        new ColumnMetadata(COL_NODE, Schema.NODE, "Hostname.", true, false),
                        new ColumnMetadata(COL_FILTER_NAME, Schema.STRING, "The filter name.", false, true),
                        new ColumnMetadata(
                                COL_CURRENT_LINE,
                                Schema.STRING,
                                "The index of the line in the current filter.",
                                true,
                                false),
                        new ColumnMetadata(
                                COL_CURRENT_NAME, Schema.STRING, "The current filter line content.", false, true),
                        new ColumnMetadata(
                                COL_CURRENT_ACTION, Schema.STRING, "The current filter line action.", false, true),
                        new ColumnMetadata(
                                COL_REFERENCE_LINE,
                                Schema.STRING,
                                "The index of the line in the reference filter.",
                                false,
                                true),
                        new ColumnMetadata(
                                COL_REFERENCE_NAME,
                                Schema.STRING,
                                "The reference filter line content.",
                                true,
                                false));
        return new TableMetadata(columnMetadata, "Lines that change treat flows differently.");
    }
}