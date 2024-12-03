/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.txncollection;

public interface CollectionTxnOperation {
    public long getItemId();

    public boolean isRemoveOperation();
}

