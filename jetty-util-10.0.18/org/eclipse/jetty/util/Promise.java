/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.LoggerFactory
 */
package org.eclipse.jetty.util;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import org.slf4j.LoggerFactory;

public interface Promise<C> {
    default public void completeWith(CompletableFuture<C> cf) {
        cf.whenComplete((c, x) -> {
            if (x == null) {
                this.succeeded(c);
            } else {
                this.failed((Throwable)x);
            }
        });
    }

    default public void succeeded(C result) {
    }

    default public void failed(Throwable x) {
    }

    public static <T> Promise<T> from(final Consumer<T> success, final Consumer<Throwable> failure) {
        return new Promise<T>(){

            @Override
            public void succeeded(T result) {
                success.accept(result);
            }

            @Override
            public void failed(Throwable x) {
                failure.accept(x);
            }
        };
    }

    public static <T> Promise<T> from(final CompletableFuture<? super T> completable) {
        if (completable instanceof Promise) {
            return (Promise)((Object)completable);
        }
        return new Promise<T>(){

            @Override
            public void succeeded(T result) {
                completable.complete(result);
            }

            @Override
            public void failed(Throwable x) {
                completable.completeExceptionally(x);
            }
        };
    }

    public static class Wrapper<W>
    implements Promise<W> {
        private final Promise<W> promise;

        public Wrapper(Promise<W> promise) {
            this.promise = Objects.requireNonNull(promise);
        }

        @Override
        public void succeeded(W result) {
            this.promise.succeeded(result);
        }

        @Override
        public void failed(Throwable x) {
            this.promise.failed(x);
        }

        public Promise<W> getPromise() {
            return this.promise;
        }

        public Promise<W> unwrap() {
            Promise<W> result = this.promise;
            while (result instanceof Wrapper) {
                result = ((Wrapper)result).unwrap();
            }
            return result;
        }
    }

    public static class Completable<S>
    extends CompletableFuture<S>
    implements Promise<S> {
        @Override
        public void succeeded(S result) {
            this.complete(result);
        }

        @Override
        public void failed(Throwable x) {
            this.completeExceptionally(x);
        }
    }

    public static class Adapter<U>
    implements Promise<U> {
        @Override
        public void failed(Throwable x) {
            LoggerFactory.getLogger(this.getClass()).warn("Failed", x);
        }
    }
}

