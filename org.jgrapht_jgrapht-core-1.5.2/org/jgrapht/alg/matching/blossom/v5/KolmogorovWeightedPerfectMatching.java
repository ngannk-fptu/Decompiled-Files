/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.matching.blossom.v5;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.MatchingAlgorithm;
import org.jgrapht.alg.matching.blossom.v5.BlossomVDualUpdater;
import org.jgrapht.alg.matching.blossom.v5.BlossomVEdge;
import org.jgrapht.alg.matching.blossom.v5.BlossomVInitializer;
import org.jgrapht.alg.matching.blossom.v5.BlossomVNode;
import org.jgrapht.alg.matching.blossom.v5.BlossomVOptions;
import org.jgrapht.alg.matching.blossom.v5.BlossomVPrimalUpdater;
import org.jgrapht.alg.matching.blossom.v5.BlossomVState;
import org.jgrapht.alg.matching.blossom.v5.BlossomVTree;
import org.jgrapht.alg.matching.blossom.v5.BlossomVTreeEdge;
import org.jgrapht.alg.matching.blossom.v5.ObjectiveSense;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.graph.AsWeightedGraph;

public class KolmogorovWeightedPerfectMatching<V, E>
implements MatchingAlgorithm<V, E> {
    public static final double EPS = 1.0E-9;
    public static final double INFINITY = 1.0E100;
    public static final double NO_PERFECT_MATCHING_THRESHOLD = 1.0E10;
    public static final BlossomVOptions DEFAULT_OPTIONS = new BlossomVOptions();
    static final boolean DEBUG = false;
    static final String NO_PERFECT_MATCHING = "There is no perfect matching in the specified graph";
    private final Graph<V, E> initialGraph;
    private final Graph<V, E> graph;
    BlossomVState<V, E> state;
    private BlossomVPrimalUpdater<V, E> primalUpdater;
    private BlossomVDualUpdater<V, E> dualUpdater;
    private MatchingAlgorithm.Matching<V, E> matching;
    private DualSolution<V, E> dualSolution;
    private BlossomVOptions options;
    private ObjectiveSense objectiveSense;

    public KolmogorovWeightedPerfectMatching(Graph<V, E> graph) {
        this(graph, DEFAULT_OPTIONS, ObjectiveSense.MINIMIZE);
    }

    public KolmogorovWeightedPerfectMatching(Graph<V, E> graph, ObjectiveSense objectiveSense) {
        this(graph, DEFAULT_OPTIONS, objectiveSense);
    }

    public KolmogorovWeightedPerfectMatching(Graph<V, E> graph, BlossomVOptions options) {
        this(graph, options, ObjectiveSense.MINIMIZE);
    }

    public KolmogorovWeightedPerfectMatching(Graph<V, E> graph, BlossomVOptions options, ObjectiveSense objectiveSense) {
        Objects.requireNonNull(graph);
        this.objectiveSense = objectiveSense;
        if ((graph.vertexSet().size() & 1) == 1) {
            throw new IllegalArgumentException(NO_PERFECT_MATCHING);
        }
        this.graph = objectiveSense == ObjectiveSense.MAXIMIZE ? new AsWeightedGraph<V, Object>(graph, e -> -graph.getEdgeWeight(e), true, false) : graph;
        this.initialGraph = graph;
        this.options = Objects.requireNonNull(options);
    }

    @Override
    public MatchingAlgorithm.Matching<V, E> getMatching() {
        if (this.matching == null) {
            this.lazyComputeWeightedPerfectMatching();
        }
        return this.matching;
    }

    public DualSolution<V, E> getDualSolution() {
        this.dualSolution = this.lazyComputeDualSolution();
        return this.dualSolution;
    }

    public boolean testOptimality() {
        this.lazyComputeWeightedPerfectMatching();
        return this.getError() < 1.0E-9;
    }

    public double getError() {
        this.lazyComputeWeightedPerfectMatching();
        double error = this.testNonNegativity();
        Set<E> matchedEdges = this.matching.getEdges();
        for (int i = 0; i < this.state.graphEdges.size(); ++i) {
            Object graphEdge = this.state.graphEdges.get(i);
            BlossomVEdge edge = this.state.edges[i];
            double slack = this.graph.getEdgeWeight(graphEdge);
            slack -= this.state.minEdgeWeight;
            BlossomVNode a = edge.headOriginal[0];
            BlossomVNode b = edge.headOriginal[1];
            Pair<BlossomVNode, BlossomVNode> lca = this.lca(a, b);
            slack -= this.totalDual(a, lca.getFirst());
            slack -= this.totalDual(b, lca.getSecond());
            if (lca.getFirst() == lca.getSecond()) {
                slack += 2.0 * lca.getFirst().getTrueDual();
            }
            if (!(slack < 0.0) && !matchedEdges.contains(graphEdge)) continue;
            error += Math.abs(slack);
        }
        return error;
    }

    private void lazyComputeWeightedPerfectMatching() {
        if (this.matching != null) {
            return;
        }
        BlossomVInitializer<V, E> initializer = new BlossomVInitializer<V, E>(this.graph);
        this.state = initializer.initialize(this.options);
        this.primalUpdater = new BlossomVPrimalUpdater<V, E>(this.state);
        this.dualUpdater = new BlossomVDualUpdater<V, E>(this.state, this.primalUpdater);
        while (true) {
            int cycleTreeNum = this.state.treeNum;
            BlossomVNode currentRoot = this.state.nodes[this.state.nodeNum].treeSiblingNext;
            while (currentRoot != null) {
                BlossomVNode nextRoot = currentRoot.treeSiblingNext;
                BlossomVNode nextNextRoot = null;
                if (nextRoot != null) {
                    nextNextRoot = nextRoot.treeSiblingNext;
                }
                BlossomVTree tree = currentRoot.tree;
                int iterationTreeNum = this.state.treeNum;
                this.setCurrentEdgesAndTryToAugment(tree);
                if (iterationTreeNum == this.state.treeNum && this.options.updateDualsBefore) {
                    this.dualUpdater.updateDualsSingle(tree);
                }
                while (iterationTreeNum == this.state.treeNum) {
                    BlossomVEdge edge;
                    if (!tree.plusInfinityEdges.isEmpty()) {
                        edge = (BlossomVEdge)tree.plusInfinityEdges.findMin().getValue();
                        if (edge.slack <= tree.eps) {
                            this.primalUpdater.grow(edge, true, true);
                            continue;
                        }
                    }
                    if (!tree.plusPlusEdges.isEmpty()) {
                        edge = (BlossomVEdge)tree.plusPlusEdges.findMin().getValue();
                        if (edge.slack <= 2.0 * tree.eps) {
                            this.primalUpdater.shrink(edge, true);
                            continue;
                        }
                    }
                    if (tree.minusBlossoms.isEmpty()) break;
                    BlossomVNode node = (BlossomVNode)tree.minusBlossoms.findMin().getValue();
                    if (!(node.dual <= tree.eps)) break;
                    this.primalUpdater.expand(node, true);
                }
                if (this.state.treeNum == iterationTreeNum) {
                    tree.currentEdge = null;
                    if (this.options.updateDualsAfter && this.dualUpdater.updateDualsSingle(tree)) continue;
                    tree.clearCurrentEdges();
                }
                currentRoot = nextRoot;
                if (nextRoot == null || !nextRoot.isInfinityNode()) continue;
                currentRoot = nextNextRoot;
            }
            if (this.state.treeNum == 0) break;
            if (cycleTreeNum != this.state.treeNum || !(this.dualUpdater.updateDuals(this.options.dualUpdateStrategy) <= 0.0)) continue;
            this.dualUpdater.updateDuals(BlossomVOptions.DualUpdateStrategy.MULTIPLE_TREE_CONNECTED_COMPONENTS);
        }
        this.finish();
    }

    private void setCurrentEdgesAndTryToAugment(BlossomVTree tree) {
        BlossomVTree.TreeEdgeIterator iterator = tree.treeEdgeIterator();
        while (iterator.hasNext()) {
            BlossomVTreeEdge treeEdge = iterator.next();
            BlossomVTree opposite = treeEdge.head[iterator.getCurrentDirection()];
            if (!treeEdge.plusPlusEdges.isEmpty()) {
                BlossomVEdge edge = (BlossomVEdge)treeEdge.plusPlusEdges.findMin().getValue();
                if (edge.slack <= tree.eps + opposite.eps) {
                    this.primalUpdater.augment(edge);
                    break;
                }
            }
            opposite.currentEdge = treeEdge;
            opposite.currentDirection = iterator.getCurrentDirection();
        }
    }

    private double testNonNegativity() {
        BlossomVNode[] nodes = this.state.nodes;
        double error = 0.0;
        block0: for (int i = 0; i < this.state.nodeNum; ++i) {
            BlossomVNode node = nodes[i].blossomParent;
            while (node != null && !node.isMarked) {
                if (node.dual < 0.0) {
                    error += Math.abs(node.dual);
                    continue block0;
                }
                node.isMarked = true;
                node = node.blossomParent;
            }
        }
        this.clearMarked();
        return error;
    }

    private double totalDual(BlossomVNode start, BlossomVNode end) {
        if (end == start) {
            return start.getTrueDual();
        }
        double result = 0.0;
        BlossomVNode current = start;
        do {
            result += current.getTrueDual();
        } while ((current = current.blossomParent) != null && current != end);
        return result += end.getTrueDual();
    }

    private Pair<BlossomVNode, BlossomVNode> lca(BlossomVNode a, BlossomVNode b) {
        Pair<BlossomVNode, BlossomVNode> result;
        BlossomVNode[] branches = new BlossomVNode[]{a, b};
        int dir = 0;
        while (true) {
            if (branches[dir].isMarked) {
                result = new Pair<BlossomVNode, BlossomVNode>(branches[dir], branches[dir]);
                break;
            }
            branches[dir].isMarked = true;
            if (branches[dir].isOuter) {
                BlossomVNode jumpNode = branches[1 - dir];
                while (!jumpNode.isOuter && !jumpNode.isMarked) {
                    jumpNode = jumpNode.blossomParent;
                }
                if (jumpNode.isMarked) {
                    result = new Pair<BlossomVNode, BlossomVNode>(jumpNode, jumpNode);
                    break;
                }
                result = dir == 0 ? new Pair<BlossomVNode, BlossomVNode>(branches[dir], jumpNode) : new Pair<BlossomVNode, BlossomVNode>(jumpNode, branches[dir]);
                break;
            }
            branches[dir] = branches[dir].blossomParent;
            dir = 1 - dir;
        }
        this.clearMarked(a);
        this.clearMarked(b);
        return result;
    }

    private void clearMarked(BlossomVNode node) {
        do {
            node.isMarked = false;
        } while ((node = node.blossomParent) != null && node.isMarked);
    }

    private void clearMarked() {
        BlossomVNode[] nodes = this.state.nodes;
        for (int i = 0; i < this.state.nodeNum; ++i) {
            BlossomVNode current = nodes[i];
            do {
                current.isMarked = false;
            } while ((current = current.blossomParent) != null && current.isMarked);
        }
    }

    private void finish() {
        HashSet edges = new HashSet();
        BlossomVNode[] nodes = this.state.nodes;
        LinkedList<BlossomVNode> processed = new LinkedList<BlossomVNode>();
        for (int i = 0; i < this.state.nodeNum; ++i) {
            Object blossomRoot;
            if (nodes[i].matched != null) continue;
            BlossomVNode blossomPrev = null;
            BlossomVNode blossom = nodes[i];
            do {
                blossom.blossomGrandparent = blossomPrev;
                blossomPrev = blossom;
                blossom = blossomPrev.blossomParent;
            } while (!blossom.isOuter);
            while (true) {
                if ((blossomRoot = blossom.matched.getCurrentOriginal(blossom)) == null) {
                    Object object = blossomRoot = blossom.matched.head[0].isProcessed ? blossom.matched.headOriginal[1] : blossom.matched.headOriginal[0];
                }
                while (((BlossomVNode)blossomRoot).blossomParent != blossom) {
                    blossomRoot = ((BlossomVNode)blossomRoot).blossomParent;
                }
                ((BlossomVNode)blossomRoot).matched = blossom.matched;
                BlossomVNode node = blossom.getOppositeMatched();
                if (node != null) {
                    node.isProcessed = true;
                    processed.add(node);
                }
                node = ((BlossomVNode)blossomRoot).blossomSibling.getOpposite((BlossomVNode)blossomRoot);
                while (node != blossomRoot) {
                    node.matched = node.blossomSibling;
                    BlossomVNode nextNode = node.blossomSibling.getOpposite(node);
                    nextNode.matched = node.matched;
                    node = nextNode.blossomSibling.getOpposite(nextNode);
                }
                if (!blossomPrev.isBlossom) break;
                blossom = blossomPrev;
                blossomPrev = blossom.blossomGrandparent;
            }
            blossomRoot = processed.iterator();
            while (blossomRoot.hasNext()) {
                BlossomVNode processedNode = (BlossomVNode)blossomRoot.next();
                processedNode.isProcessed = false;
            }
            processed.clear();
        }
        double weight = 0.0;
        for (int i = 0; i < this.state.nodeNum; ++i) {
            Object graphEdge = this.state.graphEdges.get(nodes[i].matched.pos);
            if (edges.contains(graphEdge)) continue;
            edges.add(graphEdge);
            weight += this.state.graph.getEdgeWeight(graphEdge);
        }
        if (this.objectiveSense == ObjectiveSense.MAXIMIZE) {
            weight = -weight;
        }
        this.matching = new MatchingAlgorithm.MatchingImpl(this.state.graph, edges, weight);
    }

    private void prepareForDualSolution() {
        BlossomVNode[] nodes = this.state.nodes;
        for (int i = 0; i < this.state.nodeNum; ++i) {
            BlossomVNode current = nodes[i];
            BlossomVNode prev = null;
            do {
                current.blossomGrandparent = prev;
                current.isMarked = true;
                prev = current;
            } while ((current = current.blossomParent) != null && !current.isMarked);
        }
        this.clearMarked();
    }

    private Set<V> getBlossomNodes(BlossomVNode pseudonode, Map<BlossomVNode, Set<V>> blossomNodes) {
        BlossomVNode endNode;
        if (blossomNodes.containsKey(pseudonode)) {
            return blossomNodes.get(pseudonode);
        }
        HashSet result = new HashSet();
        BlossomVNode current = endNode = pseudonode.blossomGrandparent;
        do {
            if (current.isBlossom) {
                if (!blossomNodes.containsKey(current)) {
                    result.addAll(this.getBlossomNodes(current, blossomNodes));
                    continue;
                }
                result.addAll((Collection)blossomNodes.get(current));
                continue;
            }
            result.add(this.state.graphVertices.get(current.pos));
        } while ((current = current.blossomSibling.getOpposite(current)) != endNode);
        blossomNodes.put(pseudonode, result);
        return result;
    }

    private DualSolution<V, E> lazyComputeDualSolution() {
        this.lazyComputeWeightedPerfectMatching();
        if (this.dualSolution != null) {
            return this.dualSolution;
        }
        HashMap<Set<Double>, Double> dualMap = new HashMap<Set<Double>, Double>();
        HashMap<BlossomVNode, Set<V>> nodesInBlossoms = new HashMap<BlossomVNode, Set<V>>();
        BlossomVNode[] nodes = this.state.nodes;
        this.prepareForDualSolution();
        double dualShift = this.state.minEdgeWeight / 2.0;
        for (int i = 0; i < this.state.nodeNum; ++i) {
            BlossomVNode current = nodes[i];
            do {
                double dual = current.getTrueDual();
                if (!current.isBlossom) {
                    dual += dualShift;
                }
                if (this.objectiveSense == ObjectiveSense.MAXIMIZE) {
                    dual = -dual;
                }
                if (Math.abs(dual) > 1.0E-9) {
                    if (current.isBlossom) {
                        dualMap.put(this.getBlossomNodes(current, nodesInBlossoms), dual);
                    } else {
                        dualMap.put(Collections.singleton(this.state.graphVertices.get(current.pos)), dual);
                    }
                }
                current.isMarked = true;
            } while (!current.isOuter && (current = current.blossomParent) != null && !current.isMarked);
        }
        this.clearMarked();
        return new DualSolution<V, E>(this.initialGraph, dualMap);
    }

    private void printState() {
        int i;
        BlossomVNode[] nodes = this.state.nodes;
        BlossomVEdge[] edges = this.state.edges;
        System.out.println();
        for (int i2 = 0; i2 < 20; ++i2) {
            System.out.print("-");
        }
        System.out.println();
        HashSet<BlossomVEdge> matched = new HashSet<BlossomVEdge>();
        for (i = 0; i < this.state.nodeNum; ++i) {
            BlossomVNode node = nodes[i];
            if (node.matched != null) {
                BlossomVEdge matchedEdge = node.matched;
                matched.add(node.matched);
                if (matchedEdge.head[0].matched == null || matchedEdge.head[1].matched == null) {
                    System.out.println("Problem with edge " + matchedEdge);
                    throw new RuntimeException();
                }
            }
            System.out.println(nodes[i]);
        }
        for (i = 0; i < 20; ++i) {
            System.out.print("-");
        }
        System.out.println();
        for (i = 0; i < this.state.edgeNum; ++i) {
            System.out.println(edges[i] + (matched.contains(edges[i]) ? ", matched" : ""));
        }
    }

    private void printTrees() {
        System.out.println("Printing trees");
        BlossomVNode root = this.state.nodes[this.state.nodeNum].treeSiblingNext;
        while (root != null) {
            BlossomVTree tree = root.tree;
            System.out.println(tree);
            root = root.treeSiblingNext;
        }
    }

    private void printMap() {
        System.out.println(this.state.nodeNum + " " + this.state.edgeNum);
        for (int i = 0; i < this.state.nodeNum; ++i) {
            System.out.println(this.state.graphVertices.get(i) + " -> " + this.state.nodes[i]);
        }
    }

    public Statistics getStatistics() {
        return this.state.statistics;
    }

    public static class DualSolution<V, E> {
        Graph<V, E> graph;
        Map<Set<V>, Double> dualVariables;

        public DualSolution(Graph<V, E> graph, Map<Set<V>, Double> dualVariables) {
            this.graph = graph;
            this.dualVariables = dualVariables;
        }

        public Graph<V, E> getGraph() {
            return this.graph;
        }

        public Map<Set<V>, Double> getDualVariables() {
            return this.dualVariables;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder("DualSolution{");
            sb.append("graph=").append(this.graph);
            sb.append(", dualVariables=").append(this.dualVariables);
            sb.append('}');
            return sb.toString();
        }
    }

    public static class Statistics {
        int shrinkNum = 0;
        int expandNum = 0;
        int growNum = 0;
        long augmentTime = 0L;
        long expandTime = 0L;
        long shrinkTime = 0L;
        long growTime = 0L;
        long dualUpdatesTime = 0L;

        public int getShrinkNum() {
            return this.shrinkNum;
        }

        public int getExpandNum() {
            return this.expandNum;
        }

        public int getGrowNum() {
            return this.growNum;
        }

        public long getAugmentTime() {
            return this.augmentTime;
        }

        public long getExpandTime() {
            return this.expandTime;
        }

        public long getShrinkTime() {
            return this.shrinkTime;
        }

        public long getGrowTime() {
            return this.growTime;
        }

        public long getDualUpdatesTime() {
            return this.dualUpdatesTime;
        }

        public String toString() {
            return "Statistics{shrinkNum=" + this.shrinkNum + ", expandNum=" + this.expandNum + ", growNum=" + this.growNum + ", augmentTime=" + this.augmentTime + ", expandTime=" + this.expandTime + ", shrinkTime=" + this.shrinkTime + ", growTime=" + this.growTime + "}";
        }
    }
}

