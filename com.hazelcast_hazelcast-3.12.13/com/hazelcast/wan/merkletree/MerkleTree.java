/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.wan.merkletree;

import com.hazelcast.util.function.Consumer;
import com.hazelcast.wan.merkletree.MerkleTreeView;

public interface MerkleTree
extends MerkleTreeView {
    public void updateAdd(Object var1, Object var2);

    public void updateReplace(Object var1, Object var2, Object var3);

    public void updateRemove(Object var1, Object var2);

    public void forEachKeyOfNode(int var1, Consumer<Object> var2);

    public int getNodeKeyCount(int var1);

    public long footprint();

    public void clear();
}

