/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.shortestpath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.FloydWarshallShortestPaths;
import org.jgrapht.alg.util.ToleranceDoubleComparator;

public class GraphMeasurer<V, E> {
    private final Graph<V, E> graph;
    private final ShortestPathAlgorithm<V, E> shortestPathAlgorithm;
    private Map<V, Double> eccentricityMap = null;
    private double diameter = 0.0;
    private double radius = Double.POSITIVE_INFINITY;

    public GraphMeasurer(Graph<V, E> graph) {
        this(graph, new FloydWarshallShortestPaths<V, E>(graph));
    }

    public GraphMeasurer(Graph<V, E> graph, ShortestPathAlgorithm<V, E> shortestPathAlgorithm) {
        this.graph = graph;
        this.shortestPathAlgorithm = shortestPathAlgorithm;
    }

    public double getDiameter() {
        this.computeEccentricityMap();
        return this.diameter;
    }

    public double getRadius() {
        this.computeEccentricityMap();
        return this.radius;
    }

    public Map<V, Double> getVertexEccentricityMap() {
        this.computeEccentricityMap();
        return Collections.unmodifiableMap(this.eccentricityMap);
    }

    public Set<V> getGraphCenter() {
        this.computeEccentricityMap();
        LinkedHashSet<V> graphCenter = new LinkedHashSet<V>();
        ToleranceDoubleComparator comp = new ToleranceDoubleComparator();
        for (Map.Entry<V, Double> entry : this.eccentricityMap.entrySet()) {
            if (comp.compare(entry.getValue(), this.radius) != 0) continue;
            graphCenter.add(entry.getKey());
        }
        return graphCenter;
    }

    public Set<V> getGraphPeriphery() {
        this.computeEccentricityMap();
        LinkedHashSet<V> graphPeriphery = new LinkedHashSet<V>();
        ToleranceDoubleComparator comp = new ToleranceDoubleComparator();
        for (Map.Entry<V, Double> entry : this.eccentricityMap.entrySet()) {
            if (comp.compare(entry.getValue(), this.diameter) != 0) continue;
            graphPeriphery.add(entry.getKey());
        }
        return graphPeriphery;
    }

    public Set<V> getGraphPseudoPeriphery() {
        this.computeEccentricityMap();
        LinkedHashSet<V> graphPseudoPeriphery = new LinkedHashSet<V>();
        ToleranceDoubleComparator comp = new ToleranceDoubleComparator();
        for (Map.Entry<V, Double> entry : this.eccentricityMap.entrySet()) {
            V u = entry.getKey();
            for (V v : this.graph.vertexSet()) {
                if (comp.compare(this.shortestPathAlgorithm.getPathWeight(u, v), entry.getValue()) != 0 || comp.compare(entry.getValue(), this.eccentricityMap.get(v)) != 0) continue;
                graphPseudoPeriphery.add(entry.getKey());
            }
        }
        return graphPseudoPeriphery;
    }

    private void computeEccentricityMap() {
        if (this.eccentricityMap != null) {
            return;
        }
        this.eccentricityMap = new LinkedHashMap<V, Double>();
        if (this.graph.getType().isUndirected()) {
            int i;
            ArrayList<V> vertices = new ArrayList<V>(this.graph.vertexSet());
            double[] eccentricityVector = new double[vertices.size()];
            for (i = 0; i < vertices.size() - 1; ++i) {
                for (int j = i + 1; j < vertices.size(); ++j) {
                    double dist = this.shortestPathAlgorithm.getPathWeight(vertices.get(i), vertices.get(j));
                    eccentricityVector[i] = Math.max(eccentricityVector[i], dist);
                    eccentricityVector[j] = Math.max(eccentricityVector[j], dist);
                }
            }
            for (i = 0; i < vertices.size(); ++i) {
                this.eccentricityMap.put((Double)vertices.get(i), eccentricityVector[i]);
            }
        } else {
            for (V u : this.graph.vertexSet()) {
                double eccentricity = 0.0;
                for (V v : this.graph.vertexSet()) {
                    eccentricity = Double.max(eccentricity, this.shortestPathAlgorithm.getPathWeight(u, v));
                }
                this.eccentricityMap.put((Double)u, eccentricity);
            }
        }
        if (this.eccentricityMap.isEmpty()) {
            this.diameter = 0.0;
            this.radius = 0.0;
        } else {
            for (V v : this.graph.vertexSet()) {
                this.diameter = Math.max(this.diameter, this.eccentricityMap.get(v));
                this.radius = Math.min(this.radius, this.eccentricityMap.get(v));
            }
        }
    }
}

