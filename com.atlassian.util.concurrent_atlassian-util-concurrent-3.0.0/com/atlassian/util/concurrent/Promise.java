/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.Beta
 *  com.google.common.base.Function
 *  com.google.common.util.concurrent.FutureCallback
 *  com.google.common.util.concurrent.ListenableFuture
 */
package com.atlassian.util.concurrent;

import com.atlassian.util.concurrent.Effect;
import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.ListenableFuture;

@Beta
public interface Promise<A>
extends ListenableFuture<A> {
    public A claim();

    public Promise<A> done(Effect<? super A> var1);

    public Promise<A> fail(Effect<Throwable> var1);

    public Promise<A> then(FutureCallback<? super A> var1);

    public <B> Promise<B> map(Function<? super A, ? extends B> var1);

    public <B> Promise<B> flatMap(Function<? super A, ? extends Promise<? extends B>> var1);

    public Promise<A> recover(Function<Throwable, ? extends A> var1);

    public <B> Promise<B> fold(Function<Throwable, ? extends B> var1, Function<? super A, ? extends B> var2);
}

