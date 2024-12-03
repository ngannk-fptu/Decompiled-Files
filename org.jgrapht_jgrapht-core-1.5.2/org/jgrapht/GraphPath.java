/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;

public interface GraphPath<V, E> {
    public Graph<V, E> getGraph();

    public V getStartVertex();

    public V getEndVertex();

    default public List<E> getEdgeList() {
        List<V> vertexList = this.getVertexList();
        if (vertexList.size() < 2) {
            return Collections.emptyList();
        }
        Graph<V, E> g = this.getGraph();
        ArrayList<E> edgeList = new ArrayList<E>();
        Iterator<V> vertexIterator = vertexList.iterator();
        V u = vertexIterator.next();
        while (vertexIterator.hasNext()) {
            V v = vertexIterator.next();
            edgeList.add(g.getEdge(u, v));
            u = v;
        }
        return edgeList;
    }

    default public List<V> getVertexList() {
        List<E> edgeList = this.getEdgeList();
        if (edgeList.isEmpty()) {
            V startVertex = this.getStartVertex();
            if (startVertex != null && startVertex.equals(this.getEndVertex())) {
                return Collections.singletonList(startVertex);
            }
            return Collections.emptyList();
        }
        Graph<V, E> g = this.getGraph();
        ArrayList<V> list = new ArrayList<V>();
        V v = this.getStartVertex();
        list.add(v);
        for (E e : edgeList) {
            v = Graphs.getOppositeVertex(g, e, v);
            list.add(v);
        }
        return list;
    }

    public double getWeight();

    default public int getLength() {
        return this.getEdgeList().size();
    }
}

