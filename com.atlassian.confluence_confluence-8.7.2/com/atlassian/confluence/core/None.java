/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.atlassian.fugue.Effect
 *  io.atlassian.fugue.Maybe
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.core;

import io.atlassian.fugue.Effect;
import io.atlassian.fugue.Maybe;
import java.util.Collections;
import java.util.Iterator;
import java.util.function.Predicate;
import java.util.function.Supplier;
import org.checkerframework.checker.nullness.qual.NonNull;

public abstract class None<T>
implements Maybe<T> {
    public static <T> Maybe<T> becauseOfNoResult(Maybe<?> maybe) {
        if (maybe.isDefined()) {
            throw new IllegalArgumentException("Maybe is defined");
        }
        return maybe;
    }

    public static <T> Maybe<T> becauseOf(String message, Object ... args) {
        return None.becauseOfException(new RuntimeException(args.length == 0 ? message : String.format(message, args)));
    }

    public static <T> Maybe<T> becauseOfException(final Exception exception) {
        return new None<T>(){

            public T get() {
                if (exception instanceof RuntimeException) {
                    throw (RuntimeException)exception;
                }
                throw new RuntimeException(exception);
            }
        };
    }

    private None() {
    }

    public T getOrError(Supplier<String> msg) {
        try {
            return (T)this.get();
        }
        catch (RuntimeException e) {
            throw new RuntimeException(msg.get(), e);
        }
    }

    public T getOr(Supplier<? extends T> supplier) {
        return this.getOrElse((Object)supplier);
    }

    public T getOrElse(Supplier<? extends T> supplier) {
        return supplier.get();
    }

    public <X extends Throwable> T getOrThrow(Supplier<X> xSupplier) throws X {
        throw (Throwable)xSupplier.get();
    }

    public <B extends T> T getOrElse(B other) {
        return (T)this.getOr(() -> other);
    }

    public void foreach(Effect<? super T> effect) {
    }

    public T getOrNull() {
        return null;
    }

    public boolean exists(Predicate<? super T> p) {
        return false;
    }

    public @NonNull Iterator<T> iterator() {
        return Collections.emptyIterator();
    }

    public boolean forall(Predicate<? super T> p) {
        return true;
    }

    public boolean isEmpty() {
        return true;
    }

    public boolean isDefined() {
        return false;
    }
}

