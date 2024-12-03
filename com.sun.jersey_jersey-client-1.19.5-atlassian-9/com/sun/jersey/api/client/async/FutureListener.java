/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.api.client.async;

import java.util.concurrent.Future;

public interface FutureListener<T> {
    public void onComplete(Future<T> var1) throws InterruptedException;
}

