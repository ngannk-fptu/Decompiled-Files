/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.DoNotMock
 */
package com.google.common.util.concurrent;

import com.google.common.util.concurrent.ElementTypesAreNonnullByDefault;
import com.google.errorprone.annotations.DoNotMock;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;

@DoNotMock(value="Use the methods in Futures (like immediateFuture) or SettableFuture")
@ElementTypesAreNonnullByDefault
public interface ListenableFuture<V>
extends Future<V> {
    public void addListener(Runnable var1, Executor var2);
}

