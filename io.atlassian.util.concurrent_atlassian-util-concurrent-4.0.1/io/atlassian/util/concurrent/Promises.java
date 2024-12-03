/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package io.atlassian.util.concurrent;

import io.atlassian.util.concurrent.Promise;
import io.atlassian.util.concurrent.RuntimeInterruptedException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nonnull;

public final class Promises {
    private Promises() {
    }

    public static <A> SettablePromise<A> settablePromise() {
        return Promises.settablePromise(Optional.empty());
    }

    public static <A> SettablePromise<A> settablePromise(@Nonnull Optional<Executor> executor) {
        Objects.requireNonNull(executor, "Executor");
        return new Settable(executor);
    }

    public static <A> Promise<A> forCompletionStage(@Nonnull CompletionStage<A> stage) {
        return Promises.forCompletionStage(stage, Optional.empty());
    }

    public static <A> Promise<A> forCompletionStage(@Nonnull CompletionStage<A> stage, @Nonnull Optional<Executor> executor) {
        Objects.requireNonNull(stage, "CompletionStage");
        Objects.requireNonNull(executor, "Executor");
        return new OfStage<A>(stage, executor);
    }

    public static <B, A extends B> CompletableFuture<B> toCompletableFuture(@Nonnull Promise<A> promise) {
        if (promise instanceof OfStage) {
            return ((OfStage)promise).future;
        }
        CompletableFuture aCompletableFuture = new CompletableFuture();
        promise.then(Promises.compose(aCompletableFuture::complete, t -> {
            if (promise.isCancelled() && !(t instanceof CancellationException)) {
                aCompletableFuture.completeExceptionally(new CancellationException(t.getMessage()));
            } else {
                aCompletableFuture.completeExceptionally(Promises.getRealException(t));
            }
        }));
        return aCompletableFuture;
    }

    @SafeVarargs
    public static <A> Promise<List<A>> when(Promise<? extends A> ... promises) {
        return Promises.when(Stream.of(promises));
    }

    public static <A> Promise<List<A>> when(@Nonnull Iterable<? extends Promise<? extends A>> promises) {
        return Promises.when(StreamSupport.stream(promises.spliterator(), false).map(Function.identity()));
    }

    public static <A> Promise<List<A>> when(@Nonnull Stream<? extends Promise<? extends A>> promises) {
        List<CompletableFuture> futures = promises.map(Promises::toCompletableFuture).collect(Collectors.toList());
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]));
        futures.forEach(cf -> cf.whenComplete((a, t) -> {
            if (t != null) {
                futures.forEach(f -> f.cancel(true));
            }
        }));
        Function<Void, List> gatherValues = o -> futures.stream().map(CompletableFuture::join).collect(Collectors.toList());
        return Promises.forCompletionStage(allFutures.thenApply(gatherValues));
    }

    @Deprecated
    public static <A> Function<A, Promise<A>> toPromise() {
        return Promises::promise;
    }

    public static <A> Promise<A> promise(A value) {
        CompletableFuture<A> future = new CompletableFuture<A>();
        future.complete(value);
        return Promises.forCompletionStage(future);
    }

    public static <A> Promise<A> rejected(@Nonnull Throwable t) {
        CompletableFuture future = new CompletableFuture();
        future.completeExceptionally(t);
        return Promises.forCompletionStage(future);
    }

    public static <A> Promise<A> forFuture(@Nonnull Future<A> future, @Nonnull Executor executor) {
        CompletableFuture newFuture = new CompletableFuture();
        executor.execute(() -> {
            try {
                newFuture.complete(future.get());
            }
            catch (ExecutionException ee) {
                newFuture.completeExceptionally(ee.getCause());
            }
            catch (InterruptedException ee) {
                newFuture.cancel(true);
            }
            catch (Throwable t) {
                newFuture.completeExceptionally(t);
            }
        });
        newFuture.whenComplete((a, t) -> {
            if (t instanceof CancellationException) {
                future.cancel(true);
            }
        });
        return Promises.forCompletionStage(newFuture, Optional.of(executor));
    }

    public static <A> Promise.TryConsumer<A> compose(final @Nonnull Consumer<? super A> success, final @Nonnull Consumer<Throwable> failure) {
        return new Promise.TryConsumer<A>(){

            @Override
            public void accept(A result) {
                success.accept(result);
            }

            @Override
            public void fail(@Nonnull Throwable t) {
                failure.accept(t);
            }
        };
    }

    private static Throwable getRealException(@Nonnull Throwable t) {
        if (t instanceof CompletionException) {
            return t.getCause();
        }
        return t;
    }

    private static <A, B> BiFunction<A, Throwable, B> biFunction(Function<? super A, ? extends B> f, Function<Throwable, ? extends B> ft) {
        return (a, t) -> {
            if (t == null) {
                return f.apply((Object)a);
            }
            return ft.apply(Promises.getRealException(t));
        };
    }

    private static <A> BiConsumer<A, Throwable> biConsumer(Consumer<? super A> c, Consumer<Throwable> ct) {
        return (a, t) -> {
            if (t == null) {
                c.accept((Object)a);
            } else {
                ct.accept(Promises.getRealException(t));
            }
        };
    }

    public static interface SettablePromise<A>
    extends Promise<A>,
    Callback<A> {
    }

    public static interface Callback<A> {
        public void set(A var1);

        public void exception(@Nonnull Throwable var1);
    }

    static class Settable<A>
    extends OfStage<A>
    implements SettablePromise<A> {
        private final CompletableFuture<A> completableFuture;

        public Settable(@Nonnull Optional<Executor> ex) {
            this(new CompletableFuture(), ex);
        }

        private Settable(@Nonnull CompletableFuture<A> cf, @Nonnull Optional<Executor> ex) {
            super(cf, ex);
            this.completableFuture = cf;
        }

        @Override
        public void set(A result) {
            this.completableFuture.complete(result);
        }

        @Override
        public void exception(@Nonnull Throwable t) {
            this.completableFuture.completeExceptionally(t);
        }
    }

    static class OfStage<A>
    implements Promise<A> {
        private final CompletableFuture<A> future;
        private final Optional<Executor> executor;

        public OfStage(@Nonnull CompletionStage<A> delegate, @Nonnull Optional<Executor> ex) {
            this.future = this.buildCompletableFuture(delegate, ex);
            this.executor = ex;
        }

        @Override
        public A claim() {
            try {
                return this.future.get();
            }
            catch (InterruptedException e) {
                throw new RuntimeInterruptedException(e);
            }
            catch (CompletionException e) {
                Throwable cause = e.getCause();
                if (cause instanceof RuntimeException) {
                    throw (RuntimeException)cause;
                }
                if (cause instanceof Error) {
                    throw (Error)cause;
                }
                throw e;
            }
            catch (ExecutionException e) {
                Throwable cause = e.getCause();
                if (cause instanceof RuntimeException) {
                    throw (RuntimeException)cause;
                }
                if (cause instanceof Error) {
                    throw (Error)cause;
                }
                throw new RuntimeException(cause);
            }
        }

        @Override
        public Promise<A> done(Consumer<? super A> e) {
            return this.then(e, t -> {});
        }

        @Override
        public Promise<A> fail(Consumer<Throwable> e) {
            return this.then(a -> {}, e);
        }

        @Override
        public Promise<A> then(Promise.TryConsumer<? super A> callback) {
            return this.then(callback::accept, callback::fail);
        }

        @Override
        public <B> Promise<B> map(Function<? super A, ? extends B> function) {
            return Promises.forCompletionStage(this.future.thenApply(function));
        }

        @Override
        public <B> Promise<B> flatMap(Function<? super A, ? extends Promise<? extends B>> f) {
            Function<Object, CompletableFuture> fn = a -> Promises.toCompletableFuture((Promise)f.apply((Object)a));
            return this.newPromise(this.future::thenCompose, this.future::thenComposeAsync).apply(fn);
        }

        @Override
        public Promise<A> recover(Function<Throwable, ? extends A> handleThrowable) {
            return Promises.forCompletionStage(this.future.exceptionally(handleThrowable.compose(x$0 -> Promises.getRealException(x$0))));
        }

        @Override
        public <B> Promise<B> fold(Function<Throwable, ? extends B> ft, Function<? super A, ? extends B> fa) {
            Function<Object, Object> fn = a -> {
                try {
                    return fa.apply((Object)a);
                }
                catch (Throwable t) {
                    return ft.apply(t);
                }
            };
            return this.newPromise(this.future::handle, this.future::handleAsync).apply(Promises.biFunction(fn, ft));
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            return this.future.cancel(mayInterruptIfRunning);
        }

        @Override
        public boolean isCancelled() {
            return this.future.isCancelled();
        }

        @Override
        public boolean isDone() {
            return this.future.isDone();
        }

        @Override
        public A get() throws InterruptedException, ExecutionException {
            return this.future.get();
        }

        @Override
        public A get(long timeout, @Nonnull TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            return this.future.get(timeout, unit);
        }

        private Promise<A> then(Consumer<? super A> onSuccess, Consumer<Throwable> onFailure) {
            return this.newPromise(this.future::whenComplete, this.future::whenCompleteAsync).apply(Promises.biConsumer(onSuccess, onFailure));
        }

        private <I, O> Function<I, Promise<O>> newPromise(Function<I, CompletionStage<O>> f1, BiFunction<I, Executor, CompletionStage<O>> f2) {
            return i -> {
                if (this.executor.isPresent()) {
                    return Promises.forCompletionStage((CompletionStage)f2.apply(i, this.executor.get()));
                }
                return Promises.forCompletionStage((CompletionStage)f1.apply(i));
            };
        }

        private CompletableFuture<A> buildCompletableFuture(CompletionStage<A> completionStage, Optional<Executor> executor) {
            try {
                return completionStage.toCompletableFuture();
            }
            catch (UnsupportedOperationException uoe) {
                CompletableFuture aCompletableFuture = new CompletableFuture();
                BiConsumer action = Promises.biConsumer(aCompletableFuture::complete, aCompletableFuture::completeExceptionally);
                if (executor.isPresent()) {
                    completionStage.whenCompleteAsync(action, executor.get());
                } else {
                    completionStage.whenComplete(action);
                }
                return aCompletableFuture;
            }
        }
    }
}

