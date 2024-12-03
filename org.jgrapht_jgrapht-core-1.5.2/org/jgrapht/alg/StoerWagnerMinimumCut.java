/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg;

import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.GraphTests;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

public class StoerWagnerMinimumCut<V, E> {
    final Graph<Set<V>, DefaultWeightedEdge> workingGraph;
    protected double bestCutWeight = Double.POSITIVE_INFINITY;
    protected Set<V> bestCut;

    public StoerWagnerMinimumCut(Graph<V, E> graph) {
        GraphTests.requireUndirected(graph, "Graph must be undirected");
        if (graph.vertexSet().size() < 2) {
            throw new IllegalArgumentException("Graph has less than 2 vertices");
        }
        this.workingGraph = new SimpleWeightedGraph<Set<V>, DefaultWeightedEdge>(DefaultWeightedEdge.class);
        HashMap vertexMap = new HashMap();
        for (V v : graph.vertexSet()) {
            HashSet<V> list = new HashSet<V>();
            list.add(v);
            vertexMap.put(v, list);
            this.workingGraph.addVertex(list);
        }
        for (Object e : graph.edgeSet()) {
            V t;
            Set tNew;
            if (graph.getEdgeWeight(e) < 0.0) {
                throw new IllegalArgumentException("Negative edge weights not allowed");
            }
            V s = graph.getEdgeSource(e);
            Set sNew = (Set)vertexMap.get(s);
            DefaultWeightedEdge eNew = this.workingGraph.getEdge(sNew, tNew = (Set)vertexMap.get(t = graph.getEdgeTarget(e)));
            if (eNew == null) {
                eNew = this.workingGraph.addEdge(sNew, tNew);
                this.workingGraph.setEdgeWeight(eNew, graph.getEdgeWeight(e));
                continue;
            }
            this.workingGraph.setEdgeWeight(eNew, this.workingGraph.getEdgeWeight(eNew) + graph.getEdgeWeight(e));
        }
        Set<V> a = this.workingGraph.vertexSet().iterator().next();
        while (this.workingGraph.vertexSet().size() > 1) {
            this.minimumCutPhase(a);
        }
    }

    protected void minimumCutPhase(Set<V> a) {
        Set<V> last = a;
        Set<V> beforelast = null;
        PriorityQueue<VertexAndWeight> queue = new PriorityQueue<VertexAndWeight>();
        HashMap<Set<V>, VertexAndWeight> dmap = new HashMap<Set<V>, VertexAndWeight>();
        for (Set<V> v : this.workingGraph.vertexSet()) {
            if (v == a) continue;
            DefaultWeightedEdge e = this.workingGraph.getEdge(v, a);
            Double w = e == null ? 0.0 : this.workingGraph.getEdgeWeight(e);
            VertexAndWeight vandw = new VertexAndWeight(v, w, e != null);
            queue.add(vandw);
            dmap.put(v, vandw);
        }
        while (!queue.isEmpty()) {
            Set v = ((VertexAndWeight)queue.poll()).vertex;
            dmap.remove(v);
            beforelast = last;
            last = v;
            for (DefaultWeightedEdge e : this.workingGraph.edgesOf(v)) {
                Set vc = Graphs.getOppositeVertex(this.workingGraph, e, v);
                VertexAndWeight vcandw = (VertexAndWeight)dmap.get(vc);
                if (vcandw == null) continue;
                queue.remove(vcandw);
                vcandw.active = true;
                VertexAndWeight vertexAndWeight = vcandw;
                Double.valueOf(vertexAndWeight.weight + this.workingGraph.getEdgeWeight(e));
                vertexAndWeight.weight = vertexAndWeight.weight;
                queue.add(vcandw);
            }
        }
        double w = this.vertexWeight(last);
        if (w < this.bestCutWeight) {
            this.bestCutWeight = w;
            this.bestCut = last;
        }
        this.mergeVertices(beforelast, last);
    }

    public double minCutWeight() {
        return this.bestCutWeight;
    }

    public Set<V> minCut() {
        return this.bestCut;
    }

    protected VertexAndWeight mergeVertices(Set<V> s, Set<V> t) {
        HashSet<V> set = new HashSet<V>();
        set.addAll(s);
        set.addAll(t);
        this.workingGraph.addVertex(set);
        double wsum = 0.0;
        for (Set<V> v : this.workingGraph.vertexSet()) {
            if (s == v || t == v) continue;
            double neww = 0.0;
            DefaultWeightedEdge etv = this.workingGraph.getEdge(t, v);
            DefaultWeightedEdge esv = this.workingGraph.getEdge(s, v);
            if (etv != null) {
                neww += this.workingGraph.getEdgeWeight(etv);
            }
            if (esv != null) {
                neww += this.workingGraph.getEdgeWeight(esv);
            }
            if (etv == null && esv == null) continue;
            wsum += neww;
            this.workingGraph.setEdgeWeight(this.workingGraph.addEdge(set, v), neww);
        }
        this.workingGraph.removeVertex(t);
        this.workingGraph.removeVertex(s);
        return new VertexAndWeight(set, wsum, false);
    }

    public double vertexWeight(Set<V> v) {
        double wsum = 0.0;
        for (DefaultWeightedEdge e : this.workingGraph.edgesOf(v)) {
            wsum += this.workingGraph.getEdgeWeight(e);
        }
        return wsum;
    }

    protected class VertexAndWeight
    implements Comparable<VertexAndWeight> {
        public Set<V> vertex;
        public Double weight;
        public boolean active;

        public VertexAndWeight(Set<V> v, double w, boolean active) {
            this.vertex = v;
            this.weight = w;
            this.active = active;
        }

        @Override
        public int compareTo(VertexAndWeight that) {
            if (this.active && that.active) {
                return -Double.compare(this.weight, that.weight);
            }
            if (this.active && !that.active) {
                return -1;
            }
            if (!this.active && that.active) {
                return 1;
            }
            return 0;
        }

        public String toString() {
            return "(" + this.vertex + ", " + this.weight + ")";
        }
    }
}

