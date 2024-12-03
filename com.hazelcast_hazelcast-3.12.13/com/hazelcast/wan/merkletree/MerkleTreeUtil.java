/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.wan.merkletree;

import com.hazelcast.util.Preconditions;
import com.hazelcast.util.QuickMath;
import com.hazelcast.wan.merkletree.MerkleTreeView;
import com.hazelcast.wan.merkletree.RemoteMerkleTreeView;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;

public final class MerkleTreeUtil {
    static final int HUGE_PRIME = 2038079003;
    private static final long INT_RANGE = 0x100000000L;

    private MerkleTreeUtil() {
    }

    static int getLeafOrderForHash(int hash, int level) {
        long hashStepForLevel = MerkleTreeUtil.getNodeHashRangeOnLevel(level);
        long hashDistanceFromMin = (long)hash - Integer.MIN_VALUE;
        int steps = (int)(hashDistanceFromMin / hashStepForLevel);
        int leftMostNodeOrderOnLevel = MerkleTreeUtil.getLeftMostNodeOrderOnLevel(level);
        return leftMostNodeOrderOnLevel + steps;
    }

    static long getNodeHashRangeOnLevel(int level) {
        int nodesOnLevel = MerkleTreeUtil.getNodesOnLevel(level);
        return 0x100000000L / (long)nodesOnLevel;
    }

    static int getNodeRangeLow(int nodeOrder) {
        int level = MerkleTreeUtil.getLevelOfNode(nodeOrder);
        int leftMostLeafOrder = MerkleTreeUtil.getLeftMostNodeOrderOnLevel(level);
        int levelHashStep = (int)MerkleTreeUtil.getNodeHashRangeOnLevel(level);
        int leafOrderOnLevel = nodeOrder - leftMostLeafOrder;
        return Integer.MIN_VALUE + leafOrderOnLevel * levelHashStep;
    }

    static int getNodeRangeHigh(int nodeOrder) {
        int level = MerkleTreeUtil.getLevelOfNode(nodeOrder);
        int leftMostLeafOrder = MerkleTreeUtil.getLeftMostNodeOrderOnLevel(level);
        int levelHashStep = (int)MerkleTreeUtil.getNodeHashRangeOnLevel(level);
        int leafOrderOnLevel = nodeOrder - leftMostLeafOrder;
        return Integer.MIN_VALUE + (leafOrderOnLevel + 1) * levelHashStep - 1;
    }

    static int addHash(int originalHash, int addedHash) {
        return originalHash + 2038079003 * addedHash;
    }

    static int removeHash(int originalHash, int removedHash) {
        return originalHash - 2038079003 * removedHash;
    }

    public static int sumHash(int leftHash, int rightHash) {
        return leftHash + rightHash;
    }

    public static int getLevelOfNode(int nodeOrder) {
        return QuickMath.log2(nodeOrder + 1);
    }

    static int getLeftMostNodeOrderOnLevel(int level) {
        return (1 << level) - 1;
    }

    public static int getNodesOnLevel(int level) {
        return 1 << level;
    }

    public static int getParentOrder(int nodeOrder) {
        return nodeOrder - 1 >> 1;
    }

    public static int getLeftChildOrder(int nodeOrder) {
        return (nodeOrder << 1) + 1;
    }

    public static int getRightChildOrder(int nodeOrder) {
        return (nodeOrder << 1) + 2;
    }

    static int getNumberOfNodes(int depth) {
        return (1 << depth) - 1;
    }

    static boolean isLeaf(int nodeOrder, int depth) {
        Preconditions.checkTrue(depth > 0, "Invalid depth: " + depth);
        int leafLevel = depth - 1;
        int numberOfNodes = MerkleTreeUtil.getNumberOfNodes(depth);
        int maxNodeOrder = numberOfNodes - 1;
        Preconditions.checkTrue(nodeOrder >= 0 && nodeOrder <= maxNodeOrder, "Invalid nodeOrder: " + nodeOrder + " in a tree with depth " + depth);
        int leftMostLeafOrder = MerkleTreeUtil.getLeftMostNodeOrderOnLevel(leafLevel);
        return nodeOrder >= leftMostLeafOrder;
    }

    static int getLeftMostLeafUnderNode(int nodeOrder, int depth) {
        if (MerkleTreeUtil.isLeaf(nodeOrder, depth)) {
            return nodeOrder;
        }
        int leafLevel = depth - 1;
        int levelOfNode = MerkleTreeUtil.getLevelOfNode(nodeOrder);
        int distanceFromLeafLevel = depth - levelOfNode - 1;
        int leftMostNodeOrderOnLevel = MerkleTreeUtil.getLeftMostNodeOrderOnLevel(levelOfNode);
        int relativeLevelOrder = nodeOrder - leftMostNodeOrderOnLevel;
        int leftMostLeaf = MerkleTreeUtil.getLeftMostNodeOrderOnLevel(leafLevel);
        return leftMostLeaf + (2 << distanceFromLeafLevel - 1) * relativeLevelOrder;
    }

    static int getRightMostLeafUnderNode(int nodeOrder, int depth) {
        if (MerkleTreeUtil.isLeaf(nodeOrder, depth)) {
            return nodeOrder;
        }
        int levelOfNode = MerkleTreeUtil.getLevelOfNode(nodeOrder);
        int distanceFromLeafLevel = depth - levelOfNode - 1;
        int leftMostLeafUnderNode = MerkleTreeUtil.getLeftMostLeafUnderNode(nodeOrder, depth);
        int leavesOfSubtreeUnderNode = MerkleTreeUtil.getNodesOnLevel(distanceFromLeafLevel);
        return leftMostLeafUnderNode + leavesOfSubtreeUnderNode - 1;
    }

    public static Collection<Integer> compareTrees(MerkleTreeView local, MerkleTreeView remote) {
        int leftMostLeaf;
        LinkedList<Integer> deltaOrders = new LinkedList<Integer>();
        MerkleTreeView baseTree = local.depth() <= remote.depth() ? local : remote;
        MerkleTreeView otherTree = local.depth() <= remote.depth() ? remote : local;
        int leafLevel = baseTree.depth() - 1;
        int numberOfLeaves = MerkleTreeUtil.getNodesOnLevel(leafLevel);
        for (int leafOrder = leftMostLeaf = MerkleTreeUtil.getLeftMostNodeOrderOnLevel(leafLevel); leafOrder < leftMostLeaf + numberOfLeaves; ++leafOrder) {
            if (baseTree.getNodeHash(leafOrder) == otherTree.getNodeHash(leafOrder)) continue;
            deltaOrders.add(leafOrder);
        }
        return deltaOrders;
    }

    public static void writeLeaves(DataOutput out, MerkleTreeView merkleTreeView) throws IOException {
        int leafLevel = merkleTreeView.depth() - 1;
        int numberOfLeaves = MerkleTreeUtil.getNodesOnLevel(leafLevel);
        int leftMostLeaf = MerkleTreeUtil.getLeftMostNodeOrderOnLevel(leafLevel);
        out.writeInt(numberOfLeaves);
        for (int leafOrder = leftMostLeaf; leafOrder < leftMostLeaf + numberOfLeaves; ++leafOrder) {
            out.writeInt(merkleTreeView.getNodeHash(leafOrder));
        }
    }

    public static RemoteMerkleTreeView createRemoteMerkleTreeView(DataInput in) throws IOException {
        int numberOfLeaves = in.readInt();
        int depth = QuickMath.log2(numberOfLeaves << 1);
        int[] leaves = new int[numberOfLeaves];
        for (int i = 0; i < numberOfLeaves; ++i) {
            leaves[i] = in.readInt();
        }
        return new RemoteMerkleTreeView(leaves, depth);
    }
}

