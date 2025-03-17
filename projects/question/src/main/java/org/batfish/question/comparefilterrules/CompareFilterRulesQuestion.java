package org.batfish.question.comparefilterrules;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.batfish.datamodel.questions.Question;
import org.batfish.specifier.AllNodesNodeSpecifier;
import org.batfish.specifier.NodeSpecifier;
import org.batfish.specifier.SpecifierFactories;
import org.batfish.specifier.AllFiltersFilterSpecifier;
import org.batfish.specifier.FilterSpecifier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

/** A question to compute conflicting overlaps among any two rules in a filter **/
@ParametersAreNonnullByDefault
public class CompareFilterRulesQuestion extends Question {
    private static final String PROP_FILTERS = "filters";
    private static final String PROP_NODES = "nodes";

    private final static FilterSpecifier DEFAULT_FILTER_SPECIFIER = AllFiltersFilterSpecifier.INSTANCE;

    private static final NodeSpecifier DEFAULT_NODE_SPECIFIER = AllNodesNodeSpecifier.INSTANCE;

    private final @Nullable String _filters;
    private final @Nullable String _nodes;

    CompareFilterRulesQuestion() { this(null, null); }

    CompareFilterRulesQuestion(
            @Nullable String filters,
            @Nullable String nodes
    ){
        _filters = filters;
        _nodes = nodes;
        setDifferential(false);
    }

    @JsonCreator
    private static CompareFilterRulesQuestion jsonCreator(
            @JsonProperty(PROP_FILTERS) @Nullable String filters,
            @JsonProperty(PROP_NODES) @Nullable String nodes){
        return new CompareFilterRulesQuestion(filters, nodes);
    }

    @Override
    public boolean getDataPlane() { return false; }

    @Override
    public String getName() { return "compareFilterRules"; }

    @JsonProperty
    public @Nullable String getFilters() { return _filters; }

    @JsonIgnore
    @Nonnull
    FilterSpecifier getFilterSpecifier() {
        return SpecifierFactories.getFilterSpecifierOrDefault(_filters, DEFAULT_FILTER_SPECIFIER);
    }

    @JsonProperty
    public @Nullable String getNodes() { return _nodes; }

    @JsonIgnore
    @Nonnull
    NodeSpecifier getNodeSpecifier() {
        return SpecifierFactories.getNodeSpecifierOrDefault(_nodes, DEFAULT_NODE_SPECIFIER);
    }
}
