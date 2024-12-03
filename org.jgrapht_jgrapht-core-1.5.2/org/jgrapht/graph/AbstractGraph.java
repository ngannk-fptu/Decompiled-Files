/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.util.TypeUtil;

public abstract class AbstractGraph<V, E>
implements Graph<V, E> {
    protected AbstractGraph() {
    }

    @Override
    public boolean containsEdge(V sourceVertex, V targetVertex) {
        return this.getEdge(sourceVertex, targetVertex) != null;
    }

    @Override
    public boolean removeAllEdges(Collection<? extends E> edges) {
        boolean modified = false;
        for (E e : edges) {
            modified |= this.removeEdge(e);
        }
        return modified;
    }

    @Override
    public Set<E> removeAllEdges(V sourceVertex, V targetVertex) {
        Set removed = this.getAllEdges(sourceVertex, targetVertex);
        if (removed == null) {
            return null;
        }
        this.removeAllEdges(removed);
        return removed;
    }

    @Override
    public boolean removeAllVertices(Collection<? extends V> vertices) {
        boolean modified = false;
        for (V v : vertices) {
            modified |= this.removeVertex(v);
        }
        return modified;
    }

    public String toString() {
        return this.toStringFromSets(this.vertexSet(), this.edgeSet(), this.getType().isDirected());
    }

    protected boolean assertVertexExist(V v) {
        if (this.containsVertex(v)) {
            return true;
        }
        if (v == null) {
            throw new NullPointerException();
        }
        throw new IllegalArgumentException("no such vertex in graph: " + v.toString());
    }

    protected boolean removeAllEdges(E[] edges) {
        boolean modified = false;
        for (E edge : edges) {
            modified |= this.removeEdge(edge);
        }
        return modified;
    }

    protected String toStringFromSets(Collection<? extends V> vertexSet, Collection<? extends E> edgeSet, boolean directed) {
        ArrayList<String> renderedEdges = new ArrayList<String>();
        StringBuilder sb = new StringBuilder();
        for (E e : edgeSet) {
            if (e.getClass() != DefaultEdge.class && e.getClass() != DefaultWeightedEdge.class) {
                sb.append(e.toString());
                sb.append("=");
            }
            if (directed) {
                sb.append("(");
            } else {
                sb.append("{");
            }
            sb.append(this.getEdgeSource(e));
            sb.append(",");
            sb.append(this.getEdgeTarget(e));
            if (directed) {
                sb.append(")");
            } else {
                sb.append("}");
            }
            renderedEdges.add(sb.toString());
            sb.setLength(0);
        }
        return "(" + vertexSet + ", " + renderedEdges + ")";
    }

    public int hashCode() {
        int hash = this.vertexSet().hashCode();
        boolean isDirected = this.getType().isDirected();
        for (Object e : this.edgeSet()) {
            int part = e.hashCode();
            int source = this.getEdgeSource(e).hashCode();
            int target = this.getEdgeTarget(e).hashCode();
            int pairing = source + target;
            if (isDirected) {
                pairing = pairing * (pairing + 1) / 2 + target;
            }
            part = 31 * part + pairing;
            part = 31 * part + Double.hashCode(this.getEdgeWeight(e));
            hash += part;
        }
        return hash;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        Graph g = (Graph)TypeUtil.uncheckedCast(obj);
        if (!this.vertexSet().equals(g.vertexSet())) {
            return false;
        }
        if (this.edgeSet().size() != g.edgeSet().size()) {
            return false;
        }
        boolean isDirected = this.getType().isDirected();
        for (Object e : this.edgeSet()) {
            Object source = this.getEdgeSource(e);
            Object target = this.getEdgeTarget(e);
            if (!g.containsEdge(e)) {
                return false;
            }
            Object gSource = g.getEdgeSource(e);
            Object gTarget = g.getEdgeTarget(e);
            if (isDirected ? !gSource.equals(source) || !gTarget.equals(target) : !(gSource.equals(source) && gTarget.equals(target) || gSource.equals(target) && gTarget.equals(source))) {
                return false;
            }
            if (Double.compare(this.getEdgeWeight(e), g.getEdgeWeight(e)) == 0) continue;
            return false;
        }
        return true;
    }
}

