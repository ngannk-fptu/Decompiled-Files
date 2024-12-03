/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.tour;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.tour.HamiltonianCycleAlgorithmBase;

public class NearestInsertionHeuristicTSP<V, E>
extends HamiltonianCycleAlgorithmBase<V, E> {
    private GraphPath<V, E> subtour;

    public NearestInsertionHeuristicTSP() {
        this(null);
    }

    public NearestInsertionHeuristicTSP(GraphPath<V, E> subtour) {
        this.subtour = subtour;
    }

    @Override
    public GraphPath<V, E> getTour(Graph<V, E> graph) {
        this.checkGraph(graph);
        if (graph.vertexSet().size() == 1) {
            return this.getSingletonTour(graph);
        }
        return this.vertexListToTour(this.augment(this.subtour(graph), graph), graph);
    }

    private List<V> subtour(Graph<V, E> graph) {
        ArrayList<V> subtourVertices = new ArrayList<V>();
        if (this.subtour != null) {
            if (this.subtour.getGraph() != null && !graph.equals(this.subtour.getGraph())) {
                throw new IllegalArgumentException("Specified sub-tour is for a different Graph instance");
            }
            if (!graph.vertexSet().containsAll(this.subtour.getVertexList())) {
                throw new IllegalArgumentException("Graph does not contain specified sub-tour vertices");
            }
            if (!graph.edgeSet().containsAll(this.subtour.getEdgeList())) {
                throw new IllegalArgumentException("Graph does not contain specified sub-tour edges");
            }
            if (this.subtour.getStartVertex().equals(this.subtour.getEndVertex())) {
                subtourVertices.addAll(this.subtour.getVertexList().subList(1, this.subtour.getVertexList().size()));
            } else {
                subtourVertices.addAll(this.subtour.getVertexList());
            }
        }
        if (subtourVertices.isEmpty()) {
            E shortestEdge = Collections.min(graph.edgeSet(), (e1, e2) -> Double.compare(graph.getEdgeWeight(e1), graph.getEdgeWeight(e2)));
            subtourVertices.add(graph.getEdgeSource(shortestEdge));
            subtourVertices.add(graph.getEdgeTarget(shortestEdge));
        }
        return subtourVertices;
    }

    private Map<V, Closest<V>> getClosest(List<V> tourVertices, Set<V> unvisited, Graph<V, E> graph) {
        return tourVertices.stream().collect(Collectors.toMap(v -> v, v -> this.getClosest(v, unvisited, graph)));
    }

    private Closest<V> getClosest(V tourVertex, Set<V> unvisited, Graph<V, E> graph) {
        Object closest = null;
        double minDist = Double.MAX_VALUE;
        for (V unvisitedVertex : unvisited) {
            double vDist = graph.getEdgeWeight(graph.getEdge(tourVertex, unvisitedVertex));
            if (!(vDist < minDist)) continue;
            closest = unvisitedVertex;
            minDist = vDist;
        }
        return new Closest<Object>(tourVertex, closest, minDist);
    }

    private void updateClosest(Map<V, Closest<V>> currentClosest, Closest<V> chosen, Set<V> unvisited, Graph<V, E> graph) {
        unvisited.remove(chosen.getUnvisitedVertex());
        if (unvisited.isEmpty()) {
            currentClosest.clear();
            return;
        }
        currentClosest.replaceAll((v, c) -> {
            if (chosen.getTourVertex().equals(v) || chosen.getUnvisitedVertex().equals(c.getUnvisitedVertex())) {
                return this.getClosest(v, unvisited, graph);
            }
            return c;
        });
        currentClosest.put((Closest)chosen.getUnvisitedVertex(), (Closest<Closest>)this.getClosest(chosen.getUnvisitedVertex(), unvisited, graph));
    }

    private Closest<V> chooseClosest(Map<V, Closest<V>> closestVertices) {
        return Collections.min(closestVertices.values());
    }

    private List<V> augment(List<V> subtour, Graph<V, E> graph) {
        HashSet<V> unvisited = new HashSet<V>(graph.vertexSet());
        unvisited.removeAll(subtour);
        return this.augment(subtour, this.getClosest(subtour, (Set<V>)unvisited, graph), unvisited, graph);
    }

    private List<V> augment(List<V> subtour, Map<V, Closest<V>> closestVertices, Set<V> unvisited, Graph<V, E> graph) {
        while (!unvisited.isEmpty()) {
            double insertionCostAfter;
            Closest<V> closestVertex = this.chooseClosest(closestVertices);
            int i = subtour.indexOf(closestVertex.getTourVertex());
            V vertexBefore = subtour.get(i == 0 ? subtour.size() - 1 : i - 1);
            V vertexAfter = subtour.get(i == subtour.size() - 1 ? 0 : i + 1);
            double insertionCostBefore = graph.getEdgeWeight(graph.getEdge(vertexBefore, closestVertex.getUnvisitedVertex())) + closestVertex.getDistance() - graph.getEdgeWeight(graph.getEdge(vertexBefore, closestVertex.getTourVertex()));
            if (insertionCostBefore < (insertionCostAfter = graph.getEdgeWeight(graph.getEdge(vertexAfter, closestVertex.getUnvisitedVertex())) + closestVertex.getDistance() - graph.getEdgeWeight(graph.getEdge(vertexAfter, closestVertex.getTourVertex())))) {
                subtour.add(i, closestVertex.getUnvisitedVertex());
            } else {
                subtour.add(i + 1, closestVertex.getUnvisitedVertex());
            }
            this.updateClosest(closestVertices, closestVertex, unvisited, graph);
        }
        return subtour;
    }

    private static class Closest<V>
    implements Comparable<Closest<V>> {
        private final V tourVertex;
        private final V unvisitedVertex;
        private final double distance;

        Closest(V tourVertex, V unvisitedVertex, double distance) {
            this.tourVertex = tourVertex;
            this.unvisitedVertex = unvisitedVertex;
            this.distance = distance;
        }

        public V getTourVertex() {
            return this.tourVertex;
        }

        public V getUnvisitedVertex() {
            return this.unvisitedVertex;
        }

        public double getDistance() {
            return this.distance;
        }

        @Override
        public int compareTo(Closest<V> o) {
            return Double.compare(this.distance, o.distance);
        }
    }
}

