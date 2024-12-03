/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.multimap.impl.txn;

import com.hazelcast.nio.serialization.Data;

class TransactionRecordKey {
    final String name;
    final Data key;

    public TransactionRecordKey(String name, Data key) {
        this.name = name;
        this.key = key;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TransactionRecordKey)) {
            return false;
        }
        TransactionRecordKey that = (TransactionRecordKey)o;
        if (!this.key.equals(that.key)) {
            return false;
        }
        return this.name.equals(that.name);
    }

    public int hashCode() {
        int result = this.name.hashCode();
        result = 31 * result + this.key.hashCode();
        return result;
    }
}

