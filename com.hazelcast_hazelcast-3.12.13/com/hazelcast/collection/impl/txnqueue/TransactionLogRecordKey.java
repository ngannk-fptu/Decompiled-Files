/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.txnqueue;

class TransactionLogRecordKey {
    private final long itemId;
    private final String name;

    public TransactionLogRecordKey(long itemId, String name) {
        this.itemId = itemId;
        this.name = name;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TransactionLogRecordKey)) {
            return false;
        }
        TransactionLogRecordKey that = (TransactionLogRecordKey)o;
        if (this.itemId != that.itemId) {
            return false;
        }
        return this.name.equals(that.name);
    }

    public int hashCode() {
        int result = (int)(this.itemId ^ this.itemId >>> 32);
        result = 31 * result + this.name.hashCode();
        return result;
    }
}

