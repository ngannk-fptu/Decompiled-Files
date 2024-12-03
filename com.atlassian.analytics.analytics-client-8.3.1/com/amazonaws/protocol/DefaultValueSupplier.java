/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.protocol;

import com.amazonaws.annotation.SdkProtectedApi;

@SdkProtectedApi
public interface DefaultValueSupplier<T> {
    public T get();
}

