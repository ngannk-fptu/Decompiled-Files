/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.txncollection;

class TransactionLogRecordKey {
    private final String name;
    private final long itemId;
    private final String serviceName;

    TransactionLogRecordKey(String name, long itemId, String serviceName) {
        this.name = name;
        this.itemId = itemId;
        this.serviceName = serviceName;
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
        if (!this.name.equals(that.name)) {
            return false;
        }
        return this.serviceName.equals(that.serviceName);
    }

    public int hashCode() {
        int result = this.name.hashCode();
        result = 31 * result + (int)(this.itemId ^ this.itemId >>> 32);
        result = 31 * result + this.serviceName.hashCode();
        return result;
    }
}

