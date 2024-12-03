/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 */
package com.google.common.graph;

import com.google.common.annotations.Beta;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.graph.AbstractGraphBuilder;
import com.google.common.graph.ElementOrder;
import com.google.common.graph.ElementTypesAreNonnullByDefault;
import com.google.common.graph.Graphs;
import com.google.common.graph.ImmutableValueGraph;
import com.google.common.graph.MutableValueGraph;
import com.google.common.graph.StandardMutableValueGraph;
import com.google.common.graph.ValueGraph;
import com.google.errorprone.annotations.CanIgnoreReturnValue;

@ElementTypesAreNonnullByDefault
@Beta
public final class ValueGraphBuilder<N, V>
extends AbstractGraphBuilder<N> {
    private ValueGraphBuilder(boolean directed) {
        super(directed);
    }

    public static ValueGraphBuilder<Object, Object> directed() {
        return new ValueGraphBuilder<Object, Object>(true);
    }

    public static ValueGraphBuilder<Object, Object> undirected() {
        return new ValueGraphBuilder<Object, Object>(false);
    }

    public static <N, V> ValueGraphBuilder<N, V> from(ValueGraph<N, V> graph) {
        return new ValueGraphBuilder<N, V>(graph.isDirected()).allowsSelfLoops(graph.allowsSelfLoops()).nodeOrder(graph.nodeOrder()).incidentEdgeOrder(graph.incidentEdgeOrder());
    }

    public <N1 extends N, V1 extends V> ImmutableValueGraph.Builder<N1, V1> immutable() {
        ValueGraphBuilder<N1, V1> castBuilder = this.cast();
        return new ImmutableValueGraph.Builder<N1, V1>(castBuilder);
    }

    @CanIgnoreReturnValue
    public ValueGraphBuilder<N, V> allowsSelfLoops(boolean allowsSelfLoops) {
        this.allowsSelfLoops = allowsSelfLoops;
        return this;
    }

    @CanIgnoreReturnValue
    public ValueGraphBuilder<N, V> expectedNodeCount(int expectedNodeCount) {
        this.expectedNodeCount = Optional.of(Graphs.checkNonNegative(expectedNodeCount));
        return this;
    }

    public <N1 extends N> ValueGraphBuilder<N1, V> nodeOrder(ElementOrder<N1> nodeOrder) {
        ValueGraphBuilder newBuilder = this.cast();
        newBuilder.nodeOrder = Preconditions.checkNotNull(nodeOrder);
        return newBuilder;
    }

    public <N1 extends N> ValueGraphBuilder<N1, V> incidentEdgeOrder(ElementOrder<N1> incidentEdgeOrder) {
        Preconditions.checkArgument(incidentEdgeOrder.type() == ElementOrder.Type.UNORDERED || incidentEdgeOrder.type() == ElementOrder.Type.STABLE, "The given elementOrder (%s) is unsupported. incidentEdgeOrder() only supports ElementOrder.unordered() and ElementOrder.stable().", incidentEdgeOrder);
        ValueGraphBuilder newBuilder = this.cast();
        newBuilder.incidentEdgeOrder = Preconditions.checkNotNull(incidentEdgeOrder);
        return newBuilder;
    }

    public <N1 extends N, V1 extends V> MutableValueGraph<N1, V1> build() {
        return new StandardMutableValueGraph(this);
    }

    ValueGraphBuilder<N, V> copy() {
        ValueGraphBuilder<N, V> newBuilder = new ValueGraphBuilder<N, V>(this.directed);
        newBuilder.allowsSelfLoops = this.allowsSelfLoops;
        newBuilder.nodeOrder = this.nodeOrder;
        newBuilder.expectedNodeCount = this.expectedNodeCount;
        newBuilder.incidentEdgeOrder = this.incidentEdgeOrder;
        return newBuilder;
    }

    private <N1 extends N, V1 extends V> ValueGraphBuilder<N1, V1> cast() {
        return this;
    }
}

