/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.impl.getters;

import com.hazelcast.query.impl.getters.Getter;

public final class NullGetter
extends Getter {
    public static final NullGetter NULL_GETTER = new NullGetter();

    private NullGetter() {
        super(null);
    }

    @Override
    Object getValue(Object obj) throws Exception {
        return null;
    }

    @Override
    Class getReturnType() {
        return null;
    }

    @Override
    boolean isCacheable() {
        return false;
    }
}

