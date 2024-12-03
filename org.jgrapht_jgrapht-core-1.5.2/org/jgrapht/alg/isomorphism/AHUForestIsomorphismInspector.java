/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.isomorphism;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.GraphMapping;
import org.jgrapht.GraphTests;
import org.jgrapht.alg.isomorphism.AHURootedTreeIsomorphismInspector;
import org.jgrapht.alg.isomorphism.IsomorphicGraphMapping;
import org.jgrapht.alg.isomorphism.IsomorphismInspector;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.graph.AsGraphUnion;
import org.jgrapht.graph.builder.GraphTypeBuilder;

public class AHUForestIsomorphismInspector<V, E>
implements IsomorphismInspector<V, E> {
    private final Graph<V, E> forest1;
    private final Graph<V, E> forest2;
    private final Set<V> roots1;
    private final Set<V> roots2;
    private boolean computed = false;
    private IsomorphicGraphMapping<V, E> isomorphicMapping;

    public AHUForestIsomorphismInspector(Graph<V, E> forest1, Set<V> roots1, Graph<V, E> forest2, Set<V> roots2) {
        this.validateForest(forest1, roots1);
        this.forest1 = forest1;
        this.roots1 = roots1;
        this.validateForest(forest2, roots2);
        this.forest2 = forest2;
        this.roots2 = roots2;
    }

    private void validateForest(Graph<V, E> forest, Set<V> roots) {
        assert (GraphTests.isSimple(forest));
        Objects.requireNonNull(forest, "input forest cannot be null");
        Objects.requireNonNull(roots, "set of roots cannot be null");
        if (forest.vertexSet().isEmpty()) {
            throw new IllegalArgumentException("input forest cannot be empty");
        }
        if (roots.isEmpty()) {
            throw new IllegalArgumentException("set of roots cannot be empty");
        }
        if (!forest.vertexSet().containsAll(roots)) {
            throw new IllegalArgumentException("root not contained in forest");
        }
    }

    @Override
    public Iterator<GraphMapping<V, E>> getMappings() {
        IsomorphicGraphMapping<V, E> iterMapping = this.getMapping();
        if (iterMapping == null) {
            return Collections.emptyIterator();
        }
        return Collections.singletonList(iterMapping).iterator();
    }

    @Override
    public boolean isomorphismExists() {
        return this.getMapping() != null;
    }

    private Pair<V, Graph<V, E>> createSingleRootGraph(Graph<V, E> forest, Set<V> roots) {
        Graph<V, E> freshForest = GraphTypeBuilder.forGraph(forest).weighted(false).buildGraph();
        roots.forEach(freshForest::addVertex);
        V freshVertex = freshForest.addVertex();
        for (V root : roots) {
            freshForest.addEdge(freshVertex, root);
        }
        return Pair.of(freshVertex, new AsGraphUnion<V, E>(freshForest, forest));
    }

    public IsomorphicGraphMapping<V, E> getMapping() {
        if (this.computed) {
            return this.isomorphicMapping;
        }
        if (this.roots1.size() == 1 && this.roots2.size() == 1) {
            V root1 = this.roots1.iterator().next();
            V root2 = this.roots2.iterator().next();
            this.isomorphicMapping = new AHURootedTreeIsomorphismInspector<V, E>(this.forest1, root1, this.forest2, root2).getMapping();
        } else {
            Pair<V, Graph<V, E>> pair1 = this.createSingleRootGraph(this.forest1, this.roots1);
            Pair<V, Graph<V, E>> pair2 = this.createSingleRootGraph(this.forest2, this.roots2);
            V fresh1 = pair1.getFirst();
            Graph<V, E> freshForest1 = pair1.getSecond();
            V fresh2 = pair2.getFirst();
            Graph<V, E> freshForest2 = pair2.getSecond();
            IsomorphicGraphMapping<V, E> mapping = new AHURootedTreeIsomorphismInspector<V, E>(freshForest1, fresh1, freshForest2, fresh2).getMapping();
            if (mapping != null) {
                HashMap<V, V> newForwardMapping = new HashMap<V, V>(mapping.getForwardMapping());
                HashMap<V, V> newBackwardMapping = new HashMap<V, V>(mapping.getBackwardMapping());
                newForwardMapping.remove(fresh1);
                newBackwardMapping.remove(fresh2);
                this.isomorphicMapping = new IsomorphicGraphMapping<V, E>(newForwardMapping, newBackwardMapping, this.forest1, this.forest2);
            }
        }
        this.computed = true;
        return this.isomorphicMapping;
    }
}

