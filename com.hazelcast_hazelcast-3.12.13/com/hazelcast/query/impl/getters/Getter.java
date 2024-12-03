/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.impl.getters;

abstract class Getter {
    protected final Getter parent;

    public Getter(Getter parent) {
        this.parent = parent;
    }

    abstract Object getValue(Object var1) throws Exception;

    Object getValue(Object obj, String attributePath) throws Exception {
        return this.getValue(obj);
    }

    Object getValue(Object obj, String attributePath, Object metadata) throws Exception {
        return this.getValue(obj, attributePath);
    }

    abstract Class getReturnType();

    abstract boolean isCacheable();
}

