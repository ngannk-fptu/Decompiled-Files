/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.connectivity;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.alg.connectivity.AbstractStrongConnectivityInspector;
import org.jgrapht.graph.EdgeReversedGraph;
import org.jgrapht.util.CollectionUtil;

public class KosarajuStrongConnectivityInspector<V, E>
extends AbstractStrongConnectivityInspector<V, E> {
    private LinkedList<VertexData<V>> orderedVertices;
    private Map<V, VertexData<V>> vertexToVertexData;

    public KosarajuStrongConnectivityInspector(Graph<V, E> graph) {
        super(graph);
    }

    @Override
    public List<Set<V>> stronglyConnectedSets() {
        if (this.stronglyConnectedSets == null) {
            this.orderedVertices = new LinkedList();
            this.stronglyConnectedSets = new ArrayList();
            this.createVertexData();
            for (VertexData<V> data : this.vertexToVertexData.values()) {
                if (data.isDiscovered()) continue;
                this.dfsVisit(this.graph, data, null);
            }
            EdgeReversedGraph inverseGraph = new EdgeReversedGraph(this.graph);
            this.resetVertexData();
            for (VertexData vertexData : this.orderedVertices) {
                if (vertexData.isDiscovered()) continue;
                HashSet set = new HashSet();
                this.stronglyConnectedSets.add(set);
                this.dfsVisit(inverseGraph, vertexData, set);
            }
            this.orderedVertices = null;
            this.vertexToVertexData = null;
        }
        return this.stronglyConnectedSets;
    }

    private void createVertexData() {
        this.vertexToVertexData = CollectionUtil.newHashMapWithExpectedSize(this.graph.vertexSet().size());
        for (Object vertex : this.graph.vertexSet()) {
            this.vertexToVertexData.put(vertex, new VertexData2(vertex, false, false));
        }
    }

    private void dfsVisit(Graph<V, E> visitedGraph, VertexData<V> vertexData, Set<V> vertices) {
        ArrayDeque<VertexData<V>> stack = new ArrayDeque<VertexData<V>>();
        stack.add(vertexData);
        while (!stack.isEmpty()) {
            VertexData data = (VertexData)stack.removeLast();
            if (!data.isDiscovered()) {
                data.setDiscovered(true);
                if (vertices != null) {
                    vertices.add(data.getVertex());
                }
                stack.add(new VertexData1(data, true, true));
                for (E edge : visitedGraph.outgoingEdgesOf(data.getVertex())) {
                    VertexData<V> targetData = this.vertexToVertexData.get(visitedGraph.getEdgeTarget(edge));
                    if (targetData.isDiscovered()) continue;
                    stack.add(targetData);
                }
                continue;
            }
            if (!data.isFinished() || vertices != null) continue;
            this.orderedVertices.addFirst(data.getFinishedData());
        }
    }

    private void resetVertexData() {
        for (VertexData<V> data : this.vertexToVertexData.values()) {
            data.setDiscovered(false);
            data.setFinished(false);
        }
    }

    private static abstract class VertexData<V> {
        private byte bitfield = 0;

        private VertexData(boolean discovered, boolean finished) {
            this.setDiscovered(discovered);
            this.setFinished(finished);
        }

        private boolean isDiscovered() {
            return (this.bitfield & 1) == 1;
        }

        private boolean isFinished() {
            return (this.bitfield & 2) == 2;
        }

        private void setDiscovered(boolean discovered) {
            this.bitfield = discovered ? (byte)(this.bitfield | 1) : (byte)(this.bitfield & 0xFFFFFFFE);
        }

        private void setFinished(boolean finished) {
            this.bitfield = finished ? (byte)(this.bitfield | 2) : (byte)(this.bitfield & 0xFFFFFFFD);
        }

        abstract VertexData<V> getFinishedData();

        abstract V getVertex();
    }

    private static final class VertexData2<V>
    extends VertexData<V> {
        private final V vertex;

        private VertexData2(V vertex, boolean discovered, boolean finished) {
            super(discovered, finished);
            this.vertex = vertex;
        }

        @Override
        VertexData<V> getFinishedData() {
            return null;
        }

        @Override
        V getVertex() {
            return this.vertex;
        }
    }

    private static final class VertexData1<V>
    extends VertexData<V> {
        private final VertexData<V> finishedData;

        private VertexData1(VertexData<V> finishedData, boolean discovered, boolean finished) {
            super(discovered, finished);
            this.finishedData = finishedData;
        }

        @Override
        VertexData<V> getFinishedData() {
            return this.finishedData;
        }

        @Override
        V getVertex() {
            return null;
        }
    }
}

