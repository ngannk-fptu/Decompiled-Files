/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.flow;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.alg.flow.MaximumFlowAlgorithmBase;
import org.jgrapht.alg.interfaces.MaximumFlowAlgorithm;
import org.jgrapht.alg.util.extension.ExtensionFactory;

public final class EdmondsKarpMFImpl<V, E>
extends MaximumFlowAlgorithmBase<V, E> {
    private VertexExtension currentSource;
    private VertexExtension currentSink;
    private final ExtensionFactory<VertexExtension> vertexExtensionsFactory = () -> new VertexExtension();
    private final ExtensionFactory<MaximumFlowAlgorithmBase.AnnotatedFlowEdge> edgeExtensionsFactory = () -> new MaximumFlowAlgorithmBase.AnnotatedFlowEdge();

    public EdmondsKarpMFImpl(Graph<V, E> network) {
        this(network, 1.0E-9);
    }

    public EdmondsKarpMFImpl(Graph<V, E> network, double epsilon) {
        super(network, epsilon);
        if (network == null) {
            throw new NullPointerException("network is null");
        }
        if (epsilon <= 0.0) {
            throw new IllegalArgumentException("invalid epsilon (must be positive)");
        }
        for (E e : network.edgeSet()) {
            if (!(network.getEdgeWeight(e) < -epsilon)) continue;
            throw new IllegalArgumentException("invalid capacity (must be non-negative)");
        }
    }

    @Override
    public MaximumFlowAlgorithm.MaximumFlow<E> getMaximumFlow(V source, V sink) {
        this.calculateMaximumFlow(source, sink);
        this.maxFlow = this.composeFlow();
        return new MaximumFlowAlgorithm.MaximumFlowImpl(this.maxFlowValue, this.maxFlow);
    }

    public double calculateMaximumFlow(V source, V sink) {
        super.init(source, sink, this.vertexExtensionsFactory, this.edgeExtensionsFactory);
        if (!this.network.containsVertex(source)) {
            throw new IllegalArgumentException("invalid source (null or not from this network)");
        }
        if (!this.network.containsVertex(sink)) {
            throw new IllegalArgumentException("invalid sink (null or not from this network)");
        }
        if (source.equals(sink)) {
            throw new IllegalArgumentException("source is equal to sink");
        }
        this.currentSource = this.getVertexExtension(source);
        this.currentSink = this.getVertexExtension(sink);
        while (true) {
            this.breadthFirstSearch();
            if (!this.currentSink.visited) break;
            this.maxFlowValue += this.augmentFlow();
        }
        return this.maxFlowValue;
    }

    private void breadthFirstSearch() {
        for (Object v : this.network.vertexSet()) {
            this.getVertexExtension(v).visited = false;
            this.getVertexExtension(v).lastArcs = null;
        }
        ArrayDeque<VertexExtension> queue = new ArrayDeque<VertexExtension>();
        queue.offer(this.currentSource);
        this.currentSource.visited = true;
        this.currentSource.excess = Double.POSITIVE_INFINITY;
        this.currentSink.excess = 0.0;
        boolean seenSink = false;
        while (queue.size() != 0) {
            VertexExtension ux = (VertexExtension)queue.poll();
            for (MaximumFlowAlgorithmBase.AnnotatedFlowEdge ex : ux.getOutgoing()) {
                if (this.comparator.compare(ex.flow, ex.capacity) >= 0) continue;
                VertexExtension vx = (VertexExtension)ex.getTarget();
                if (vx == this.currentSink) {
                    vx.visited = true;
                    if (vx.lastArcs == null) {
                        vx.lastArcs = new ArrayList<MaximumFlowAlgorithmBase.AnnotatedFlowEdge>();
                    }
                    vx.lastArcs.add(ex);
                    vx.excess += Math.min(ux.excess, ex.capacity - ex.flow);
                    seenSink = true;
                    continue;
                }
                if (vx.visited) continue;
                vx.visited = true;
                vx.excess = Math.min(ux.excess, ex.capacity - ex.flow);
                vx.lastArcs = Collections.singletonList(ex);
                if (seenSink) continue;
                queue.add(vx);
            }
        }
    }

    private double augmentFlow() {
        double flowIncrease = 0.0;
        HashSet<VertexExtension> seen = new HashSet<VertexExtension>();
        for (MaximumFlowAlgorithmBase.AnnotatedFlowEdge ex : this.currentSink.lastArcs) {
            double deltaFlow = Math.min(((MaximumFlowAlgorithmBase.VertexExtensionBase)ex.getSource()).excess, ex.capacity - ex.flow);
            if (!this.augmentFlowAlongInternal(deltaFlow, (VertexExtension)ex.getSource(), seen)) continue;
            this.pushFlowThrough(ex, deltaFlow);
            flowIncrease += deltaFlow;
        }
        return flowIncrease;
    }

    private boolean augmentFlowAlongInternal(double deltaFlow, VertexExtension node, Set<VertexExtension> seen) {
        if (node == this.currentSource) {
            return true;
        }
        if (seen.contains(node)) {
            return false;
        }
        seen.add(node);
        MaximumFlowAlgorithmBase.AnnotatedFlowEdge prev = node.lastArcs.get(0);
        if (this.augmentFlowAlongInternal(deltaFlow, (VertexExtension)prev.getSource(), seen)) {
            this.pushFlowThrough(prev, deltaFlow);
            return true;
        }
        return false;
    }

    private VertexExtension getVertexExtension(V v) {
        return (VertexExtension)this.vertexExtensionManager.getExtension(v);
    }

    class VertexExtension
    extends MaximumFlowAlgorithmBase.VertexExtensionBase {
        boolean visited;
        List<MaximumFlowAlgorithmBase.AnnotatedFlowEdge> lastArcs;

        VertexExtension() {
        }
    }
}

