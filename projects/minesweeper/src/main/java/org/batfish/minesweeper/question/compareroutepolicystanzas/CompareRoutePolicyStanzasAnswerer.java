package org.batfish.minesweeper.question.compareroutepolicystanzas;

import com.google.common.collect.ImmutableList;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.batfish.common.Answerer;
import org.batfish.common.NetworkSnapshot;
import org.batfish.common.plugin.IBatfish;
import org.batfish.datamodel.answers.AnswerElement;
import org.batfish.datamodel.questions.Question;
import org.batfish.datamodel.table.Row;
import org.batfish.datamodel.table.TableAnswerElement;
import org.batfish.minesweeper.question.compareroutepolicies.CompareRoutePoliciesUtils;
import org.batfish.question.testroutepolicies.TestRoutePoliciesAnswerer;


/** An answerer for {@link CompareRoutePolicyStanzasQuestion} */
@ParametersAreNonnullByDefault
public final class CompareRoutePolicyStanzasAnswerer extends Answerer {

    private final @Nonnull CompareRoutePoliciesUtils _utils;

    public CompareRoutePolicyStanzasAnswerer(
            org.batfish.minesweeper.question.compareroutepolicystanzas.CompareRoutePolicyStanzasQuestion question,
            IBatfish batfish
    ) {
        super(question, batfish);
        _utils =
                new CompareRoutePoliciesUtils(
                        question.getDirection(),
                        question.getPolicy(),
                        null,
                        question.getNodes(),
                        _batfish);
    }

    public static TableAnswerElement toTableAnswer(Question question, List<Row> rows) {
        TableAnswerElement answerElement =
                new TableAnswerElement(TestRoutePoliciesAnswerer.compareMetadata());
        answerElement.postProcessAnswer(question, rows);
        return answerElement;
    }

    @Override
    public AnswerElement answer(NetworkSnapshot snapshot) {
        List<Row> rows =
                _utils
                        .getDifferencesStream2(snapshot)
                        .map(t -> TestRoutePoliciesAnswerer.toCompareRow(t.getFirst(), t.getSecond()))
                        .collect(ImmutableList.toImmutableList());

        return toTableAnswer(_question, rows);
    }
}
