/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jheaps.AddressableHeap
 *  org.jheaps.AddressableHeap$Handle
 *  org.jheaps.array.DaryArrayAddressableHeap
 */
package org.jgrapht.alg.shortestpath;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Supplier;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.BaseShortestPathAlgorithm;
import org.jgrapht.graph.GraphWalk;
import org.jheaps.AddressableHeap;
import org.jheaps.array.DaryArrayAddressableHeap;

public final class IntVertexDijkstraShortestPath<E>
extends BaseShortestPathAlgorithm<Integer, E> {
    private final Supplier<AddressableHeap<Double, Integer>> heapSupplier;

    public IntVertexDijkstraShortestPath(Graph<Integer, E> graph) {
        this(graph, () -> new DaryArrayAddressableHeap(4));
    }

    public IntVertexDijkstraShortestPath(Graph<Integer, E> graph, Supplier<AddressableHeap<Double, Integer>> heapSupplier) {
        super(graph);
        this.heapSupplier = heapSupplier;
    }

    public static <E> GraphPath<Integer, E> findPathBetween(Graph<Integer, E> graph, Integer source, Integer sink) {
        return new IntVertexDijkstraShortestPath<E>(graph).getPath(source, sink);
    }

    @Override
    public GraphPath<Integer, E> getPath(Integer source, Integer sink) {
        if (!this.graph.containsVertex(source)) {
            throw new IllegalArgumentException("Graph must contain the source vertex!");
        }
        if (!this.graph.containsVertex(sink)) {
            throw new IllegalArgumentException("Graph must contain the sink vertex!");
        }
        return new Algorithm().getPath(source, sink);
    }

    @Override
    public ShortestPathAlgorithm.SingleSourcePaths<Integer, E> getPaths(Integer source) {
        if (!this.graph.containsVertex(source)) {
            throw new IllegalArgumentException("Graph must contain the source vertex!");
        }
        return new Algorithm().getPaths(source);
    }

    private class Algorithm {
        private int totalVertices;
        private AddressableHeap<Double, Integer> heap;
        private AddressableHeap.Handle<Double, Integer>[] nodes;
        private double[] dist;
        private E[] pred;
        private IdentifierMap idMap;

        public Algorithm() {
            this.totalVertices = IntVertexDijkstraShortestPath.this.graph.vertexSet().size();
            this.nodes = (AddressableHeap.Handle[])Array.newInstance(AddressableHeap.Handle.class, this.totalVertices);
            this.heap = IntVertexDijkstraShortestPath.this.heapSupplier.get();
            this.dist = new double[this.totalVertices];
            this.pred = new Object[this.totalVertices];
            boolean remapVertices = false;
            int i = 0;
            for (Integer v : IntVertexDijkstraShortestPath.this.graph.vertexSet()) {
                if (v < 0 || v >= this.totalVertices) {
                    remapVertices = true;
                }
                this.dist[i] = Double.POSITIVE_INFINITY;
                this.pred[i] = null;
                ++i;
            }
            if (remapVertices) {
                this.idMap = new IdentifierMap(this.totalVertices);
                i = 0;
                for (Integer v : IntVertexDijkstraShortestPath.this.graph.vertexSet()) {
                    this.idMap.put(v, i++);
                }
            }
        }

        public ShortestPathAlgorithm.SingleSourcePaths<Integer, E> getPaths(Integer source) {
            if (this.idMap == null) {
                return this.getPathsWithoutIdMap(source, null);
            }
            return this.getPathsWithIdMap(source, null);
        }

        public ShortestPathAlgorithm.SingleSourcePaths<Integer, E> getPathsWithoutIdMap(Integer source, Integer target) {
            this.dist[source.intValue()] = 0.0;
            this.pred[source.intValue()] = null;
            this.nodes[source.intValue()] = this.heap.insert((Object)0.0, (Object)source);
            while (!this.heap.isEmpty()) {
                double vDistance;
                AddressableHeap.Handle vNode = this.heap.deleteMin();
                Integer v = (Integer)vNode.getValue();
                this.dist[v.intValue()] = vDistance = ((Double)vNode.getKey()).doubleValue();
                if (target != null && v.intValue() == target.intValue()) break;
                for (Object e : IntVertexDijkstraShortestPath.this.graph.outgoingEdgesOf(v)) {
                    Integer u = Graphs.getOppositeVertex(IntVertexDijkstraShortestPath.this.graph, e, v);
                    double eWeight = IntVertexDijkstraShortestPath.this.graph.getEdgeWeight(e);
                    if (eWeight < 0.0) {
                        throw new IllegalArgumentException("Negative edge weight not allowed");
                    }
                    AddressableHeap.Handle<Double, Integer> uNode = this.nodes[u];
                    double uDist = vDistance + eWeight;
                    if (uNode == null) {
                        this.nodes[u.intValue()] = this.heap.insert((Object)uDist, (Object)u);
                        this.pred[u.intValue()] = e;
                        continue;
                    }
                    if (!(uDist < (Double)uNode.getKey())) continue;
                    uNode.decreaseKey((Object)uDist);
                    this.pred[u.intValue()] = e;
                }
            }
            return new ArrayBasedSingleSourcePathsImpl(source, this.dist, this.pred, this.idMap);
        }

        public ShortestPathAlgorithm.SingleSourcePaths<Integer, E> getPathsWithIdMap(Integer source, Integer target) {
            this.dist[this.idMap.get((int)source.intValue())] = 0.0;
            this.pred[this.idMap.get((int)source.intValue())] = null;
            this.nodes[this.idMap.get((int)source.intValue())] = this.heap.insert((Object)0.0, (Object)source);
            while (!this.heap.isEmpty()) {
                double vDistance;
                AddressableHeap.Handle vNode = this.heap.deleteMin();
                Integer v = (Integer)vNode.getValue();
                this.dist[this.idMap.get((int)v.intValue())] = vDistance = ((Double)vNode.getKey()).doubleValue();
                if (target != null && v.intValue() == target.intValue()) break;
                for (Object e : IntVertexDijkstraShortestPath.this.graph.outgoingEdgesOf(v)) {
                    Integer u = Graphs.getOppositeVertex(IntVertexDijkstraShortestPath.this.graph, e, v);
                    double eWeight = IntVertexDijkstraShortestPath.this.graph.getEdgeWeight(e);
                    if (eWeight < 0.0) {
                        throw new IllegalArgumentException("Negative edge weight not allowed");
                    }
                    AddressableHeap.Handle<Double, Integer> uNode = this.nodes[this.idMap.get(u)];
                    double uDist = vDistance + eWeight;
                    if (uNode == null) {
                        this.nodes[this.idMap.get((int)u.intValue())] = this.heap.insert((Object)uDist, (Object)u);
                        this.pred[this.idMap.get((int)u.intValue())] = e;
                        continue;
                    }
                    if (!(uDist < (Double)uNode.getKey())) continue;
                    uNode.decreaseKey((Object)uDist);
                    this.pred[this.idMap.get((int)u.intValue())] = e;
                }
            }
            return new ArrayBasedSingleSourcePathsImpl(source, this.dist, this.pred, this.idMap);
        }

        public GraphPath<Integer, E> getPath(Integer source, Integer target) {
            if (this.idMap == null) {
                return this.getPathsWithoutIdMap(source, target).getPath(target);
            }
            return this.getPathsWithIdMap(source, target).getPath(target);
        }
    }

    private class IdentifierMap {
        private int[] keys;
        private int[] values;
        private int m;

        public IdentifierMap(int m) {
            this.m = m;
            this.keys = new int[m];
            Arrays.fill(this.keys, -1);
            this.values = new int[m];
        }

        public void put(int key, int value) {
            int i = this.hash(key);
            while (this.keys[i] != -1) {
                if (this.keys[i] == key) {
                    this.values[i] = value;
                    return;
                }
                i = (i + 1) % this.m;
            }
            this.keys[i] = key;
            this.values[i] = value;
        }

        public int get(int key) {
            int i = this.hash(key);
            while (this.keys[i] != -1) {
                if (this.keys[i] == key) {
                    return this.values[i];
                }
                i = (i + 1) % this.m;
            }
            return -1;
        }

        private int hash(int key) {
            return (key & Integer.MAX_VALUE) % this.m;
        }
    }

    private class ArrayBasedSingleSourcePathsImpl
    implements ShortestPathAlgorithm.SingleSourcePaths<Integer, E>,
    Serializable {
        private static final long serialVersionUID = 2912496450441089175L;
        private Integer source;
        private double[] dist;
        private E[] pred;
        private IdentifierMap idMap;

        public ArrayBasedSingleSourcePathsImpl(Integer source, double[] dist, E[] pred, IdentifierMap idMap) {
            this.source = source;
            this.dist = dist;
            this.pred = pred;
            this.idMap = idMap;
        }

        @Override
        public Graph<Integer, E> getGraph() {
            return IntVertexDijkstraShortestPath.this.graph;
        }

        @Override
        public Integer getSourceVertex() {
            return this.source;
        }

        @Override
        public double getWeight(Integer targetVertex) {
            if (this.idMap == null) {
                return this.dist[targetVertex];
            }
            return this.dist[this.idMap.get(targetVertex)];
        }

        @Override
        public GraphPath<Integer, E> getPath(Integer targetVertex) {
            double distance;
            if (this.source.equals(targetVertex)) {
                return GraphWalk.singletonWalk(IntVertexDijkstraShortestPath.this.graph, this.source, 0.0);
            }
            ArrayDeque edgeList = new ArrayDeque();
            Integer cur = targetVertex;
            if (this.idMap != null) {
                Object e;
                if (this.pred[this.idMap.get(cur)] == null) {
                    return null;
                }
                while ((e = this.pred[this.idMap.get(cur)]) != null) {
                    edgeList.addFirst(e);
                    cur = Graphs.getOppositeVertex(IntVertexDijkstraShortestPath.this.graph, e, cur);
                }
                distance = this.dist[this.idMap.get(targetVertex)];
            } else {
                Object e;
                if (this.pred[cur] == null) {
                    return null;
                }
                while ((e = this.pred[cur]) != null) {
                    edgeList.addFirst(e);
                    cur = Graphs.getOppositeVertex(IntVertexDijkstraShortestPath.this.graph, e, cur);
                }
                distance = this.dist[targetVertex];
            }
            return new GraphWalk(IntVertexDijkstraShortestPath.this.graph, this.source, targetVertex, null, new ArrayList(edgeList), distance);
        }
    }
}

