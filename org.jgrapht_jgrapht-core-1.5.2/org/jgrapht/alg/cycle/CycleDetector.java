/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.cycle;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.GraphTests;
import org.jgrapht.alg.connectivity.KosarajuStrongConnectivityInspector;
import org.jgrapht.traverse.DepthFirstIterator;

public class CycleDetector<V, E> {
    private Graph<V, E> graph;

    public CycleDetector(Graph<V, E> graph) {
        this.graph = GraphTests.requireDirected(graph);
    }

    public boolean detectCycles() {
        try {
            this.execute(null, null);
        }
        catch (CycleDetectedException ex) {
            return true;
        }
        return false;
    }

    public boolean detectCyclesContainingVertex(V v) {
        try {
            this.execute(null, v);
        }
        catch (CycleDetectedException ex) {
            return true;
        }
        return false;
    }

    public Set<V> findCycles() {
        KosarajuStrongConnectivityInspector<V, E> inspector = new KosarajuStrongConnectivityInspector<V, E>(this.graph);
        List components = inspector.stronglyConnectedSets();
        LinkedHashSet set = new LinkedHashSet();
        for (Set component : components) {
            if (component.size() > 1) {
                set.addAll(component);
                continue;
            }
            Object v = component.iterator().next();
            if (!this.graph.containsEdge(v, v)) continue;
            set.add(v);
        }
        return set;
    }

    public Set<V> findCyclesContainingVertex(V v) {
        LinkedHashSet set = new LinkedHashSet();
        this.execute(set, v);
        return set;
    }

    private void execute(Set<V> s, V v) {
        ProbeIterator<V, E> iter = new ProbeIterator<V, E>(this.graph, s, v);
        while (iter.hasNext()) {
            iter.next();
        }
    }

    private static class CycleDetectedException
    extends RuntimeException {
        private static final long serialVersionUID = 3834305137802950712L;

        private CycleDetectedException() {
        }
    }

    private static class ProbeIterator<V, E>
    extends DepthFirstIterator<V, E> {
        private List<V> path = new ArrayList<V>();
        private Set<V> cycleSet;
        private V root;

        ProbeIterator(Graph<V, E> graph, Set<V> cycleSet, V startVertex) {
            super(graph, startVertex);
            this.cycleSet = cycleSet;
            this.root = startVertex;
        }

        /*
         * Enabled force condition propagation
         * Lifted jumps to return sites
         */
        @Override
        protected void encounterVertexAgain(V vertex, E edge) {
            int i;
            super.encounterVertexAgain(vertex, edge);
            if (this.root != null) {
                if (vertex.equals(this.root)) {
                    i = 0;
                } else {
                    if (this.cycleSet == null || !this.cycleSet.contains(vertex)) return;
                    i = 0;
                }
            } else {
                i = this.path.indexOf(vertex);
            }
            if (i <= -1) return;
            if (this.cycleSet == null) {
                throw new CycleDetectedException();
            }
            while (i < this.path.size()) {
                this.cycleSet.add(this.path.get(i));
                ++i;
            }
        }

        @Override
        protected V provideNextVertex() {
            Object v = super.provideNextVertex();
            for (int i = this.path.size() - 1; i >= 0 && !this.graph.containsEdge(this.path.get(i), v); --i) {
                this.path.remove(i);
            }
            this.path.add(v);
            return v;
        }
    }
}

