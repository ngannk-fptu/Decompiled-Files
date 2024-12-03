/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.impl;

import com.hazelcast.nio.serialization.Data;
import com.hazelcast.query.impl.QueryableEntry;
import java.util.AbstractSet;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

public class SingleResultSet
extends AbstractSet<QueryableEntry> {
    private final Map<Data, QueryableEntry> records;

    public SingleResultSet(Map<Data, QueryableEntry> records) {
        this.records = records;
    }

    @Override
    public boolean contains(Object mapEntry) {
        if (this.records == null) {
            return false;
        }
        Data keyData = ((QueryableEntry)mapEntry).getKeyData();
        return this.records.containsKey(keyData);
    }

    @Override
    public Iterator<QueryableEntry> iterator() {
        if (this.records == null) {
            return Collections.EMPTY_SET.iterator();
        }
        return this.records.values().iterator();
    }

    @Override
    public int size() {
        if (this.records == null) {
            return 0;
        }
        return this.records.size();
    }
}

