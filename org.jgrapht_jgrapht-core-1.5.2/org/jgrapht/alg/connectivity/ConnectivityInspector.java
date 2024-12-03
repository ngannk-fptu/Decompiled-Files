/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.connectivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.event.ConnectedComponentTraversalEvent;
import org.jgrapht.event.GraphEdgeChangeEvent;
import org.jgrapht.event.GraphListener;
import org.jgrapht.event.GraphVertexChangeEvent;
import org.jgrapht.event.TraversalListenerAdapter;
import org.jgrapht.event.VertexTraversalEvent;
import org.jgrapht.graph.AsUndirectedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.util.CollectionUtil;

public class ConnectivityInspector<V, E>
implements GraphListener<V, E> {
    private List<Set<V>> connectedSets;
    private Map<V, Set<V>> vertexToConnectedSet;
    private Graph<V, E> graph;

    public ConnectivityInspector(Graph<V, E> g) {
        this.init();
        this.graph = Objects.requireNonNull(g);
        if (g.getType().isDirected()) {
            this.graph = new AsUndirectedGraph<V, E>(g);
        }
    }

    public boolean isConnected() {
        return this.lazyFindConnectedSets().size() == 1;
    }

    public Set<V> connectedSetOf(V vertex) {
        Set<V> connectedSet = this.vertexToConnectedSet.get(vertex);
        if (connectedSet == null) {
            connectedSet = new HashSet<V>();
            BreadthFirstIterator<V, E> i = new BreadthFirstIterator<V, E>(this.graph, vertex);
            while (i.hasNext()) {
                connectedSet.add(i.next());
            }
            this.vertexToConnectedSet.put((Set<V>)vertex, (Set<Set<V>>)connectedSet);
        }
        return connectedSet;
    }

    public List<Set<V>> connectedSets() {
        return this.lazyFindConnectedSets();
    }

    @Override
    public void edgeAdded(GraphEdgeChangeEvent<V, E> e) {
        Set<V> targetSet;
        V source = e.getEdgeSource();
        V target = e.getEdgeTarget();
        Set<V> sourceSet = this.connectedSetOf(source);
        if (sourceSet != (targetSet = this.connectedSetOf(target))) {
            HashSet<V> merge = CollectionUtil.newHashSetWithExpectedSize(sourceSet.size() + targetSet.size());
            merge.addAll(sourceSet);
            merge.addAll(targetSet);
            this.connectedSets.remove(sourceSet);
            this.connectedSets.remove(targetSet);
            this.connectedSets.add(merge);
            for (Object v : merge) {
                this.vertexToConnectedSet.put((HashSet<V>)v, (Set<HashSet<V>>)merge);
            }
        }
    }

    @Override
    public void edgeRemoved(GraphEdgeChangeEvent<V, E> e) {
        this.init();
    }

    public boolean pathExists(V sourceVertex, V targetVertex) {
        return this.connectedSetOf(sourceVertex).contains(targetVertex);
    }

    @Override
    public void vertexAdded(GraphVertexChangeEvent<V> e) {
        HashSet<V> component = new HashSet<V>();
        component.add(e.getVertex());
        this.connectedSets.add(component);
        this.vertexToConnectedSet.put(e.getVertex(), component);
    }

    @Override
    public void vertexRemoved(GraphVertexChangeEvent<V> e) {
        this.init();
    }

    private void init() {
        this.connectedSets = null;
        this.vertexToConnectedSet = new HashMap<V, Set<V>>();
    }

    private List<Set<V>> lazyFindConnectedSets() {
        if (this.connectedSets == null) {
            this.connectedSets = new ArrayList<Set<V>>();
            Set<V> vertexSet = this.graph.vertexSet();
            if (!vertexSet.isEmpty()) {
                BreadthFirstIterator<V, E> i = new BreadthFirstIterator<V, E>(this.graph);
                i.addTraversalListener(new MyTraversalListener());
                while (i.hasNext()) {
                    i.next();
                }
            }
        }
        return this.connectedSets;
    }

    private class MyTraversalListener
    extends TraversalListenerAdapter<V, E> {
        private Set<V> currentConnectedSet;

        private MyTraversalListener() {
        }

        @Override
        public void connectedComponentFinished(ConnectedComponentTraversalEvent e) {
            ConnectivityInspector.this.connectedSets.add(this.currentConnectedSet);
        }

        @Override
        public void connectedComponentStarted(ConnectedComponentTraversalEvent e) {
            this.currentConnectedSet = new HashSet();
        }

        @Override
        public void vertexTraversed(VertexTraversalEvent<V> e) {
            Object v = e.getVertex();
            this.currentConnectedSet.add(v);
            ConnectivityInspector.this.vertexToConnectedSet.put((Set)v, (Set)this.currentConnectedSet);
        }
    }
}

