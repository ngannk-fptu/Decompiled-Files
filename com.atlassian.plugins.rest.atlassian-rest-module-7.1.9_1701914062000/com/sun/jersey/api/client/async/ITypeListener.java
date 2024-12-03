/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.api.client.async;

import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.async.FutureListener;

public interface ITypeListener<T>
extends FutureListener<T> {
    public Class<T> getType();

    public GenericType<T> getGenericType();
}

