/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jheaps.MergeableAddressableHeap
 */
package org.jgrapht.alg.matching.blossom.v5;

import org.jgrapht.alg.matching.blossom.v5.BlossomVEdge;
import org.jgrapht.alg.matching.blossom.v5.BlossomVNode;
import org.jgrapht.alg.matching.blossom.v5.BlossomVOptions;
import org.jgrapht.alg.matching.blossom.v5.BlossomVPrimalUpdater;
import org.jgrapht.alg.matching.blossom.v5.BlossomVState;
import org.jgrapht.alg.matching.blossom.v5.BlossomVTree;
import org.jgrapht.alg.matching.blossom.v5.BlossomVTreeEdge;
import org.jheaps.MergeableAddressableHeap;

class BlossomVDualUpdater<V, E> {
    private BlossomVState<V, E> state;
    private BlossomVPrimalUpdater<V, E> primalUpdater;

    public BlossomVDualUpdater(BlossomVState<V, E> state, BlossomVPrimalUpdater<V, E> primalUpdater) {
        this.state = state;
        this.primalUpdater = primalUpdater;
    }

    public double updateDuals(BlossomVOptions.DualUpdateStrategy type) {
        long start = System.nanoTime();
        BlossomVEdge augmentEdge = null;
        BlossomVNode root = this.state.nodes[this.state.nodeNum].treeSiblingNext;
        while (root != null) {
            BlossomVTree tree = root.tree;
            double eps = this.getEps(tree);
            tree.accumulatedEps = eps - tree.eps;
            root = root.treeSiblingNext;
        }
        if (type == BlossomVOptions.DualUpdateStrategy.MULTIPLE_TREE_FIXED_DELTA) {
            augmentEdge = this.multipleTreeFixedDelta();
        } else if (type == BlossomVOptions.DualUpdateStrategy.MULTIPLE_TREE_CONNECTED_COMPONENTS) {
            augmentEdge = this.updateDualsConnectedComponents();
        }
        double dualChange = 0.0;
        BlossomVNode root2 = this.state.nodes[this.state.nodeNum].treeSiblingNext;
        while (root2 != null) {
            if (root2.tree.accumulatedEps > 1.0E-9) {
                dualChange += root2.tree.accumulatedEps;
                root2.tree.eps += root2.tree.accumulatedEps;
            }
            root2 = root2.treeSiblingNext;
        }
        this.state.statistics.dualUpdatesTime += System.nanoTime() - start;
        if (augmentEdge != null) {
            this.primalUpdater.augment(augmentEdge);
        }
        return dualChange;
    }

    private double getEps(BlossomVTree tree) {
        BlossomVEdge edge;
        double eps = 1.0E100;
        if (!tree.plusInfinityEdges.isEmpty()) {
            edge = (BlossomVEdge)tree.plusInfinityEdges.findMin().getValue();
            if (edge.slack < eps) {
                eps = edge.slack;
            }
        }
        if (!tree.minusBlossoms.isEmpty()) {
            BlossomVNode node = (BlossomVNode)tree.minusBlossoms.findMin().getValue();
            if (node.dual < eps) {
                eps = node.dual;
            }
        }
        if (!tree.plusPlusEdges.isEmpty()) {
            edge = (BlossomVEdge)tree.plusPlusEdges.findMin().getValue();
            if (2.0 * eps > edge.slack) {
                eps = edge.slack / 2.0;
            }
        }
        return eps;
    }

    public boolean updateDualsSingle(BlossomVTree tree) {
        long start = System.nanoTime();
        double eps = this.getEps(tree);
        double epsAugment = 1.0E100;
        BlossomVEdge augmentEdge = null;
        double delta = 0.0;
        BlossomVTree.TreeEdgeIterator iterator = tree.treeEdgeIterator();
        while (iterator.hasNext()) {
            MergeableAddressableHeap<Double, BlossomVEdge> currentPlusMinusHeap;
            BlossomVTreeEdge treeEdge = iterator.next();
            BlossomVTree opposite = treeEdge.head[iterator.getCurrentDirection()];
            if (!treeEdge.plusPlusEdges.isEmpty()) {
                BlossomVEdge plusPlusEdge = (BlossomVEdge)treeEdge.plusPlusEdges.findMin().getValue();
                if (plusPlusEdge.slack - opposite.eps < epsAugment) {
                    epsAugment = plusPlusEdge.slack - opposite.eps;
                    augmentEdge = plusPlusEdge;
                }
            }
            if ((currentPlusMinusHeap = treeEdge.getCurrentPlusMinusHeap(opposite.currentDirection)).isEmpty()) continue;
            BlossomVEdge edge = (BlossomVEdge)currentPlusMinusHeap.findMin().getValue();
            if (!(edge.slack + opposite.eps < eps)) continue;
            eps = edge.slack + opposite.eps;
        }
        if (eps > epsAugment) {
            eps = epsAugment;
        }
        if (eps > 1.0E10) {
            throw new IllegalArgumentException("There is no perfect matching in the specified graph");
        }
        if (eps > tree.eps) {
            delta = eps - tree.eps;
            tree.eps = eps;
        }
        this.state.statistics.dualUpdatesTime += System.nanoTime() - start;
        if (augmentEdge != null && epsAugment <= tree.eps) {
            this.primalUpdater.augment(augmentEdge);
            return false;
        }
        return delta > 1.0E-9;
    }

    private BlossomVEdge updateDualsConnectedComponents() {
        BlossomVTree dummyTree = new BlossomVTree();
        BlossomVEdge augmentEdge = null;
        double augmentEps = 1.0E100;
        BlossomVNode root = this.state.nodes[this.state.nodeNum].treeSiblingNext;
        while (root != null) {
            root.tree.nextTree = null;
            root = root.treeSiblingNext;
        }
        root = this.state.nodes[this.state.nodeNum].treeSiblingNext;
        while (root != null) {
            BlossomVTree startTree = root.tree;
            if (startTree.nextTree == null) {
                double eps = startTree.accumulatedEps;
                startTree.nextTree = startTree;
                BlossomVTree connectedComponentLast = startTree;
                BlossomVTree currentTree = startTree;
                while (true) {
                    BlossomVTree.TreeEdgeIterator iterator = currentTree.treeEdgeIterator();
                    while (iterator.hasNext()) {
                        double oppositeEps;
                        BlossomVTreeEdge currentEdge = iterator.next();
                        int dir = iterator.getCurrentDirection();
                        BlossomVTree opposite = currentEdge.head[dir];
                        double plusPlusEps = 1.0E100;
                        int dirRev = 1 - dir;
                        if (!currentEdge.plusPlusEdges.isEmpty() && augmentEps > (plusPlusEps = (Double)currentEdge.plusPlusEdges.findMin().getKey() - currentTree.eps - opposite.eps)) {
                            augmentEps = plusPlusEps;
                            augmentEdge = (BlossomVEdge)currentEdge.plusPlusEdges.findMin().getValue();
                        }
                        if (opposite.nextTree != null && opposite.nextTree != dummyTree) {
                            if (!(2.0 * eps > plusPlusEps)) continue;
                            eps = plusPlusEps / 2.0;
                            continue;
                        }
                        double[] plusMinusEps = new double[2];
                        plusMinusEps[dir] = 1.0E100;
                        if (!currentEdge.getCurrentPlusMinusHeap(dir).isEmpty()) {
                            plusMinusEps[dir] = (Double)currentEdge.getCurrentPlusMinusHeap(dir).findMin().getKey() - currentTree.eps + opposite.eps;
                        }
                        plusMinusEps[dirRev] = 1.0E100;
                        if (!currentEdge.getCurrentPlusMinusHeap(dirRev).isEmpty()) {
                            plusMinusEps[dirRev] = (Double)currentEdge.getCurrentPlusMinusHeap(dirRev).findMin().getKey() - opposite.eps + currentTree.eps;
                        }
                        if (opposite.nextTree == dummyTree) {
                            oppositeEps = opposite.accumulatedEps;
                        } else if (plusMinusEps[0] > 0.0 && plusMinusEps[1] > 0.0) {
                            oppositeEps = 0.0;
                        } else {
                            connectedComponentLast.nextTree = opposite;
                            connectedComponentLast = opposite.nextTree = opposite;
                            if (!(eps > opposite.accumulatedEps)) continue;
                            eps = opposite.accumulatedEps;
                            continue;
                        }
                        if (eps > plusPlusEps - oppositeEps) {
                            eps = plusPlusEps - oppositeEps;
                        }
                        if (!(eps > plusMinusEps[dir] + oppositeEps)) continue;
                        eps = plusMinusEps[dir] + oppositeEps;
                    }
                    if (currentTree.nextTree == currentTree) break;
                    currentTree = currentTree.nextTree;
                }
                if (eps > 1.0E10) {
                    throw new IllegalArgumentException("There is no perfect matching in the specified graph");
                }
                BlossomVTree nextTree = startTree;
                do {
                    currentTree = nextTree;
                    nextTree = nextTree.nextTree;
                    currentTree.nextTree = dummyTree;
                    currentTree.accumulatedEps = eps;
                } while (currentTree != nextTree);
            }
            root = root.treeSiblingNext;
        }
        if (augmentEdge != null && augmentEps - augmentEdge.head[0].tree.accumulatedEps - augmentEdge.head[1].tree.accumulatedEps <= 0.0) {
            return augmentEdge;
        }
        return null;
    }

    private BlossomVEdge multipleTreeFixedDelta() {
        BlossomVEdge augmentEdge = null;
        double eps = 1.0E100;
        double augmentEps = 1.0E100;
        BlossomVNode root = this.state.nodes[this.state.nodeNum].treeSiblingNext;
        while (root != null) {
            BlossomVTree tree = root.tree;
            double treeEps = tree.eps;
            eps = Math.min(eps, tree.accumulatedEps);
            BlossomVTreeEdge outgoingTreeEdge = tree.first[0];
            while (outgoingTreeEdge != null) {
                if (!outgoingTreeEdge.plusPlusEdges.isEmpty()) {
                    BlossomVEdge varEdge = (BlossomVEdge)outgoingTreeEdge.plusPlusEdges.findMin().getValue();
                    double slack = varEdge.slack - treeEps - outgoingTreeEdge.head[0].eps;
                    eps = Math.min(eps, slack / 2.0);
                    if (augmentEps > slack) {
                        augmentEps = slack;
                        augmentEdge = varEdge;
                    }
                }
                outgoingTreeEdge = outgoingTreeEdge.next[0];
            }
            root = root.treeSiblingNext;
        }
        if (eps > 1.0E10) {
            throw new IllegalArgumentException("There is no perfect matching in the specified graph");
        }
        root = this.state.nodes[this.state.nodeNum].treeSiblingNext;
        while (root != null) {
            root.tree.accumulatedEps = eps;
            root = root.treeSiblingNext;
        }
        if (augmentEps <= 2.0 * eps) {
            return augmentEdge;
        }
        return null;
    }
}

