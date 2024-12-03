/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.impl.getters;

import com.hazelcast.query.impl.getters.Getter;

final class ThisGetter
extends Getter {
    private final Object object;

    public ThisGetter(Getter parent, Object object) {
        super(parent);
        this.object = object;
    }

    @Override
    Object getValue(Object obj) throws Exception {
        return obj;
    }

    @Override
    Class getReturnType() {
        return this.object.getClass();
    }

    @Override
    boolean isCacheable() {
        return false;
    }
}

