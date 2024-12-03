/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.impl;

import com.hazelcast.query.impl.Index;
import com.hazelcast.query.impl.Indexes;
import com.hazelcast.query.impl.predicates.PredicateUtils;

public class QueryContext {
    protected Indexes indexes;
    protected int ownedPartitionCount = -1;

    public QueryContext(Indexes indexes, int ownedPartitionCount) {
        this.indexes = indexes;
        this.ownedPartitionCount = ownedPartitionCount;
    }

    QueryContext() {
    }

    public int getOwnedPartitionCount() {
        return this.ownedPartitionCount;
    }

    public void setOwnedPartitionCount(int ownedPartitionCount) {
        this.ownedPartitionCount = ownedPartitionCount;
    }

    void attachTo(Indexes indexes, int ownedPartitionCount) {
        this.indexes = indexes;
        this.ownedPartitionCount = ownedPartitionCount;
    }

    void applyPerQueryStats() {
    }

    public Index getIndex(String attribute) {
        return this.matchIndex(PredicateUtils.canonicalizeAttribute(attribute), IndexMatchHint.NONE);
    }

    public Index matchIndex(String pattern, IndexMatchHint matchHint) {
        return this.indexes.matchIndex(pattern, matchHint, this.ownedPartitionCount);
    }

    public static enum IndexMatchHint {
        NONE,
        PREFER_UNORDERED,
        PREFER_ORDERED,
        EXACT_NAME;

    }
}

