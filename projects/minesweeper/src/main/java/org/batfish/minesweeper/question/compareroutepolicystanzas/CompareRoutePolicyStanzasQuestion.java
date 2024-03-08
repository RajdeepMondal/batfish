package org.batfish.minesweeper.question.compareroutepolicystanzas;

import static org.apache.commons.lang3.ObjectUtils.firstNonNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.annotations.VisibleForTesting;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.batfish.datamodel.questions.Question;
import org.batfish.datamodel.routing_policy.Environment;

/** A question for comparing route policy stanzas */
@ParametersAreNonnullByDefault
public final class CompareRoutePolicyStanzasQuestion extends Question{

    private static final String PROP_DIRECTION = "direction";
    private static final String PROP_POLICY = "policy";
    private static final String PROP_NODES = "nodes";

    @VisibleForTesting
    static final Environment.Direction DEFAULT_DIRECTION = Environment.Direction.IN;

    private final @Nonnull Environment.Direction _direction;
    private final @Nullable String _policy;
    private final @Nullable String _nodes;

    public CompareRoutePolicyStanzasQuestion() { this(DEFAULT_DIRECTION, null, null); }

    public CompareRoutePolicyStanzasQuestion(
            Environment.Direction direction,
            @Nullable String policy,
            @Nullable String nodes) {

        _direction = direction;
        _policy = policy;
        _nodes = nodes;
    }

    @JsonCreator
    private static CompareRoutePolicyStanzasQuestion jsonCreator(
            @JsonProperty(PROP_DIRECTION) @Nullable Environment.Direction direction,
            @JsonProperty(PROP_POLICY) @Nullable String policy,
            @JsonProperty(PROP_NODES) @Nullable String nodes) {
        return new CompareRoutePolicyStanzasQuestion(
                firstNonNull(direction, DEFAULT_DIRECTION), policy, nodes);
    }

    @JsonIgnore
    @Override
    public boolean getDataPlane() { return false; }

    @JsonProperty(PROP_DIRECTION)
    public @Nonnull Environment.Direction getDirection() { return _direction; }

    @JsonIgnore
    @Override
    public @Nonnull String getName() { return "compareRoutePolicyStanzas"; }

    @JsonProperty(PROP_POLICY)
    public @Nullable String getPolicy() { return _policy; }

    @JsonProperty(PROP_NODES)
    public @Nullable String getNodes() { return _nodes; }
}
