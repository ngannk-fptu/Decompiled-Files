/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.clique;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.GraphTests;
import org.jgrapht.Graphs;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.builder.GraphTypeBuilder;

public class CliqueMinimalSeparatorDecomposition<V, E> {
    private Graph<V, E> graph;
    private Graph<V, E> chordalGraph;
    private Set<E> fillEdges;
    private LinkedList<V> meo;
    private List<V> generators;
    private Set<Set<V>> separators;
    private Set<Set<V>> atoms;
    private Map<Set<V>, Integer> fullComponentCount = new HashMap<Set<V>, Integer>();

    public CliqueMinimalSeparatorDecomposition(Graph<V, E> g) {
        this.graph = GraphTests.requireUndirected(g);
        this.fillEdges = new HashSet();
    }

    private void computeMinimalTriangulation() {
        this.chordalGraph = GraphTypeBuilder.undirected().edgeSupplier(this.graph.getEdgeSupplier()).vertexSupplier(this.graph.getVertexSupplier()).allowingMultipleEdges(false).allowingSelfLoops(false).buildGraph();
        for (V v : this.graph.vertexSet()) {
            this.chordalGraph.addVertex(v);
        }
        Graph gprime = CliqueMinimalSeparatorDecomposition.copyAsSimpleGraph(this.graph);
        int s = -1;
        this.generators = new ArrayList<V>();
        this.meo = new LinkedList();
        HashMap<Object, Integer> vertexLabels = new HashMap<Object, Integer>();
        for (V v : gprime.vertexSet()) {
            vertexLabels.put(v, 0);
        }
        int n = this.graph.vertexSet().size();
        for (int i = 1; i <= n; ++i) {
            Object v = this.getMaxLabelVertex(vertexLabels);
            LinkedList<V> neighborsY = new LinkedList<V>(Graphs.neighborListOf(gprime, v));
            if ((Integer)vertexLabels.get(v) <= s) {
                this.generators.add(v);
            }
            s = (Integer)vertexLabels.get(v);
            HashSet<Object> reached = new HashSet<Object>();
            reached.add(v);
            HashMap<Integer, HashSet<V>> reach = new HashMap<Integer, HashSet<V>>();
            for (Object y : neighborsY) {
                reached.add(y);
                this.addToReach((Integer)vertexLabels.get(y), y, reach);
            }
            for (int j = 0; j < this.graph.vertexSet().size(); ++j) {
                if (!reach.containsKey(j)) continue;
                while (reach.get(j).size() > 0) {
                    Object y;
                    y = reach.get(j).iterator().next();
                    reach.get(j).remove(y);
                    for (V z : Graphs.neighborListOf(gprime, y)) {
                        if (reached.contains(z)) continue;
                        reached.add(z);
                        if ((Integer)vertexLabels.get(z) > j) {
                            neighborsY.add(z);
                            E fillEdge = this.graph.getEdgeSupplier().get();
                            this.fillEdges.add(fillEdge);
                            this.addToReach((Integer)vertexLabels.get(z), z, reach);
                            continue;
                        }
                        this.addToReach(j, z, reach);
                    }
                }
            }
            for (Object y : neighborsY) {
                this.chordalGraph.addEdge(v, y);
                vertexLabels.put(y, (Integer)vertexLabels.get(y) + 1);
            }
            this.meo.addLast(v);
            gprime.removeVertex(v);
            vertexLabels.remove(v);
        }
    }

    private V getMaxLabelVertex(Map<V, Integer> vertexLabels) {
        Iterator<Map.Entry<V, Integer>> iterator = vertexLabels.entrySet().iterator();
        Map.Entry<V, Integer> max = iterator.next();
        while (iterator.hasNext()) {
            Map.Entry<Integer, Integer> e = iterator.next();
            if (e.getValue() <= max.getValue()) continue;
            max = e;
        }
        return max.getKey();
    }

    private void addToReach(Integer k, V v, HashMap<Integer, HashSet<V>> r) {
        if (r.containsKey(k)) {
            r.get(k).add(v);
        } else {
            HashSet<V> set = new HashSet<V>();
            set.add(v);
            r.put(k, set);
        }
    }

    private void computeAtoms() {
        if (this.chordalGraph == null) {
            this.computeMinimalTriangulation();
        }
        this.separators = new HashSet<Set<V>>();
        Graph<V, E> gprime = CliqueMinimalSeparatorDecomposition.copyAsSimpleGraph(this.graph);
        Graph<V, E> hprime = CliqueMinimalSeparatorDecomposition.copyAsSimpleGraph(this.chordalGraph);
        this.atoms = new HashSet<Set<V>>();
        Iterator<V> iterator = this.meo.descendingIterator();
        while (iterator.hasNext()) {
            HashSet<V> separator;
            V v = iterator.next();
            if (this.generators.contains(v) && CliqueMinimalSeparatorDecomposition.isClique(this.graph, separator = new HashSet<V>(Graphs.neighborListOf(hprime, v)))) {
                if (separator.size() > 0) {
                    if (this.separators.contains(separator)) {
                        this.fullComponentCount.put(separator, this.fullComponentCount.get(separator) + 1);
                    } else {
                        this.fullComponentCount.put(separator, 2);
                        this.separators.add(separator);
                    }
                }
                Graph<V, E> tmpGraph = CliqueMinimalSeparatorDecomposition.copyAsSimpleGraph(gprime);
                tmpGraph.removeAllVertices(separator);
                ConnectivityInspector<V, E> con = new ConnectivityInspector<V, E>(tmpGraph);
                if (con.isConnected()) {
                    throw new RuntimeException("separator did not separate the graph");
                }
                for (Set<V> component : con.connectedSets()) {
                    if (!component.contains(v)) continue;
                    gprime.removeAllVertices(component);
                    component.addAll(separator);
                    this.atoms.add(new HashSet<V>(component));
                    assert (component.size() > 0);
                    break;
                }
            }
            hprime.removeVertex(v);
        }
        if (gprime.vertexSet().size() > 0) {
            this.atoms.add(new HashSet<V>(gprime.vertexSet()));
        }
    }

    private static <V, E> boolean isClique(Graph<V, E> graph, Set<V> vertices) {
        for (V v1 : vertices) {
            for (V v2 : vertices) {
                if (v1.equals(v2) || graph.getEdge(v1, v2) != null) continue;
                return false;
            }
        }
        return true;
    }

    private static <V, E> Graph<V, E> copyAsSimpleGraph(Graph<V, E> graph) {
        Graph<V, E> copy = GraphTypeBuilder.undirected().edgeSupplier(graph.getEdgeSupplier()).vertexSupplier(graph.getVertexSupplier()).allowingMultipleEdges(false).allowingSelfLoops(false).buildGraph();
        if (graph.getType().isSimple()) {
            Graphs.addGraph(copy, graph);
        } else {
            Graphs.addAllVertices(copy, graph.vertexSet());
            for (E e : graph.edgeSet()) {
                V v2;
                V v1 = graph.getEdgeSource(e);
                if (v1.equals(v2 = graph.getEdgeTarget(e)) || copy.containsEdge(e)) continue;
                copy.addEdge(v1, v2);
            }
        }
        return copy;
    }

    public boolean isChordal() {
        if (this.chordalGraph == null) {
            this.computeMinimalTriangulation();
        }
        return this.chordalGraph.edgeSet().size() == this.graph.edgeSet().size();
    }

    public Set<E> getFillEdges() {
        if (this.fillEdges == null) {
            this.computeMinimalTriangulation();
        }
        return this.fillEdges;
    }

    public Graph<V, E> getMinimalTriangulation() {
        if (this.chordalGraph == null) {
            this.computeMinimalTriangulation();
        }
        return this.chordalGraph;
    }

    public List<V> getGenerators() {
        if (this.generators == null) {
            this.computeMinimalTriangulation();
        }
        return this.generators;
    }

    public LinkedList<V> getMeo() {
        if (this.meo == null) {
            this.computeMinimalTriangulation();
        }
        return this.meo;
    }

    public Map<Set<V>, Integer> getFullComponentCount() {
        if (this.fullComponentCount == null) {
            this.computeAtoms();
        }
        return this.fullComponentCount;
    }

    public Set<Set<V>> getAtoms() {
        if (this.atoms == null) {
            this.computeAtoms();
        }
        return this.atoms;
    }

    public Set<Set<V>> getSeparators() {
        if (this.separators == null) {
            this.computeAtoms();
        }
        return this.separators;
    }

    public Graph<V, E> getGraph() {
        return this.graph;
    }
}

