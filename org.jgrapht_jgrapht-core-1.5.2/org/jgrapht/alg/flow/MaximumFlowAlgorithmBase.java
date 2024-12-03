/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.flow;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.MaximumFlowAlgorithm;
import org.jgrapht.alg.interfaces.MinimumSTCutAlgorithm;
import org.jgrapht.alg.util.ToleranceDoubleComparator;
import org.jgrapht.alg.util.extension.Extension;
import org.jgrapht.alg.util.extension.ExtensionFactory;
import org.jgrapht.alg.util.extension.ExtensionManager;
import org.jgrapht.util.TypeUtil;

public abstract class MaximumFlowAlgorithmBase<V, E>
implements MaximumFlowAlgorithm<V, E>,
MinimumSTCutAlgorithm<V, E> {
    public static final double DEFAULT_EPSILON = 1.0E-9;
    protected Graph<V, E> network;
    protected final boolean directedGraph;
    protected Comparator<Double> comparator;
    protected ExtensionManager<V, ? extends VertexExtensionBase> vertexExtensionManager;
    protected ExtensionManager<E, ? extends AnnotatedFlowEdge> edgeExtensionManager;
    protected V source = null;
    protected V sink = null;
    protected double maxFlowValue = -1.0;
    protected Map<E, Double> maxFlow = null;
    protected Set<V> sourcePartition;
    protected Set<V> sinkPartition;
    protected Set<E> cutEdges;

    public MaximumFlowAlgorithmBase(Graph<V, E> network, double epsilon) {
        this.network = network;
        this.directedGraph = network.getType().isDirected();
        this.comparator = new ToleranceDoubleComparator(epsilon);
    }

    protected <VE extends VertexExtensionBase> void init(V source, V sink, ExtensionFactory<VE> vertexExtensionFactory, ExtensionFactory<AnnotatedFlowEdge> edgeExtensionFactory) {
        this.vertexExtensionManager = new ExtensionManager(vertexExtensionFactory);
        this.edgeExtensionManager = new ExtensionManager(edgeExtensionFactory);
        this.buildInternal();
        this.source = source;
        this.sink = sink;
        this.maxFlowValue = 0.0;
        this.maxFlow = null;
        this.sourcePartition = null;
        this.sinkPartition = null;
        this.cutEdges = null;
    }

    private void buildInternal() {
        if (this.directedGraph) {
            for (V v : this.network.vertexSet()) {
                VertexExtensionBase vx = this.vertexExtensionManager.getExtension(v);
                vx.prototype = v;
            }
            for (V u : this.network.vertexSet()) {
                VertexExtensionBase ux = this.vertexExtensionManager.getExtension(u);
                for (E e : this.network.outgoingEdgesOf(u)) {
                    V v = this.network.getEdgeTarget(e);
                    VertexExtensionBase vx = this.vertexExtensionManager.getExtension(v);
                    AnnotatedFlowEdge forwardEdge = this.createEdge(ux, vx, e, this.network.getEdgeWeight(e));
                    AnnotatedFlowEdge backwardEdge = this.createBackwardEdge(forwardEdge);
                    ux.getOutgoing().add(forwardEdge);
                    if (backwardEdge.prototype != null) continue;
                    vx.getOutgoing().add(backwardEdge);
                }
            }
        } else {
            for (V v : this.network.vertexSet()) {
                VertexExtensionBase vx = this.vertexExtensionManager.getExtension(v);
                vx.prototype = v;
            }
            for (Object e : this.network.edgeSet()) {
                VertexExtensionBase ux = this.vertexExtensionManager.getExtension(this.network.getEdgeSource(e));
                VertexExtensionBase vx = this.vertexExtensionManager.getExtension(this.network.getEdgeTarget(e));
                AnnotatedFlowEdge forwardEdge = this.createEdge(ux, vx, e, this.network.getEdgeWeight(e));
                AnnotatedFlowEdge backwardEdge = this.createBackwardEdge(forwardEdge);
                ux.getOutgoing().add(forwardEdge);
                vx.getOutgoing().add(backwardEdge);
            }
        }
    }

    private AnnotatedFlowEdge createEdge(VertexExtensionBase source, VertexExtensionBase target, E e, double weight) {
        AnnotatedFlowEdge ex = this.edgeExtensionManager.getExtension(e);
        ex.source = source;
        ex.target = target;
        ex.capacity = weight;
        ex.prototype = e;
        return ex;
    }

    private AnnotatedFlowEdge createBackwardEdge(AnnotatedFlowEdge forwardEdge) {
        AnnotatedFlowEdge backwardEdge;
        E backwardPrototype = this.network.getEdge(forwardEdge.target.prototype, forwardEdge.source.prototype);
        if (this.directedGraph && backwardPrototype != null) {
            backwardEdge = this.createEdge(forwardEdge.target, forwardEdge.source, backwardPrototype, this.network.getEdgeWeight(backwardPrototype));
        } else {
            backwardEdge = this.edgeExtensionManager.createExtension();
            backwardEdge.source = forwardEdge.target;
            backwardEdge.target = forwardEdge.source;
            if (!this.directedGraph) {
                backwardEdge.capacity = this.network.getEdgeWeight(backwardPrototype);
                backwardEdge.prototype = backwardPrototype;
            }
        }
        forwardEdge.inverse = backwardEdge;
        backwardEdge.inverse = forwardEdge;
        return backwardEdge;
    }

    protected void pushFlowThrough(AnnotatedFlowEdge edge, double flow) {
        AnnotatedFlowEdge inverseEdge = edge.getInverse();
        assert (this.comparator.compare(edge.flow, 0.0) == 0 || this.comparator.compare(inverseEdge.flow, 0.0) == 0);
        if (this.comparator.compare(inverseEdge.flow, flow) < 0) {
            double flowDifference = flow - inverseEdge.flow;
            edge.flow += flowDifference;
            edge.capacity -= inverseEdge.flow;
            inverseEdge.flow = 0.0;
            inverseEdge.capacity += flowDifference;
        } else {
            edge.capacity -= flow;
            inverseEdge.flow -= flow;
        }
    }

    protected Map<E, Double> composeFlow() {
        HashMap<E, Double> maxFlow = new HashMap<E, Double>();
        for (E e : this.network.edgeSet()) {
            AnnotatedFlowEdge annotatedFlowEdge = this.edgeExtensionManager.getExtension(e);
            maxFlow.put(e, this.directedGraph ? annotatedFlowEdge.flow : Math.max(annotatedFlowEdge.flow, annotatedFlowEdge.inverse.flow));
        }
        return maxFlow;
    }

    public V getCurrentSource() {
        return this.source;
    }

    public V getCurrentSink() {
        return this.sink;
    }

    public double getMaximumFlowValue() {
        return this.maxFlowValue;
    }

    @Override
    public Map<E, Double> getFlowMap() {
        if (this.maxFlow == null) {
            this.maxFlow = this.composeFlow();
        }
        return this.maxFlow;
    }

    @Override
    public V getFlowDirection(E e) {
        if (!this.network.containsEdge(e)) {
            throw new IllegalArgumentException("Cannot query the flow on an edge which does not exist in the input graph!");
        }
        AnnotatedFlowEdge annotatedFlowEdge = this.edgeExtensionManager.getExtension(e);
        if (this.directedGraph) {
            return ((VertexExtensionBase)annotatedFlowEdge.getTarget()).prototype;
        }
        AnnotatedFlowEdge inverseEdge = annotatedFlowEdge.getInverse();
        if (annotatedFlowEdge.flow > inverseEdge.flow) {
            return ((VertexExtensionBase)annotatedFlowEdge.getTarget()).prototype;
        }
        return ((VertexExtensionBase)inverseEdge.getTarget()).prototype;
    }

    @Override
    public double calculateMinCut(V source, V sink) {
        return this.getMaximumFlowValue(source, sink);
    }

    @Override
    public double getCutCapacity() {
        return this.getMaximumFlowValue();
    }

    @Override
    public Set<V> getSourcePartition() {
        if (this.sourcePartition == null) {
            this.calculateSourcePartition();
        }
        return this.sourcePartition;
    }

    @Override
    public Set<V> getSinkPartition() {
        if (this.sinkPartition == null) {
            this.sinkPartition = new LinkedHashSet<V>(this.network.vertexSet());
            this.sinkPartition.removeAll(this.getSourcePartition());
        }
        return this.sinkPartition;
    }

    @Override
    public Set<E> getCutEdges() {
        if (this.cutEdges != null) {
            return this.cutEdges;
        }
        this.cutEdges = new LinkedHashSet();
        Set<V> p1 = this.getSourcePartition();
        if (this.directedGraph) {
            for (V vertex : p1) {
                this.cutEdges.addAll(this.network.outgoingEdgesOf(vertex).stream().filter(edge -> !p1.contains(this.network.getEdgeTarget(edge))).collect(Collectors.toList()));
            }
        } else {
            this.cutEdges.addAll(this.network.edgeSet().stream().filter(e -> p1.contains(this.network.getEdgeSource(e)) ^ p1.contains(this.network.getEdgeTarget(e))).collect(Collectors.toList()));
        }
        return this.cutEdges;
    }

    protected void calculateSourcePartition() {
        this.sourcePartition = new LinkedHashSet<V>();
        ArrayDeque<VertexExtensionBase> processQueue = new ArrayDeque<VertexExtensionBase>();
        processQueue.add(this.vertexExtensionManager.getExtension(this.getCurrentSource()));
        while (!processQueue.isEmpty()) {
            VertexExtensionBase vx = (VertexExtensionBase)processQueue.poll();
            if (this.sourcePartition.contains(vx.prototype)) continue;
            this.sourcePartition.add(vx.prototype);
            for (AnnotatedFlowEdge ex : vx.getOutgoing()) {
                if (!ex.hasCapacity()) continue;
                processQueue.add((VertexExtensionBase)ex.getTarget());
            }
        }
    }

    class VertexExtensionBase
    implements Extension {
        private final List<AnnotatedFlowEdge> outgoing = new ArrayList<AnnotatedFlowEdge>();
        V prototype;
        double excess;

        VertexExtensionBase() {
        }

        public List<AnnotatedFlowEdge> getOutgoing() {
            return this.outgoing;
        }
    }

    class AnnotatedFlowEdge
    implements Extension {
        private VertexExtensionBase source;
        private VertexExtensionBase target;
        private AnnotatedFlowEdge inverse;
        E prototype;
        double capacity;
        double flow;

        AnnotatedFlowEdge() {
        }

        public <VE extends VertexExtensionBase> VE getSource() {
            return (VE)((VertexExtensionBase)TypeUtil.uncheckedCast(this.source));
        }

        public void setSource(VertexExtensionBase source) {
            this.source = source;
        }

        public <VE extends VertexExtensionBase> VE getTarget() {
            return (VE)((VertexExtensionBase)TypeUtil.uncheckedCast(this.target));
        }

        public void setTarget(VertexExtensionBase target) {
            this.target = target;
        }

        public AnnotatedFlowEdge getInverse() {
            return this.inverse;
        }

        public boolean hasCapacity() {
            return MaximumFlowAlgorithmBase.this.comparator.compare(this.capacity, this.flow) > 0;
        }

        public double getResidualCapacity() {
            return this.capacity - this.flow;
        }

        public String toString() {
            return "(" + (this.source == null ? null : this.source.prototype) + "," + (this.target == null ? null : this.target.prototype) + ",c:" + this.capacity + " f: " + this.flow + ")";
        }
    }
}

