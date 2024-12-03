/*
 * Decompiled with CFR 0.152.
 */
package io.atlassian.fugue;

import io.atlassian.fugue.Try;

public class Checked {
    private Checked() {
    }

    public static <A, B, E extends Exception> java.util.function.Function<A, Try<B>> lift(Function<A, B, E> f) {
        return a -> Checked.now(() -> f.apply(a));
    }

    public static <A, B, E extends Exception> java.util.function.Function<A, Try<B>> delayedLift(Function<A, B, E> f) {
        return a -> Checked.delay(() -> f.apply(a));
    }

    @Deprecated
    public static <A, E extends Exception> Try<A> of(Supplier<A, E> s) {
        return Checked.now(s);
    }

    public static <A, E extends Exception> Try<A> now(Supplier<A, E> s) {
        try {
            return Try.successful(s.get());
        }
        catch (Exception e) {
            return Try.failure(e);
        }
    }

    public static <A, E extends Exception> Try<A> delay(Supplier<A, E> s) {
        return Try.delayed(() -> Checked.now(s));
    }

    @FunctionalInterface
    public static interface Supplier<A, E extends Exception> {
        public A get() throws E;

        default public Try<A> attempt() {
            return Checked.now(this);
        }
    }

    @FunctionalInterface
    public static interface Function<A, B, E extends Exception> {
        public B apply(A var1) throws E;

        default public java.util.function.Function<A, Try<B>> lift() {
            return Checked.lift(this);
        }
    }
}

