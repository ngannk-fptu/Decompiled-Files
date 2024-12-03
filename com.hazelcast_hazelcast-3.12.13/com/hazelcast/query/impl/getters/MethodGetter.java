/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.impl.getters;

import com.hazelcast.query.impl.getters.AbstractMultiValueGetter;
import com.hazelcast.query.impl.getters.Getter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

final class MethodGetter
extends AbstractMultiValueGetter {
    private final Method method;

    MethodGetter(Getter parent, Method method, String modifierSuffix, Class resultType) {
        super(parent, modifierSuffix, method.getReturnType(), resultType);
        this.method = method;
    }

    @Override
    protected Object extractFrom(Object object) throws IllegalAccessException, InvocationTargetException {
        try {
            return this.method.invoke(object, new Object[0]);
        }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(MethodGetter.composeAttributeValueExtractionFailedMessage(this.method), e);
        }
    }

    @Override
    boolean isCacheable() {
        return true;
    }

    public String toString() {
        return "MethodGetter [parent=" + this.parent + ", method=" + this.method.getName() + ", modifier = " + this.getModifier() + "]";
    }
}

