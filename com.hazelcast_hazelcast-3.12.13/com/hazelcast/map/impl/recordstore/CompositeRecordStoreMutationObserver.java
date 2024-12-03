/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.recordstore;

import com.hazelcast.map.impl.record.Record;
import com.hazelcast.map.impl.recordstore.RecordStoreMutationObserver;
import com.hazelcast.nio.serialization.Data;
import java.util.Collection;
import java.util.LinkedList;

class CompositeRecordStoreMutationObserver<R extends Record>
implements RecordStoreMutationObserver<R> {
    private final Collection<RecordStoreMutationObserver<R>> mutationObservers = new LinkedList<RecordStoreMutationObserver<R>>();

    CompositeRecordStoreMutationObserver(Collection<RecordStoreMutationObserver<R>> mutationObservers) {
        this.mutationObservers.addAll(mutationObservers);
    }

    @Override
    public void onClear() {
        for (RecordStoreMutationObserver<R> mutationObserver : this.mutationObservers) {
            mutationObserver.onClear();
        }
    }

    @Override
    public void onPutRecord(Data key, R record) {
        for (RecordStoreMutationObserver<R> mutationObserver : this.mutationObservers) {
            mutationObserver.onPutRecord(key, record);
        }
    }

    @Override
    public void onReplicationPutRecord(Data key, R record) {
        for (RecordStoreMutationObserver<R> mutationObserver : this.mutationObservers) {
            mutationObserver.onReplicationPutRecord(key, record);
        }
    }

    @Override
    public void onUpdateRecord(Data key, R record, Object newValue) {
        for (RecordStoreMutationObserver<R> mutationObserver : this.mutationObservers) {
            mutationObserver.onUpdateRecord(key, record, newValue);
        }
    }

    @Override
    public void onRemoveRecord(Data key, R record) {
        for (RecordStoreMutationObserver<R> mutationObserver : this.mutationObservers) {
            mutationObserver.onRemoveRecord(key, record);
        }
    }

    @Override
    public void onEvictRecord(Data key, R record) {
        for (RecordStoreMutationObserver<R> mutationObserver : this.mutationObservers) {
            mutationObserver.onEvictRecord(key, record);
        }
    }

    @Override
    public void onLoadRecord(Data key, R record) {
        for (RecordStoreMutationObserver<R> mutationObserver : this.mutationObservers) {
            mutationObserver.onLoadRecord(key, record);
        }
    }

    @Override
    public void onDestroy(boolean internal) {
        for (RecordStoreMutationObserver<R> mutationObserver : this.mutationObservers) {
            mutationObserver.onDestroy(internal);
        }
    }

    @Override
    public void onReset() {
        for (RecordStoreMutationObserver<R> mutationObserver : this.mutationObservers) {
            mutationObserver.onReset();
        }
    }
}

