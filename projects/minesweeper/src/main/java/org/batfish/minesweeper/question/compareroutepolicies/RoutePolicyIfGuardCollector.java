package org.batfish.minesweeper.question.compareroutepolicies;

import com.google.common.collect.ImmutableSet;
import java.util.List;

import org.batfish.datamodel.Configuration;
import org.batfish.datamodel.routing_policy.communities.SetCommunities;
import org.batfish.datamodel.routing_policy.expr.BooleanExpr;
import org.batfish.datamodel.routing_policy.statement.StatementVisitor;
import org.batfish.datamodel.routing_policy.statement.BufferedStatement;
import org.batfish.datamodel.routing_policy.statement.CallStatement;
import org.batfish.datamodel.routing_policy.statement.Comment;
import org.batfish.datamodel.routing_policy.statement.If;
import org.batfish.datamodel.routing_policy.statement.ExcludeAsPath;
import org.batfish.datamodel.routing_policy.statement.SetIsisMetricType;
import org.batfish.datamodel.routing_policy.statement.SetTunnelEncapsulationAttribute;
import org.batfish.datamodel.routing_policy.statement.SetVarMetricType;
import org.batfish.datamodel.routing_policy.statement.SetTag;
import org.batfish.datamodel.routing_policy.statement.SetOspfMetricType;
import org.batfish.datamodel.routing_policy.statement.SetEigrpMetric;
import org.batfish.datamodel.routing_policy.statement.SetOriginatorIp;
import org.batfish.datamodel.routing_policy.statement.SetWeight;
import org.batfish.datamodel.routing_policy.statement.Statements.StaticStatement;
import org.batfish.datamodel.routing_policy.statement.Statement;
import org.batfish.datamodel.routing_policy.statement.TraceableStatement;
import org.batfish.datamodel.routing_policy.statement.SetMetric;
import org.batfish.datamodel.routing_policy.statement.SetOrigin;
import org.batfish.datamodel.routing_policy.statement.SetIsisLevel;
import org.batfish.datamodel.routing_policy.statement.SetLocalPreference;
import org.batfish.datamodel.routing_policy.statement.SetDefaultTag;
import org.batfish.datamodel.routing_policy.statement.SetAdministrativeCost;
import org.batfish.datamodel.routing_policy.statement.SetDefaultPolicy;
import org.batfish.datamodel.routing_policy.statement.ReplaceAsesInAsSequence;
import org.batfish.datamodel.routing_policy.statement.RemoveTunnelEncapsulationAttribute;
import org.batfish.datamodel.routing_policy.statement.PrependAsPath;
import org.batfish.datamodel.routing_policy.statement.SetNextHop;


import org.batfish.minesweeper.utils.Tuple;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Set;

@ParametersAreNonnullByDefault
public class RoutePolicyIfGuardCollector
        implements StatementVisitor<Set<BooleanExpr>, Tuple<Set<String>, Configuration>> {
    @Override
    public Set<BooleanExpr> visitBufferedStatement(
            BufferedStatement bufferedStatement, Tuple<Set<String>, Configuration> arg) {
        return bufferedStatement.getStatement().accept(this, arg);
    }

    @Override
    public Set<BooleanExpr> visitCallStatement(
            CallStatement callStatement, Tuple<Set<String>, Configuration> arg) {
        if (arg.getFirst().contains(callStatement.getCalledPolicyName())) {
            // If we have already visited this policy then don't visit again
            return ImmutableSet.of();
        }
        // Otherwise update the set of seen policies and continue.
        arg.getFirst().add(callStatement.getCalledPolicyName());

        return visitAll(
                arg.getSecond()
                        .getRoutingPolicies()
                        .get(callStatement.getCalledPolicyName())
                        .getStatements(),
                arg);
    }

    @Override
    public Set<BooleanExpr> visitComment(Comment comment, Tuple<Set<String>, Configuration> arg) {
        return ImmutableSet.of();
    }

    @Override
    public Set<BooleanExpr> visitIf(If if1, Tuple<Set<String>, Configuration> arg) {
        ImmutableSet.Builder<BooleanExpr> builder = ImmutableSet.builder();
        return builder
                .add(if1.getGuard())
                .addAll(visitAll(if1.getTrueStatements(), arg))
                .addAll(visitAll(if1.getFalseStatements(), arg))
                .build();
    }

    @Override
    public Set<BooleanExpr> visitPrependAsPath(
            PrependAsPath prependAsPath, Tuple<Set<String>, Configuration> arg) {
        return ImmutableSet.of();
    }

    @Override
    public Set<BooleanExpr> visitReplaceAsesInAsSequence(
            ReplaceAsesInAsSequence replaceAsesInAsPathSequence) {
        return ImmutableSet.of();
    }

    @Override
    public Set<BooleanExpr> visitSetOriginatorIp(
            SetOriginatorIp setOriginatorIp, Tuple<Set<String>, Configuration> arg) {
        return ImmutableSet.of();
    }

    @Override
    public Set<BooleanExpr> visitExcludeAsPath(
            ExcludeAsPath excludeAsPath, Tuple<Set<String>, Configuration> arg) {
        // if/when TransferBDD gets updated to support AS-path excluding, this will have to be updated
        return ImmutableSet.of();
    }

    @Override
    public Set<BooleanExpr> visitRemoveTunnelEncapsulationAttribute(
            RemoveTunnelEncapsulationAttribute removeTunnelAttribute,
            Tuple<Set<String>, Configuration> arg) {
        return ImmutableSet.of();
    }

    @Override
    public Set<BooleanExpr> visitSetAdministrativeCost(
            SetAdministrativeCost setAdministrativeCost, Tuple<Set<String>, Configuration> arg) {
        return ImmutableSet.of();
    }

    @Override
    public Set<BooleanExpr> visitSetCommunities(
            SetCommunities setCommunities, Tuple<Set<String>, Configuration> arg) {
        return ImmutableSet.of();
    }

    @Override
    public Set<BooleanExpr> visitSetDefaultPolicy(
            SetDefaultPolicy setDefaultPolicy, Tuple<Set<String>, Configuration> arg) {
        return ImmutableSet.of();
    }

    @Override
    public Set<BooleanExpr> visitSetEigrpMetric(
            SetEigrpMetric setEigrpMetric, Tuple<Set<String>, Configuration> arg) {
        return ImmutableSet.of();
    }

    @Override
    public Set<BooleanExpr> visitSetIsisLevel(
            SetIsisLevel setIsisLevel, Tuple<Set<String>, Configuration> arg) {
        return ImmutableSet.of();
    }

    @Override
    public Set<BooleanExpr> visitSetIsisMetricType(
            SetIsisMetricType setIsisMetricType, Tuple<Set<String>, Configuration> arg) {
        return ImmutableSet.of();
    }

    @Override
    public Set<BooleanExpr> visitSetLocalPreference(
            SetLocalPreference setLocalPreference, Tuple<Set<String>, Configuration> arg) {
        return ImmutableSet.of();
    }

    @Override
    public Set<BooleanExpr> visitSetMetric(
            SetMetric setMetric, Tuple<Set<String>, Configuration> arg) {
        return ImmutableSet.of();
    }

    @Override
    public Set<BooleanExpr> visitSetNextHop(
            SetNextHop setNextHop, Tuple<Set<String>, Configuration> arg) {
        return ImmutableSet.of();
    }

    @Override
    public Set<BooleanExpr> visitSetOrigin(
            SetOrigin setOrigin, Tuple<Set<String>, Configuration> arg) {
        return ImmutableSet.of();
    }

    @Override
    public Set<BooleanExpr> visitSetOspfMetricType(
            SetOspfMetricType setOspfMetricType, Tuple<Set<String>, Configuration> arg) {
        return ImmutableSet.of();
    }

    @Override
    public Set<BooleanExpr> visitSetTag(SetTag setTag, Tuple<Set<String>, Configuration> arg) {
        return ImmutableSet.of();
    }

    @Override
    public Set<BooleanExpr> visitSetDefaultTag(
            SetDefaultTag setDefaultTag, Tuple<Set<String>, Configuration> arg) {
        return ImmutableSet.of();
    }

    @Override
    public Set<BooleanExpr> visitSetTunnelEncapsulationAttribute(
            SetTunnelEncapsulationAttribute setTunnelAttribute, Tuple<Set<String>, Configuration> arg) {
        return ImmutableSet.of();
    }

    @Override
    public Set<BooleanExpr> visitSetVarMetricType(
            SetVarMetricType setVarMetricType, Tuple<Set<String>, Configuration> arg) {
        return ImmutableSet.of();
    }

    @Override
    public Set<BooleanExpr> visitSetWeight(
            SetWeight setWeight, Tuple<Set<String>, Configuration> arg) {
        return ImmutableSet.of();
    }

    @Override
    public Set<BooleanExpr> visitStaticStatement(
            StaticStatement staticStatement, Tuple<Set<String>, Configuration> arg) {
        return ImmutableSet.of();
    }

    @Override
    public Set<BooleanExpr> visitTraceableStatement(
            TraceableStatement traceableStatement, Tuple<Set<String>, Configuration> arg) {
        return visitAll(traceableStatement.getInnerStatements(), arg);
    }

    public Set<BooleanExpr> visitAll(
            List<Statement> statements, Tuple<Set<String>, Configuration> arg) {
        return statements.stream()
                .flatMap(stmt -> stmt.accept(this, arg).stream())
                .collect(ImmutableSet.toImmutableSet());
    }
}