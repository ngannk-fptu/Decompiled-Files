/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.impl.getters;

import com.hazelcast.query.impl.getters.Getter;
import com.hazelcast.query.impl.getters.ImmutableMultiResult;
import com.hazelcast.query.impl.getters.MultiResult;

public final class NullMultiValueGetter
extends Getter {
    public static final NullMultiValueGetter NULL_MULTIVALUE_GETTER = new NullMultiValueGetter();
    private static final MultiResult NULL_MULTIVALUE_RESULT;

    private NullMultiValueGetter() {
        super(null);
    }

    @Override
    Object getValue(Object obj) throws Exception {
        return NULL_MULTIVALUE_RESULT;
    }

    @Override
    Class getReturnType() {
        return null;
    }

    @Override
    boolean isCacheable() {
        return false;
    }

    static {
        MultiResult result = new MultiResult();
        result.addNullOrEmptyTarget();
        NULL_MULTIVALUE_RESULT = new ImmutableMultiResult(result);
    }
}

