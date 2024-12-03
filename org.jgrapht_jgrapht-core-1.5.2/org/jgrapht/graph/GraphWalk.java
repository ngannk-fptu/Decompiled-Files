/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.graph;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.function.Function;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.graph.InvalidGraphWalkException;

public class GraphWalk<V, E>
implements GraphPath<V, E>,
Serializable {
    private static final long serialVersionUID = 7663410644865380676L;
    protected Graph<V, E> graph;
    protected List<V> vertexList;
    protected List<E> edgeList;
    protected V startVertex;
    protected V endVertex;
    protected double weight;

    public GraphWalk(Graph<V, E> graph, V startVertex, V endVertex, List<E> edgeList, double weight) {
        this(graph, startVertex, endVertex, null, edgeList, weight);
    }

    public GraphWalk(Graph<V, E> graph, List<V> vertexList, double weight) {
        this((Graph<Object, E>)graph, (vertexList.isEmpty() ? null : (V)vertexList.get(0)), (vertexList.isEmpty() ? null : (V)vertexList.get(vertexList.size() - 1)), (List<Object>)vertexList, null, weight);
    }

    public GraphWalk(Graph<V, E> graph, V startVertex, V endVertex, List<V> vertexList, List<E> edgeList, double weight) {
        if (vertexList == null && edgeList == null) {
            throw new IllegalArgumentException("Vertex list and edge list cannot both be null!");
        }
        if (startVertex != null && vertexList != null && edgeList != null && edgeList.size() + 1 != vertexList.size()) {
            throw new IllegalArgumentException("VertexList and edgeList do not correspond to the same path (cardinality of vertexList +1 must equal the cardinality of the edgeList)");
        }
        if (startVertex == null ^ endVertex == null) {
            throw new IllegalArgumentException("Either the start and end vertices must both be null, or they must both be not null (one of them is null)");
        }
        this.graph = Objects.requireNonNull(graph);
        this.startVertex = startVertex;
        this.endVertex = endVertex;
        this.vertexList = vertexList;
        this.edgeList = edgeList;
        this.weight = weight;
    }

    @Override
    public Graph<V, E> getGraph() {
        return this.graph;
    }

    @Override
    public V getStartVertex() {
        return this.startVertex;
    }

    @Override
    public V getEndVertex() {
        return this.endVertex;
    }

    @Override
    public List<E> getEdgeList() {
        return this.edgeList != null ? this.edgeList : GraphPath.super.getEdgeList();
    }

    @Override
    public List<V> getVertexList() {
        return this.vertexList != null ? this.vertexList : GraphPath.super.getVertexList();
    }

    @Override
    public double getWeight() {
        return this.weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    @Override
    public int getLength() {
        if (this.edgeList != null) {
            return this.edgeList.size();
        }
        if (this.vertexList != null && !this.vertexList.isEmpty()) {
            return this.vertexList.size() - 1;
        }
        return 0;
    }

    public String toString() {
        if (this.vertexList != null) {
            return this.vertexList.toString();
        }
        return this.edgeList.toString();
    }

    public boolean equals(Object o) {
        if (o == null || !(o instanceof GraphWalk)) {
            return false;
        }
        if (this == o) {
            return true;
        }
        GraphWalk other = (GraphWalk)o;
        if (this.isEmpty() && other.isEmpty()) {
            return true;
        }
        if (this.isEmpty()) {
            return false;
        }
        if (!this.startVertex.equals(other.getStartVertex()) || !this.endVertex.equals(other.getEndVertex())) {
            return false;
        }
        if (this.edgeList == null && !other.getGraph().getType().isAllowingMultipleEdges()) {
            return this.vertexList.equals(other.getVertexList());
        }
        return this.getEdgeList().equals(other.getEdgeList());
    }

    public int hashCode() {
        int hashCode = 1;
        if (this.isEmpty()) {
            return hashCode;
        }
        hashCode = 31 * hashCode + this.startVertex.hashCode();
        hashCode = 31 * hashCode + this.endVertex.hashCode();
        if (this.edgeList != null) {
            return 31 * hashCode + this.edgeList.hashCode();
        }
        return 31 * hashCode + this.vertexList.hashCode();
    }

    public GraphWalk<V, E> reverse() {
        return this.reverse(null);
    }

    public GraphWalk<V, E> reverse(Function<GraphWalk<V, E>, Double> walkWeightCalculator) {
        ArrayList<V> revVertexList = null;
        ArrayList<E> revEdgeList = null;
        double revWeight = 0.0;
        if (this.vertexList != null) {
            revVertexList = new ArrayList<V>(this.vertexList);
            Collections.reverse(revVertexList);
            if (this.graph.getType().isUndirected()) {
                revWeight = this.weight;
            }
            if (!this.graph.getType().isUndirected() && this.edgeList == null) {
                for (int i = 0; i < revVertexList.size() - 1; ++i) {
                    Object v;
                    Object u = revVertexList.get(i);
                    E edge = this.graph.getEdge(u, v = revVertexList.get(i + 1));
                    if (edge == null) {
                        throw new InvalidGraphWalkException("this walk cannot be reversed. The graph does not contain a reverse arc for arc " + this.graph.getEdge(v, u));
                    }
                    revWeight += this.graph.getEdgeWeight(edge);
                }
            }
        }
        if (this.edgeList != null) {
            revEdgeList = new ArrayList<E>(this.edgeList.size());
            if (this.graph.getType().isUndirected()) {
                revEdgeList.addAll(this.edgeList);
                Collections.reverse(revEdgeList);
                revWeight = this.weight;
            } else {
                ListIterator<E> listIterator = this.edgeList.listIterator(this.edgeList.size());
                while (listIterator.hasPrevious()) {
                    E e = listIterator.previous();
                    V u = this.graph.getEdgeSource(e);
                    V v = this.graph.getEdgeTarget(e);
                    E revEdge = this.graph.getEdge(v, u);
                    if (revEdge == null) {
                        throw new InvalidGraphWalkException("this walk cannot be reversed. The graph does not contain a reverse arc for arc " + e);
                    }
                    revEdgeList.add(revEdge);
                    revWeight += this.graph.getEdgeWeight(revEdge);
                }
            }
        }
        GraphWalk<V, E> gw = new GraphWalk<V, E>(this.graph, this.endVertex, this.startVertex, revVertexList, revEdgeList, 0.0);
        gw.weight = walkWeightCalculator == null ? revWeight : walkWeightCalculator.apply(gw);
        return gw;
    }

    public GraphWalk<V, E> concat(GraphWalk<V, E> extension, Function<GraphWalk<V, E>, Double> walkWeightCalculator) {
        if (this.isEmpty()) {
            throw new IllegalArgumentException("An empty path cannot be extended");
        }
        if (!this.endVertex.equals(extension.getStartVertex())) {
            throw new IllegalArgumentException("This path can only be extended by another path if the end vertex of the orginal path and the start vertex of the extension are equal.");
        }
        ArrayList<V> concatVertexList = null;
        ArrayList<E> concatEdgeList = null;
        if (this.vertexList != null) {
            concatVertexList = new ArrayList<V>(this.vertexList);
            List<V> vertexListExtension = extension.getVertexList();
            concatVertexList.addAll(vertexListExtension.subList(1, vertexListExtension.size()));
        }
        if (this.edgeList != null) {
            concatEdgeList = new ArrayList<E>(this.edgeList);
            concatEdgeList.addAll(extension.getEdgeList());
        }
        GraphWalk<V, E> gw = new GraphWalk<V, E>(this.graph, this.startVertex, extension.getEndVertex(), concatVertexList, concatEdgeList, 0.0);
        gw.setWeight(walkWeightCalculator.apply(gw));
        return gw;
    }

    public boolean isEmpty() {
        return this.startVertex == null;
    }

    public void verify() {
        V v;
        Object u;
        if (this.isEmpty()) {
            return;
        }
        if (this.vertexList != null && !this.vertexList.isEmpty()) {
            if (!this.startVertex.equals(this.vertexList.get(0))) {
                throw new InvalidGraphWalkException("The start vertex must be the first vertex in the vertex list");
            }
            if (!this.endVertex.equals(this.vertexList.get(this.vertexList.size() - 1))) {
                throw new InvalidGraphWalkException("The end vertex must be the last vertex in the vertex list");
            }
            if (!this.graph.vertexSet().containsAll(this.vertexList)) {
                throw new InvalidGraphWalkException("Not all vertices in the path are contained in the graph");
            }
            if (this.edgeList == null) {
                Iterator<V> it = this.vertexList.iterator();
                u = it.next();
                while (it.hasNext()) {
                    v = it.next();
                    if (this.graph.getEdge(u, v) == null) {
                        throw new InvalidGraphWalkException("The vertexList does not constitute to a feasible path. Edge (" + u + "," + v + " does not exist in the graph.");
                    }
                    u = v;
                }
            }
        }
        if (this.edgeList != null && !this.edgeList.isEmpty()) {
            if (!Graphs.testIncidence(this.graph, this.edgeList.get(0), this.startVertex)) {
                throw new InvalidGraphWalkException("The first edge in the edge list must leave the start vertex");
            }
            if (!this.graph.edgeSet().containsAll(this.edgeList)) {
                throw new InvalidGraphWalkException("Not all edges in the path are contained in the graph");
            }
            if (this.vertexList == null) {
                V u2 = this.startVertex;
                for (Object edge : this.edgeList) {
                    if (!Graphs.testIncidence(this.graph, edge, u2)) {
                        throw new InvalidGraphWalkException("The edgeList does not constitute to a feasible path. Conflicting edge: " + edge);
                    }
                    u2 = Graphs.getOppositeVertex(this.graph, edge, u2);
                }
                if (!u2.equals(this.endVertex)) {
                    throw new InvalidGraphWalkException("The path defined by the edgeList does not end in the endVertex.");
                }
            }
        }
        if (this.vertexList != null && this.edgeList != null) {
            if (this.edgeList.size() + 1 != this.vertexList.size()) {
                throw new InvalidGraphWalkException("VertexList and edgeList do not correspond to the same path (cardinality of vertexList +1 must equal the cardinality of the edgeList)");
            }
            for (int i = 0; i < this.vertexList.size() - 1; ++i) {
                u = this.vertexList.get(i);
                v = this.vertexList.get(i + 1);
                E edge = this.getEdgeList().get(i);
                if (!(this.graph.getType().isDirected() ? !this.graph.getEdgeSource(edge).equals(u) || !this.graph.getEdgeTarget(edge).equals(v) : !Graphs.testIncidence(this.graph, edge, u) || !Graphs.getOppositeVertex(this.graph, edge, u).equals(v))) continue;
                throw new InvalidGraphWalkException("VertexList and edgeList do not form a feasible path");
            }
        }
    }

    public static <V, E> GraphWalk<V, E> emptyWalk(Graph<V, E> graph) {
        return new GraphWalk<Object, E>(graph, null, null, Collections.emptyList(), Collections.emptyList(), 0.0);
    }

    public static <V, E> GraphWalk<V, E> singletonWalk(Graph<V, E> graph, V v) {
        return GraphWalk.singletonWalk(graph, v, 0.0);
    }

    public static <V, E> GraphWalk<V, E> singletonWalk(Graph<V, E> graph, V v, double weight) {
        return new GraphWalk<V, E>(graph, v, v, Collections.singletonList(v), Collections.emptyList(), weight);
    }
}

