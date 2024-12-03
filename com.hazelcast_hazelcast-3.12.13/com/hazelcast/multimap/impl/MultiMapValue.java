/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.multimap.impl;

import com.hazelcast.multimap.impl.MultiMapRecord;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class MultiMapValue {
    private final Collection<MultiMapRecord> collection;
    private long hits;

    public MultiMapValue(Collection<MultiMapRecord> collection) {
        this.collection = collection;
    }

    public Collection<MultiMapRecord> getCollection(boolean copyOf) {
        if (copyOf) {
            return this.getCopyOfCollection();
        }
        return this.collection;
    }

    private Collection<MultiMapRecord> getCopyOfCollection() {
        if (this.collection instanceof Set) {
            return new HashSet<MultiMapRecord>(this.collection);
        }
        if (this.collection instanceof List) {
            return new LinkedList<MultiMapRecord>(this.collection);
        }
        throw new IllegalArgumentException("No Matching CollectionProxyType!");
    }

    public void incrementHit() {
        ++this.hits;
    }

    public long getHits() {
        return this.hits;
    }

    public void setHits(long hits) {
        this.hits = hits;
    }

    public boolean containsRecordId(long recordId) {
        for (MultiMapRecord record : this.collection) {
            if (record.getRecordId() != recordId) continue;
            return true;
        }
        return false;
    }
}

