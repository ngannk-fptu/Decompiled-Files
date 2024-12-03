/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.wan.merkletree;

import com.hazelcast.util.JVMUtil;
import com.hazelcast.util.collection.OAHashSet;
import com.hazelcast.util.function.Consumer;
import com.hazelcast.wan.merkletree.AbstractMerkleTreeView;
import com.hazelcast.wan.merkletree.MerkleTree;
import com.hazelcast.wan.merkletree.MerkleTreeUtil;
import java.util.Arrays;

public class ArrayMerkleTree
extends AbstractMerkleTreeView
implements MerkleTree {
    private final OAHashSet<Object>[] leafKeys;
    private final int leafLevel;
    private volatile long footprint;

    public ArrayMerkleTree(int depth) {
        super(depth);
        this.leafLevel = depth - 1;
        int leaves = MerkleTreeUtil.getNodesOnLevel(this.leafLevel);
        this.leafKeys = new OAHashSet[leaves];
        for (int i = 0; i < leaves; ++i) {
            this.leafKeys[i] = new OAHashSet(1);
        }
        this.initializeFootprint();
    }

    @Override
    public void updateAdd(Object key, Object value) {
        int keyHash = key.hashCode();
        int valueHash = value.hashCode();
        int leafOrder = MerkleTreeUtil.getLeafOrderForHash(keyHash, this.leafLevel);
        int leafCurrentHash = this.getNodeHash(leafOrder);
        int leafNewHash = MerkleTreeUtil.addHash(leafCurrentHash, valueHash);
        this.setNodeHash(leafOrder, leafNewHash);
        this.addKeyToLeaf(leafOrder, keyHash, key);
        this.updateBranch(leafOrder);
    }

    @Override
    public void updateReplace(Object key, Object oldValue, Object newValue) {
        int keyHash = key.hashCode();
        int oldValueHash = oldValue.hashCode();
        int newValueHash = newValue.hashCode();
        int leafOrder = MerkleTreeUtil.getLeafOrderForHash(keyHash, this.leafLevel);
        int leafCurrentHash = this.getNodeHash(leafOrder);
        int leafNewHash = MerkleTreeUtil.removeHash(leafCurrentHash, oldValueHash);
        leafNewHash = MerkleTreeUtil.addHash(leafNewHash, newValueHash);
        this.setNodeHash(leafOrder, leafNewHash);
        this.updateBranch(leafOrder);
    }

    @Override
    public void updateRemove(Object key, Object removedValue) {
        int keyHash = key.hashCode();
        int removedValueHash = removedValue.hashCode();
        int leafOrder = MerkleTreeUtil.getLeafOrderForHash(keyHash, this.leafLevel);
        int leafCurrentHash = this.getNodeHash(leafOrder);
        int leafNewHash = MerkleTreeUtil.removeHash(leafCurrentHash, removedValueHash);
        this.setNodeHash(leafOrder, leafNewHash);
        this.removeKeyFromLeaf(leafOrder, keyHash, key);
        this.updateBranch(leafOrder);
    }

    @Override
    public int getNodeHash(int nodeOrder) {
        return this.tree[nodeOrder];
    }

    @Override
    public void forEachKeyOfNode(int nodeOrder, Consumer<Object> consumer) {
        if (MerkleTreeUtil.isLeaf(nodeOrder, this.depth)) {
            this.forEachKeyOfLeaf(nodeOrder, consumer);
        } else {
            this.forEachKeyOfNonLeaf(nodeOrder, consumer);
        }
    }

    private void forEachKeyOfLeaf(int nodeOrder, Consumer<Object> consumer) {
        int relativeLeafOrder = nodeOrder - this.leafLevelOrder;
        for (Object key : this.leafKeys[relativeLeafOrder]) {
            consumer.accept(key);
        }
    }

    private void forEachKeyOfNonLeaf(int nodeOrder, Consumer<Object> consumer) {
        int leftMostLeaf = MerkleTreeUtil.getLeftMostLeafUnderNode(nodeOrder, this.depth);
        int rightMostLeaf = MerkleTreeUtil.getRightMostLeafUnderNode(nodeOrder, this.depth);
        for (int leafOrder = leftMostLeaf; leafOrder <= rightMostLeaf; ++leafOrder) {
            int relativeLeafOrder = leafOrder - this.leafLevelOrder;
            for (Object key : this.leafKeys[relativeLeafOrder]) {
                consumer.accept(key);
            }
        }
    }

    @Override
    public int getNodeKeyCount(int nodeOrder) {
        if (MerkleTreeUtil.isLeaf(nodeOrder, this.depth)) {
            return this.getLeafKeyCount(nodeOrder);
        }
        return this.getNonLeafKeyCount(nodeOrder);
    }

    private int getLeafKeyCount(int nodeOrder) {
        int relativeLeafOrder = nodeOrder - this.leafLevelOrder;
        return this.leafKeys[relativeLeafOrder].size();
    }

    private int getNonLeafKeyCount(int nodeOrder) {
        int leftMostLeaf = MerkleTreeUtil.getLeftMostLeafUnderNode(nodeOrder, this.depth);
        int rightMostLeaf = MerkleTreeUtil.getRightMostLeafUnderNode(nodeOrder, this.depth);
        int count = 0;
        for (int leafOrder = leftMostLeaf; leafOrder <= rightMostLeaf; ++leafOrder) {
            int relativeLeafOrder = leafOrder - this.leafLevelOrder;
            count += this.leafKeys[relativeLeafOrder].size();
        }
        return count;
    }

    @Override
    public long footprint() {
        return this.footprint;
    }

    @Override
    public void clear() {
        Arrays.fill(this.tree, 0);
        for (OAHashSet<Object> leafKeysSet : this.leafKeys) {
            leafKeysSet.clear();
        }
    }

    private void updateBranch(int leafOrder) {
        int nodeOrder = MerkleTreeUtil.getParentOrder(leafOrder);
        for (int level = this.leafLevel; level > 0; --level) {
            int leftChildOrder = MerkleTreeUtil.getLeftChildOrder(nodeOrder);
            int rightChildOrder = MerkleTreeUtil.getRightChildOrder(nodeOrder);
            int leftChildHash = this.getNodeHash(leftChildOrder);
            int rightChildHash = this.getNodeHash(rightChildOrder);
            int newNodeHash = MerkleTreeUtil.sumHash(leftChildHash, rightChildHash);
            this.setNodeHash(nodeOrder, newNodeHash);
            nodeOrder = MerkleTreeUtil.getParentOrder(nodeOrder);
        }
    }

    private void addKeyToLeaf(int leafOrder, int keyHash, Object key) {
        int relativeLeafOrder = leafOrder - this.leafLevelOrder;
        OAHashSet<Object> leafKeySet = this.leafKeys[relativeLeafOrder];
        long leafKeysFootprintBefore = leafKeySet.footprint();
        leafKeySet.add(key, keyHash);
        this.adjustFootprintWithLeafKeySetChange(leafKeySet.footprint(), leafKeysFootprintBefore);
    }

    private void removeKeyFromLeaf(int leafOrder, int keyHash, Object key) {
        int relativeLeafOrder = leafOrder - this.leafLevelOrder;
        OAHashSet<Object> leafKeySet = this.leafKeys[relativeLeafOrder];
        long leafKeysFootprintBefore = leafKeySet.footprint();
        leafKeySet.remove(key, keyHash);
        this.adjustFootprintWithLeafKeySetChange(leafKeySet.footprint(), leafKeysFootprintBefore);
    }

    private void adjustFootprintWithLeafKeySetChange(long currentFootprint, long footprintBeforeUpdate) {
        long footprintDelta = currentFootprint - footprintBeforeUpdate;
        if (footprintDelta != 0L) {
            this.footprint += footprintDelta;
        }
    }

    private void initializeFootprint() {
        long leafKeysSetsFootprint = 0L;
        for (OAHashSet<Object> leafKeysSet : this.leafKeys) {
            leafKeysSetsFootprint += leafKeysSet.footprint();
        }
        this.footprint = leafKeysSetsFootprint + (long)(4 * this.tree.length) + (long)(JVMUtil.REFERENCE_COST_IN_BYTES * this.leafKeys.length) + (long)JVMUtil.REFERENCE_COST_IN_BYTES + (long)JVMUtil.REFERENCE_COST_IN_BYTES + 4L + 4L + 4L + 8L;
    }
}

