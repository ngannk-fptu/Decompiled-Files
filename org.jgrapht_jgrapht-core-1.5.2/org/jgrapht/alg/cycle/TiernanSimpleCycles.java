/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.cycle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import org.jgrapht.Graph;
import org.jgrapht.GraphTests;
import org.jgrapht.alg.cycle.DirectedSimpleCycles;

public class TiernanSimpleCycles<V, E>
implements DirectedSimpleCycles<V, E> {
    private Graph<V, E> graph;

    public TiernanSimpleCycles() {
    }

    public TiernanSimpleCycles(Graph<V, E> graph) {
        this.graph = GraphTests.requireDirected(graph, "Graph must be directed");
    }

    public Graph<V, E> getGraph() {
        return this.graph;
    }

    public void setGraph(Graph<V, E> graph) {
        this.graph = GraphTests.requireDirected(graph, "Graph must be directed");
    }

    /*
     * Unable to fully structure code
     * Could not resolve type clashes
     */
    @Override
    public void findSimpleCycles(Consumer<List<V>> consumer) {
        if (this.graph == null) {
            throw new IllegalArgumentException("Null graph.");
        }
        indices = new HashMap<V, Integer>();
        path = new ArrayList<V>();
        pathSet = new HashSet<V>();
        blocked = new HashMap<V, HashSet<E>>();
        index = 0;
        for (V v : this.graph.vertexSet()) {
            blocked.put(v, new HashSet<E>());
            indices.put(v, index++);
        }
        vertexIterator = this.graph.vertexSet().iterator();
        if (!vertexIterator.hasNext()) {
            return;
        }
        endOfPath /* !! */  = vertexIterator.next();
        path.add(endOfPath /* !! */ );
        pathSet.add(endOfPath /* !! */ );
        block1: while (true) {
            extensionFound = false;
            for (E e : this.graph.outgoingEdgesOf(endOfPath /* !! */ )) {
                n = this.graph.getEdgeTarget(e);
                cmp = ((Integer)indices.get(n)).compareTo((Integer)indices.get(path.get(0)));
                if (cmp <= 0 || pathSet.contains(n) || ((Set)blocked.get(endOfPath /* !! */ )).contains(n)) continue;
                path.add(n);
                pathSet.add(n);
                endOfPath /* !! */  = n;
                extensionFound = true;
                break;
            }
            if (extensionFound) continue;
            startOfPath = path.get(0);
            if (this.graph.containsEdge(endOfPath /* !! */ , startOfPath)) {
                cycle = new ArrayList<E>(path);
                consumer.accept(cycle);
            }
            if (path.size() > 1) {
                ((Set)blocked.get(endOfPath /* !! */ )).clear();
                endIndex = path.size() - 1;
                path.remove(endIndex);
                pathSet.remove(endOfPath /* !! */ );
                temp = endOfPath /* !! */ ;
                endOfPath /* !! */  = path.get(--endIndex);
                ((Set)blocked.get(endOfPath /* !! */ )).add(temp);
                continue;
            }
            if (!vertexIterator.hasNext()) break;
            path.clear();
            pathSet.clear();
            endOfPath /* !! */  = vertexIterator.next();
            path.add(endOfPath /* !! */ );
            pathSet.add(endOfPath /* !! */ );
            var13_13 = blocked.keySet().iterator();
            while (true) {
                if (var13_13.hasNext()) ** break;
                continue block1;
                vt = var13_13.next();
                ((Set)blocked.get(vt)).clear();
            }
            break;
        }
    }
}

