/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.impl;

import com.hazelcast.internal.json.NonTerminalJsonValue;
import com.hazelcast.monitor.impl.IndexOperationStats;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.query.impl.BaseIndexStore;
import com.hazelcast.query.impl.DuplicateDetectingMultiResult;
import com.hazelcast.query.impl.FastMultiResultSet;
import com.hazelcast.query.impl.IndexCopyBehavior;
import com.hazelcast.query.impl.MultiResultSet;
import com.hazelcast.query.impl.QueryableEntry;
import com.hazelcast.query.impl.getters.MultiResult;
import java.util.List;

public abstract class BaseSingleValueIndexStore
extends BaseIndexStore {
    private boolean multiResultHasToDetectDuplicates;

    BaseSingleValueIndexStore(IndexCopyBehavior copyOn) {
        super(copyOn);
    }

    abstract Object insertInternal(Comparable var1, QueryableEntry var2);

    abstract Object removeInternal(Comparable var1, Data var2);

    final MultiResultSet createMultiResultSet() {
        return this.multiResultHasToDetectDuplicates ? new DuplicateDetectingMultiResult() : new FastMultiResultSet();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public final void insert(Object value, QueryableEntry record, IndexOperationStats operationStats) {
        this.takeWriteLock();
        try {
            this.unwrapAndInsertToIndex(value, record, operationStats);
        }
        finally {
            this.releaseWriteLock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public final void update(Object oldValue, Object newValue, QueryableEntry entry, IndexOperationStats operationStats) {
        this.takeWriteLock();
        try {
            Data indexKey = entry.getKeyData();
            this.unwrapAndRemoveFromIndex(oldValue, indexKey, operationStats);
            this.unwrapAndInsertToIndex(newValue, entry, operationStats);
        }
        finally {
            this.releaseWriteLock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public final void remove(Object value, Data entryKey, Object entryValue, IndexOperationStats operationStats) {
        this.takeWriteLock();
        try {
            this.unwrapAndRemoveFromIndex(value, entryKey, operationStats);
        }
        finally {
            this.releaseWriteLock();
        }
    }

    @Override
    public void destroy() {
    }

    private void unwrapAndInsertToIndex(Object newValue, QueryableEntry record, IndexOperationStats operationStats) {
        if (newValue == NonTerminalJsonValue.INSTANCE) {
            return;
        }
        if (newValue instanceof MultiResult) {
            this.multiResultHasToDetectDuplicates = true;
            List results = ((MultiResult)newValue).getResults();
            for (Object o : results) {
                Comparable sanitizedValue = this.sanitizeValue(o);
                Object oldValue = this.insertInternal(sanitizedValue, record);
                operationStats.onEntryAdded(oldValue, newValue);
            }
        } else {
            Comparable sanitizedValue = this.sanitizeValue(newValue);
            Object oldValue = this.insertInternal(sanitizedValue, record);
            operationStats.onEntryAdded(oldValue, newValue);
        }
    }

    private void unwrapAndRemoveFromIndex(Object oldValue, Data indexKey, IndexOperationStats operationStats) {
        if (oldValue == NonTerminalJsonValue.INSTANCE) {
            return;
        }
        if (oldValue instanceof MultiResult) {
            List results = ((MultiResult)oldValue).getResults();
            for (Object o : results) {
                Comparable sanitizedValue = this.sanitizeValue(o);
                Object removedValue = this.removeInternal(sanitizedValue, indexKey);
                operationStats.onEntryRemoved(removedValue);
            }
        } else {
            Comparable sanitizedValue = this.sanitizeValue(oldValue);
            Object removedValue = this.removeInternal(sanitizedValue, indexKey);
            operationStats.onEntryRemoved(removedValue);
        }
    }
}

