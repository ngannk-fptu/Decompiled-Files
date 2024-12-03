/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.transaction.impl;

public class TransactionalObjectKey {
    private final String serviceName;
    private final String name;

    public TransactionalObjectKey(String serviceName, String name) {
        this.serviceName = serviceName;
        this.name = name;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TransactionalObjectKey)) {
            return false;
        }
        TransactionalObjectKey that = (TransactionalObjectKey)o;
        if (!this.name.equals(that.name)) {
            return false;
        }
        return this.serviceName.equals(that.serviceName);
    }

    public int hashCode() {
        int result = this.serviceName.hashCode();
        result = 31 * result + this.name.hashCode();
        return result;
    }
}

