/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.isomorphism;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import org.jgrapht.Graph;
import org.jgrapht.GraphMapping;
import org.jgrapht.GraphTests;
import org.jgrapht.alg.isomorphism.AHURootedTreeIsomorphismInspector;
import org.jgrapht.alg.isomorphism.IsomorphicGraphMapping;
import org.jgrapht.alg.isomorphism.IsomorphismInspector;
import org.jgrapht.alg.shortestpath.TreeMeasurer;

public class AHUUnrootedTreeIsomorphismInspector<V, E>
implements IsomorphismInspector<V, E> {
    private final Graph<V, E> tree1;
    private final Graph<V, E> tree2;
    private boolean computed;
    private AHURootedTreeIsomorphismInspector<V, E> ahuRootedTreeIsomorphismInspector;

    public AHUUnrootedTreeIsomorphismInspector(Graph<V, E> tree1, Graph<V, E> tree2) {
        this.validateTree(tree1);
        this.tree1 = tree1;
        this.validateTree(tree2);
        this.tree2 = tree2;
    }

    private void validateTree(Graph<V, E> tree) {
        GraphTests.requireUndirected(tree);
        assert (GraphTests.isSimple(tree));
        if (tree.vertexSet().isEmpty()) {
            throw new IllegalArgumentException("tree cannot be empty");
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
        if (this.computed) {
            if (this.ahuRootedTreeIsomorphismInspector != null) {
                return this.ahuRootedTreeIsomorphismInspector.isomorphismExists();
            }
            return false;
        }
        this.computed = true;
        TreeMeasurer<V, E> treeMeasurer1 = new TreeMeasurer<V, E>(this.tree1);
        ArrayList<V> centers1 = new ArrayList<V>(treeMeasurer1.getGraphCenter());
        TreeMeasurer<V, E> treeMeasurer2 = new TreeMeasurer<V, E>(this.tree2);
        ArrayList<V> centers2 = new ArrayList<V>(treeMeasurer2.getGraphCenter());
        if (centers1.size() == 1 && centers2.size() == 1) {
            this.ahuRootedTreeIsomorphismInspector = new AHURootedTreeIsomorphismInspector<V, E>(this.tree1, centers1.get(0), this.tree2, centers2.get(0));
        } else if (centers1.size() == 2 && centers2.size() == 2) {
            this.ahuRootedTreeIsomorphismInspector = new AHURootedTreeIsomorphismInspector<V, E>(this.tree1, centers1.get(0), this.tree2, centers2.get(0));
            if (!this.ahuRootedTreeIsomorphismInspector.isomorphismExists()) {
                this.ahuRootedTreeIsomorphismInspector = new AHURootedTreeIsomorphismInspector<V, E>(this.tree1, centers1.get(1), this.tree2, centers2.get(0));
            }
        } else {
            return false;
        }
        return this.ahuRootedTreeIsomorphismInspector.isomorphismExists();
    }

    public IsomorphicGraphMapping<V, E> getMapping() {
        if (this.isomorphismExists()) {
            return this.ahuRootedTreeIsomorphismInspector.getMapping();
        }
        return null;
    }
}

