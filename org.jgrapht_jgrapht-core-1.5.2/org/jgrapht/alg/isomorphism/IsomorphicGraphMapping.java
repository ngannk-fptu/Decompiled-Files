/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.isomorphism;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import org.jgrapht.Graph;
import org.jgrapht.GraphMapping;
import org.jgrapht.alg.isomorphism.GraphOrdering;
import org.jgrapht.util.CollectionUtil;

public class IsomorphicGraphMapping<V, E>
implements GraphMapping<V, E> {
    public static final int NULL_NODE = -1;
    private final Map<V, V> forwardMapping;
    private final Map<V, V> backwardMapping;
    private final Graph<V, E> graph1;
    private final Graph<V, E> graph2;

    public IsomorphicGraphMapping(GraphOrdering<V, E> g1, GraphOrdering<V, E> g2, int[] core1, int[] core2) {
        int uNumber;
        int vNumber;
        this.graph1 = g1.getGraph();
        this.graph2 = g2.getGraph();
        this.forwardMapping = CollectionUtil.newHashMapWithExpectedSize(this.graph1.vertexSet().size());
        this.backwardMapping = CollectionUtil.newHashMapWithExpectedSize(this.graph1.vertexSet().size());
        for (V v : this.graph1.vertexSet()) {
            vNumber = g1.getVertexNumber(v);
            uNumber = core1[vNumber];
            if (uNumber == -1) continue;
            this.forwardMapping.put(v, g2.getVertex(uNumber));
        }
        for (V v : this.graph2.vertexSet()) {
            vNumber = g2.getVertexNumber(v);
            uNumber = core2[vNumber];
            if (uNumber == -1) continue;
            this.backwardMapping.put(v, g1.getVertex(uNumber));
        }
    }

    public IsomorphicGraphMapping(Map<V, V> forwardMapping, Map<V, V> backwardMapping, Graph<V, E> graph1, Graph<V, E> graph2) {
        this.forwardMapping = Objects.requireNonNull(forwardMapping);
        this.backwardMapping = Objects.requireNonNull(backwardMapping);
        this.graph1 = Objects.requireNonNull(graph1);
        this.graph2 = Objects.requireNonNull(graph2);
    }

    @Override
    public V getVertexCorrespondence(V v, boolean forward) {
        if (forward) {
            return this.forwardMapping.get(v);
        }
        return this.backwardMapping.get(v);
    }

    @Override
    public E getEdgeCorrespondence(E e, boolean forward) {
        Graph<V, E> toGraph;
        Graph<V, E> fromGraph;
        if (forward) {
            fromGraph = this.graph1;
            toGraph = this.graph2;
        } else {
            fromGraph = this.graph2;
            toGraph = this.graph1;
        }
        V u = fromGraph.getEdgeSource(e);
        V v = fromGraph.getEdgeTarget(e);
        V uu = this.getVertexCorrespondence(u, forward);
        if (uu == null) {
            return null;
        }
        V vv = this.getVertexCorrespondence(v, forward);
        if (vv == null) {
            return null;
        }
        return toGraph.getEdge(uu, vv);
    }

    public Map<V, V> getForwardMapping() {
        return Collections.unmodifiableMap(this.forwardMapping);
    }

    public Map<V, V> getBackwardMapping() {
        return Collections.unmodifiableMap(this.backwardMapping);
    }

    public Set<V> getMappingDomain() {
        return Collections.unmodifiableSet(this.forwardMapping.keySet());
    }

    public Set<V> getMappingRange() {
        return Collections.unmodifiableSet(this.backwardMapping.keySet());
    }

    public boolean hasVertexCorrespondence(V v) {
        return this.getVertexCorrespondence(v, true) != null;
    }

    public boolean hasEdgeCorrespondence(E e) {
        return this.getEdgeCorrespondence(e, true) != null;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        IsomorphicGraphMapping that = (IsomorphicGraphMapping)o;
        return Objects.equals(this.forwardMapping, that.forwardMapping) && Objects.equals(this.backwardMapping, that.backwardMapping) && this.graph1 == that.graph1 && this.graph2 == that.graph2;
    }

    public int hashCode() {
        return Objects.hash(this.forwardMapping, this.backwardMapping, System.identityHashCode(this.graph1), System.identityHashCode(this.graph2));
    }

    public String toString() {
        StringBuilder str = new StringBuilder("[");
        Set<V> vertexSet = this.graph1.vertexSet();
        TreeMap<String, V> vertexMap = new TreeMap<String, V>();
        for (V v : vertexSet) {
            vertexMap.put(v.toString(), v);
        }
        int i = 0;
        for (Map.Entry entry : vertexMap.entrySet()) {
            Object u = this.getVertexCorrespondence(entry.getValue(), true);
            str.append(i++ == 0 ? "" : " ").append((String)entry.getKey()).append("=").append((Object)(u == null ? "~~" : u));
        }
        return str + "]";
    }

    public boolean isValidIsomorphism() {
        V v;
        V u;
        Object e;
        for (V v2 : this.graph1.vertexSet()) {
            if (this.forwardMapping.containsKey(v2) && this.graph2.containsVertex(this.forwardMapping.get(v2))) continue;
            return false;
        }
        for (V v2 : this.graph2.vertexSet()) {
            if (this.backwardMapping.containsKey(v2) && this.graph1.containsVertex(this.backwardMapping.get(v2))) continue;
            return false;
        }
        for (Object edge : this.graph1.edgeSet()) {
            e = this.getEdgeCorrespondence(edge, true);
            u = this.graph1.getEdgeSource(e);
            if (this.graph2.containsEdge(u, v = this.graph1.getEdgeTarget(e))) continue;
            return false;
        }
        for (Object edge : this.graph2.edgeSet()) {
            e = this.getEdgeCorrespondence(edge, false);
            u = this.graph2.getEdgeSource(e);
            if (this.graph1.containsEdge(u, v = this.graph2.getEdgeTarget(e))) continue;
            return false;
        }
        return true;
    }

    public boolean isEqualMapping(GraphMapping<V, E> rel) {
        for (V v : this.graph2.vertexSet()) {
            if (this.getVertexCorrespondence(v, false).equals(rel.getVertexCorrespondence(v, false))) continue;
            return false;
        }
        return true;
    }

    public IsomorphicGraphMapping<V, E> compose(IsomorphicGraphMapping<V, E> otherMapping) {
        HashMap<V, V> fMap = CollectionUtil.newHashMapWithExpectedSize(this.forwardMapping.size());
        HashMap<V, V> bMap = CollectionUtil.newHashMapWithExpectedSize(this.forwardMapping.size());
        for (V v : this.graph1.vertexSet()) {
            V u = otherMapping.getVertexCorrespondence(this.forwardMapping.get(v), true);
            fMap.put(v, u);
            bMap.put(u, v);
        }
        return new IsomorphicGraphMapping(fMap, bMap, this.graph1, otherMapping.graph2);
    }

    public static <V, E> IsomorphicGraphMapping<V, E> identity(Graph<V, E> graph) {
        HashMap<V, V> fMap = CollectionUtil.newHashMapWithExpectedSize(graph.vertexSet().size());
        HashMap<V, V> bMap = CollectionUtil.newHashMapWithExpectedSize(graph.vertexSet().size());
        for (V v : graph.vertexSet()) {
            fMap.put(v, v);
            bMap.put(v, v);
        }
        return new IsomorphicGraphMapping(fMap, bMap, graph, graph);
    }
}

