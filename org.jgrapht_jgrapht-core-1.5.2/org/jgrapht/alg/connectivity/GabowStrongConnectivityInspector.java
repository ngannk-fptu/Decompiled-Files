/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.connectivity;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.alg.connectivity.AbstractStrongConnectivityInspector;
import org.jgrapht.util.CollectionUtil;

public class GabowStrongConnectivityInspector<V, E>
extends AbstractStrongConnectivityInspector<V, E> {
    private Deque<VertexNumber<V>> stackS = new ArrayDeque<VertexNumber<V>>();
    private Deque<VertexNumber<V>> stackB = new ArrayDeque<VertexNumber<V>>();
    private Map<V, VertexNumber<V>> vertexToVertexNumber;
    private int c;

    public GabowStrongConnectivityInspector(Graph<V, E> graph) {
        super(graph);
    }

    @Override
    public List<Set<V>> stronglyConnectedSets() {
        if (this.stronglyConnectedSets == null) {
            this.stronglyConnectedSets = new ArrayList();
            this.createVertexNumber();
            for (VertexNumber<V> data : this.vertexToVertexNumber.values()) {
                if (data.number != 0) continue;
                this.dfsVisit(data);
            }
            this.vertexToVertexNumber = null;
            this.stackS = null;
            this.stackB = null;
        }
        return this.stronglyConnectedSets;
    }

    private void createVertexNumber() {
        this.c = this.graph.vertexSet().size();
        this.vertexToVertexNumber = CollectionUtil.newHashMapWithExpectedSize(this.c);
        for (Object vertex : this.graph.vertexSet()) {
            this.vertexToVertexNumber.put(vertex, new VertexNumber(vertex));
        }
        this.stackS = new ArrayDeque<VertexNumber<V>>(this.c);
        this.stackB = new ArrayDeque<VertexNumber<V>>(this.c);
    }

    private void dfsVisit(VertexNumber<V> v) {
        this.stackS.push(v);
        v.number = this.stackS.size();
        this.stackB.push(v);
        for (Object edge : this.graph.outgoingEdgesOf(v.vertex)) {
            VertexNumber<V> w = this.vertexToVertexNumber.get(this.graph.getEdgeTarget(edge));
            if (w.number == 0) {
                this.dfsVisit(w);
                continue;
            }
            while (w.number < this.stackB.peek().number) {
                this.stackB.pop();
            }
        }
        if (v == this.stackB.peek()) {
            this.stackB.pop();
            ++this.c;
            Set<V> sccVertices = this.createSCCVertexSetAndNumberVertices(v);
            this.stronglyConnectedSets.add(sccVertices);
        }
    }

    private Set<V> createSCCVertexSetAndNumberVertices(VertexNumber<V> v) {
        Set<Object> scc;
        int sccSize = this.stackS.size() - v.number + 1;
        if (sccSize == 1) {
            VertexNumber<V> r = this.stackS.pop();
            scc = Collections.singleton(r.vertex);
            r.number = this.c;
        } else {
            scc = CollectionUtil.newHashSetWithExpectedSize(sccSize);
            for (int i = 0; i < sccSize; ++i) {
                VertexNumber<V> r = this.stackS.pop();
                scc.add(r.vertex);
                r.number = this.c;
            }
        }
        return scc;
    }

    private static final class VertexNumber<V> {
        private final V vertex;
        private int number = 0;

        private VertexNumber(V vertex) {
            this.vertex = vertex;
        }
    }
}

