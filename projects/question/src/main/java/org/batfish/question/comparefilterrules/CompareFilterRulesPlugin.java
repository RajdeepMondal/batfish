package org.batfish.question.comparefilterrules;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.auto.service.AutoService;
import org.batfish.common.Answerer;
import org.batfish.common.plugin.IBatfish;
import org.batfish.common.plugin.Plugin;
import org.batfish.datamodel.questions.Question;
import org.batfish.question.QuestionPlugin;

/** Plugin for {@link CompareFilterRulesQuestion}. */
@AutoService(Plugin.class)
public final class CompareFilterRulesPlugin extends QuestionPlugin {
    @Override
    protected Answerer createAnswerer(Question question, IBatfish batfish) {
        checkArgument(question instanceof CompareFilterRulesQuestion);
        return new CompareFilterRulesAnswerer((CompareFilterRulesQuestion) question, batfish);
    }

    @Override
    protected Question createQuestion() {
        return new CompareFilterRulesQuestion();
    }
}
