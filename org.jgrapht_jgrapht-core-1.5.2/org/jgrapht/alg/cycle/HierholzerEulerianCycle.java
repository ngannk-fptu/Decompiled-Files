/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.cycle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.GraphTests;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.alg.connectivity.KosarajuStrongConnectivityInspector;
import org.jgrapht.alg.interfaces.EulerianCycleAlgorithm;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.graph.GraphWalk;
import org.jgrapht.util.TypeUtil;

public class HierholzerEulerianCycle<V, E>
implements EulerianCycleAlgorithm<V, E> {
    protected Graph<V, E> g;
    protected boolean isDirected;
    protected VertexNode verticesHead;
    protected EdgeNode eulerianHead;
    protected V startVertex;

    public boolean isEulerian(Graph<V, E> graph) {
        GraphTests.requireDirectedOrUndirected(graph);
        if (graph.vertexSet().isEmpty()) {
            return false;
        }
        if (graph.edgeSet().isEmpty()) {
            return true;
        }
        if (graph.getType().isUndirected()) {
            for (Object v : graph.vertexSet()) {
                if (graph.degreeOf(v) % 2 != 1) continue;
                return false;
            }
            boolean foundComponentWithEdges = false;
            block1: for (Set set : new ConnectivityInspector(graph).connectedSets()) {
                for (Object v : set) {
                    if (graph.degreeOf(v) <= 0) continue;
                    if (foundComponentWithEdges) {
                        return false;
                    }
                    foundComponentWithEdges = true;
                    continue block1;
                }
            }
            return true;
        }
        for (Object v : graph.vertexSet()) {
            if (graph.inDegreeOf(v) == graph.outDegreeOf(v)) continue;
            return false;
        }
        boolean foundComponentWithEdges = false;
        block4: for (Set set : new KosarajuStrongConnectivityInspector(graph).stronglyConnectedSets()) {
            for (Object v : set) {
                if (graph.inDegreeOf(v) <= 0 && graph.outDegreeOf(v) <= 0) continue;
                if (foundComponentWithEdges) {
                    return false;
                }
                foundComponentWithEdges = true;
                continue block4;
            }
        }
        return true;
    }

    @Override
    public GraphPath<V, E> getEulerianCycle(Graph<V, E> g) {
        if (!this.isEulerian(g)) {
            throw new IllegalArgumentException("Graph is not Eulerian");
        }
        if (g.vertexSet().isEmpty()) {
            throw new IllegalArgumentException("Null graph not permitted");
        }
        if (GraphTests.isEmpty(g)) {
            return GraphWalk.emptyWalk(g);
        }
        this.initialize(g);
        while (this.verticesHead != null) {
            EdgeNode whereToInsert = this.verticesHead.insertLocation;
            Pair<EdgeNode, EdgeNode> partialCycle = this.computePartialCycle();
            this.updateGraphAndInsertLocations(partialCycle, this.verticesHead);
            if (whereToInsert == null) {
                this.eulerianHead = partialCycle.getFirst();
                continue;
            }
            partialCycle.getSecond().next = whereToInsert.next;
            whereToInsert.next = partialCycle.getFirst();
        }
        GraphWalk<V, E> walk = this.buildWalk();
        this.cleanup();
        return walk;
    }

    protected void initialize(Graph<V, E> g) {
        this.g = g;
        this.isDirected = g.getType().isDirected();
        this.verticesHead = null;
        this.eulerianHead = null;
        this.startVertex = null;
        HashMap<V, VertexNode> vertices = new HashMap<V, VertexNode>();
        for (V v : g.vertexSet()) {
            if (g.outDegreeOf(v) <= 0) continue;
            VertexNode n = new VertexNode(null, v, this.verticesHead);
            if (this.verticesHead != null) {
                this.verticesHead.prev = n;
            }
            this.verticesHead = n;
            vertices.put(v, n);
        }
        for (Object e : g.edgeSet()) {
            VertexNode sNode = (VertexNode)vertices.get(g.getEdgeSource(e));
            VertexNode tNode = (VertexNode)vertices.get(g.getEdgeTarget(e));
            this.addEdge(sNode, tNode, e);
        }
    }

    protected void cleanup() {
        this.g = null;
        this.verticesHead = null;
        this.eulerianHead = null;
        this.startVertex = null;
    }

    protected Pair<EdgeNode, EdgeNode> computePartialCycle() {
        if (this.startVertex == null) {
            this.startVertex = this.verticesHead.v;
        }
        EdgeNode partialHead = null;
        EdgeNode partialTail = null;
        VertexNode v = this.verticesHead;
        do {
            EdgeNode e = v.adjEdgesHead;
            v = this.getOppositeVertex(v, e);
            this.unlink(e);
            if (partialTail == null) {
                partialHead = partialTail = e;
                continue;
            }
            partialTail = partialTail.next = e;
        } while (!v.equals(this.verticesHead));
        return Pair.of(partialHead, partialTail);
    }

    protected void updateGraphAndInsertLocations(Pair<EdgeNode, EdgeNode> partialCycle, VertexNode partialCycleSourceVertex) {
        EdgeNode e = partialCycle.getFirst();
        assert (e != null) : "Graph is not Eulerian";
        VertexNode v = this.getOppositeVertex(partialCycleSourceVertex, e);
        while (true) {
            if (v.adjEdgesHead != null) {
                v.insertLocation = e;
                this.moveToFront(v);
            } else {
                this.unlink(v);
            }
            e = e.next;
            if (e == null) break;
            v = this.getOppositeVertex(v, e);
        }
    }

    protected GraphWalk<V, E> buildWalk() {
        double totalWeight = 0.0;
        ArrayList result = new ArrayList();
        EdgeNode it = this.eulerianHead;
        while (it != null) {
            result.add(it.e);
            totalWeight += this.g.getEdgeWeight(it.e);
            it = it.next;
        }
        return new GraphWalk<V, E>(this.g, this.startVertex, this.startVertex, result, totalWeight);
    }

    protected void addEdge(VertexNode sNode, VertexNode tNode, E e) {
        EdgeNode sHead = sNode.adjEdgesHead;
        if (sHead == null) {
            sHead = new EdgeNode(sNode, tNode, null, e, null, null);
        } else {
            EdgeNode n;
            sHead.prev = n = new EdgeNode(sNode, tNode, null, e, null, sHead);
            sHead = n;
        }
        sNode.adjEdgesHead = sHead;
        if (!this.isDirected && !sNode.equals(tNode)) {
            EdgeNode tHead = tNode.adjEdgesHead;
            if (tHead == null) {
                tHead = new EdgeNode(tNode, sNode, null, e, sHead, null);
            } else {
                EdgeNode n;
                tHead.prev = n = new EdgeNode(tNode, sNode, null, e, sHead, tHead);
                tHead = n;
            }
            sHead.reverse = tHead;
            tNode.adjEdgesHead = tHead;
        }
    }

    protected void unlink(VertexNode vNode) {
        if (this.verticesHead == null) {
            return;
        }
        if (!this.verticesHead.equals(vNode) && vNode.prev == null && vNode.next == null) {
            return;
        }
        if (vNode.prev != null) {
            vNode.prev.next = vNode.next;
            if (vNode.next != null) {
                vNode.next.prev = vNode.prev;
            }
        } else {
            this.verticesHead = vNode.next;
            if (this.verticesHead != null) {
                this.verticesHead.prev = null;
            }
        }
        vNode.next = null;
        vNode.prev = null;
    }

    protected void moveToFront(VertexNode vNode) {
        if (vNode.prev != null) {
            vNode.prev.next = vNode.next;
            if (vNode.next != null) {
                vNode.next.prev = vNode.prev;
            }
            this.verticesHead.prev = vNode;
            vNode.next = this.verticesHead;
            vNode.prev = null;
            this.verticesHead = vNode;
        }
    }

    protected void unlink(EdgeNode eNode) {
        VertexNode vNode = eNode.sourceNode;
        if (eNode.prev != null) {
            eNode.prev.next = eNode.next;
            if (eNode.next != null) {
                eNode.next.prev = eNode.prev;
            }
        } else {
            if (eNode.next != null) {
                eNode.next.prev = null;
            }
            vNode.adjEdgesHead = eNode.next;
        }
        if (!this.isDirected && eNode.reverse != null) {
            EdgeNode revNode = eNode.reverse;
            VertexNode uNode = revNode.sourceNode;
            if (revNode.prev != null) {
                revNode.prev.next = revNode.next;
                if (revNode.next != null) {
                    revNode.next.prev = revNode.prev;
                }
            } else {
                if (revNode.next != null) {
                    revNode.next.prev = null;
                }
                uNode.adjEdgesHead = revNode.next;
            }
        }
        eNode.next = null;
        eNode.prev = null;
        eNode.reverse = null;
    }

    protected VertexNode getOppositeVertex(VertexNode v, EdgeNode e) {
        return v.equals(e.sourceNode) ? e.targetNode : e.sourceNode;
    }

    protected class VertexNode {
        public V v;
        public VertexNode prev;
        public VertexNode next;
        public EdgeNode insertLocation;
        public EdgeNode adjEdgesHead;

        public VertexNode(VertexNode prev, V v, VertexNode next) {
            this.prev = prev;
            this.v = v;
            this.next = next;
            this.adjEdgesHead = null;
            this.insertLocation = null;
        }

        public int hashCode() {
            int prime = 31;
            int result = 1;
            result = 31 * result + (this.v == null ? 0 : this.v.hashCode());
            return result;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            VertexNode other = (VertexNode)TypeUtil.uncheckedCast(obj);
            return Objects.equals(this.v, other.v);
        }

        public String toString() {
            return this.v.toString();
        }
    }

    protected class EdgeNode {
        public E e;
        public EdgeNode next;
        public EdgeNode prev;
        public EdgeNode reverse;
        public VertexNode sourceNode;
        public VertexNode targetNode;

        public EdgeNode(VertexNode sourceNode, VertexNode targetNode, EdgeNode prev, E e, EdgeNode reverse, EdgeNode next) {
            this.sourceNode = sourceNode;
            this.targetNode = targetNode;
            this.prev = prev;
            this.e = e;
            this.reverse = reverse;
            this.next = next;
        }

        public int hashCode() {
            int prime = 31;
            int result = 1;
            result = 31 * result + (this.e == null ? 0 : this.e.hashCode());
            return result;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            EdgeNode other = (EdgeNode)TypeUtil.uncheckedCast(obj);
            return Objects.equals(this.e, other.e);
        }

        public String toString() {
            return this.e.toString();
        }
    }
}

