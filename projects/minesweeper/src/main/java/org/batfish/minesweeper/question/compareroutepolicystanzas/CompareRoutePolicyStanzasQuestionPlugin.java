package org.batfish.minesweeper.question.compareroutepolicystanzas;

import com.google.auto.service.AutoService;
import org.batfish.common.Answerer;
import org.batfish.common.plugin.IBatfish;
import org.batfish.common.plugin.Plugin;
import org.batfish.datamodel.questions.Question;
import org.batfish.question.QuestionPlugin;

/** QuestionPlugin for {@link CompareRoutePolicyStanzasQuestion}. */
@AutoService(Plugin.class)
public final class CompareRoutePolicyStanzasQuestionPlugin extends QuestionPlugin {
    @Override
    protected Answerer createAnswerer(Question question, IBatfish batfish) {
        return new CompareRoutePolicyStanzasAnswerer((CompareRoutePolicyStanzasQuestion) question, batfish);
    }

    @Override
    protected Question createQuestion() {
        return new CompareRoutePolicyStanzasQuestion();
    }
}
