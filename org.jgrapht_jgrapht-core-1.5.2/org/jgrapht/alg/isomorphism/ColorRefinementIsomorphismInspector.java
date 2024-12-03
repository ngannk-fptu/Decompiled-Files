/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.isomorphism;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.GraphMapping;
import org.jgrapht.GraphTests;
import org.jgrapht.GraphType;
import org.jgrapht.alg.color.ColorRefinementAlgorithm;
import org.jgrapht.alg.interfaces.VertexColoringAlgorithm;
import org.jgrapht.alg.isomorphism.GraphOrdering;
import org.jgrapht.alg.isomorphism.IsomorphicGraphMapping;
import org.jgrapht.alg.isomorphism.IsomorphismInspector;
import org.jgrapht.alg.isomorphism.IsomorphismUndecidableException;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.graph.AsGraphUnion;
import org.jgrapht.graph.builder.GraphTypeBuilder;

public class ColorRefinementIsomorphismInspector<V, E>
implements IsomorphismInspector<V, E> {
    private Graph<V, E> graph1;
    private Graph<V, E> graph2;
    private IsomorphicGraphMapping<V, E> isomorphicGraphMapping;
    private Boolean isIsomorphic;
    private boolean isColoringDiscrete;
    private boolean isForest;
    private boolean isomorphismTestExecuted;

    public ColorRefinementIsomorphismInspector(Graph<V, E> graph1, Graph<V, E> graph2) {
        GraphType type1 = graph1.getType();
        GraphType type2 = graph2.getType();
        if (type1.isAllowingMultipleEdges() || type2.isAllowingMultipleEdges()) {
            throw new IllegalArgumentException("graphs with multiple (parallel) edges are not supported");
        }
        if (type1.isMixed() || type2.isMixed()) {
            throw new IllegalArgumentException("mixed graphs not supported");
        }
        if (type1.isUndirected() && type2.isDirected() || type1.isDirected() && type2.isUndirected()) {
            throw new IllegalArgumentException("can not match directed with undirected graphs");
        }
        this.graph1 = graph1;
        this.graph2 = graph2;
        this.isomorphicGraphMapping = null;
        this.isColoringDiscrete = false;
        this.isomorphismTestExecuted = false;
        this.isForest = false;
    }

    @Override
    public Iterator<GraphMapping<V, E>> getMappings() {
        if (!this.isomorphismTestExecuted) {
            this.isomorphismExists();
        }
        ArrayList<IsomorphicGraphMapping<V, E>> iteratorList = new ArrayList<IsomorphicGraphMapping<V, E>>(1);
        if (this.isIsomorphic != null && this.isIsomorphic.booleanValue()) {
            iteratorList.add(this.isomorphicGraphMapping);
        }
        return iteratorList.iterator();
    }

    @Override
    public boolean isomorphismExists() {
        if (this.isomorphismTestExecuted) {
            if (this.isIsomorphic != null) {
                return this.isIsomorphic;
            }
            throw new IsomorphismUndecidableException();
        }
        if (this.graph1 == this.graph2) {
            this.isomorphismTestExecuted = true;
            this.isIsomorphic = true;
            this.isomorphicGraphMapping = IsomorphicGraphMapping.identity(this.graph1);
            return this.isIsomorphic;
        }
        if (this.graph1.vertexSet().size() != this.graph2.vertexSet().size()) {
            this.isomorphismTestExecuted = true;
            this.isIsomorphic = false;
            return this.isIsomorphic;
        }
        Graph<DistinctGraphObject<V, V, E>, DistinctGraphObject<E, V, E>> graph = this.getDisjointGraphUnion(this.graph1, this.graph2);
        ColorRefinementAlgorithm<DistinctGraphObject<V, V, E>, DistinctGraphObject<E, V, E>> colorRefinementAlgorithm = new ColorRefinementAlgorithm<DistinctGraphObject<V, V, E>, DistinctGraphObject<E, V, E>>(graph);
        VertexColoringAlgorithm.Coloring<DistinctGraphObject<V, V, E>> coloring = colorRefinementAlgorithm.getColoring();
        this.isomorphismTestExecuted = true;
        this.isIsomorphic = this.coarseColoringAreEqual(coloring);
        if (this.isIsomorphic.booleanValue()) assert (this.isomorphicGraphMapping.isValidIsomorphism());
        return this.isIsomorphic;
    }

    boolean isColoringDiscrete() {
        if (!this.isomorphismTestExecuted) {
            this.isomorphismExists();
        }
        return this.isColoringDiscrete;
    }

    boolean isForest() {
        if (!this.isomorphismTestExecuted) {
            this.isomorphismExists();
        }
        return this.isForest;
    }

    private boolean coarseColoringAreEqual(VertexColoringAlgorithm.Coloring<DistinctGraphObject<V, V, E>> coloring) throws IsomorphismUndecidableException {
        Pair<VertexColoringAlgorithm.Coloring<V>, VertexColoringAlgorithm.Coloring<V>> coloringPair = this.splitColoring(coloring);
        VertexColoringAlgorithm.Coloring<V> coloring1 = coloringPair.getFirst();
        VertexColoringAlgorithm.Coloring<V> coloring2 = coloringPair.getSecond();
        if (coloring1.getNumberColors() != coloring2.getNumberColors()) {
            return false;
        }
        List<Set<V>> colorClasses1 = coloring1.getColorClasses();
        List<Set<V>> colorClasses2 = coloring2.getColorClasses();
        if (colorClasses1.size() != colorClasses2.size()) {
            return false;
        }
        this.sortColorClasses(colorClasses1, coloring1);
        this.sortColorClasses(colorClasses2, coloring2);
        Iterator<Set<V>> it1 = colorClasses1.iterator();
        Iterator<Set<V>> it2 = colorClasses2.iterator();
        while (it1.hasNext() && it2.hasNext()) {
            Set<V> cur1 = it1.next();
            Set<V> cur2 = it2.next();
            if (cur1.size() != cur2.size()) {
                return false;
            }
            if (!cur1.iterator().hasNext() || coloring1.getColors().get(cur1.iterator().next()).equals(coloring2.getColors().get(cur2.iterator().next()))) continue;
            return false;
        }
        if (!it1.hasNext() && !it2.hasNext()) {
            if (coloring1.getColorClasses().size() == this.graph1.vertexSet().size() && coloring2.getColorClasses().size() == this.graph2.vertexSet().size()) {
                this.isColoringDiscrete = true;
                this.calculateGraphMapping(coloring1, coloring2);
                return true;
            }
            if (GraphTests.isForest(this.graph1) && GraphTests.isForest(this.graph2)) {
                this.isForest = true;
                this.calculateGraphMapping(coloring1, coloring2);
                return true;
            }
            this.isIsomorphic = null;
            throw new IsomorphismUndecidableException("Color refinement cannot decide whether the two graphs are isomorphic or not.");
        }
        return false;
    }

    private Pair<VertexColoringAlgorithm.Coloring<V>, VertexColoringAlgorithm.Coloring<V>> splitColoring(VertexColoringAlgorithm.Coloring<DistinctGraphObject<V, V, E>> coloring) {
        HashMap<V, Integer> col1 = new HashMap<V, Integer>();
        HashMap<V, Integer> col2 = new HashMap<V, Integer>();
        int index = 0;
        for (Set<DistinctGraphObject<V, V, E>> set1 : coloring.getColorClasses()) {
            for (DistinctGraphObject<V, V, E> entry : set1) {
                if (entry.getGraph() == this.graph1) {
                    col1.put(entry.getObject(), index);
                    continue;
                }
                col2.put(entry.getObject(), index);
            }
            ++index;
        }
        VertexColoringAlgorithm.ColoringImpl coloring1 = new VertexColoringAlgorithm.ColoringImpl(col1, col1.size());
        VertexColoringAlgorithm.ColoringImpl coloring2 = new VertexColoringAlgorithm.ColoringImpl(col2, col2.size());
        return new Pair<VertexColoringAlgorithm.Coloring<V>, VertexColoringAlgorithm.Coloring<V>>(coloring1, coloring2);
    }

    private void sortColorClasses(List<Set<V>> colorClasses, VertexColoringAlgorithm.Coloring<V> coloring) {
        colorClasses.sort((o1, o2) -> {
            if (o1.size() == o2.size()) {
                Iterator it1 = o1.iterator();
                Iterator it2 = o2.iterator();
                if (!it1.hasNext() || !it2.hasNext()) {
                    return Integer.compare(o1.size(), o2.size());
                }
                return coloring.getColors().get(it1.next()).compareTo(coloring.getColors().get(it2.next()));
            }
            return Integer.compare(o1.size(), o2.size());
        });
    }

    private void calculateGraphMapping(VertexColoringAlgorithm.Coloring<V> coloring1, VertexColoringAlgorithm.Coloring<V> coloring2) {
        GraphOrdering<V, E> graphOrdering1 = new GraphOrdering<V, E>(this.graph1);
        GraphOrdering<V, E> graphOrdering2 = new GraphOrdering<V, E>(this.graph2);
        int[] core1 = new int[this.graph1.vertexSet().size()];
        int[] core2 = new int[this.graph2.vertexSet().size()];
        Iterator<Set<V>> setIterator1 = coloring1.getColorClasses().iterator();
        Iterator<Set<V>> setIterator2 = coloring2.getColorClasses().iterator();
        while (setIterator1.hasNext()) {
            Iterator<V> vertexIterator1 = setIterator1.next().iterator();
            Iterator<V> vertexIterator2 = setIterator2.next().iterator();
            while (vertexIterator1.hasNext()) {
                int numberOfV2;
                V v1 = vertexIterator1.next();
                V v2 = vertexIterator2.next();
                int numberOfV1 = graphOrdering1.getVertexNumber(v1);
                core1[numberOfV1] = numberOfV2 = graphOrdering2.getVertexNumber(v2);
                core2[numberOfV2] = numberOfV1;
            }
        }
        this.isomorphicGraphMapping = new IsomorphicGraphMapping<V, E>(graphOrdering1, graphOrdering2, core1, core2);
    }

    private Graph<DistinctGraphObject<V, V, E>, DistinctGraphObject<E, V, E>> getDisjointGraphUnion(Graph<V, E> graph1, Graph<V, E> graph2) {
        return new AsGraphUnion<DistinctGraphObject<V, V, E>, DistinctGraphObject<E, V, E>>(this.getDistinctObjectGraph(graph1), this.getDistinctObjectGraph(graph2));
    }

    private Graph<DistinctGraphObject<V, V, E>, DistinctGraphObject<E, V, E>> getDistinctObjectGraph(Graph<V, E> graph) {
        Graph<DistinctGraphObject<DistinctGraphObject<V, V, E>, DistinctGraphObject<V, V, E>, DistinctGraphObject<Object, V, E>>, DistinctGraphObject<DistinctGraphObject<Object, V, E>, DistinctGraphObject<V, V, E>, DistinctGraphObject<Object, V, E>>> transformedGraph = GraphTypeBuilder.forGraphType(graph.getType()).buildGraph();
        for (V vertex : graph.vertexSet()) {
            transformedGraph.addVertex(new DistinctGraphObject<V, V, E>(vertex, graph));
        }
        for (Object edge : graph.edgeSet()) {
            transformedGraph.addEdge(new DistinctGraphObject<V, V, E>(graph.getEdgeSource(edge), graph), new DistinctGraphObject<V, V, E>(graph.getEdgeTarget(edge), graph), new DistinctGraphObject<Object, V, E>(edge, graph));
        }
        return transformedGraph;
    }

    private static class DistinctGraphObject<T, V, E> {
        private Pair<T, Graph<V, E>> pair;

        private DistinctGraphObject(T object, Graph<V, E> graph) {
            this.pair = Pair.of(object, graph);
        }

        public T getObject() {
            return this.pair.getFirst();
        }

        public Graph<V, E> getGraph() {
            return this.pair.getSecond();
        }

        public String toString() {
            return this.pair.toString();
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof DistinctGraphObject)) {
                return false;
            }
            DistinctGraphObject other = (DistinctGraphObject)o;
            return Objects.equals(this.getObject(), other.getObject()) && this.getGraph() == other.getGraph();
        }

        public int hashCode() {
            return Objects.hash(this.getObject(), System.identityHashCode(this.getGraph()));
        }

        public static <T, V, E> DistinctGraphObject<T, V, E> of(T object, Graph<V, E> graph) {
            return new DistinctGraphObject<T, V, E>(object, graph);
        }
    }
}

