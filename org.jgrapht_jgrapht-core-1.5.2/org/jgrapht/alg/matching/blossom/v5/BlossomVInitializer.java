/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jheaps.AddressableHeap
 *  org.jheaps.tree.PairingHeap
 */
package org.jgrapht.alg.matching.blossom.v5;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import org.jgrapht.Graph;
import org.jgrapht.alg.matching.blossom.v5.BlossomVEdge;
import org.jgrapht.alg.matching.blossom.v5.BlossomVNode;
import org.jgrapht.alg.matching.blossom.v5.BlossomVOptions;
import org.jgrapht.alg.matching.blossom.v5.BlossomVState;
import org.jgrapht.alg.matching.blossom.v5.BlossomVTree;
import org.jgrapht.alg.matching.blossom.v5.BlossomVTreeEdge;
import org.jgrapht.util.CollectionUtil;
import org.jheaps.AddressableHeap;
import org.jheaps.tree.PairingHeap;

class BlossomVInitializer<V, E> {
    private final Graph<V, E> graph;
    private int nodeNum;
    private int edgeNum = 0;
    private BlossomVNode[] nodes;
    private BlossomVEdge[] edges;
    private List<V> graphVertices;
    private List<E> graphEdges;

    public BlossomVInitializer(Graph<V, E> graph) {
        this.graph = graph;
        this.nodeNum = graph.vertexSet().size();
    }

    public BlossomVState<V, E> initialize(BlossomVOptions options) {
        switch (options.initializationType) {
            case NONE: {
                return this.simpleInitialization(options);
            }
            case GREEDY: {
                return this.greedyInitialization(options);
            }
            case FRACTIONAL: {
                return this.fractionalMatchingInitialization(options);
            }
        }
        return null;
    }

    private BlossomVState<V, E> simpleInitialization(BlossomVOptions options) {
        double minEdgeWeight = this.initGraph();
        for (BlossomVNode node : this.nodes) {
            node.isOuter = true;
        }
        this.allocateTrees();
        this.initAuxiliaryGraph();
        return new BlossomVState<V, E>(this.graph, this.nodes, this.edges, this.nodeNum, this.edgeNum, this.nodeNum, this.graphVertices, this.graphEdges, options, minEdgeWeight);
    }

    private BlossomVState<V, E> greedyInitialization(BlossomVOptions options) {
        double minEdgeWeight = this.initGraph();
        int treeNum = this.initGreedy();
        this.allocateTrees();
        this.initAuxiliaryGraph();
        return new BlossomVState<V, E>(this.graph, this.nodes, this.edges, this.nodeNum, this.edgeNum, treeNum, this.graphVertices, this.graphEdges, options, minEdgeWeight);
    }

    private BlossomVState<V, E> fractionalMatchingInitialization(BlossomVOptions options) {
        double minEdgeWeight = this.initGraph();
        this.initGreedy();
        this.allocateTrees();
        int treeNum = this.initFractional();
        this.initAuxiliaryGraph();
        return new BlossomVState<V, E>(this.graph, this.nodes, this.edges, this.nodeNum, this.edgeNum, treeNum, this.graphVertices, this.graphEdges, options, minEdgeWeight);
    }

    private double initGraph() {
        int expectedEdgeNum = this.graph.edgeSet().size();
        this.nodes = new BlossomVNode[this.nodeNum + 1];
        this.edges = new BlossomVEdge[expectedEdgeNum];
        this.graphVertices = new ArrayList<V>(this.nodeNum);
        this.graphEdges = new ArrayList(expectedEdgeNum);
        HashMap<V, BlossomVNode> vertexMap = CollectionUtil.newHashMapWithExpectedSize(this.nodeNum);
        int i = 0;
        for (V vertex : this.graph.vertexSet()) {
            this.nodes[i] = new BlossomVNode(i);
            this.graphVertices.add(vertex);
            vertexMap.put(vertex, this.nodes[i]);
            ++i;
        }
        this.nodes[this.nodeNum] = new BlossomVNode(this.nodeNum);
        i = 0;
        double minEdgeWeight = this.graph.edgeSet().stream().map(this.graph::getEdgeWeight).min(Comparator.naturalOrder()).orElse(0.0);
        for (E e : this.graph.edgeSet()) {
            BlossomVEdge edge;
            BlossomVNode target;
            BlossomVNode source = (BlossomVNode)vertexMap.get(this.graph.getEdgeSource(e));
            if (source == (target = (BlossomVNode)vertexMap.get(this.graph.getEdgeTarget(e)))) continue;
            ++this.edgeNum;
            this.edges[i] = edge = this.addEdge(source, target, this.graph.getEdgeWeight(e) - minEdgeWeight, i);
            this.graphEdges.add(e);
            ++i;
        }
        return minEdgeWeight;
    }

    public BlossomVEdge addEdge(BlossomVNode from, BlossomVNode to, double slack, int pos) {
        BlossomVEdge edge = new BlossomVEdge(pos);
        edge.slack = slack;
        edge.headOriginal[0] = to;
        edge.headOriginal[1] = from;
        from.addEdge(edge, 0);
        to.addEdge(edge, 1);
        return edge;
    }

    private int initGreedy() {
        BlossomVEdge edge;
        int i;
        for (i = 0; i < this.nodeNum; ++i) {
            this.nodes[i].dual = 1.0E100;
        }
        for (i = 0; i < this.edgeNum; ++i) {
            edge = this.edges[i];
            if (edge.head[0].dual > edge.slack) {
                edge.head[0].dual = edge.slack;
            }
            if (!(edge.head[1].dual > edge.slack)) continue;
            edge.head[1].dual = edge.slack;
        }
        for (i = 0; i < this.edgeNum; ++i) {
            edge = this.edges[i];
            BlossomVNode source = edge.head[0];
            BlossomVNode target = edge.head[1];
            if (!source.isOuter) {
                source.isOuter = true;
                source.dual /= 2.0;
            }
            edge.slack -= source.dual;
            if (!target.isOuter) {
                target.isOuter = true;
                target.dual /= 2.0;
            }
            edge.slack -= target.dual;
        }
        int treeNum = this.nodeNum;
        for (int i2 = 0; i2 < this.nodeNum; ++i2) {
            BlossomVNode node = this.nodes[i2];
            if (node.isInfinityNode()) continue;
            double minSlack = 1.0E100;
            BlossomVNode.IncidentEdgeIterator incidentEdgeIterator = node.incidentEdgesIterator();
            while (incidentEdgeIterator.hasNext()) {
                BlossomVEdge edge2 = incidentEdgeIterator.next();
                if (!(edge2.slack < minSlack)) continue;
                minSlack = edge2.slack;
            }
            node.dual += minSlack;
            double resultMinSlack = minSlack;
            BlossomVNode.IncidentEdgeIterator incidentEdgeIterator2 = node.incidentEdgesIterator();
            while (incidentEdgeIterator2.hasNext()) {
                BlossomVEdge edge3 = incidentEdgeIterator2.next();
                int dir = incidentEdgeIterator2.getDir();
                if (edge3.slack <= resultMinSlack && node.isPlusNode() && edge3.head[dir].isPlusNode()) {
                    node.label = BlossomVNode.Label.INFINITY;
                    edge3.head[dir].label = BlossomVNode.Label.INFINITY;
                    node.matched = edge3;
                    edge3.head[dir].matched = edge3;
                    treeNum -= 2;
                }
                edge3.slack -= resultMinSlack;
            }
        }
        return treeNum;
    }

    private void initAuxiliaryGraph() {
        BlossomVNode root = this.nodes[this.nodeNum].treeSiblingNext;
        while (root != null) {
            BlossomVTree tree = root.tree;
            BlossomVNode.IncidentEdgeIterator edgeIterator = root.incidentEdgesIterator();
            while (edgeIterator.hasNext()) {
                BlossomVEdge edge = edgeIterator.next();
                BlossomVNode opposite = edge.head[edgeIterator.getDir()];
                if (opposite.isInfinityNode()) {
                    tree.addPlusInfinityEdge(edge);
                    continue;
                }
                if (opposite.isProcessed) continue;
                if (opposite.tree.currentEdge == null) {
                    BlossomVTree.addTreeEdge(tree, opposite.tree);
                }
                opposite.tree.currentEdge.addPlusPlusEdge(edge);
            }
            root.isProcessed = true;
            BlossomVTree.TreeEdgeIterator treeEdgeIterator = tree.treeEdgeIterator();
            while (treeEdgeIterator.hasNext()) {
                BlossomVTreeEdge treeEdge = treeEdgeIterator.next();
                treeEdge.head[treeEdgeIterator.getCurrentDirection()].currentEdge = null;
            }
            root = root.treeSiblingNext;
        }
        root = this.nodes[this.nodeNum].treeSiblingNext;
        while (root != null) {
            root.isProcessed = false;
            root = root.treeSiblingNext;
        }
    }

    private void allocateTrees() {
        BlossomVNode lastRoot = this.nodes[this.nodeNum];
        for (int i = 0; i < this.nodeNum; ++i) {
            BlossomVNode node = this.nodes[i];
            if (!node.isPlusNode()) continue;
            node.treeSiblingPrev = lastRoot;
            lastRoot.treeSiblingNext = node;
            lastRoot = node;
            new BlossomVTree(node);
        }
        lastRoot.treeSiblingNext = null;
    }

    private int finish() {
        BlossomVNode prevRoot = this.nodes[this.nodeNum];
        int treeNum = 0;
        for (int i = 0; i < this.nodeNum; ++i) {
            BlossomVNode node = this.nodes[i];
            node.treeSiblingPrev = null;
            node.treeSiblingNext = null;
            node.firstTreeChild = null;
            if (node.isOuter) continue;
            this.expandInit(node, null);
            node.parentEdge = null;
            node.label = BlossomVNode.Label.PLUS;
            new BlossomVTree(node);
            prevRoot.treeSiblingNext = node;
            node.treeSiblingPrev = prevRoot;
            prevRoot = node;
            ++treeNum;
        }
        return treeNum;
    }

    private void updateDuals(AddressableHeap<Double, BlossomVEdge> heap, BlossomVNode root, double eps) {
        BlossomVTree.TreeNodeIterator treeNodeIterator = new BlossomVTree.TreeNodeIterator(root);
        while (treeNodeIterator.hasNext()) {
            BlossomVNode treeNode = treeNodeIterator.next();
            if (!treeNode.isProcessed) continue;
            treeNode.dual += eps;
            if (!treeNode.isTreeRoot) {
                BlossomVNode minusNode = treeNode.getOppositeMatched();
                minusNode.dual -= eps;
                double delta = eps - treeNode.matched.slack;
                BlossomVNode.IncidentEdgeIterator iterator = minusNode.incidentEdgesIterator();
                while (iterator.hasNext()) {
                    iterator.next().slack += delta;
                }
            }
            BlossomVNode.IncidentEdgeIterator iterator = treeNode.incidentEdgesIterator();
            while (iterator.hasNext()) {
                iterator.next().slack -= eps;
            }
            treeNode.isProcessed = false;
        }
        while (!heap.isEmpty()) {
            BlossomVEdge edge = (BlossomVEdge)heap.findMin().getValue();
            BlossomVNode node = edge.head[0].isInfinityNode() ? edge.head[0] : edge.head[1];
            this.removeFromHeap(node);
        }
    }

    private void addToHead(AddressableHeap<Double, BlossomVEdge> heap, BlossomVNode node, BlossomVEdge bestEdge) {
        bestEdge.handle = heap.insert((Object)bestEdge.slack, (Object)bestEdge);
        node.bestEdge = bestEdge;
    }

    private void removeFromHeap(BlossomVNode node) {
        node.bestEdge.handle.delete();
        node.bestEdge.handle = null;
        node.bestEdge = null;
    }

    private BlossomVNode findBlossomRootInit(BlossomVEdge blossomFormingEdge) {
        BlossomVNode jumpNode;
        BlossomVNode upperBound;
        BlossomVNode root;
        BlossomVNode[] branches = new BlossomVNode[]{blossomFormingEdge.head[0], blossomFormingEdge.head[1]};
        int dir = 0;
        while (true) {
            if (!branches[dir].isOuter) {
                root = branches[dir];
                upperBound = branches[1 - dir];
                break;
            }
            branches[dir].isOuter = false;
            if (branches[dir].isTreeRoot) {
                upperBound = branches[dir];
                jumpNode = branches[1 - dir];
                while (jumpNode.isOuter) {
                    jumpNode.isOuter = false;
                    jumpNode = jumpNode.getTreeParent();
                    jumpNode.isOuter = false;
                    jumpNode = jumpNode.getTreeParent();
                }
                root = jumpNode;
                break;
            }
            BlossomVNode node = branches[dir].getTreeParent();
            node.isOuter = false;
            branches[dir] = node.getTreeParent();
            dir = 1 - dir;
        }
        for (jumpNode = root; jumpNode != upperBound; jumpNode = jumpNode.getTreeParent()) {
            jumpNode = jumpNode.getTreeParent();
            jumpNode.isOuter = true;
            jumpNode.isOuter = true;
        }
        return root;
    }

    private void handleInfinityEdgeInit(AddressableHeap<Double, BlossomVEdge> heap, BlossomVEdge infinityEdge, int dir, double eps, double criticalEps) {
        BlossomVNode inTreeNode = infinityEdge.head[1 - dir];
        BlossomVNode oppositeNode = infinityEdge.head[dir];
        if (infinityEdge.slack > eps) {
            if (infinityEdge.slack < criticalEps) {
                if (oppositeNode.bestEdge == null) {
                    this.addToHead(heap, oppositeNode, infinityEdge);
                } else if (infinityEdge.slack < oppositeNode.bestEdge.slack) {
                    this.removeFromHeap(oppositeNode);
                    this.addToHead(heap, oppositeNode, infinityEdge);
                }
            }
        } else {
            if (oppositeNode.bestEdge != null) {
                this.removeFromHeap(oppositeNode);
            }
            oppositeNode.label = BlossomVNode.Label.MINUS;
            inTreeNode.addChild(oppositeNode, infinityEdge, true);
            BlossomVNode plusNode = oppositeNode.matched.getOpposite(oppositeNode);
            if (plusNode.bestEdge != null) {
                this.removeFromHeap(plusNode);
            }
            plusNode.label = BlossomVNode.Label.PLUS;
            oppositeNode.addChild(plusNode, plusNode.matched, true);
        }
    }

    private void augmentBranchInit(BlossomVNode treeRoot, BlossomVNode branchStart, BlossomVEdge augmentEdge) {
        BlossomVTree.TreeNodeIterator iterator = new BlossomVTree.TreeNodeIterator(treeRoot);
        while (iterator.hasNext()) {
            iterator.next().label = BlossomVNode.Label.INFINITY;
        }
        BlossomVNode plusNode = branchStart;
        BlossomVNode minusNode = branchStart.getTreeParent();
        BlossomVEdge matchedEdge = augmentEdge;
        while (minusNode != null) {
            plusNode.matched = matchedEdge;
            minusNode.matched = matchedEdge = minusNode.parentEdge;
            plusNode = minusNode.getTreeParent();
            minusNode = plusNode.getTreeParent();
        }
        treeRoot.matched = matchedEdge;
        treeRoot.removeFromChildList();
        treeRoot.isTreeRoot = false;
    }

    private void shrinkInit(BlossomVEdge blossomFormingEdge, BlossomVNode treeRoot) {
        BlossomVTree.TreeNodeIterator iterator = new BlossomVTree.TreeNodeIterator(treeRoot);
        while (iterator.hasNext()) {
            iterator.next().label = BlossomVNode.Label.INFINITY;
        }
        BlossomVNode blossomRoot = this.findBlossomRootInit(blossomFormingEdge);
        if (!blossomRoot.isTreeRoot) {
            BlossomVNode minusNode = blossomRoot.getTreeParent();
            BlossomVEdge prevEdge = minusNode.parentEdge;
            minusNode.matched = minusNode.parentEdge;
            BlossomVNode plusNode = minusNode.getTreeParent();
            while (plusNode != treeRoot) {
                minusNode = plusNode.getTreeParent();
                plusNode.matched = prevEdge;
                minusNode.matched = prevEdge = minusNode.parentEdge;
                plusNode = minusNode.getTreeParent();
            }
            plusNode.matched = prevEdge;
        }
        BlossomVEdge prevEdge = blossomFormingEdge;
        BlossomVEdge.BlossomNodesIterator iterator2 = blossomFormingEdge.blossomNodesIterator(blossomRoot);
        while (iterator2.hasNext()) {
            BlossomVNode current = iterator2.next();
            current.label = BlossomVNode.Label.PLUS;
            if (iterator2.getCurrentDirection() == 0) {
                current.blossomSibling = prevEdge;
                prevEdge = current.parentEdge;
                continue;
            }
            current.blossomSibling = current.parentEdge;
        }
        treeRoot.removeFromChildList();
        treeRoot.isTreeRoot = false;
    }

    private void expandInit(BlossomVNode blossomNode, BlossomVEdge blossomNodeMatched) {
        BlossomVNode currentNode = blossomNode.blossomSibling.getOpposite(blossomNode);
        blossomNode.isOuter = true;
        blossomNode.label = BlossomVNode.Label.INFINITY;
        blossomNode.matched = blossomNodeMatched;
        do {
            currentNode.matched = currentNode.blossomSibling;
            BlossomVEdge prevEdge = currentNode.blossomSibling;
            currentNode.isOuter = true;
            currentNode.label = BlossomVNode.Label.INFINITY;
            currentNode = currentNode.blossomSibling.getOpposite(currentNode);
            currentNode.matched = prevEdge;
            currentNode.isOuter = true;
            currentNode.label = BlossomVNode.Label.INFINITY;
        } while ((currentNode = currentNode.blossomSibling.getOpposite(currentNode)) != blossomNode);
    }

    private int initFractional() {
        PairingHeap heap = new PairingHeap();
        BlossomVNode root = this.nodes[this.nodeNum].treeSiblingNext;
        while (root != null) {
            BlossomVNode root2 = root.treeSiblingNext;
            BlossomVNode root3 = null;
            if (root2 != null) {
                root3 = root2.treeSiblingNext;
            }
            BlossomVNode currentNode = root;
            heap.clear();
            double branchEps = 0.0;
            Action flag = Action.NONE;
            BlossomVNode branchRoot = currentNode;
            BlossomVEdge criticalEdge = null;
            double criticalEps = 1.0E100;
            int criticalDir = -1;
            boolean primalOperation = false;
            while (true) {
                BlossomVEdge minSlackEdge;
                currentNode.isProcessed = true;
                currentNode.dual -= branchEps;
                if (!currentNode.isTreeRoot) {
                    currentNode.getOppositeMatched().dual += branchEps;
                }
                BlossomVNode.IncidentEdgeIterator iterator = currentNode.incidentEdgesIterator();
                while (iterator.hasNext()) {
                    BlossomVEdge currentEdge = iterator.next();
                    int dir = iterator.getDir();
                    currentEdge.slack += branchEps;
                    BlossomVNode oppositeNode = currentEdge.head[dir];
                    if (oppositeNode.tree == root.tree) {
                        if (!oppositeNode.isPlusNode()) continue;
                        double slack = currentEdge.slack;
                        if (!oppositeNode.isProcessed) {
                            slack += branchEps;
                        }
                        if (!(2.0 * criticalEps > slack) && criticalEdge != null) continue;
                        flag = Action.SHRINK;
                        criticalEps = slack / 2.0;
                        criticalEdge = currentEdge;
                        criticalDir = dir;
                        if (!(criticalEps <= branchEps)) continue;
                        primalOperation = true;
                        break;
                    }
                    if (oppositeNode.isPlusNode()) {
                        if (!(criticalEps >= currentEdge.slack) && criticalEdge != null) continue;
                        flag = Action.AUGMENT;
                        criticalEps = currentEdge.slack;
                        criticalEdge = currentEdge;
                        criticalDir = dir;
                        if (!(criticalEps <= branchEps)) continue;
                        primalOperation = true;
                        break;
                    }
                    this.handleInfinityEdgeInit((AddressableHeap<Double, BlossomVEdge>)heap, currentEdge, dir, branchEps, criticalEps);
                }
                if (primalOperation) {
                    while (iterator.hasNext()) {
                        iterator.next().slack += branchEps;
                    }
                    break;
                }
                if (currentNode.firstTreeChild != null) {
                    currentNode = currentNode.firstTreeChild.getOppositeMatched();
                    continue;
                }
                while (currentNode != branchRoot && currentNode.treeSiblingNext == null) {
                    currentNode = currentNode.getTreeParent();
                }
                if (currentNode.isMinusNode()) {
                    currentNode = currentNode.treeSiblingNext.getOppositeMatched();
                    continue;
                }
                if (currentNode != branchRoot) continue;
                BlossomVEdge blossomVEdge = minSlackEdge = heap.isEmpty() ? null : (BlossomVEdge)heap.findMin().getValue();
                if (minSlackEdge == null || minSlackEdge.slack >= criticalEps) {
                    if (criticalEps > 1.0E10) {
                        throw new IllegalArgumentException("There is no perfect matching in the specified graph");
                    }
                    branchEps = criticalEps;
                    break;
                }
                int dirToFreeNode = minSlackEdge.head[0].isInfinityNode() ? 0 : 1;
                currentNode = minSlackEdge.head[1 - dirToFreeNode];
                BlossomVNode minusNode = minSlackEdge.head[dirToFreeNode];
                this.removeFromHeap(minusNode);
                minusNode.label = BlossomVNode.Label.MINUS;
                currentNode.addChild(minusNode, minSlackEdge, true);
                branchEps = minSlackEdge.slack;
                BlossomVNode plusNode = minusNode.getOppositeMatched();
                if (plusNode.bestEdge != null) {
                    this.removeFromHeap(plusNode);
                }
                plusNode.label = BlossomVNode.Label.PLUS;
                minusNode.addChild(plusNode, minusNode.matched, true);
                currentNode = branchRoot = plusNode;
            }
            this.updateDuals((AddressableHeap<Double, BlossomVEdge>)heap, root, branchEps);
            BlossomVNode from = criticalEdge.head[1 - criticalDir];
            BlossomVNode to = criticalEdge.head[criticalDir];
            if (flag == Action.SHRINK) {
                this.shrinkInit(criticalEdge, root);
            } else {
                this.augmentBranchInit(root, from, criticalEdge);
                if (to.isOuter) {
                    this.augmentBranchInit(to, to, criticalEdge);
                } else {
                    this.expandInit(to, criticalEdge);
                }
            }
            if ((root = root2) == null || root.isTreeRoot) continue;
            root = root3;
        }
        return this.finish();
    }

    static enum Action {
        NONE,
        SHRINK,
        AUGMENT;

    }
}

