/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.api.client.async;

import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.async.ITypeListener;

public abstract class TypeListener<T>
implements ITypeListener<T> {
    private final Class<T> type;
    private final GenericType<T> genericType;

    public TypeListener(Class<T> type) {
        this.type = type;
        this.genericType = null;
    }

    public TypeListener(GenericType<T> genericType) {
        this.type = genericType.getRawClass();
        this.genericType = genericType;
    }

    @Override
    public Class<T> getType() {
        return this.type;
    }

    @Override
    public GenericType<T> getGenericType() {
        return this.genericType;
    }
}

