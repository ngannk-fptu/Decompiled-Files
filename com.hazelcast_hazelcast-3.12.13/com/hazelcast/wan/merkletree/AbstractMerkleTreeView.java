/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.wan.merkletree;

import com.hazelcast.wan.merkletree.MerkleTreeUtil;
import com.hazelcast.wan.merkletree.MerkleTreeView;

abstract class AbstractMerkleTreeView
implements MerkleTreeView {
    private static final int MIN_DEPTH = 2;
    private static final int MAX_DEPTH = 27;
    protected final int[] tree;
    protected final int depth;
    protected final int leafLevelOrder;

    AbstractMerkleTreeView(int depth) {
        if (depth < 2 || depth > 27) {
            throw new IllegalArgumentException("Parameter depth " + depth + " is outside of the allowed range " + 2 + "-" + 27 + ". ");
        }
        this.leafLevelOrder = MerkleTreeUtil.getLeftMostNodeOrderOnLevel(depth - 1);
        this.depth = depth;
        int nodes = MerkleTreeUtil.getNumberOfNodes(depth);
        this.tree = new int[nodes];
    }

    protected void setNodeHash(int nodeOrder, int hash) {
        this.tree[nodeOrder] = hash;
    }

    @Override
    public int depth() {
        return this.depth;
    }
}

