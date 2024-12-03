/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.wan.merkletree;

interface MerkleTreeView {
    public int getNodeHash(int var1);

    public int depth();
}

