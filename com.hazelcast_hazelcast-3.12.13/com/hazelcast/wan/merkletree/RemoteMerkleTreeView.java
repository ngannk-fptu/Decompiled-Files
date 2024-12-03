/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.wan.merkletree;

import com.hazelcast.util.Preconditions;
import com.hazelcast.wan.merkletree.AbstractMerkleTreeView;
import com.hazelcast.wan.merkletree.MerkleTreeUtil;

public class RemoteMerkleTreeView
extends AbstractMerkleTreeView {
    RemoteMerkleTreeView(int[] remoteTreeLeaves, int depth) {
        super(depth);
        int leafLevel = depth - 1;
        int numberOfLeaves = MerkleTreeUtil.getNodesOnLevel(leafLevel);
        int leftMostLeafOrder = MerkleTreeUtil.getLeftMostNodeOrderOnLevel(leafLevel);
        Preconditions.checkTrue(remoteTreeLeaves.length >= numberOfLeaves, "The provided array can't hold a tree with depth " + depth + ". Size of the provided array should be at least " + numberOfLeaves + ". Size of the provided array: " + remoteTreeLeaves.length);
        System.arraycopy(remoteTreeLeaves, 0, this.tree, leftMostLeafOrder, numberOfLeaves);
        this.buildTree();
    }

    private void buildTree() {
        for (int nodeOrder = this.leafLevelOrder - 1; nodeOrder >= 0; --nodeOrder) {
            int leftChildOrder = MerkleTreeUtil.getLeftChildOrder(nodeOrder);
            int rightChildOrder = MerkleTreeUtil.getRightChildOrder(nodeOrder);
            int leftChildHash = this.getNodeHash(leftChildOrder);
            int rightChildHash = this.getNodeHash(rightChildOrder);
            int newNodeHash = MerkleTreeUtil.sumHash(leftChildHash, rightChildHash);
            this.setNodeHash(nodeOrder, newNodeHash);
        }
    }

    @Override
    public int getNodeHash(int nodeOrder) {
        return this.tree[nodeOrder];
    }
}

