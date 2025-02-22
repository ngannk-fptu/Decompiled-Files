/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.impl;

import com.hazelcast.query.impl.QueryableEntry;
import com.hazelcast.util.SetUtil;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class OrResultSet
extends AbstractSet<QueryableEntry> {
    private static final int ENTRY_MULTIPLE = 4;
    private static final int ENTRY_MIN_SIZE = 8;
    private final List<Set<QueryableEntry>> indexedResults;
    private Set<QueryableEntry> entries;

    public OrResultSet(List<Set<QueryableEntry>> indexedResults) {
        this.indexedResults = indexedResults;
    }

    @Override
    public boolean contains(Object o) {
        for (Set<QueryableEntry> otherIndexedResult : this.indexedResults) {
            if (!otherIndexedResult.contains(o)) continue;
            return true;
        }
        return false;
    }

    @Override
    public Iterator<QueryableEntry> iterator() {
        return this.getEntries().iterator();
    }

    @Override
    public int size() {
        return this.getEntries().size();
    }

    public int estimatedSize() {
        if (this.entries == null) {
            if (this.indexedResults.isEmpty()) {
                return 0;
            }
            return this.indexedResults.get(0).size();
        }
        return this.entries.size();
    }

    private Set<QueryableEntry> getEntries() {
        if (this.entries == null) {
            if (this.indexedResults.isEmpty()) {
                this.entries = Collections.emptySet();
            } else if (this.indexedResults.size() == 1) {
                this.entries = new HashSet<QueryableEntry>((Collection)this.indexedResults.get(0));
            } else {
                this.entries = SetUtil.createHashSet(Math.max(8, this.indexedResults.size() * 4));
                for (Set<QueryableEntry> result : this.indexedResults) {
                    this.entries.addAll(result);
                }
            }
        }
        return this.entries;
    }
}

