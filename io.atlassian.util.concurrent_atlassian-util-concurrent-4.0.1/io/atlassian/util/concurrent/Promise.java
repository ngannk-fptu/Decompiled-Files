/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package io.atlassian.util.concurrent;

import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nonnull;

public interface Promise<A>
extends Future<A> {
    public A claim();

    public Promise<A> done(Consumer<? super A> var1);

    public Promise<A> fail(Consumer<Throwable> var1);

    public Promise<A> then(TryConsumer<? super A> var1);

    public <B> Promise<B> map(Function<? super A, ? extends B> var1);

    public <B> Promise<B> flatMap(Function<? super A, ? extends Promise<? extends B>> var1);

    public Promise<A> recover(Function<Throwable, ? extends A> var1);

    public <B> Promise<B> fold(Function<Throwable, ? extends B> var1, Function<? super A, ? extends B> var2);

    public static interface TryConsumer<A>
    extends Consumer<A> {
        public void fail(@Nonnull Throwable var1);
    }
}

