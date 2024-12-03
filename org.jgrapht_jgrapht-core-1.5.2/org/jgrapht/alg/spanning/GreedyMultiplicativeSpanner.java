/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jheaps.AddressableHeap
 *  org.jheaps.AddressableHeap$Handle
 *  org.jheaps.tree.PairingHeap
 */
package org.jgrapht.alg.spanning;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.interfaces.SpannerAlgorithm;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.graph.builder.GraphTypeBuilder;
import org.jgrapht.util.CollectionUtil;
import org.jheaps.AddressableHeap;
import org.jheaps.tree.PairingHeap;

public class GreedyMultiplicativeSpanner<V, E>
implements SpannerAlgorithm<E> {
    private final Graph<V, E> graph;
    private final int k;
    private static final int MAX_K = 0x20000000;

    public GreedyMultiplicativeSpanner(Graph<V, E> graph, int k) {
        this.graph = Objects.requireNonNull(graph, "Graph cannot be null");
        if (!graph.getType().isUndirected()) {
            throw new IllegalArgumentException("graph is not undirected");
        }
        if (k <= 0) {
            throw new IllegalArgumentException("k should be positive in (2k-1)-spanner construction");
        }
        this.k = Math.min(k, 0x20000000);
    }

    @Override
    public SpannerAlgorithm.Spanner<E> getSpanner() {
        if (this.graph.getType().isWeighted()) {
            return new WeightedSpannerAlgorithm().run();
        }
        return new UnweightedSpannerAlgorithm().run();
    }

    private class WeightedSpannerAlgorithm
    extends SpannerAlgorithmBase {
        protected Graph<V, DefaultWeightedEdge> spanner;
        protected AddressableHeap<Double, V> heap;
        protected Map<V, AddressableHeap.Handle<Double, V>> nodes;

        public WeightedSpannerAlgorithm() {
            this.spanner = new SimpleWeightedGraph(DefaultWeightedEdge.class);
            for (Object v : GreedyMultiplicativeSpanner.this.graph.vertexSet()) {
                this.spanner.addVertex(v);
            }
            this.heap = new PairingHeap();
            this.nodes = new LinkedHashMap();
        }

        @Override
        public boolean isSpannerReachable(V s, V t, double distance) {
            this.heap.clear();
            this.nodes.clear();
            AddressableHeap.Handle sNode = this.heap.insert((Object)0.0, s);
            this.nodes.put((AddressableHeap.Handle)s, (AddressableHeap.Handle<Double, AddressableHeap.Handle>)sNode);
            while (!this.heap.isEmpty()) {
                AddressableHeap.Handle uNode = this.heap.deleteMin();
                double uDistance = (Double)uNode.getKey();
                Object u = uNode.getValue();
                if (uDistance > distance) {
                    return false;
                }
                if (u.equals(t)) {
                    return true;
                }
                for (DefaultWeightedEdge e : this.spanner.edgesOf(u)) {
                    Object v = Graphs.getOppositeVertex(this.spanner, e, u);
                    AddressableHeap.Handle vNode = this.nodes.get(v);
                    double vDistance = uDistance + this.spanner.getEdgeWeight(e);
                    if (vNode == null) {
                        vNode = this.heap.insert((Object)vDistance, v);
                        this.nodes.put((AddressableHeap.Handle)v, (AddressableHeap.Handle<Double, AddressableHeap.Handle>)vNode);
                        continue;
                    }
                    if (!(vDistance < (Double)vNode.getKey())) continue;
                    vNode.decreaseKey((Object)vDistance);
                }
            }
            return false;
        }

        @Override
        public void addSpannerEdge(V s, V t, double weight) {
            Graphs.addEdge(this.spanner, s, t, weight);
        }
    }

    private class UnweightedSpannerAlgorithm
    extends SpannerAlgorithmBase {
        protected Graph<V, E> spanner;
        protected Map<V, Integer> vertexDistance;
        protected Deque<V> queue;
        protected Deque<V> touchedVertices;

        public UnweightedSpannerAlgorithm() {
            this.spanner = GraphTypeBuilder.undirected().allowingMultipleEdges(false).allowingSelfLoops(false).edgeSupplier(GreedyMultiplicativeSpanner.this.graph.getEdgeSupplier()).buildGraph();
            this.touchedVertices = new ArrayDeque(GreedyMultiplicativeSpanner.this.graph.vertexSet().size());
            for (Object v : GreedyMultiplicativeSpanner.this.graph.vertexSet()) {
                this.spanner.addVertex(v);
                this.touchedVertices.push(v);
            }
            this.vertexDistance = CollectionUtil.newHashMapWithExpectedSize(GreedyMultiplicativeSpanner.this.graph.vertexSet().size());
            this.queue = new ArrayDeque();
        }

        @Override
        public boolean isSpannerReachable(V s, V t, double hops) {
            Object u;
            while (!this.touchedVertices.isEmpty()) {
                u = this.touchedVertices.pop();
                this.vertexDistance.put((Integer)u, Integer.MAX_VALUE);
            }
            while (!this.queue.isEmpty()) {
                this.queue.pop();
            }
            this.touchedVertices.push(s);
            this.queue.push(s);
            this.vertexDistance.put((Integer)s, 0);
            while (!this.queue.isEmpty()) {
                u = this.queue.pop();
                Integer uDistance = this.vertexDistance.get(u);
                if (u.equals(t)) {
                    return (double)uDistance.intValue() <= hops;
                }
                for (Object e : this.spanner.edgesOf(u)) {
                    Object v = Graphs.getOppositeVertex(this.spanner, e, u);
                    Integer vDistance = this.vertexDistance.get(v);
                    if (vDistance != Integer.MAX_VALUE) continue;
                    this.touchedVertices.push(v);
                    this.vertexDistance.put((Integer)v, uDistance + 1);
                    this.queue.push(v);
                }
            }
            return false;
        }

        @Override
        public void addSpannerEdge(V s, V t, double weight) {
            this.spanner.addEdge(s, t);
        }
    }

    private abstract class SpannerAlgorithmBase {
        private SpannerAlgorithmBase() {
        }

        public abstract boolean isSpannerReachable(V var1, V var2, double var3);

        public abstract void addSpannerEdge(V var1, V var2, double var3);

        public SpannerAlgorithm.Spanner<E> run() {
            ArrayList<Object> allEdges = new ArrayList<Object>(GreedyMultiplicativeSpanner.this.graph.edgeSet());
            allEdges.sort(Comparator.comparingDouble(GreedyMultiplicativeSpanner.this.graph::getEdgeWeight));
            double minWeight = GreedyMultiplicativeSpanner.this.graph.getEdgeWeight(allEdges.get(0));
            if (minWeight < 0.0) {
                throw new IllegalArgumentException("Illegal edge weight: negative");
            }
            LinkedHashSet edgeList = new LinkedHashSet();
            double edgeListWeight = 0.0;
            for (Object e : allEdges) {
                double eWeight;
                Object t;
                Object s = GreedyMultiplicativeSpanner.this.graph.getEdgeSource(e);
                if (s.equals(t = GreedyMultiplicativeSpanner.this.graph.getEdgeTarget(e)) || this.isSpannerReachable(s, t, (double)(2 * GreedyMultiplicativeSpanner.this.k - 1) * (eWeight = GreedyMultiplicativeSpanner.this.graph.getEdgeWeight(e)))) continue;
                edgeList.add(e);
                edgeListWeight += eWeight;
                this.addSpannerEdge(s, t, eWeight);
            }
            return new SpannerAlgorithm.SpannerImpl(edgeList, edgeListWeight);
        }
    }
}

