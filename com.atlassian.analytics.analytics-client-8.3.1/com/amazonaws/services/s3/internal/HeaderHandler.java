/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.internal;

import com.amazonaws.http.HttpResponse;

public interface HeaderHandler<T> {
    public void handle(T var1, HttpResponse var2);
}

