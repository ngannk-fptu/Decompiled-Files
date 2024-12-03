/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.isomorphism;

import java.lang.reflect.Array;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.jgrapht.Graph;
import org.jgrapht.GraphMapping;
import org.jgrapht.GraphTests;
import org.jgrapht.Graphs;
import org.jgrapht.alg.isomorphism.IsomorphicGraphMapping;
import org.jgrapht.alg.isomorphism.IsomorphismInspector;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.util.CollectionUtil;
import org.jgrapht.util.RadixSort;

public class AHURootedTreeIsomorphismInspector<V, E>
implements IsomorphismInspector<V, E> {
    private final Graph<V, E> tree1;
    private final Graph<V, E> tree2;
    private V root1;
    private V root2;
    private Map<V, V> forwardMapping;
    private Map<V, V> backwardMapping;

    public AHURootedTreeIsomorphismInspector(Graph<V, E> tree1, V root1, Graph<V, E> tree2, V root2) {
        this.validateTree(tree1, root1);
        this.tree1 = tree1;
        this.root1 = root1;
        this.validateTree(tree2, root2);
        this.tree2 = tree2;
        this.root2 = root2;
    }

    private void validateTree(Graph<V, E> tree, V root) {
        assert (GraphTests.isSimple(tree));
        Objects.requireNonNull(tree, "input forest cannot be null");
        Objects.requireNonNull(root, "root cannot be null");
        if (tree.vertexSet().isEmpty()) {
            throw new IllegalArgumentException("tree cannot be empty");
        }
        if (!tree.containsVertex(root)) {
            throw new IllegalArgumentException("root not contained in forest");
        }
    }

    private void bfs(Graph<V, E> graph, V root, List<List<V>> levels) {
        BreadthFirstIterator bfs = new BreadthFirstIterator(graph, root);
        while (bfs.hasNext()) {
            Object u = bfs.next();
            if (levels.size() < bfs.getDepth(u) + 1) {
                levels.add(new ArrayList());
            }
            levels.get(bfs.getDepth(u)).add(u);
        }
    }

    private List<List<V>> computeLevels(Graph<V, E> graph, V root) {
        ArrayList<List<V>> levels = new ArrayList<List<V>>();
        this.bfs(graph, root, levels);
        return levels;
    }

    private void matchVerticesWithSameLabel(V root1, V root2, Map<V, Integer>[] canonicalName) {
        ArrayDeque<Pair<Object, V>> queue = new ArrayDeque<Pair<Object, V>>();
        queue.add(Pair.of(root1, root2));
        while (!queue.isEmpty()) {
            V next;
            Pair pair = (Pair)queue.poll();
            Object u = pair.getFirst();
            Object v = pair.getSecond();
            this.forwardMapping.put(u, v);
            this.backwardMapping.put(v, u);
            HashMap<Integer, List> labelList = CollectionUtil.newHashMapWithExpectedSize(this.tree1.degreeOf(u));
            for (E edge : this.tree1.outgoingEdgesOf(u)) {
                next = Graphs.getOppositeVertex(this.tree1, edge, u);
                if (this.forwardMapping.containsKey(next)) continue;
                labelList.computeIfAbsent(canonicalName[0].get(next), x -> new ArrayList()).add(next);
            }
            for (E edge : this.tree2.outgoingEdgesOf(v)) {
                next = Graphs.getOppositeVertex(this.tree2, edge, v);
                if (this.backwardMapping.containsKey(next)) continue;
                List list = (List)labelList.get(canonicalName[1].get(next));
                if (list == null || list.isEmpty()) {
                    this.forwardMapping.clear();
                    this.backwardMapping.clear();
                    return;
                }
                Object pairedNext = list.remove(list.size() - 1);
                queue.add(Pair.of(pairedNext, next));
            }
        }
    }

    private boolean isomorphismExists(V root1, V root2) {
        if (this.forwardMapping != null) {
            return !this.forwardMapping.isEmpty();
        }
        this.forwardMapping = new HashMap<V, V>();
        this.backwardMapping = new HashMap<V, V>();
        Map[] canonicalName = (Map[])Array.newInstance(Map.class, 2);
        canonicalName[0] = CollectionUtil.newHashMapWithExpectedSize(this.tree1.vertexSet().size());
        canonicalName[1] = CollectionUtil.newHashMapWithExpectedSize(this.tree2.vertexSet().size());
        List<List<V>> nodesByLevel1 = this.computeLevels(this.tree1, root1);
        List<List<V>> nodesByLevel2 = this.computeLevels(this.tree2, root2);
        if (nodesByLevel1.size() != nodesByLevel2.size()) {
            return false;
        }
        int maxLevel = nodesByLevel1.size() - 1;
        HashMap<ArrayList<Integer>, Integer> canonicalNameToInt = new HashMap<ArrayList<Integer>, Integer>();
        int freshName = 0;
        for (int lvl = maxLevel; lvl >= 0; --lvl) {
            List[] level = (List[])Array.newInstance(List.class, 2);
            level[0] = nodesByLevel1.get(lvl);
            level[1] = nodesByLevel2.get(lvl);
            if (level[0].size() != level[1].size()) {
                return false;
            }
            int n = level[0].size();
            for (int k = 0; k < 2; ++k) {
                Graph graph = k == 0 ? this.tree1 : this.tree2;
                for (int i = 0; i < n; ++i) {
                    Object u = level[k].get(i);
                    ArrayList<Integer> list = new ArrayList<Integer>();
                    for (E edge : graph.outgoingEdgesOf(u)) {
                        V v = Graphs.getOppositeVertex(graph, edge, u);
                        int name = canonicalName[k].getOrDefault(v, -1);
                        if (name == -1) continue;
                        list.add(name);
                    }
                    RadixSort.sort(list);
                    Integer intName = (Integer)canonicalNameToInt.get(list);
                    if (intName == null) {
                        canonicalNameToInt.put(list, freshName);
                        intName = freshName;
                        ++freshName;
                    }
                    canonicalName[k].put(u, intName);
                }
            }
        }
        this.matchVerticesWithSameLabel(root1, root2, canonicalName);
        if (this.forwardMapping.size() != this.tree1.vertexSet().size()) {
            this.forwardMapping.clear();
            this.backwardMapping.clear();
            return false;
        }
        return true;
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
        return this.isomorphismExists(this.root1, this.root2);
    }

    public IsomorphicGraphMapping<V, E> getMapping() {
        if (this.isomorphismExists()) {
            return new IsomorphicGraphMapping<V, E>(this.forwardMapping, this.backwardMapping, this.tree1, this.tree2);
        }
        return null;
    }
}

