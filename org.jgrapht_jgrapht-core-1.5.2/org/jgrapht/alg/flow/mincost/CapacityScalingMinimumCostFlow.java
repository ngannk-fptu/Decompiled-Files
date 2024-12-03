/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jheaps.AddressableHeap$Handle
 *  org.jheaps.tree.PairingHeap
 */
package org.jgrapht.alg.flow.mincost;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.alg.flow.mincost.MinimumCostFlowProblem;
import org.jgrapht.alg.interfaces.MinimumCostFlowAlgorithm;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.util.CollectionUtil;
import org.jheaps.AddressableHeap;
import org.jheaps.tree.PairingHeap;

public class CapacityScalingMinimumCostFlow<V, E>
implements MinimumCostFlowAlgorithm<V, E> {
    public static final int CAP_INF = 1000000000;
    public static final double COST_INF = 1.0E9;
    public static final int DEFAULT_SCALING_FACTOR = 8;
    private static final boolean DEBUG = false;
    private final int scalingFactor;
    private int n;
    private int m;
    private int counter = 1;
    private MinimumCostFlowProblem<V, E> problem;
    private MinimumCostFlowAlgorithm.MinimumCostFlow<E> minimumCostFlow;
    private Node[] nodes;
    private Arc[] arcs;
    private List<V> graphVertices;
    private List<E> graphEdges;

    public CapacityScalingMinimumCostFlow() {
        this(8);
    }

    public CapacityScalingMinimumCostFlow(int scalingFactor) {
        this.scalingFactor = scalingFactor;
        Node.nextID = 0;
    }

    @Override
    public Map<E, Double> getFlowMap() {
        return this.minimumCostFlow == null ? null : this.minimumCostFlow.getFlowMap();
    }

    @Override
    public V getFlowDirection(E edge) {
        return this.problem.getGraph().getEdgeTarget(edge);
    }

    @Override
    public MinimumCostFlowAlgorithm.MinimumCostFlow<E> getMinimumCostFlow(MinimumCostFlowProblem<V, E> minimumCostFlowProblem) {
        this.problem = Objects.requireNonNull(minimumCostFlowProblem);
        if (this.problem.getGraph().getType().isUndirected()) {
            throw new IllegalArgumentException("The algorithm doesn't support undirected flow networks");
        }
        this.n = this.problem.getGraph().vertexSet().size();
        this.m = this.problem.getGraph().edgeSet().size();
        this.calculateMinimumCostFlow();
        return this.minimumCostFlow;
    }

    public Map<V, Double> getDualSolution() {
        if (this.minimumCostFlow == null) {
            return null;
        }
        HashMap<V, Double> dualVariables = new HashMap<V, Double>();
        for (int i = 0; i < this.n; ++i) {
            dualVariables.put(this.graphVertices.get(i), this.nodes[i].potential);
        }
        return dualVariables;
    }

    private void calculateMinimumCostFlow() {
        this.init();
        if (this.scalingFactor > 1) {
            int delta;
            int u = this.getU();
            for (delta = this.scalingFactor; u >= delta; delta *= this.scalingFactor) {
            }
            delta /= this.scalingFactor;
            while (delta >= 1) {
                Pair<List<Node>, Set<Node>> pair = this.scale(delta);
                this.pushAllFlow(pair.getFirst(), pair.getSecond(), delta);
                delta /= this.scalingFactor;
            }
        } else {
            Pair<List<Node>, Set<Node>> pair = this.scale(1);
            this.pushAllFlow(pair.getFirst(), pair.getSecond(), 1);
        }
        this.minimumCostFlow = this.finish();
    }

    private void init() {
        int supplySum = 0;
        this.nodes = new Node[this.n + 1];
        this.nodes[this.n] = new Node(0);
        this.arcs = new Arc[this.m];
        this.graphEdges = new ArrayList(this.m);
        this.graphVertices = new ArrayList<V>(this.n);
        HashMap<V, Node> nodeMap = CollectionUtil.newHashMapWithExpectedSize(this.n);
        Graph<V, Object> graph = this.problem.getGraph();
        int i = 0;
        for (V vertex : graph.vertexSet()) {
            this.graphVertices.add(vertex);
            int supply = this.problem.getNodeSupply().apply(vertex);
            supplySum += supply;
            this.nodes[i] = new Node(supply);
            nodeMap.put(vertex, this.nodes[i]);
            this.nodes[i].addArcTo(this.nodes[this.n], 1000000000, 1.0E9);
            this.nodes[this.n].addArcTo(this.nodes[i], 1000000000, 1.0E9);
            ++i;
        }
        if (Math.abs(supplySum) > 0) {
            throw new IllegalArgumentException("Total node supply isn't equal to 0");
        }
        i = 0;
        for (Object edge : graph.edgeSet()) {
            this.graphEdges.add(edge);
            Node node = (Node)nodeMap.get(graph.getEdgeSource(edge));
            Node opposite = (Node)nodeMap.get(graph.getEdgeTarget(edge));
            int upperCap = this.problem.getArcCapacityUpperBounds().apply(edge);
            int lowerCap = this.problem.getArcCapacityLowerBounds().apply(edge);
            double cost = graph.getEdgeWeight(edge);
            if (upperCap < 0) {
                throw new IllegalArgumentException("Negative edge capacities are not allowed");
            }
            if (lowerCap > upperCap) {
                throw new IllegalArgumentException("Lower edge capacity must not exceed upper edge capacity");
            }
            if (lowerCap >= 1000000000) {
                throw new IllegalArgumentException("The problem is unbounded due to the infinite lower capacity");
            }
            if (upperCap >= 1000000000 && cost < 0.0) {
                throw new IllegalArgumentException("The algorithm doesn't support infinite capacity arcs with negative cost");
            }
            if (Math.abs(cost) >= 1.0E9) {
                throw new IllegalArgumentException("Specified flow network contains an edge of infinite cost");
            }
            if (node == opposite) {
                throw new IllegalArgumentException("Self-loops aren't allowed");
            }
            node.excess -= lowerCap;
            opposite.excess += lowerCap;
            if (cost < 0.0) {
                node.excess -= upperCap - lowerCap;
                opposite.excess += upperCap - lowerCap;
                Node t = node;
                node = opposite;
                opposite = t;
                cost *= -1.0;
            }
            this.arcs[i] = node.addArcTo(opposite, upperCap - lowerCap, cost);
            ++i;
        }
    }

    private int getU() {
        int result = 0;
        for (Node node : this.nodes) {
            result = Math.max(result, Math.abs(node.excess));
        }
        for (Arc arc : this.arcs) {
            if (arc.isInfiniteCapacityArc()) continue;
            result = Math.max(result, arc.residualCapacity);
        }
        return result;
    }

    private Pair<List<Node>, Set<Node>> scale(int delta) {
        for (Node node : this.nodes) {
            Arc nextArc;
            Arc arc = nextArc = node.firstNonSaturated;
            while (arc != null) {
                nextArc = nextArc.next;
                int residualCapacity = arc.residualCapacity;
                if (arc.residualCapacity >= delta && arc.getReducedCost() < 0.0) {
                    arc.sendFlow(residualCapacity);
                    arc.head.excess += residualCapacity;
                    arc.revArc.head.excess -= residualCapacity;
                }
                arc = nextArc;
            }
        }
        ArrayList<Node> positiveExcessNodes = new ArrayList<Node>();
        HashSet<Node> negativeExcessNodes = new HashSet<Node>();
        for (Node node : this.nodes) {
            if (node.excess >= delta) {
                positiveExcessNodes.add(node);
                continue;
            }
            if (node.excess > -delta) continue;
            negativeExcessNodes.add(node);
        }
        return new Pair<List<Node>, Set<Node>>(positiveExcessNodes, negativeExcessNodes);
    }

    private void pushAllFlow(List<Node> positiveExcessNodes, Set<Node> negativeExcessNodes, int delta) {
        for (Node node : positiveExcessNodes) {
            while (node.excess >= delta) {
                if (negativeExcessNodes.isEmpty()) {
                    return;
                }
                this.pushDijkstra(node, negativeExcessNodes, delta);
            }
        }
    }

    private void pushDijkstra(Node start, Set<Node> negativeExcessNodes, int delta) {
        int temporarilyLabeledType = this.counter++;
        int permanentlyLabeledType = this.counter++;
        PairingHeap heap = new PairingHeap();
        LinkedList<Node> permanentlyLabeled = new LinkedList<Node>();
        start.parentArc = null;
        start.handle = heap.insert((Object)0.0, (Object)start);
        while (!heap.isEmpty()) {
            AddressableHeap.Handle currentFibNode = heap.deleteMin();
            double distance = (Double)currentFibNode.getKey();
            Node currentNode = (Node)currentFibNode.getValue();
            if (negativeExcessNodes.contains(currentNode)) {
                this.augmentPath(start, currentNode);
                if (currentNode.excess > -delta) {
                    negativeExcessNodes.remove(currentNode);
                }
                for (Node node : permanentlyLabeled) {
                    node.potential += distance;
                }
                return;
            }
            currentNode.labelType = permanentlyLabeledType;
            permanentlyLabeled.add(currentNode);
            Arc currentArc = currentNode.firstNonSaturated;
            while (currentArc != null) {
                if (currentArc.residualCapacity >= delta) {
                    Node opposite = currentArc.head;
                    if (opposite.labelType != permanentlyLabeledType) {
                        if (opposite.labelType == temporarilyLabeledType) {
                            if (distance + currentArc.getReducedCost() < (Double)opposite.handle.getKey()) {
                                opposite.handle.decreaseKey((Object)(distance + currentArc.getReducedCost()));
                                opposite.parentArc = currentArc;
                            }
                        } else {
                            opposite.labelType = temporarilyLabeledType;
                            opposite.handle = heap.insert((Object)(distance + currentArc.getReducedCost()), (Object)opposite);
                            opposite.parentArc = currentArc;
                        }
                    }
                }
                currentArc = currentArc.next;
            }
            currentNode.potential -= distance;
        }
    }

    private void augmentPath(Node start, Node end) {
        int valueToAugment = Math.min(start.excess, -end.excess);
        Arc arc = end.parentArc;
        while (arc != null) {
            valueToAugment = Math.min(valueToAugment, arc.residualCapacity);
            arc = arc.revArc.head.parentArc;
        }
        end.excess += valueToAugment;
        arc = end.parentArc;
        while (arc != null) {
            arc.sendFlow(valueToAugment);
            arc = arc.revArc.head.parentArc;
        }
        start.excess -= valueToAugment;
    }

    private MinimumCostFlowAlgorithm.MinimumCostFlow<E> finish() {
        HashMap<E, Double> flowMap = CollectionUtil.newHashMapWithExpectedSize(this.m);
        double totalCost = 0.0;
        Arc arc = this.nodes[this.n].firstNonSaturated;
        while (arc != null) {
            if (arc.revArc.residualCapacity > 0) {
                throw new IllegalArgumentException("Specified flow network problem has no feasible solution");
            }
            arc = arc.next;
        }
        for (int i = 0; i < this.m; ++i) {
            E graphEdge = this.graphEdges.get(i);
            Arc arc2 = this.arcs[i];
            double flowOnArc = arc2.revArc.residualCapacity;
            if (this.problem.getGraph().getEdgeWeight(graphEdge) < 0.0) {
                flowOnArc = (double)(this.problem.getArcCapacityUpperBounds().apply(graphEdge) - this.problem.getArcCapacityLowerBounds().apply(graphEdge)) - flowOnArc;
            }
            flowMap.put(graphEdge, flowOnArc += (double)this.problem.getArcCapacityLowerBounds().apply(graphEdge).intValue());
            totalCost += flowOnArc * this.problem.getGraph().getEdgeWeight(graphEdge);
        }
        return new MinimumCostFlowAlgorithm.MinimumCostFlowImpl(totalCost, flowMap);
    }

    public boolean testOptimality(double eps) {
        if (this.minimumCostFlow == null) {
            throw new RuntimeException("Cannot return a dual solution before getMinimumCostFlow(MinimumCostFlowProblem minimumCostFlowProblem) is invoked!");
        }
        for (Node node : this.nodes) {
            Arc arc = node.firstNonSaturated;
            while (arc != null) {
                if (arc.getReducedCost() < -eps) {
                    return false;
                }
                arc = arc.next;
            }
        }
        return true;
    }

    private static class Node {
        private static int nextID = 0;
        AddressableHeap.Handle<Double, Node> handle;
        Arc parentArc;
        int labelType;
        int excess;
        double potential;
        Arc firstSaturated;
        Arc firstNonSaturated;
        private int id = nextID++;

        public Node(int excess) {
            this.excess = excess;
        }

        Arc addArcTo(Node opposite, int capacity, double cost) {
            Arc forwardArc = new Arc(opposite, capacity, cost);
            if (capacity > 0) {
                if (this.firstNonSaturated != null) {
                    this.firstNonSaturated.prev = forwardArc;
                }
                forwardArc.next = this.firstNonSaturated;
                this.firstNonSaturated = forwardArc;
            } else {
                if (this.firstSaturated != null) {
                    this.firstSaturated.prev = forwardArc;
                }
                forwardArc.next = this.firstSaturated;
                this.firstSaturated = forwardArc;
            }
            Arc reverseArc = new Arc(this, 0, -cost);
            if (opposite.firstSaturated != null) {
                opposite.firstSaturated.prev = reverseArc;
            }
            reverseArc.next = opposite.firstSaturated;
            opposite.firstSaturated = reverseArc;
            forwardArc.revArc = reverseArc;
            reverseArc.revArc = forwardArc;
            return forwardArc;
        }

        public String toString() {
            return String.format("Id = %d, excess = %d, potential = %.1f", this.id, this.excess, this.potential);
        }
    }

    private static class Arc {
        final Node head;
        final double cost;
        Arc revArc;
        Arc prev;
        Arc next;
        int residualCapacity;

        Arc(Node head, int residualCapacity, double cost) {
            this.head = head;
            this.cost = cost;
            this.residualCapacity = residualCapacity;
        }

        double getReducedCost() {
            return this.cost + this.head.potential - this.revArc.head.potential;
        }

        void sendFlow(int value) {
            this.decreaseResidualCapacity(value);
            this.revArc.increaseResidualCapacity(value);
        }

        private void decreaseResidualCapacity(int value) {
            if (this.residualCapacity >= 1000000000) {
                return;
            }
            this.residualCapacity -= value;
            if (this.residualCapacity == 0) {
                Node tail = this.revArc.head;
                if (this.next != null) {
                    this.next.prev = this.prev;
                }
                if (this.prev != null) {
                    this.prev.next = this.next;
                } else {
                    tail.firstNonSaturated = this.next;
                }
                this.next = tail.firstSaturated;
                if (tail.firstSaturated != null) {
                    tail.firstSaturated.prev = this;
                }
                tail.firstSaturated = this;
                this.prev = null;
            }
        }

        private void increaseResidualCapacity(int value) {
            if (this.residualCapacity >= 1000000000) {
                return;
            }
            if (this.residualCapacity == 0) {
                Node tail = this.revArc.head;
                if (this.next != null) {
                    this.next.prev = this.prev;
                }
                if (this.prev != null) {
                    this.prev.next = this.next;
                } else {
                    tail.firstSaturated = this.next;
                }
                this.next = tail.firstNonSaturated;
                if (tail.firstNonSaturated != null) {
                    tail.firstNonSaturated.prev = this;
                }
                tail.firstNonSaturated = this;
                this.prev = null;
            }
            this.residualCapacity += value;
        }

        public boolean isInfiniteCapacityArc() {
            return this.residualCapacity >= 1000000000;
        }

        public String toString() {
            return String.format("(%d, %d), residual capacity = %s, reduced cost = %.1f, cost = %.1f", this.revArc.head.id, this.head.id, this.residualCapacity >= 1000000000 ? "INF" : String.valueOf(this.residualCapacity), this.getReducedCost(), this.cost);
        }
    }
}

