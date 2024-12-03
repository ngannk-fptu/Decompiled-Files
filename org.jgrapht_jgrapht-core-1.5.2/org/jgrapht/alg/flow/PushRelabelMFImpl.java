/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.flow;

import java.lang.reflect.Array;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import org.jgrapht.Graph;
import org.jgrapht.alg.flow.MaximumFlowAlgorithmBase;
import org.jgrapht.alg.interfaces.MaximumFlowAlgorithm;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.alg.util.ToleranceDoubleComparator;
import org.jgrapht.alg.util.extension.ExtensionFactory;

public class PushRelabelMFImpl<V, E>
extends MaximumFlowAlgorithmBase<V, E> {
    private static final boolean DIAGNOSTIC_ENABLED = false;
    @Deprecated(since="1.5.2", forRemoval=true)
    public static boolean USE_GLOBAL_RELABELING_HEURISTIC = true;
    @Deprecated(since="1.5.2", forRemoval=true)
    public static boolean USE_GAP_RELABELING_HEURISTIC = true;
    private final ExtensionFactory<VertexExtension> vertexExtensionsFactory = () -> new VertexExtension();
    private final ExtensionFactory<MaximumFlowAlgorithmBase.AnnotatedFlowEdge> edgeExtensionsFactory = () -> new MaximumFlowAlgorithmBase.AnnotatedFlowEdge();
    private int[] countHeight;
    private Queue<VertexExtension> activeVertices;
    private PushRelabelDiagnostic diagnostic;
    private final int n;
    private final VertexExtension[] vertexExtension;
    private int relabelCounter;
    private static ToleranceDoubleComparator comparator = new ToleranceDoubleComparator();

    public static void setUseGlobalRelabelingHeuristic(boolean useGlobalRelabelingHeuristic) {
        USE_GLOBAL_RELABELING_HEURISTIC = useGlobalRelabelingHeuristic;
    }

    public static void setUseGapRelabelingHeuristic(boolean useGapRelabelingHeuristic) {
        USE_GAP_RELABELING_HEURISTIC = useGapRelabelingHeuristic;
    }

    public PushRelabelMFImpl(Graph<V, E> network) {
        this(network, 1.0E-9);
    }

    public PushRelabelMFImpl(Graph<V, E> network, double epsilon) {
        super(network, epsilon);
        this.n = network.vertexSet().size();
        this.vertexExtension = (VertexExtension[])Array.newInstance(VertexExtension.class, this.n);
    }

    private void enqueue(VertexExtension vx) {
        if (!vx.active && vx.hasExcess()) {
            vx.active = true;
            this.activeVertices.add(vx);
        }
    }

    void init(V source, V sink) {
        super.init(source, sink, this.vertexExtensionsFactory, this.edgeExtensionsFactory);
        this.countHeight = new int[2 * this.n + 1];
        int id = 0;
        for (Object v : this.network.vertexSet()) {
            VertexExtension vx = this.getVertexExtension(v);
            vx.id = id;
            this.vertexExtension[id] = vx;
            ++id;
        }
    }

    public void initialize(VertexExtension source, VertexExtension sink, Queue<VertexExtension> active) {
        this.activeVertices = active;
        for (int i = 0; i < this.n; ++i) {
            this.vertexExtension[i].excess = 0.0;
            this.vertexExtension[i].height = 0;
            this.vertexExtension[i].active = false;
            this.vertexExtension[i].currentArc = 0;
        }
        source.height = this.n;
        source.active = true;
        sink.active = true;
        this.countHeight[this.n] = 1;
        this.countHeight[0] = this.n - 1;
        for (MaximumFlowAlgorithmBase.AnnotatedFlowEdge ex : source.getOutgoing()) {
            source.excess += ex.capacity;
            this.push(ex);
        }
        if (USE_GLOBAL_RELABELING_HEURISTIC) {
            this.recomputeHeightsHeuristic();
            this.relabelCounter = 0;
        }
    }

    @Override
    public MaximumFlowAlgorithm.MaximumFlow<E> getMaximumFlow(V source, V sink) {
        this.calculateMaximumFlow(source, sink);
        this.maxFlow = this.composeFlow();
        return new MaximumFlowAlgorithm.MaximumFlowImpl(this.maxFlowValue, this.maxFlow);
    }

    public double calculateMaximumFlow(V source, V sink) {
        this.init(source, sink);
        this.activeVertices = new ArrayDeque<VertexExtension>(this.n);
        this.initialize(this.getVertexExtension(source), this.getVertexExtension(sink), this.activeVertices);
        while (!this.activeVertices.isEmpty()) {
            VertexExtension vx = this.activeVertices.poll();
            vx.active = false;
            this.discharge(vx);
        }
        for (Object e : this.network.edgesOf(sink)) {
            MaximumFlowAlgorithmBase.AnnotatedFlowEdge edge = (MaximumFlowAlgorithmBase.AnnotatedFlowEdge)this.edgeExtensionManager.getExtension(e);
            this.maxFlowValue += this.directedGraph ? edge.flow : edge.flow + edge.getInverse().flow;
        }
        return this.maxFlowValue;
    }

    @Override
    protected void pushFlowThrough(MaximumFlowAlgorithmBase.AnnotatedFlowEdge ex, double f) {
        ((MaximumFlowAlgorithmBase.VertexExtensionBase)ex.getSource()).excess -= f;
        ((MaximumFlowAlgorithmBase.VertexExtensionBase)ex.getTarget()).excess += f;
        assert (((MaximumFlowAlgorithmBase.VertexExtensionBase)ex.getSource()).excess >= 0.0 && ((MaximumFlowAlgorithmBase.VertexExtensionBase)ex.getTarget()).excess >= 0.0);
        super.pushFlowThrough(ex, f);
    }

    private void push(MaximumFlowAlgorithmBase.AnnotatedFlowEdge ex) {
        VertexExtension ux = (VertexExtension)ex.getSource();
        VertexExtension vx = (VertexExtension)ex.getTarget();
        double delta = Math.min(ux.excess, ex.capacity - ex.flow);
        if (ux.height <= vx.height || comparator.compare(delta, 0.0) <= 0) {
            return;
        }
        this.pushFlowThrough(ex, delta);
        this.enqueue(vx);
    }

    private void gapHeuristic(int l) {
        for (int i = 0; i < this.n; ++i) {
            if (l >= this.vertexExtension[i].height || this.vertexExtension[i].height >= this.n) continue;
            int n = this.vertexExtension[i].height;
            this.countHeight[n] = this.countHeight[n] - 1;
            int n2 = this.vertexExtension[i].height = Math.max(this.vertexExtension[i].height, this.n + 1);
            this.countHeight[n2] = this.countHeight[n2] + 1;
        }
    }

    private void relabel(VertexExtension ux) {
        int oldHeight = ux.height;
        int n = ux.height;
        this.countHeight[n] = this.countHeight[n] - 1;
        ux.height = 2 * this.n;
        for (MaximumFlowAlgorithmBase.AnnotatedFlowEdge ex : ux.getOutgoing()) {
            if (!ex.hasCapacity()) continue;
            ux.height = Math.min(ux.height, ((VertexExtension)ex.getTarget()).height + 1);
        }
        int n2 = ux.height;
        this.countHeight[n2] = this.countHeight[n2] + 1;
        if (USE_GAP_RELABELING_HEURISTIC && 0 < oldHeight && oldHeight < this.n && this.countHeight[oldHeight] == 0) {
            this.gapHeuristic(oldHeight);
        }
    }

    private void bfs(Queue<Integer> queue, boolean[] visited) {
        while (!queue.isEmpty()) {
            int vertexID = queue.poll();
            for (MaximumFlowAlgorithmBase.AnnotatedFlowEdge flowEdge : this.vertexExtension[vertexID].getOutgoing()) {
                VertexExtension vx = (VertexExtension)flowEdge.getTarget();
                if (visited[vx.id] || !flowEdge.getInverse().hasCapacity()) continue;
                vx.height = this.vertexExtension[vertexID].height + 1;
                visited[vx.id] = true;
                queue.add(vx.id);
            }
        }
    }

    private void recomputeHeightsHeuristic() {
        Arrays.fill(this.countHeight, 0);
        ArrayDeque<Integer> queue = new ArrayDeque<Integer>(this.n);
        boolean[] visited = new boolean[this.n];
        for (int i = 0; i < this.n; ++i) {
            this.vertexExtension[i].height = 2 * this.n;
        }
        int sinkID = this.getVertexExtension(this.getCurrentSink()).id;
        int sourceID = this.getVertexExtension(this.getCurrentSource()).id;
        this.vertexExtension[sourceID].height = this.n;
        visited[sourceID] = true;
        this.vertexExtension[sinkID].height = 0;
        visited[sinkID] = true;
        queue.add(sinkID);
        this.bfs(queue, visited);
        queue.add(sourceID);
        this.bfs(queue, visited);
        for (int i = 0; i < this.n; ++i) {
            int n = this.vertexExtension[i].height;
            this.countHeight[n] = this.countHeight[n] + 1;
        }
    }

    private void discharge(VertexExtension ux) {
        while (ux.hasExcess()) {
            if (ux.currentArc >= ux.getOutgoing().size()) {
                this.relabel(ux);
                if (USE_GLOBAL_RELABELING_HEURISTIC && ++this.relabelCounter == this.n) {
                    this.recomputeHeightsHeuristic();
                    for (int i = 0; i < this.n; ++i) {
                        this.vertexExtension[i].currentArc = 0;
                    }
                    this.relabelCounter = 0;
                }
                ux.currentArc = 0;
                continue;
            }
            MaximumFlowAlgorithmBase.AnnotatedFlowEdge flowEdge = (MaximumFlowAlgorithmBase.AnnotatedFlowEdge)ux.getOutgoing().get(ux.currentArc);
            if (this.isAdmissible(flowEdge)) {
                this.push(flowEdge);
                continue;
            }
            ++ux.currentArc;
        }
    }

    private boolean isAdmissible(MaximumFlowAlgorithmBase.AnnotatedFlowEdge e) {
        return e.hasCapacity() && ((VertexExtension)e.getSource()).height == ((VertexExtension)e.getTarget()).height + 1;
    }

    private VertexExtension getVertexExtension(V v) {
        assert (this.vertexExtensionManager != null);
        return (VertexExtension)this.vertexExtensionManager.getExtension(v);
    }

    public class VertexExtension
    extends MaximumFlowAlgorithmBase.VertexExtensionBase {
        private int id;
        private int height;
        private boolean active;
        private int currentArc;

        private boolean hasExcess() {
            return comparator.compare(this.excess, 0.0) > 0;
        }

        public String toString() {
            return this.prototype.toString() + String.format(" { HGT: %d } ", this.height);
        }
    }

    private class PushRelabelDiagnostic {
        Map<Pair<V, V>, Integer> discharges = new HashMap();
        long dischargesCounter = 0L;
        Map<Pair<Integer, Integer>, Integer> relabels = new HashMap<Pair<Integer, Integer>, Integer>();
        long relabelsCounter = 0L;

        private PushRelabelDiagnostic() {
        }

        private void incrementDischarges(MaximumFlowAlgorithmBase.AnnotatedFlowEdge ex) {
            Pair p = Pair.of(((MaximumFlowAlgorithmBase.VertexExtensionBase)ex.getSource()).prototype, ((MaximumFlowAlgorithmBase.VertexExtensionBase)ex.getTarget()).prototype);
            if (!this.discharges.containsKey(p)) {
                this.discharges.put(p, 0);
            }
            this.discharges.put(p, this.discharges.get(p) + 1);
            ++this.dischargesCounter;
        }

        private void incrementRelabels(int from, int to) {
            Pair<Integer, Integer> p = Pair.of(from, to);
            if (!this.relabels.containsKey(p)) {
                this.relabels.put(p, 0);
            }
            this.relabels.put(p, this.relabels.get(p) + 1);
            ++this.relabelsCounter;
        }

        void dump() {
            HashMap<Integer, Integer> labels = new HashMap<Integer, Integer>();
            for (Object v : PushRelabelMFImpl.this.network.vertexSet()) {
                VertexExtension vx = PushRelabelMFImpl.this.getVertexExtension(v);
                if (!labels.containsKey(vx.height)) {
                    labels.put(vx.height, 0);
                }
                labels.put(vx.height, (Integer)labels.get(vx.height) + 1);
            }
            System.out.println("LABELS  ");
            System.out.println("------  ");
            System.out.println(labels);
            ArrayList<Map.Entry<Pair<Integer, Integer>, Integer>> relabelsSorted = new ArrayList<Map.Entry<Pair<Integer, Integer>, Integer>>(this.relabels.entrySet());
            relabelsSorted.sort((o1, o2) -> -((Integer)o1.getValue() - (Integer)o2.getValue()));
            System.out.println("RELABELS    ");
            System.out.println("--------    ");
            System.out.println("    Count:  " + this.relabelsCounter);
            System.out.println("            " + relabelsSorted);
            ArrayList dischargesSorted = new ArrayList(this.discharges.entrySet());
            dischargesSorted.sort((one, other) -> -((Integer)one.getValue() - (Integer)other.getValue()));
            System.out.println("DISCHARGES  ");
            System.out.println("----------  ");
            System.out.println("    Count:  " + this.dischargesCounter);
            System.out.println("            " + dischargesSorted);
        }
    }
}

