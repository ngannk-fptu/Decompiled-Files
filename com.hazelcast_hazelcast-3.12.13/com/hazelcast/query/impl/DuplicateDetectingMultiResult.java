/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.impl;

import com.hazelcast.nio.serialization.Data;
import com.hazelcast.query.impl.MultiResultSet;
import com.hazelcast.query.impl.QueryableEntry;
import com.hazelcast.util.MapUtil;
import java.util.AbstractSet;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

public class DuplicateDetectingMultiResult
extends AbstractSet<QueryableEntry>
implements MultiResultSet {
    private Map<Data, QueryableEntry> records;

    @Override
    public void addResultSet(Map<Data, QueryableEntry> resultSet) {
        if (this.records == null) {
            this.records = MapUtil.createHashMap(resultSet.size());
        }
        for (Map.Entry<Data, QueryableEntry> entry : resultSet.entrySet()) {
            Data key = entry.getKey();
            QueryableEntry value = entry.getValue();
            this.records.put(key, value);
        }
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
            return Collections.emptyList().iterator();
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

