/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.matching.blossom.v5;

import org.jgrapht.alg.matching.blossom.v5.BlossomVEdge;
import org.jgrapht.alg.matching.blossom.v5.BlossomVNode;
import org.jgrapht.alg.matching.blossom.v5.BlossomVState;
import org.jgrapht.alg.matching.blossom.v5.BlossomVTree;
import org.jgrapht.alg.matching.blossom.v5.BlossomVTreeEdge;

class BlossomVPrimalUpdater<V, E> {
    private BlossomVState<V, E> state;

    public BlossomVPrimalUpdater(BlossomVState<V, E> state) {
        this.state = state;
    }

    public void grow(BlossomVEdge growEdge, boolean recursiveGrow, boolean immediateAugment) {
        long start = System.nanoTime();
        int initialTreeNum = this.state.treeNum;
        int dirToMinusNode = growEdge.head[0].isInfinityNode() ? 0 : 1;
        BlossomVNode nodeInTheTree = growEdge.head[1 - dirToMinusNode];
        BlossomVNode minusNode = growEdge.head[dirToMinusNode];
        BlossomVNode plusNode = minusNode.getOppositeMatched();
        nodeInTheTree.addChild(minusNode, growEdge, true);
        minusNode.addChild(plusNode, minusNode.matched, true);
        BlossomVNode stop = plusNode;
        while (true) {
            minusNode.label = BlossomVNode.Label.MINUS;
            plusNode.label = BlossomVNode.Label.PLUS;
            plusNode.isMarked = false;
            minusNode.isMarked = false;
            this.processMinusNodeGrow(minusNode);
            this.processPlusNodeGrow(plusNode, recursiveGrow, immediateAugment);
            if (initialTreeNum != this.state.treeNum) break;
            if (plusNode.firstTreeChild != null) {
                minusNode = plusNode.firstTreeChild;
                plusNode = minusNode.getOppositeMatched();
                continue;
            }
            while (plusNode != stop && plusNode.treeSiblingNext == null) {
                plusNode = plusNode.getTreeParent();
            }
            if (!plusNode.isMinusNode()) break;
            minusNode = plusNode.treeSiblingNext;
            plusNode = minusNode.getOppositeMatched();
        }
        this.state.statistics.growTime += System.nanoTime() - start;
    }

    public void augment(BlossomVEdge augmentEdge) {
        long start = System.nanoTime();
        for (int dir = 0; dir < 2; ++dir) {
            BlossomVNode node = augmentEdge.head[dir];
            this.augmentBranch(node, augmentEdge);
            node.matched = augmentEdge;
        }
        this.state.statistics.augmentTime += System.nanoTime() - start;
    }

    public BlossomVNode shrink(BlossomVEdge blossomFormingEdge, boolean immediateAugment) {
        long start = System.nanoTime();
        BlossomVNode blossomRoot = this.findBlossomRoot(blossomFormingEdge);
        BlossomVTree tree = blossomRoot.tree;
        BlossomVNode blossom = new BlossomVNode(this.state.nodeNum + this.state.blossomNum);
        blossom.tree = tree;
        blossom.isBlossom = true;
        blossom.isOuter = true;
        blossom.isTreeRoot = blossomRoot.isTreeRoot;
        blossom.dual = -tree.eps;
        if (blossom.isTreeRoot) {
            tree.root = blossom;
        } else {
            blossom.matched = blossomRoot.matched;
        }
        BlossomVEdge.BlossomNodesIterator iterator = blossomFormingEdge.blossomNodesIterator(blossomRoot);
        while (iterator.hasNext()) {
            iterator.next().isMarked = true;
        }
        BlossomVEdge augmentEdge = this.updateTreeStructure(blossomRoot, blossomFormingEdge, blossom);
        this.setBlossomSiblings(blossomRoot, blossomFormingEdge);
        blossomRoot.isMarked = false;
        blossomRoot.isProcessed = false;
        BlossomVNode current = blossomRoot.blossomSibling.getOpposite(blossomRoot);
        while (current != blossomRoot) {
            current.isMarked = false;
            current.isProcessed = false;
            current = current.blossomSibling.getOpposite(current);
        }
        blossomRoot.matched = null;
        ++this.state.statistics.shrinkNum;
        ++this.state.blossomNum;
        this.state.statistics.shrinkTime += System.nanoTime() - start;
        if (augmentEdge != null && immediateAugment) {
            this.augment(augmentEdge);
        }
        return blossom;
    }

    public void expand(BlossomVNode blossom, boolean immediateAugment) {
        BlossomVNode blossomRoot;
        long start = System.nanoTime();
        BlossomVTree tree = blossom.tree;
        double eps = tree.eps;
        blossom.dual -= eps;
        blossom.tree.removeMinusBlossom(blossom);
        BlossomVNode branchesEndpoint = blossom.parentEdge.getCurrentOriginal(blossom).getPenultimateBlossom();
        BlossomVNode current = blossomRoot = blossom.matched.getCurrentOriginal(blossom).getPenultimateBlossom();
        do {
            current.isMarked = true;
        } while ((current = current.blossomSibling.getOpposite(current)) != blossomRoot);
        blossom.removeFromChildList();
        BlossomVNode.IncidentEdgeIterator iterator = blossom.incidentEdgesIterator();
        while (iterator.hasNext()) {
            BlossomVEdge edge = iterator.next();
            BlossomVNode penultimateChild = edge.headOriginal[1 - iterator.getDir()].getPenultimateBlossomAndFixBlossomGrandparent();
            edge.moveEdgeTail(blossom, penultimateChild);
        }
        if (!this.forwardDirection(blossomRoot, branchesEndpoint)) {
            this.reverseBlossomSiblings(blossomRoot);
        }
        this.expandOddBranch(blossomRoot, branchesEndpoint, tree);
        BlossomVEdge augmentEdge = this.expandEvenBranch(blossomRoot, branchesEndpoint, blossom);
        current = blossomRoot;
        do {
            current.isMarked = false;
            current.isProcessed = false;
        } while ((current = current.blossomSibling.getOpposite(current)) != blossomRoot);
        ++this.state.statistics.expandNum;
        ++this.state.removedNum;
        this.state.statistics.expandTime += System.nanoTime() - start;
        if (immediateAugment && augmentEdge != null) {
            this.augment(augmentEdge);
        }
    }

    private void processMinusNodeGrow(BlossomVNode minusNode) {
        double eps = minusNode.tree.eps;
        minusNode.dual += eps;
        if (minusNode.isBlossom) {
            minusNode.tree.addMinusBlossom(minusNode);
        }
        BlossomVNode.IncidentEdgeIterator iterator = minusNode.incidentEdgesIterator();
        while (iterator.hasNext()) {
            BlossomVEdge edge = iterator.next();
            BlossomVNode opposite = edge.head[iterator.getDir()];
            edge.slack -= eps;
            if (!opposite.isPlusNode()) continue;
            if (opposite.tree != minusNode.tree) {
                if (opposite.tree.currentEdge == null) {
                    BlossomVTree.addTreeEdge(minusNode.tree, opposite.tree);
                }
                opposite.tree.removePlusInfinityEdge(edge);
                opposite.tree.currentEdge.addToCurrentMinusPlusHeap(edge, opposite.tree.currentDirection);
                continue;
            }
            if (opposite == minusNode.getOppositeMatched()) continue;
            minusNode.tree.removePlusInfinityEdge(edge);
        }
    }

    private void processPlusNodeGrow(BlossomVNode node, boolean recursiveGrow, boolean immediateAugment) {
        double eps = node.tree.eps;
        node.dual -= eps;
        BlossomVEdge augmentEdge = null;
        BlossomVNode.IncidentEdgeIterator iterator = node.incidentEdgesIterator();
        while (iterator.hasNext()) {
            BlossomVEdge edge = iterator.next();
            BlossomVNode opposite = edge.head[iterator.getDir()];
            edge.slack += eps;
            if (opposite.isPlusNode()) {
                if (opposite.tree == node.tree) {
                    node.tree.removePlusInfinityEdge(edge);
                    node.tree.addPlusPlusEdge(edge);
                    continue;
                }
                if (opposite.tree.currentEdge == null) {
                    BlossomVTree.addTreeEdge(node.tree, opposite.tree);
                }
                opposite.tree.removePlusInfinityEdge(edge);
                opposite.tree.currentEdge.addPlusPlusEdge(edge);
                if (!(edge.slack <= node.tree.eps + opposite.tree.eps)) continue;
                augmentEdge = edge;
                continue;
            }
            if (opposite.isMinusNode()) {
                if (opposite.tree == node.tree) continue;
                if (opposite.tree.currentEdge == null) {
                    BlossomVTree.addTreeEdge(node.tree, opposite.tree);
                }
                opposite.tree.currentEdge.addToCurrentPlusMinusHeap(edge, opposite.tree.currentDirection);
                continue;
            }
            if (!opposite.isInfinityNode()) continue;
            node.tree.addPlusInfinityEdge(edge);
            if (!recursiveGrow || !(edge.slack <= eps) || edge.getOpposite((BlossomVNode)node).isMarked) continue;
            BlossomVNode minusNode = edge.getOpposite(node);
            BlossomVNode plusNode = minusNode.getOppositeMatched();
            plusNode.isMarked = true;
            minusNode.isMarked = true;
            node.addChild(minusNode, edge, true);
            minusNode.addChild(plusNode, minusNode.matched, true);
        }
        if (immediateAugment && augmentEdge != null) {
            this.augment(augmentEdge);
        }
        ++this.state.statistics.growNum;
    }

    private BlossomVEdge expandEvenBranch(BlossomVNode blossomRoot, BlossomVNode branchesEndpoint, BlossomVNode blossom) {
        BlossomVNode current;
        BlossomVEdge augmentEdge = null;
        BlossomVTree tree = blossom.tree;
        blossomRoot.matched = blossom.matched;
        blossomRoot.tree = tree;
        blossomRoot.addChild(blossom.matched.getOpposite(blossomRoot), blossomRoot.matched, false);
        BlossomVNode prevNode = current = blossomRoot;
        current.label = BlossomVNode.Label.MINUS;
        current.isOuter = true;
        current.parentEdge = blossom.parentEdge;
        while (current != branchesEndpoint) {
            current = current.blossomSibling.getOpposite(current);
            current.label = BlossomVNode.Label.PLUS;
            current.isOuter = true;
            current.tree = tree;
            current.matched = current.blossomSibling;
            BlossomVEdge prevMatched = current.blossomSibling;
            current.addChild(prevNode, prevNode.blossomSibling, false);
            prevNode = current;
            current = current.blossomSibling.getOpposite(current);
            current.label = BlossomVNode.Label.MINUS;
            current.isOuter = true;
            current.tree = tree;
            current.matched = prevMatched;
            current.addChild(prevNode, prevNode.blossomSibling, false);
            prevNode = current;
        }
        blossom.parentEdge.getOpposite(branchesEndpoint).addChild(branchesEndpoint, blossom.parentEdge, false);
        current = blossomRoot;
        this.expandMinusNode(current);
        while (current != branchesEndpoint) {
            BlossomVEdge edge = this.expandPlusNode(current = current.blossomSibling.getOpposite(current));
            if (edge != null) {
                augmentEdge = edge;
            }
            current.isProcessed = true;
            current = current.blossomSibling.getOpposite(current);
            this.expandMinusNode(current);
        }
        return augmentEdge;
    }

    private void expandOddBranch(BlossomVNode blossomRoot, BlossomVNode branchesEndpoint, BlossomVTree tree) {
        BlossomVNode current = branchesEndpoint.blossomSibling.getOpposite(branchesEndpoint);
        while (current != blossomRoot) {
            current.label = BlossomVNode.Label.INFINITY;
            current.isOuter = true;
            current.tree = null;
            current.matched = current.blossomSibling;
            BlossomVEdge prevMatched = current.blossomSibling;
            this.expandInfinityNode(current, tree);
            current = current.blossomSibling.getOpposite(current);
            current.label = BlossomVNode.Label.INFINITY;
            current.isOuter = true;
            current.tree = null;
            current.matched = prevMatched;
            this.expandInfinityNode(current, tree);
            current = current.blossomSibling.getOpposite(current);
        }
    }

    private BlossomVEdge expandPlusNode(BlossomVNode plusNode) {
        BlossomVEdge augmentEdge = null;
        double eps = plusNode.tree.eps;
        plusNode.dual -= eps;
        BlossomVNode.IncidentEdgeIterator iterator = plusNode.incidentEdgesIterator();
        while (iterator.hasNext()) {
            BlossomVEdge edge = iterator.next();
            BlossomVNode opposite = edge.head[iterator.getDir()];
            if (opposite.isMarked && opposite.isPlusNode()) {
                if (!opposite.isProcessed) {
                    edge.slack += 2.0 * eps;
                }
            } else if (!opposite.isMarked) {
                edge.slack += 2.0 * eps;
            } else if (!opposite.isMinusNode()) {
                edge.slack += eps;
            }
            if (opposite.isPlusNode()) {
                if (opposite.tree == plusNode.tree) {
                    if (opposite.isProcessed) continue;
                    plusNode.tree.addPlusPlusEdge(edge);
                    continue;
                }
                opposite.tree.currentEdge.removeFromCurrentMinusPlusHeap(edge);
                opposite.tree.currentEdge.addPlusPlusEdge(edge);
                if (!(edge.slack <= eps + opposite.tree.eps)) continue;
                augmentEdge = edge;
                continue;
            }
            if (opposite.isMinusNode()) {
                if (opposite.tree == plusNode.tree) continue;
                if (opposite.tree.currentEdge == null) {
                    BlossomVTree.addTreeEdge(plusNode.tree, opposite.tree);
                }
                opposite.tree.currentEdge.addToCurrentPlusMinusHeap(edge, opposite.tree.currentDirection);
                continue;
            }
            plusNode.tree.addPlusInfinityEdge(edge);
        }
        return augmentEdge;
    }

    private void expandMinusNode(BlossomVNode minusNode) {
        double eps = minusNode.tree.eps;
        minusNode.dual += eps;
        if (minusNode.isBlossom) {
            minusNode.tree.addMinusBlossom(minusNode);
        }
        BlossomVNode.IncidentEdgeIterator iterator = minusNode.incidentEdgesIterator();
        while (iterator.hasNext()) {
            BlossomVEdge edge = iterator.next();
            BlossomVNode opposite = edge.head[iterator.getDir()];
            if (!opposite.isMarked || opposite.isPlusNode()) continue;
            edge.slack -= eps;
        }
    }

    private void expandInfinityNode(BlossomVNode infinityNode, BlossomVTree tree) {
        double eps = tree.eps;
        BlossomVNode.IncidentEdgeIterator iterator = infinityNode.incidentEdgesIterator();
        while (iterator.hasNext()) {
            BlossomVEdge edge = iterator.next();
            BlossomVNode opposite = edge.head[iterator.getDir()];
            if (opposite.isMarked) continue;
            edge.slack += eps;
            if (!opposite.isPlusNode()) continue;
            if (opposite.tree != tree) {
                opposite.tree.currentEdge.removeFromCurrentMinusPlusHeap(edge);
            }
            opposite.tree.addPlusInfinityEdge(edge);
        }
    }

    private void augmentBranch(BlossomVNode firstNode, BlossomVEdge augmentEdge) {
        BlossomVTree tree = firstNode.tree;
        double eps = tree.eps;
        BlossomVNode root = tree.root;
        tree.setCurrentEdges();
        BlossomVTree.TreeNodeIterator treeNodeIterator = tree.treeNodeIterator();
        while (treeNodeIterator.hasNext()) {
            BlossomVNode node = treeNodeIterator.next();
            if (!node.isMarked) {
                node.dual = node.isPlusNode() ? (node.dual += eps) : (node.dual -= eps);
                BlossomVNode.IncidentEdgeIterator incidentEdgeIterator = node.incidentEdgesIterator();
                while (incidentEdgeIterator.hasNext()) {
                    BlossomVTreeEdge treeEdge;
                    BlossomVEdge edge = incidentEdgeIterator.next();
                    int dir = incidentEdgeIterator.getDir();
                    BlossomVNode opposite = edge.head[dir];
                    BlossomVTree oppositeTree = opposite.tree;
                    if (node.isPlusNode()) {
                        edge.slack -= eps;
                        if (oppositeTree == null || oppositeTree == tree) continue;
                        treeEdge = oppositeTree.currentEdge;
                        if (opposite.isPlusNode()) {
                            treeEdge.removeFromPlusPlusHeap(edge);
                            oppositeTree.addPlusInfinityEdge(edge);
                            continue;
                        }
                        if (!opposite.isMinusNode()) continue;
                        treeEdge.removeFromCurrentPlusMinusHeap(edge);
                        continue;
                    }
                    edge.slack += eps;
                    if (oppositeTree == null || oppositeTree == tree || !opposite.isPlusNode()) continue;
                    treeEdge = oppositeTree.currentEdge;
                    treeEdge.removeFromCurrentMinusPlusHeap(edge);
                    oppositeTree.addPlusInfinityEdge(edge);
                }
                node.label = BlossomVNode.Label.INFINITY;
                continue;
            }
            node.isMarked = false;
        }
        BlossomVTree.TreeEdgeIterator treeEdgeIterator = tree.treeEdgeIterator();
        while (treeEdgeIterator.hasNext()) {
            BlossomVTreeEdge treeEdge = treeEdgeIterator.next();
            int dir = treeEdgeIterator.getCurrentDirection();
            BlossomVTree opposite = treeEdge.head[dir];
            opposite.currentEdge = null;
            opposite.plusPlusEdges.meld(treeEdge.plusPlusEdges);
            opposite.plusPlusEdges.meld(treeEdge.getCurrentMinusPlusHeap(dir));
            treeEdge.removeFromTreeEdgeList();
        }
        BlossomVEdge matchedEdge = augmentEdge;
        BlossomVNode plusNode = firstNode;
        BlossomVNode minusNode = plusNode.getTreeParent();
        while (minusNode != null) {
            plusNode.matched = matchedEdge;
            minusNode.matched = matchedEdge = minusNode.parentEdge;
            plusNode = minusNode.getTreeParent();
            minusNode = plusNode.getTreeParent();
        }
        root.matched = matchedEdge;
        root.removeFromChildList();
        root.isTreeRoot = false;
        --this.state.treeNum;
    }

    private BlossomVEdge updateTreeStructure(BlossomVNode blossomRoot, BlossomVEdge blossomFormingEdge, BlossomVNode blossom) {
        BlossomVEdge augmentEdge = null;
        BlossomVTree tree = blossomRoot.tree;
        BlossomVEdge.BlossomNodesIterator iterator = blossomFormingEdge.blossomNodesIterator(blossomRoot);
        while (iterator.hasNext()) {
            BlossomVNode blossomNode = iterator.next();
            if (blossomNode != blossomRoot) {
                if (blossomNode.isPlusNode()) {
                    blossomNode.removeFromChildList();
                    blossomNode.moveChildrenTo(blossom);
                    BlossomVEdge edge = this.shrinkPlusNode(blossomNode, blossom);
                    if (edge != null) {
                        augmentEdge = edge;
                    }
                    blossomNode.isProcessed = true;
                } else {
                    if (blossomNode.isBlossom) {
                        tree.removeMinusBlossom(blossomNode);
                    }
                    blossomNode.removeFromChildList();
                    this.shrinkMinusNode(blossomNode, blossom);
                }
            }
            blossomNode.blossomGrandparent = blossomNode.blossomParent = blossom;
        }
        blossomRoot.removeFromChildList();
        if (!blossomRoot.isTreeRoot) {
            blossomRoot.getTreeParent().addChild(blossom, blossomRoot.parentEdge, false);
        } else {
            blossom.treeSiblingNext = blossomRoot.treeSiblingNext;
            blossom.treeSiblingPrev = blossomRoot.treeSiblingPrev;
            blossomRoot.treeSiblingPrev.treeSiblingNext = blossom;
            if (blossomRoot.treeSiblingNext != null) {
                blossomRoot.treeSiblingNext.treeSiblingPrev = blossom;
            }
        }
        blossomRoot.moveChildrenTo(blossom);
        BlossomVEdge edge = this.shrinkPlusNode(blossomRoot, blossom);
        if (edge != null) {
            augmentEdge = edge;
        }
        blossomRoot.isTreeRoot = false;
        return augmentEdge;
    }

    private BlossomVEdge shrinkPlusNode(BlossomVNode plusNode, BlossomVNode blossom) {
        BlossomVEdge augmentEdge = null;
        BlossomVTree tree = plusNode.tree;
        double eps = tree.eps;
        plusNode.dual += eps;
        BlossomVNode.IncidentEdgeIterator iterator = plusNode.incidentEdgesIterator();
        while (iterator.hasNext()) {
            BlossomVEdge edge = iterator.next();
            BlossomVNode opposite = edge.head[iterator.getDir()];
            if (!opposite.isMarked) {
                edge.moveEdgeTail(plusNode, blossom);
                if (opposite.tree == tree || !opposite.isPlusNode() || !(edge.slack <= eps + opposite.tree.eps)) continue;
                augmentEdge = edge;
                continue;
            }
            if (!opposite.isPlusNode()) continue;
            if (!opposite.isProcessed) {
                tree.removePlusPlusEdge(edge);
            }
            edge.slack -= eps;
        }
        return augmentEdge;
    }

    private void shrinkMinusNode(BlossomVNode minusNode, BlossomVNode blossom) {
        BlossomVTree tree = minusNode.tree;
        double eps = tree.eps;
        minusNode.dual -= eps;
        BlossomVNode.IncidentEdgeIterator iterator = minusNode.incidentEdgesIterator();
        while (iterator.hasNext()) {
            BlossomVEdge edge = iterator.next();
            BlossomVNode opposite = edge.head[iterator.getDir()];
            BlossomVTree oppositeTree = opposite.tree;
            if (!opposite.isMarked) {
                edge.moveEdgeTail(minusNode, blossom);
                edge.slack += 2.0 * eps;
                if (opposite.tree == tree) {
                    if (!opposite.isPlusNode()) continue;
                    tree.addPlusPlusEdge(edge);
                    continue;
                }
                if (opposite.isPlusNode()) {
                    oppositeTree.currentEdge.removeFromCurrentMinusPlusHeap(edge);
                    oppositeTree.currentEdge.addPlusPlusEdge(edge);
                    continue;
                }
                if (opposite.isMinusNode()) {
                    if (oppositeTree.currentEdge == null) {
                        BlossomVTree.addTreeEdge(tree, oppositeTree);
                    }
                    oppositeTree.currentEdge.addToCurrentPlusMinusHeap(edge, oppositeTree.currentDirection);
                    continue;
                }
                tree.addPlusInfinityEdge(edge);
                continue;
            }
            if (!opposite.isMinusNode()) continue;
            edge.slack += eps;
        }
    }

    private void setBlossomSiblings(BlossomVNode blossomRoot, BlossomVEdge blossomFormingEdge) {
        BlossomVEdge prevEdge = blossomFormingEdge;
        BlossomVEdge.BlossomNodesIterator iterator = blossomFormingEdge.blossomNodesIterator(blossomRoot);
        while (iterator.hasNext()) {
            BlossomVNode current = iterator.next();
            if (iterator.getCurrentDirection() == 0) {
                current.blossomSibling = prevEdge;
                prevEdge = current.parentEdge;
                continue;
            }
            current.blossomSibling = current.parentEdge;
        }
    }

    BlossomVNode findBlossomRoot(BlossomVEdge blossomFormingEdge) {
        BlossomVNode jumpNode;
        BlossomVNode upperBound;
        BlossomVNode root;
        BlossomVNode[] endPoints = new BlossomVNode[]{blossomFormingEdge.head[0], blossomFormingEdge.head[1]};
        int branch = 0;
        while (true) {
            if (endPoints[branch].isMarked) {
                root = endPoints[branch];
                upperBound = endPoints[1 - branch];
                break;
            }
            endPoints[branch].isMarked = true;
            if (endPoints[branch].isTreeRoot) {
                upperBound = endPoints[branch];
                jumpNode = endPoints[1 - branch];
                while (!jumpNode.isMarked) {
                    jumpNode = jumpNode.getTreeGrandparent();
                }
                root = jumpNode;
                break;
            }
            endPoints[branch] = endPoints[branch].getTreeGrandparent();
            branch = 1 - branch;
        }
        for (jumpNode = root; jumpNode != upperBound; jumpNode = jumpNode.getTreeGrandparent()) {
            jumpNode.isMarked = false;
        }
        this.clearIsMarkedAndSetIsOuter(root, blossomFormingEdge.head[0]);
        this.clearIsMarkedAndSetIsOuter(root, blossomFormingEdge.head[1]);
        return root;
    }

    private void clearIsMarkedAndSetIsOuter(BlossomVNode root, BlossomVNode start) {
        while (start != root) {
            start.isMarked = false;
            start.isOuter = false;
            start = start.getTreeParent();
            start.isOuter = false;
            start = start.getTreeParent();
        }
        root.isOuter = false;
        root.isMarked = false;
    }

    private void reverseBlossomSiblings(BlossomVNode blossomNode) {
        BlossomVEdge prevEdge = blossomNode.blossomSibling;
        BlossomVNode current = blossomNode;
        do {
            current = prevEdge.getOpposite(current);
            BlossomVEdge tmpEdge = prevEdge;
            prevEdge = current.blossomSibling;
            current.blossomSibling = tmpEdge;
        } while (current != blossomNode);
    }

    private boolean forwardDirection(BlossomVNode blossomRoot, BlossomVNode branchesEndpoint) {
        int hops = 0;
        BlossomVNode current = blossomRoot;
        while (current != branchesEndpoint) {
            ++hops;
            current = current.blossomSibling.getOpposite(current);
        }
        return (hops & 1) == 0;
    }

    public void printBlossomNodes(BlossomVNode blossomNode) {
        System.out.println("Printing blossom nodes");
        BlossomVNode current = blossomNode;
        do {
            System.out.println(current);
        } while ((current = current.blossomSibling.getOpposite(current)) != blossomNode);
    }
}

