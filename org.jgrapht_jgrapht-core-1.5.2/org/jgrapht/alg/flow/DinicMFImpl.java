/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.flow;

import java.util.ArrayDeque;
import org.jgrapht.Graph;
import org.jgrapht.alg.flow.MaximumFlowAlgorithmBase;
import org.jgrapht.alg.interfaces.MaximumFlowAlgorithm;
import org.jgrapht.alg.util.extension.ExtensionFactory;

public class DinicMFImpl<V, E>
extends MaximumFlowAlgorithmBase<V, E> {
    private VertexExtension currentSource;
    private VertexExtension currentSink;
    private final ExtensionFactory<VertexExtension> vertexExtensionsFactory = () -> new VertexExtension();
    private final ExtensionFactory<MaximumFlowAlgorithmBase.AnnotatedFlowEdge> edgeExtensionsFactory = () -> new MaximumFlowAlgorithmBase.AnnotatedFlowEdge();

    public DinicMFImpl(Graph<V, E> network, double epsilon) {
        super(network, epsilon);
        if (epsilon <= 0.0) {
            throw new IllegalArgumentException("Epsilon must be positive!");
        }
        for (E e : network.edgeSet()) {
            if (!(network.getEdgeWeight(e) < -epsilon)) continue;
            throw new IllegalArgumentException("Capacity must be non-negative!");
        }
    }

    public DinicMFImpl(Graph<V, E> network) {
        this(network, 1.0E-9);
    }

    @Override
    public MaximumFlowAlgorithm.MaximumFlow<E> getMaximumFlow(V source, V sink) {
        this.calculateMaxFlow(source, sink);
        this.maxFlow = this.composeFlow();
        return new MaximumFlowAlgorithm.MaximumFlowImpl(this.maxFlowValue, this.maxFlow);
    }

    private double calculateMaxFlow(V source, V sink) {
        super.init(source, sink, this.vertexExtensionsFactory, this.edgeExtensionsFactory);
        if (!this.network.containsVertex(source)) {
            throw new IllegalArgumentException("Network does not contain source!");
        }
        if (!this.network.containsVertex(sink)) {
            throw new IllegalArgumentException("Network does not contain sink!");
        }
        if (source.equals(sink)) {
            throw new IllegalArgumentException("Source is equal to sink!");
        }
        this.currentSource = this.getVertexExtension(source);
        this.currentSink = this.getVertexExtension(sink);
        this.dinic();
        return this.maxFlowValue;
    }

    private boolean bfs() {
        for (Object v : this.network.vertexSet()) {
            this.getVertexExtension(v).level = -1;
        }
        ArrayDeque<VertexExtension> queue = new ArrayDeque<VertexExtension>();
        queue.offer(this.currentSource);
        this.currentSource.level = 0;
        while (!queue.isEmpty() && this.currentSink.level == -1) {
            Object v;
            v = (VertexExtension)queue.poll();
            for (MaximumFlowAlgorithmBase.AnnotatedFlowEdge edge : ((MaximumFlowAlgorithmBase.VertexExtensionBase)v).getOutgoing()) {
                VertexExtension u = (VertexExtension)edge.getTarget();
                if (this.comparator.compare(edge.flow, edge.capacity) >= 0 || u.level != -1) continue;
                u.level = ((VertexExtension)v).level + 1;
                queue.offer(u);
            }
        }
        return this.currentSink.level != -1;
    }

    public double dfs(VertexExtension v, double flow) {
        if (this.comparator.compare(0.0, flow) == 0) {
            return flow;
        }
        if (v.equals(this.currentSink)) {
            return flow;
        }
        while (v.index < v.getOutgoing().size()) {
            double pushed;
            MaximumFlowAlgorithmBase.AnnotatedFlowEdge edge = v.getOutgoing().get(v.index);
            VertexExtension u = (VertexExtension)edge.getTarget();
            if (this.comparator.compare(edge.flow, edge.capacity) < 0 && u.level == v.level + 1 && this.comparator.compare(pushed = this.dfs(u, Math.min(flow, edge.capacity - edge.flow)), 0.0) != 0) {
                this.pushFlowThrough(edge, pushed);
                return pushed;
            }
            ++v.index;
        }
        return 0.0;
    }

    public void dinic() {
        while (this.bfs()) {
            double pushed;
            for (Object v : this.network.vertexSet()) {
                this.getVertexExtension(v).index = 0;
            }
            while ((pushed = this.dfs(this.currentSource, Double.POSITIVE_INFINITY)) != 0.0) {
                this.maxFlowValue += pushed;
            }
        }
    }

    private VertexExtension getVertexExtension(V v) {
        return (VertexExtension)this.vertexExtensionManager.getExtension(v);
    }

    class VertexExtension
    extends MaximumFlowAlgorithmBase.VertexExtensionBase {
        int index;
        int level;

        VertexExtension() {
        }
    }
}

