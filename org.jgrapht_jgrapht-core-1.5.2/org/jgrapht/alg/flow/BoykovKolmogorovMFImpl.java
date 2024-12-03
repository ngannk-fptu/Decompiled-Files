/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.flow;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import org.jgrapht.Graph;
import org.jgrapht.alg.flow.MaximumFlowAlgorithmBase;
import org.jgrapht.alg.interfaces.MaximumFlowAlgorithm;
import org.jgrapht.alg.util.extension.ExtensionFactory;

public class BoykovKolmogorovMFImpl<V, E>
extends MaximumFlowAlgorithmBase<V, E> {
    private static final boolean DEBUG = false;
    private static final long FREE_NODE_TIMESTAMP = 0L;
    private static final long INITIAL_TIMESTAMP = 1L;
    private long currentTimestamp;
    private final ExtensionFactory<VertexExtension> vertexExtensionsFactory = () -> new VertexExtension();
    private final ExtensionFactory<MaximumFlowAlgorithmBase.AnnotatedFlowEdge> edgeExtensionsFactory = () -> new MaximumFlowAlgorithmBase.AnnotatedFlowEdge();
    private VertexExtension currentSource;
    private VertexExtension currentSink;
    private final Deque<VertexExtension> activeVertices = new ArrayDeque<VertexExtension>();
    private final List<VertexExtension> orphans = new ArrayList<VertexExtension>();
    private final Deque<VertexExtension> childOrphans = new ArrayDeque<VertexExtension>();

    public BoykovKolmogorovMFImpl(Graph<V, E> network) {
        this(network, 1.0E-9);
    }

    public BoykovKolmogorovMFImpl(Graph<V, E> network, double epsilon) {
        super(Objects.requireNonNull(network, "Network must be not null!"), epsilon);
    }

    @Override
    public MaximumFlowAlgorithm.MaximumFlow<E> getMaximumFlow(V source, V sink) {
        this.calculateMaximumFlow(source, sink);
        this.maxFlow = this.composeFlow();
        return new MaximumFlowAlgorithm.MaximumFlowImpl(this.maxFlowValue, this.maxFlow);
    }

    private void calculateMaximumFlow(V source, V sink) {
        MaximumFlowAlgorithmBase.AnnotatedFlowEdge boundingEdge;
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
        this.currentTimestamp = 1L;
        this.augmentShortPaths(this.currentSource, this.currentSink);
        this.currentSource.treeStatus = VertexTreeStatus.SOURCE_TREE_VERTEX;
        this.currentSink.treeStatus = VertexTreeStatus.SINK_TREE_VERTEX;
        this.makeActive(this.currentSource);
        this.makeActive(this.currentSink);
        while ((boundingEdge = this.grow()) != null) {
            this.augment(boundingEdge);
            this.nextIteration();
            this.adopt();
        }
    }

    private void augmentShortPaths(VertexExtension source, VertexExtension sink) {
        block0: for (MaximumFlowAlgorithmBase.AnnotatedFlowEdge sourceEdge : source.getOutgoing()) {
            VertexExtension mediumVertex = (VertexExtension)sourceEdge.getTarget();
            if (mediumVertex == sink) {
                double flow = sourceEdge.getResidualCapacity();
                this.pushFlowThrough(sourceEdge, flow);
                this.maxFlowValue += flow;
                continue;
            }
            for (MaximumFlowAlgorithmBase.AnnotatedFlowEdge sinkEdge : mediumVertex.getOutgoing()) {
                VertexExtension targetVertex = (VertexExtension)sinkEdge.getTarget();
                if (targetVertex == sink) {
                    double flow = Math.min(sourceEdge.getResidualCapacity(), sinkEdge.getResidualCapacity());
                    this.pushFlowThrough(sourceEdge, flow);
                    this.pushFlowThrough(sinkEdge, flow);
                    this.maxFlowValue += flow;
                }
                if (sourceEdge.hasCapacity()) continue;
                continue block0;
            }
        }
    }

    private MaximumFlowAlgorithmBase.AnnotatedFlowEdge grow() {
        VertexExtension activeVertex = this.nextActiveVertex();
        while (activeVertex != null) {
            if (activeVertex.isSourceTreeVertex()) {
                for (MaximumFlowAlgorithmBase.AnnotatedFlowEdge edge : activeVertex.getOutgoing()) {
                    if (!edge.hasCapacity()) continue;
                    VertexExtension target = (VertexExtension)edge.getTarget();
                    if (target.isSinkTreeVertex()) {
                        return edge;
                    }
                    if (target.isFreeVertex()) {
                        target.parentEdge = edge;
                        target.treeStatus = VertexTreeStatus.SOURCE_TREE_VERTEX;
                        target.distance = activeVertex.distance + 1;
                        target.timestamp = activeVertex.timestamp;
                        this.makeActive(target);
                        continue;
                    }
                    assert (target.isSourceTreeVertex());
                    if (!this.isCloserToTerminal(activeVertex, target)) continue;
                    target.parentEdge = edge;
                    target.distance = activeVertex.distance + 1;
                    target.timestamp = activeVertex.timestamp;
                }
            } else {
                assert (activeVertex.isSinkTreeVertex());
                for (MaximumFlowAlgorithmBase.AnnotatedFlowEdge edge : activeVertex.getOutgoing()) {
                    if (!edge.hasCapacity()) continue;
                    VertexExtension source = (VertexExtension)edge.getSource();
                    if (source.isSourceTreeVertex()) {
                        return edge;
                    }
                    if (source.isFreeVertex()) {
                        source.parentEdge = edge;
                        source.treeStatus = VertexTreeStatus.SINK_TREE_VERTEX;
                        source.distance = activeVertex.distance + 1;
                        source.timestamp = activeVertex.timestamp;
                        this.makeActive(source);
                        continue;
                    }
                    assert (source.isSinkTreeVertex());
                    if (!this.isCloserToTerminal(activeVertex, source)) continue;
                    source.parentEdge = edge;
                    source.distance = activeVertex.distance + 1;
                    source.timestamp = activeVertex.timestamp;
                }
            }
            this.finishVertex(activeVertex);
            activeVertex = this.nextActiveVertex();
        }
        return null;
    }

    private void augment(MaximumFlowAlgorithmBase.AnnotatedFlowEdge boundingEdge) {
        double bottleneck = this.findBottleneck(boundingEdge);
        this.pushFlowThrough(boundingEdge, bottleneck);
        VertexExtension source = (VertexExtension)boundingEdge.getSource();
        while (source != this.currentSource) {
            MaximumFlowAlgorithmBase.AnnotatedFlowEdge parentEdge = source.parentEdge;
            this.pushFlowThrough(parentEdge, bottleneck);
            if (!parentEdge.hasCapacity()) {
                source.makeOrphan();
                this.orphans.add(source);
            }
            source = (VertexExtension)parentEdge.getSource();
        }
        VertexExtension target = (VertexExtension)boundingEdge.getTarget();
        while (target != this.currentSink) {
            MaximumFlowAlgorithmBase.AnnotatedFlowEdge parentEdge = target.parentEdge;
            this.pushFlowThrough(target.parentEdge, bottleneck);
            if (!parentEdge.hasCapacity()) {
                target.makeOrphan();
                this.orphans.add(target);
            }
            target = (VertexExtension)parentEdge.getTarget();
        }
        this.maxFlowValue += bottleneck;
    }

    private double findBottleneck(MaximumFlowAlgorithmBase.AnnotatedFlowEdge boundingEdge) {
        double bottleneck = boundingEdge.getResidualCapacity();
        VertexExtension source = (VertexExtension)boundingEdge.getSource();
        while (source != this.currentSource) {
            bottleneck = Math.min(bottleneck, source.parentEdge.getResidualCapacity());
            source = (VertexExtension)source.parentEdge.getSource();
        }
        VertexExtension target = (VertexExtension)boundingEdge.getTarget();
        while (target != this.currentSink) {
            bottleneck = Math.min(bottleneck, target.parentEdge.getResidualCapacity());
            target = (VertexExtension)target.parentEdge.getTarget();
        }
        return bottleneck;
    }

    private void adopt() {
        while (!this.orphans.isEmpty() || !this.childOrphans.isEmpty()) {
            VertexExtension targetVertex;
            VertexExtension targetNode;
            int minDistance;
            MaximumFlowAlgorithmBase.AnnotatedFlowEdge newParentEdge;
            VertexExtension currentVertex;
            if (this.childOrphans.isEmpty()) {
                currentVertex = this.orphans.get(this.orphans.size() - 1);
                this.orphans.remove(this.orphans.size() - 1);
            } else {
                currentVertex = this.childOrphans.removeLast();
            }
            if (currentVertex.isSourceTreeVertex()) {
                newParentEdge = null;
                minDistance = Integer.MAX_VALUE;
                for (MaximumFlowAlgorithmBase.AnnotatedFlowEdge edge : currentVertex.getOutgoing()) {
                    if (!edge.getInverse().hasCapacity() || !(targetNode = (VertexExtension)edge.getTarget()).isSourceTreeVertex() || !this.hasConnectionToTerminal(targetNode) || targetNode.distance >= minDistance) continue;
                    minDistance = targetNode.distance;
                    newParentEdge = edge.getInverse();
                }
                if (newParentEdge == null) {
                    currentVertex.timestamp = 0L;
                    currentVertex.treeStatus = VertexTreeStatus.FREE_VERTEX;
                    for (MaximumFlowAlgorithmBase.AnnotatedFlowEdge edge : currentVertex.getOutgoing()) {
                        targetVertex = (VertexExtension)edge.getTarget();
                        if (!targetVertex.isSourceTreeVertex()) continue;
                        if (edge.getInverse().hasCapacity()) {
                            this.makeActive(targetVertex);
                        }
                        if (targetVertex.parentEdge != edge) continue;
                        targetVertex.makeOrphan();
                        this.childOrphans.addFirst(targetVertex);
                    }
                    continue;
                }
                this.makeCheckedInThisIteration(currentVertex);
                currentVertex.parentEdge = newParentEdge;
                currentVertex.distance = minDistance + 1;
                continue;
            }
            assert (currentVertex.isSinkTreeVertex());
            newParentEdge = null;
            minDistance = Integer.MAX_VALUE;
            for (MaximumFlowAlgorithmBase.AnnotatedFlowEdge edge : currentVertex.getOutgoing()) {
                if (!edge.hasCapacity() || !(targetNode = (VertexExtension)edge.getTarget()).isSinkTreeVertex() || !this.hasConnectionToTerminal(targetNode) || targetNode.distance >= minDistance) continue;
                minDistance = targetNode.distance;
                newParentEdge = edge;
            }
            if (newParentEdge == null) {
                currentVertex.timestamp = 0L;
                currentVertex.treeStatus = VertexTreeStatus.FREE_VERTEX;
                for (MaximumFlowAlgorithmBase.AnnotatedFlowEdge edge : currentVertex.getOutgoing()) {
                    targetVertex = (VertexExtension)edge.getTarget();
                    if (!targetVertex.isSinkTreeVertex()) continue;
                    if (edge.hasCapacity()) {
                        this.makeActive(targetVertex);
                    }
                    if (targetVertex.parentEdge != edge.getInverse()) continue;
                    targetVertex.makeOrphan();
                    this.childOrphans.addFirst(targetVertex);
                }
                continue;
            }
            this.makeCheckedInThisIteration(currentVertex);
            currentVertex.parentEdge = newParentEdge;
            currentVertex.distance = minDistance + 1;
        }
    }

    private void nextIteration() {
        ++this.currentTimestamp;
        this.makeCheckedInThisIteration(this.currentSource);
        this.makeCheckedInThisIteration(this.currentSink);
    }

    private void makeActive(VertexExtension vertex) {
        if (!vertex.active) {
            vertex.active = true;
            this.activeVertices.addFirst(vertex);
        }
    }

    private VertexExtension nextActiveVertex() {
        while (!this.activeVertices.isEmpty()) {
            VertexExtension nextActive = this.activeVertices.getLast();
            assert (nextActive.active);
            if (!nextActive.isFreeVertex()) {
                return nextActive;
            }
            this.activeVertices.removeLast();
            nextActive.active = false;
        }
        return null;
    }

    private void finishVertex(VertexExtension vertex) {
        assert (this.activeVertices.getLast() == vertex);
        this.activeVertices.pollLast();
        vertex.active = false;
    }

    private void makeCheckedInThisIteration(VertexExtension vertex) {
        vertex.timestamp = this.currentTimestamp;
    }

    private boolean wasCheckedInThisIteration(VertexExtension vertex) {
        return vertex.timestamp == this.currentTimestamp;
    }

    private boolean hasConnectionToTerminal(VertexExtension vertex) {
        VertexExtension currentVertex;
        int distance = 0;
        for (currentVertex = vertex; currentVertex != this.currentSource && currentVertex != this.currentSink; currentVertex = currentVertex.getParent()) {
            if (currentVertex.parentEdge == null) {
                return false;
            }
            if (this.wasCheckedInThisIteration(vertex)) {
                distance += currentVertex.distance;
                break;
            }
            ++distance;
        }
        currentVertex = vertex;
        while (!this.wasCheckedInThisIteration(currentVertex)) {
            currentVertex.distance = distance--;
            this.makeCheckedInThisIteration(currentVertex);
            currentVertex = currentVertex.getParent();
        }
        return true;
    }

    private boolean isCloserToTerminal(VertexExtension p, VertexExtension t) {
        return p.timestamp >= t.timestamp && p.distance + 1 < t.distance;
    }

    private VertexExtension getVertexExtension(V vertex) {
        return (VertexExtension)this.vertexExtensionManager.getExtension(vertex);
    }

    private class VertexExtension
    extends MaximumFlowAlgorithmBase.VertexExtensionBase {
        long timestamp;
        int distance;
        boolean active;
        MaximumFlowAlgorithmBase.AnnotatedFlowEdge parentEdge = null;
        VertexTreeStatus treeStatus = VertexTreeStatus.FREE_VERTEX;

        VertexExtension() {
        }

        boolean isSourceTreeVertex() {
            return this.treeStatus == VertexTreeStatus.SOURCE_TREE_VERTEX;
        }

        boolean isSinkTreeVertex() {
            return this.treeStatus == VertexTreeStatus.SINK_TREE_VERTEX;
        }

        boolean isFreeVertex() {
            return this.treeStatus == VertexTreeStatus.FREE_VERTEX;
        }

        void makeOrphan() {
            this.parentEdge = null;
        }

        VertexExtension getParent() {
            assert (this.parentEdge != null);
            return this == this.parentEdge.getSource() ? (VertexExtension)this.parentEdge.getTarget() : (VertexExtension)this.parentEdge.getSource();
        }

        public String toString() {
            return String.format("{%s}: parent_edge = %s, tree_status = %s, distance = %d, timestamp = %d", new Object[]{this.prototype, this.parentEdge == null ? "null" : String.format("(%s, %s)", ((MaximumFlowAlgorithmBase.VertexExtensionBase)this.parentEdge.getSource()).prototype, ((MaximumFlowAlgorithmBase.VertexExtensionBase)this.parentEdge.getTarget()).prototype), this.treeStatus, this.distance, this.timestamp});
        }
    }

    private static enum VertexTreeStatus {
        SOURCE_TREE_VERTEX{

            @Override
            public String toString() {
                return "SOURCE_TREE_VERTEX";
            }
        }
        ,
        SINK_TREE_VERTEX{

            @Override
            public String toString() {
                return "SINK_TREE_VERTEX";
            }
        }
        ,
        FREE_VERTEX{

            @Override
            public String toString() {
                return "FREE_VERTEX";
            }
        };


        public abstract String toString();
    }
}

