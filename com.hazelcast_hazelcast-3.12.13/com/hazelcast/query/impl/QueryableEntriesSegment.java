/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.impl;

import com.hazelcast.query.impl.QueryableEntry;
import java.util.Collection;

public class QueryableEntriesSegment {
    private final Collection<QueryableEntry> entries;
    private final int nextTableIndexToReadFrom;

    public QueryableEntriesSegment(Collection<QueryableEntry> entries, int nextTableIndexToReadFrom) {
        this.entries = entries;
        this.nextTableIndexToReadFrom = nextTableIndexToReadFrom;
    }

    public Collection<QueryableEntry> getEntries() {
        return this.entries;
    }

    public int getNextTableIndexToReadFrom() {
        return this.nextTableIndexToReadFrom;
    }
}

