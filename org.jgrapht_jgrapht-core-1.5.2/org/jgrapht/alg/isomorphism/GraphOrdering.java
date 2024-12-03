/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.isomorphism;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.alg.util.VertexDegreeComparator;
import org.jgrapht.util.VertexToIntegerMapping;

final class GraphOrdering<V, E> {
    private final Graph<V, E> graph;
    private final Map<V, Integer> mapVertexToOrder;
    private final List<V> mapOrderToVertex;
    private final int vertexCount;
    private final int[][] outgoingEdges;
    private final int[][] incomingEdges;
    private final E[] edgeCache;
    private final byte[] adjMatrix;
    private final boolean cacheEdges;

    public GraphOrdering(Graph<V, E> graph, boolean orderByDegree, boolean cacheEdges) {
        this.graph = graph;
        this.cacheEdges = cacheEdges;
        ArrayList<V> vertexList = new ArrayList<V>(graph.vertexSet());
        if (orderByDegree) {
            vertexList.sort(VertexDegreeComparator.of(graph));
        }
        this.vertexCount = vertexList.size();
        this.mapVertexToOrder = new VertexToIntegerMapping<V>(vertexList).getVertexMap();
        this.mapOrderToVertex = vertexList;
        if (cacheEdges) {
            this.outgoingEdges = new int[this.vertexCount][];
            this.incomingEdges = new int[this.vertexCount][];
            this.edgeCache = new Object[this.vertexCount * this.vertexCount];
            this.adjMatrix = new byte[this.vertexCount * this.vertexCount];
        } else {
            this.outgoingEdges = null;
            this.incomingEdges = null;
            this.edgeCache = null;
            this.adjMatrix = null;
        }
    }

    public GraphOrdering(Graph<V, E> graph) {
        this(graph, false, true);
    }

    public int getVertexCount() {
        return this.vertexCount;
    }

    public int[] getOutEdges(int vertexNumber) {
        if (this.cacheEdges && this.outgoingEdges[vertexNumber] != null) {
            return this.outgoingEdges[vertexNumber];
        }
        V v = this.getVertex(vertexNumber);
        Set<E> edgeSet = this.graph.outgoingEdgesOf(v);
        int[] vertexArray = new int[edgeSet.size()];
        int i = 0;
        for (E edge : edgeSet) {
            V source = this.graph.getEdgeSource(edge);
            V target = this.graph.getEdgeTarget(edge);
            vertexArray[i++] = this.mapVertexToOrder.get(source.equals(v) ? target : source);
        }
        if (this.cacheEdges) {
            this.outgoingEdges[vertexNumber] = vertexArray;
        }
        return vertexArray;
    }

    public int[] getInEdges(int vertexNumber) {
        if (this.cacheEdges && this.incomingEdges[vertexNumber] != null) {
            return this.incomingEdges[vertexNumber];
        }
        V v = this.getVertex(vertexNumber);
        Set<E> edgeSet = this.graph.incomingEdgesOf(v);
        int[] vertexArray = new int[edgeSet.size()];
        int i = 0;
        for (E edge : edgeSet) {
            V source = this.graph.getEdgeSource(edge);
            V target = this.graph.getEdgeTarget(edge);
            vertexArray[i++] = this.mapVertexToOrder.get(source.equals(v) ? target : source);
        }
        if (this.cacheEdges) {
            this.incomingEdges[vertexNumber] = vertexArray;
        }
        return vertexArray;
    }

    public boolean hasEdge(int v1Number, int v2Number) {
        int cacheIndex = 0;
        if (this.cacheEdges) {
            V v2;
            cacheIndex = v1Number * this.vertexCount + v2Number;
            byte cache = this.adjMatrix[cacheIndex];
            if (cache != 0) {
                return cache > 0;
            }
            V v1 = this.getVertex(v1Number);
            E edge = this.graph.getEdge(v1, v2 = this.getVertex(v2Number));
            if (edge == null) {
                this.adjMatrix[cacheIndex] = -1;
                return false;
            }
            this.adjMatrix[cacheIndex] = 1;
            this.edgeCache[cacheIndex] = edge;
            return true;
        }
        V v1 = this.getVertex(v1Number);
        V v2 = this.getVertex(v2Number);
        boolean containsEdge = this.graph.containsEdge(v1, v2);
        return containsEdge;
    }

    public V getVertex(int vertexNumber) {
        return this.mapOrderToVertex.get(vertexNumber);
    }

    public E getEdge(int v1Number, int v2Number) {
        if (this.cacheEdges) {
            int cacheIndex = v1Number * this.vertexCount + v2Number;
            byte containsEdge = this.adjMatrix[cacheIndex];
            if (containsEdge == 0) {
                this.hasEdge(v1Number, v2Number);
            }
            E edge = this.edgeCache[cacheIndex];
            return edge;
        }
        V v1 = this.getVertex(v1Number);
        V v2 = this.getVertex(v2Number);
        E edge = this.graph.getEdge(v1, v2);
        return edge;
    }

    public int getVertexNumber(V v) {
        return this.mapVertexToOrder.get(v);
    }

    public int[] getEdgeNumbers(E e) {
        V v1 = this.graph.getEdgeSource(e);
        V v2 = this.graph.getEdgeTarget(e);
        int[] edge = new int[]{this.mapVertexToOrder.get(v1), this.mapVertexToOrder.get(v2)};
        return edge;
    }

    public Graph<V, E> getGraph() {
        return this.graph;
    }
}

