/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Effect
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.fugue.Suppliers
 *  com.google.common.base.Predicate
 *  com.google.common.base.Supplier
 *  org.apache.commons.lang3.ArrayUtils
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.core;

import com.atlassian.fugue.Effect;
import com.atlassian.fugue.Maybe;
import com.atlassian.fugue.Suppliers;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import java.util.Collections;
import java.util.Iterator;
import org.apache.commons.lang3.ArrayUtils;
import org.checkerframework.checker.nullness.qual.NonNull;

@Deprecated
public abstract class MaybeNot<T>
implements Maybe<T> {
    public static <T> Maybe<T> becauseOfNoResult(Maybe<?> maybe) {
        if (maybe.isDefined()) {
            throw new IllegalArgumentException("Maybe is defined");
        }
        return maybe;
    }

    public static <T> Maybe<T> becauseOf(String message, Object ... args) {
        return MaybeNot.becauseOfException(new RuntimeException(ArrayUtils.isEmpty((Object[])args) ? message : String.format(message, args)));
    }

    public static <T> Maybe<T> becauseOfException(final Exception exception) {
        return new MaybeNot<T>(){

            public T get() {
                if (exception instanceof RuntimeException) {
                    throw (RuntimeException)exception;
                }
                throw new RuntimeException(exception);
            }
        };
    }

    private MaybeNot() {
    }

    public T getOrError(Supplier<String> msg) {
        try {
            return (T)this.get();
        }
        catch (RuntimeException e) {
            throw new RuntimeException((String)msg.get(), e);
        }
    }

    public T getOrElse(Supplier<? extends T> supplier) {
        return (T)supplier.get();
    }

    public <X extends Throwable> T getOrThrow(Supplier<X> xSupplier) throws X {
        throw (Throwable)xSupplier.get();
    }

    public <B extends T> T getOrElse(B other) {
        return this.getOrElse((B)Suppliers.ofInstance(other));
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

