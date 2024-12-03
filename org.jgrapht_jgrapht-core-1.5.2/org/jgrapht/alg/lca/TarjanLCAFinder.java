/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.lca;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.interfaces.LowestCommonAncestorAlgorithm;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.alg.util.UnionFind;

public class TarjanLCAFinder<V, E>
implements LowestCommonAncestorAlgorithm<V> {
    private Graph<V, E> graph;
    private Set<V> roots;
    private UnionFind<V> unionFind;
    private Map<V, V> ancestors;
    private Set<V> blackNodes;
    private HashMap<V, Set<Integer>> queryOccurs;
    private List<V> lowestCommonAncestors;
    private List<Pair<V, V>> queries;

    public TarjanLCAFinder(Graph<V, E> graph, V root) {
        this(graph, Collections.singleton(Objects.requireNonNull(root, "root cannot be null")));
    }

    public TarjanLCAFinder(Graph<V, E> graph, Set<V> roots) {
        this.graph = Objects.requireNonNull(graph, "graph cannot be null");
        this.roots = Objects.requireNonNull(roots, "roots cannot be null");
        if (this.roots.isEmpty()) {
            throw new IllegalArgumentException("roots cannot be empty");
        }
        if (!graph.vertexSet().containsAll(roots)) {
            throw new IllegalArgumentException("at least one root is not a valid vertex");
        }
    }

    @Override
    public V getLCA(V a, V b) {
        return this.getBatchLCA(Collections.singletonList(Pair.of(a, b))).get(0);
    }

    @Override
    public List<V> getBatchLCA(List<Pair<V, V>> queries) {
        return this.computeTarjan(queries);
    }

    private void initialize() {
        this.unionFind = new UnionFind(Collections.emptySet());
        this.ancestors = new HashMap<V, V>();
        this.blackNodes = new HashSet<V>();
    }

    private void clear() {
        this.unionFind = null;
        this.ancestors = null;
        this.blackNodes = null;
        this.queryOccurs = null;
        this.queries = null;
        this.lowestCommonAncestors = null;
    }

    private List<V> computeTarjan(List<Pair<V, V>> queries) {
        this.initialize();
        this.queries = queries;
        this.lowestCommonAncestors = new ArrayList<V>(queries.size());
        this.queryOccurs = new HashMap();
        for (int i = 0; i < queries.size(); ++i) {
            V a = this.queries.get(i).getFirst();
            V b = this.queries.get(i).getSecond();
            if (!this.graph.containsVertex(a)) {
                throw new IllegalArgumentException("invalid vertex: " + a);
            }
            if (!this.graph.containsVertex(b)) {
                throw new IllegalArgumentException("invalid vertex: " + b);
            }
            if (a.equals(b)) {
                this.lowestCommonAncestors.add(a);
                continue;
            }
            this.queryOccurs.computeIfAbsent((Set)a, (Function<Set, Set<Integer>>)((Function<Object, Set>)x -> new HashSet())).add(i);
            this.queryOccurs.computeIfAbsent((Set)b, (Function<Set, Set<Integer>>)((Function<Object, Set>)x -> new HashSet())).add(i);
            this.lowestCommonAncestors.add(null);
        }
        HashSet visited = new HashSet();
        for (Object root : this.roots) {
            if (visited.contains(root)) {
                throw new IllegalArgumentException("multiple roots in the same tree");
            }
            this.blackNodes.clear();
            this.computeTarjanOLCA(root, null, visited);
        }
        List<V> tmpRef = this.lowestCommonAncestors;
        this.clear();
        return tmpRef;
    }

    private void computeTarjanOLCA(V u, V p, Set<V> visited) {
        visited.add(u);
        this.unionFind.addElement(u);
        this.ancestors.put(u, u);
        for (E edge : this.graph.outgoingEdgesOf(u)) {
            V v = Graphs.getOppositeVertex(this.graph, edge, u);
            if (v.equals(p)) continue;
            this.computeTarjanOLCA(v, u, visited);
            this.unionFind.union(u, v);
            this.ancestors.put(this.unionFind.find(u), u);
        }
        this.blackNodes.add(u);
        Iterator<E> iterator = this.queryOccurs.computeIfAbsent((Set)u, (Function<Set, Set<Integer>>)((Function<Object, Set>)x -> new HashSet())).iterator();
        while (iterator.hasNext()) {
            int index = (Integer)iterator.next();
            Pair<V, V> query = this.queries.get(index);
            V v = query.getFirst().equals(u) ? query.getSecond() : query.getFirst();
            if (!this.blackNodes.contains(v)) continue;
            this.lowestCommonAncestors.set(index, this.ancestors.get(this.unionFind.find(v)));
        }
    }

    @Override
    public Set<V> getLCASet(V a, V b) {
        throw new UnsupportedOperationException();
    }
}

