/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.inject;

import com.opensymphony.xwork2.inject.Context;

public interface Factory<T> {
    public T create(Context var1) throws Exception;

    public Class<? extends T> type();
}

