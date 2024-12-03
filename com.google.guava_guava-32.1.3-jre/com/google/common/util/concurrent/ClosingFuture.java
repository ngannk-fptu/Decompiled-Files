/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 *  com.google.errorprone.annotations.DoNotMock
 *  com.google.j2objc.annotations.RetainedWith
 *  javax.annotation.CheckForNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.google.common.util.concurrent;

import com.google.common.annotations.J2ktIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Functions;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.AsyncCallable;
import com.google.common.util.concurrent.AsyncFunction;
import com.google.common.util.concurrent.ElementTypesAreNonnullByDefault;
import com.google.common.util.concurrent.FluentFuture;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ParametricNullness;
import com.google.common.util.concurrent.Platform;
import com.google.common.util.concurrent.TrustedListenableFutureTask;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.DoNotMock;
import com.google.j2objc.annotations.RetainedWith;
import java.io.Closeable;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.CheckForNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@DoNotMock(value="Use ClosingFuture.from(Futures.immediate*Future)")
@ElementTypesAreNonnullByDefault
@J2ktIncompatible
public final class ClosingFuture<V> {
    private static final Logger logger = Logger.getLogger(ClosingFuture.class.getName());
    private final AtomicReference<State> state = new AtomicReference<State>(State.OPEN);
    private final CloseableList closeables = new CloseableList();
    private final FluentFuture<V> future;

    public static <V> ClosingFuture<V> submit(ClosingCallable<V> callable, Executor executor) {
        return new ClosingFuture<V>(callable, executor);
    }

    public static <V> ClosingFuture<V> submitAsync(AsyncClosingCallable<V> callable, Executor executor) {
        return new ClosingFuture<V>(callable, executor);
    }

    public static <V> ClosingFuture<V> from(ListenableFuture<V> future) {
        return new ClosingFuture<V>(future);
    }

    @Deprecated
    public static <C> ClosingFuture<C> eventuallyClosing(ListenableFuture<C> future, final Executor closingExecutor) {
        Preconditions.checkNotNull(closingExecutor);
        final ClosingFuture<C> closingFuture = new ClosingFuture<C>(Futures.nonCancellationPropagating(future));
        Futures.addCallback(future, new FutureCallback<AutoCloseable>(){

            @Override
            public void onSuccess(@CheckForNull AutoCloseable result) {
                closingFuture.closeables.closer.eventuallyClose(result, closingExecutor);
            }

            @Override
            public void onFailure(Throwable t) {
            }
        }, MoreExecutors.directExecutor());
        return closingFuture;
    }

    public static Combiner whenAllComplete(Iterable<? extends ClosingFuture<?>> futures) {
        return new Combiner(false, futures);
    }

    public static Combiner whenAllComplete(ClosingFuture<?> future1, ClosingFuture<?> ... moreFutures) {
        return ClosingFuture.whenAllComplete(Lists.asList(future1, moreFutures));
    }

    public static Combiner whenAllSucceed(Iterable<? extends ClosingFuture<?>> futures) {
        return new Combiner(true, futures);
    }

    public static <V1, V2> Combiner2<V1, V2> whenAllSucceed(ClosingFuture<V1> future1, ClosingFuture<V2> future2) {
        return new Combiner2(future1, future2);
    }

    public static <V1, V2, V3> Combiner3<V1, V2, V3> whenAllSucceed(ClosingFuture<V1> future1, ClosingFuture<V2> future2, ClosingFuture<V3> future3) {
        return new Combiner3(future1, future2, future3);
    }

    public static <V1, V2, V3, V4> Combiner4<V1, V2, V3, V4> whenAllSucceed(ClosingFuture<V1> future1, ClosingFuture<V2> future2, ClosingFuture<V3> future3, ClosingFuture<V4> future4) {
        return new Combiner4(future1, future2, future3, future4);
    }

    public static <V1, V2, V3, V4, V5> Combiner5<V1, V2, V3, V4, V5> whenAllSucceed(ClosingFuture<V1> future1, ClosingFuture<V2> future2, ClosingFuture<V3> future3, ClosingFuture<V4> future4, ClosingFuture<V5> future5) {
        return new Combiner5(future1, future2, future3, future4, future5);
    }

    public static Combiner whenAllSucceed(ClosingFuture<?> future1, ClosingFuture<?> future2, ClosingFuture<?> future3, ClosingFuture<?> future4, ClosingFuture<?> future5, ClosingFuture<?> future6, ClosingFuture<?> ... moreFutures) {
        return ClosingFuture.whenAllSucceed(FluentIterable.of(future1, future2, future3, future4, future5, future6).append(moreFutures));
    }

    private ClosingFuture(ListenableFuture<V> future) {
        this.future = FluentFuture.from(future);
    }

    private ClosingFuture(final ClosingCallable<V> callable, Executor executor) {
        Preconditions.checkNotNull(callable);
        TrustedListenableFutureTask task = TrustedListenableFutureTask.create(new Callable<V>(){

            @Override
            @ParametricNullness
            public V call() throws Exception {
                return callable.call(ClosingFuture.this.closeables.closer);
            }

            public String toString() {
                return callable.toString();
            }
        });
        executor.execute(task);
        this.future = task;
    }

    private ClosingFuture(final AsyncClosingCallable<V> callable, Executor executor) {
        Preconditions.checkNotNull(callable);
        TrustedListenableFutureTask task = TrustedListenableFutureTask.create(new AsyncCallable<V>(){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public ListenableFuture<V> call() throws Exception {
                CloseableList newCloseables = new CloseableList();
                try {
                    ClosingFuture closingFuture = callable.call(newCloseables.closer);
                    closingFuture.becomeSubsumedInto(ClosingFuture.this.closeables);
                    FluentFuture fluentFuture = closingFuture.future;
                    return fluentFuture;
                }
                finally {
                    ClosingFuture.this.closeables.add(newCloseables, MoreExecutors.directExecutor());
                }
            }

            public String toString() {
                return callable.toString();
            }
        });
        executor.execute(task);
        this.future = task;
    }

    public ListenableFuture<?> statusFuture() {
        return Futures.nonCancellationPropagating(this.future.transform(Functions.constant(null), MoreExecutors.directExecutor()));
    }

    public <U> ClosingFuture<U> transform(final ClosingFunction<? super V, U> function, Executor executor) {
        Preconditions.checkNotNull(function);
        AsyncFunction applyFunction = new AsyncFunction<V, U>(){

            @Override
            public ListenableFuture<U> apply(V input) throws Exception {
                return ClosingFuture.this.closeables.applyClosingFunction(function, input);
            }

            public String toString() {
                return function.toString();
            }
        };
        return this.derive(this.future.transformAsync(applyFunction, executor));
    }

    public <U> ClosingFuture<U> transformAsync(final AsyncClosingFunction<? super V, U> function, Executor executor) {
        Preconditions.checkNotNull(function);
        AsyncFunction applyFunction = new AsyncFunction<V, U>(){

            @Override
            public ListenableFuture<U> apply(V input) throws Exception {
                return ClosingFuture.this.closeables.applyAsyncClosingFunction(function, input);
            }

            public String toString() {
                return function.toString();
            }
        };
        return this.derive(this.future.transformAsync(applyFunction, executor));
    }

    public static <V, U> AsyncClosingFunction<V, U> withoutCloser(final AsyncFunction<V, U> function) {
        Preconditions.checkNotNull(function);
        return new AsyncClosingFunction<V, U>(){

            @Override
            public ClosingFuture<U> apply(DeferredCloser closer, V input) throws Exception {
                return ClosingFuture.from(function.apply(input));
            }
        };
    }

    public <X extends Throwable> ClosingFuture<V> catching(Class<X> exceptionType, ClosingFunction<? super X, ? extends V> fallback, Executor executor) {
        return this.catchingMoreGeneric(exceptionType, fallback, executor);
    }

    private <X extends Throwable, W extends V> ClosingFuture<V> catchingMoreGeneric(Class<X> exceptionType, final ClosingFunction<? super X, W> fallback, Executor executor) {
        Preconditions.checkNotNull(fallback);
        AsyncFunction applyFallback = new AsyncFunction<X, W>(){

            @Override
            public ListenableFuture<W> apply(X exception) throws Exception {
                return ClosingFuture.this.closeables.applyClosingFunction(fallback, exception);
            }

            public String toString() {
                return fallback.toString();
            }
        };
        return this.derive(this.future.catchingAsync(exceptionType, applyFallback, executor));
    }

    public <X extends Throwable> ClosingFuture<V> catchingAsync(Class<X> exceptionType, AsyncClosingFunction<? super X, ? extends V> fallback, Executor executor) {
        return this.catchingAsyncMoreGeneric(exceptionType, fallback, executor);
    }

    private <X extends Throwable, W extends V> ClosingFuture<V> catchingAsyncMoreGeneric(Class<X> exceptionType, final AsyncClosingFunction<? super X, W> fallback, Executor executor) {
        Preconditions.checkNotNull(fallback);
        AsyncFunction asyncFunction = new AsyncFunction<X, W>(){

            @Override
            public ListenableFuture<W> apply(X exception) throws Exception {
                return ClosingFuture.this.closeables.applyAsyncClosingFunction(fallback, exception);
            }

            public String toString() {
                return fallback.toString();
            }
        };
        return this.derive(this.future.catchingAsync(exceptionType, asyncFunction, executor));
    }

    public FluentFuture<V> finishToFuture() {
        if (this.compareAndUpdateState(State.OPEN, State.WILL_CLOSE)) {
            logger.log(Level.FINER, "will close {0}", this);
            this.future.addListener(new Runnable(){

                @Override
                public void run() {
                    ClosingFuture.this.checkAndUpdateState(State.WILL_CLOSE, State.CLOSING);
                    ClosingFuture.this.close();
                    ClosingFuture.this.checkAndUpdateState(State.CLOSING, State.CLOSED);
                }
            }, MoreExecutors.directExecutor());
        } else {
            switch (this.state.get()) {
                case SUBSUMED: {
                    throw new IllegalStateException("Cannot call finishToFuture() after deriving another step");
                }
                case WILL_CREATE_VALUE_AND_CLOSER: {
                    throw new IllegalStateException("Cannot call finishToFuture() after calling finishToValueAndCloser()");
                }
                case WILL_CLOSE: 
                case CLOSING: 
                case CLOSED: {
                    throw new IllegalStateException("Cannot call finishToFuture() twice");
                }
                case OPEN: {
                    throw new AssertionError();
                }
            }
        }
        return this.future;
    }

    public void finishToValueAndCloser(final ValueAndCloserConsumer<? super V> consumer, Executor executor) {
        Preconditions.checkNotNull(consumer);
        if (!this.compareAndUpdateState(State.OPEN, State.WILL_CREATE_VALUE_AND_CLOSER)) {
            switch (this.state.get()) {
                case SUBSUMED: {
                    throw new IllegalStateException("Cannot call finishToValueAndCloser() after deriving another step");
                }
                case WILL_CLOSE: 
                case CLOSING: 
                case CLOSED: {
                    throw new IllegalStateException("Cannot call finishToValueAndCloser() after calling finishToFuture()");
                }
                case WILL_CREATE_VALUE_AND_CLOSER: {
                    throw new IllegalStateException("Cannot call finishToValueAndCloser() twice");
                }
            }
            throw new AssertionError(this.state);
        }
        this.future.addListener(new Runnable(){

            @Override
            public void run() {
                ClosingFuture.provideValueAndCloser(consumer, ClosingFuture.this);
            }
        }, executor);
    }

    private static <C, V extends C> void provideValueAndCloser(ValueAndCloserConsumer<C> consumer, ClosingFuture<V> closingFuture) {
        consumer.accept(new ValueAndCloser<V>(closingFuture));
    }

    @CanIgnoreReturnValue
    public boolean cancel(boolean mayInterruptIfRunning) {
        logger.log(Level.FINER, "cancelling {0}", this);
        boolean cancelled = this.future.cancel(mayInterruptIfRunning);
        if (cancelled) {
            this.close();
        }
        return cancelled;
    }

    private void close() {
        logger.log(Level.FINER, "closing {0}", this);
        this.closeables.close();
    }

    private <U> ClosingFuture<U> derive(FluentFuture<U> future) {
        ClosingFuture<U> derived = new ClosingFuture<U>(future);
        this.becomeSubsumedInto(derived.closeables);
        return derived;
    }

    private void becomeSubsumedInto(CloseableList otherCloseables) {
        this.checkAndUpdateState(State.OPEN, State.SUBSUMED);
        otherCloseables.add(this.closeables, MoreExecutors.directExecutor());
    }

    public String toString() {
        return MoreObjects.toStringHelper(this).add("state", (Object)this.state.get()).addValue(this.future).toString();
    }

    protected void finalize() {
        if (this.state.get().equals((Object)State.OPEN)) {
            logger.log(Level.SEVERE, "Uh oh! An open ClosingFuture has leaked and will close: {0}", this);
            FluentFuture<V> fluentFuture = this.finishToFuture();
        }
    }

    private static void closeQuietly(@CheckForNull AutoCloseable closeable, Executor executor) {
        if (closeable == null) {
            return;
        }
        try {
            executor.execute(() -> {
                try {
                    closeable.close();
                }
                catch (Exception e) {
                    Platform.restoreInterruptIfIsInterruptedException(e);
                    logger.log(Level.WARNING, "thrown by close()", e);
                }
            });
        }
        catch (RejectedExecutionException e) {
            if (logger.isLoggable(Level.WARNING)) {
                logger.log(Level.WARNING, String.format("while submitting close to %s; will close inline", executor), e);
            }
            ClosingFuture.closeQuietly(closeable, MoreExecutors.directExecutor());
        }
    }

    private void checkAndUpdateState(State oldState, State newState) {
        Preconditions.checkState(this.compareAndUpdateState(oldState, newState), "Expected state to be %s, but it was %s", (Object)oldState, (Object)newState);
    }

    private boolean compareAndUpdateState(State oldState, State newState) {
        return this.state.compareAndSet(oldState, newState);
    }

    @VisibleForTesting
    CountDownLatch whenClosedCountDown() {
        return this.closeables.whenClosedCountDown();
    }

    static enum State {
        OPEN,
        SUBSUMED,
        WILL_CLOSE,
        CLOSING,
        CLOSED,
        WILL_CREATE_VALUE_AND_CLOSER;

    }

    private static final class CloseableList
    extends IdentityHashMap<AutoCloseable, Executor>
    implements Closeable {
        private final DeferredCloser closer = new DeferredCloser(this);
        private volatile boolean closed;
        @CheckForNull
        private volatile CountDownLatch whenClosed;

        private CloseableList() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        <V, U> ListenableFuture<U> applyClosingFunction(ClosingFunction<? super V, U> transformation, @ParametricNullness V input) throws Exception {
            CloseableList newCloseables = new CloseableList();
            try {
                ListenableFuture<U> listenableFuture = Futures.immediateFuture(transformation.apply(newCloseables.closer, input));
                return listenableFuture;
            }
            finally {
                this.add(newCloseables, MoreExecutors.directExecutor());
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        <V, U> FluentFuture<U> applyAsyncClosingFunction(AsyncClosingFunction<V, U> transformation, @ParametricNullness V input) throws Exception {
            CloseableList newCloseables = new CloseableList();
            try {
                ClosingFuture<U> closingFuture = transformation.apply(newCloseables.closer, input);
                ((ClosingFuture)closingFuture).becomeSubsumedInto(newCloseables);
                FluentFuture fluentFuture = ((ClosingFuture)closingFuture).future;
                return fluentFuture;
            }
            finally {
                this.add(newCloseables, MoreExecutors.directExecutor());
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void close() {
            if (this.closed) {
                return;
            }
            CloseableList closeableList = this;
            synchronized (closeableList) {
                if (this.closed) {
                    return;
                }
                this.closed = true;
            }
            for (Map.Entry entry : this.entrySet()) {
                ClosingFuture.closeQuietly((AutoCloseable)entry.getKey(), (Executor)entry.getValue());
            }
            this.clear();
            if (this.whenClosed != null) {
                this.whenClosed.countDown();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        void add(@CheckForNull AutoCloseable closeable, Executor executor) {
            Preconditions.checkNotNull(executor);
            if (closeable == null) {
                return;
            }
            CloseableList closeableList = this;
            synchronized (closeableList) {
                if (!this.closed) {
                    this.put(closeable, executor);
                    return;
                }
            }
            ClosingFuture.closeQuietly(closeable, executor);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        CountDownLatch whenClosedCountDown() {
            if (this.closed) {
                return new CountDownLatch(0);
            }
            CloseableList closeableList = this;
            synchronized (closeableList) {
                if (this.closed) {
                    return new CountDownLatch(0);
                }
                Preconditions.checkState(this.whenClosed == null);
                this.whenClosed = new CountDownLatch(1);
                return this.whenClosed;
            }
        }
    }

    public static final class Combiner5<V1, V2, V3, V4, V5>
    extends Combiner {
        private final ClosingFuture<V1> future1;
        private final ClosingFuture<V2> future2;
        private final ClosingFuture<V3> future3;
        private final ClosingFuture<V4> future4;
        private final ClosingFuture<V5> future5;

        private Combiner5(ClosingFuture<V1> future1, ClosingFuture<V2> future2, ClosingFuture<V3> future3, ClosingFuture<V4> future4, ClosingFuture<V5> future5) {
            super(true, ImmutableList.of(future1, future2, future3, future4, future5));
            this.future1 = future1;
            this.future2 = future2;
            this.future3 = future3;
            this.future4 = future4;
            this.future5 = future5;
        }

        public <U> ClosingFuture<U> call(final ClosingFunction5<V1, V2, V3, V4, V5, U> function, Executor executor) {
            return this.call(new Combiner.CombiningCallable<U>(){

                @Override
                @ParametricNullness
                public U call(DeferredCloser closer, Peeker peeker) throws Exception {
                    return function.apply(closer, peeker.getDone(future1), peeker.getDone(future2), peeker.getDone(future3), peeker.getDone(future4), peeker.getDone(future5));
                }

                public String toString() {
                    return function.toString();
                }
            }, executor);
        }

        public <U> ClosingFuture<U> callAsync(final AsyncClosingFunction5<V1, V2, V3, V4, V5, U> function, Executor executor) {
            return this.callAsync(new Combiner.AsyncCombiningCallable<U>(){

                @Override
                public ClosingFuture<U> call(DeferredCloser closer, Peeker peeker) throws Exception {
                    return function.apply(closer, peeker.getDone(future1), peeker.getDone(future2), peeker.getDone(future3), peeker.getDone(future4), peeker.getDone(future5));
                }

                public String toString() {
                    return function.toString();
                }
            }, executor);
        }

        @FunctionalInterface
        public static interface AsyncClosingFunction5<V1, V2, V3, V4, V5, U> {
            public ClosingFuture<U> apply(DeferredCloser var1, @ParametricNullness V1 var2, @ParametricNullness V2 var3, @ParametricNullness V3 var4, @ParametricNullness V4 var5, @ParametricNullness V5 var6) throws Exception;
        }

        @FunctionalInterface
        public static interface ClosingFunction5<V1, V2, V3, V4, V5, U> {
            @ParametricNullness
            public U apply(DeferredCloser var1, @ParametricNullness V1 var2, @ParametricNullness V2 var3, @ParametricNullness V3 var4, @ParametricNullness V4 var5, @ParametricNullness V5 var6) throws Exception;
        }
    }

    public static final class Combiner4<V1, V2, V3, V4>
    extends Combiner {
        private final ClosingFuture<V1> future1;
        private final ClosingFuture<V2> future2;
        private final ClosingFuture<V3> future3;
        private final ClosingFuture<V4> future4;

        private Combiner4(ClosingFuture<V1> future1, ClosingFuture<V2> future2, ClosingFuture<V3> future3, ClosingFuture<V4> future4) {
            super(true, ImmutableList.of(future1, future2, future3, future4));
            this.future1 = future1;
            this.future2 = future2;
            this.future3 = future3;
            this.future4 = future4;
        }

        public <U> ClosingFuture<U> call(final ClosingFunction4<V1, V2, V3, V4, U> function, Executor executor) {
            return this.call(new Combiner.CombiningCallable<U>(){

                @Override
                @ParametricNullness
                public U call(DeferredCloser closer, Peeker peeker) throws Exception {
                    return function.apply(closer, peeker.getDone(future1), peeker.getDone(future2), peeker.getDone(future3), peeker.getDone(future4));
                }

                public String toString() {
                    return function.toString();
                }
            }, executor);
        }

        public <U> ClosingFuture<U> callAsync(final AsyncClosingFunction4<V1, V2, V3, V4, U> function, Executor executor) {
            return this.callAsync(new Combiner.AsyncCombiningCallable<U>(){

                @Override
                public ClosingFuture<U> call(DeferredCloser closer, Peeker peeker) throws Exception {
                    return function.apply(closer, peeker.getDone(future1), peeker.getDone(future2), peeker.getDone(future3), peeker.getDone(future4));
                }

                public String toString() {
                    return function.toString();
                }
            }, executor);
        }

        @FunctionalInterface
        public static interface AsyncClosingFunction4<V1, V2, V3, V4, U> {
            public ClosingFuture<U> apply(DeferredCloser var1, @ParametricNullness V1 var2, @ParametricNullness V2 var3, @ParametricNullness V3 var4, @ParametricNullness V4 var5) throws Exception;
        }

        @FunctionalInterface
        public static interface ClosingFunction4<V1, V2, V3, V4, U> {
            @ParametricNullness
            public U apply(DeferredCloser var1, @ParametricNullness V1 var2, @ParametricNullness V2 var3, @ParametricNullness V3 var4, @ParametricNullness V4 var5) throws Exception;
        }
    }

    public static final class Combiner3<V1, V2, V3>
    extends Combiner {
        private final ClosingFuture<V1> future1;
        private final ClosingFuture<V2> future2;
        private final ClosingFuture<V3> future3;

        private Combiner3(ClosingFuture<V1> future1, ClosingFuture<V2> future2, ClosingFuture<V3> future3) {
            super(true, ImmutableList.of(future1, future2, future3));
            this.future1 = future1;
            this.future2 = future2;
            this.future3 = future3;
        }

        public <U> ClosingFuture<U> call(final ClosingFunction3<V1, V2, V3, U> function, Executor executor) {
            return this.call(new Combiner.CombiningCallable<U>(){

                @Override
                @ParametricNullness
                public U call(DeferredCloser closer, Peeker peeker) throws Exception {
                    return function.apply(closer, peeker.getDone(future1), peeker.getDone(future2), peeker.getDone(future3));
                }

                public String toString() {
                    return function.toString();
                }
            }, executor);
        }

        public <U> ClosingFuture<U> callAsync(final AsyncClosingFunction3<V1, V2, V3, U> function, Executor executor) {
            return this.callAsync(new Combiner.AsyncCombiningCallable<U>(){

                @Override
                public ClosingFuture<U> call(DeferredCloser closer, Peeker peeker) throws Exception {
                    return function.apply(closer, peeker.getDone(future1), peeker.getDone(future2), peeker.getDone(future3));
                }

                public String toString() {
                    return function.toString();
                }
            }, executor);
        }

        @FunctionalInterface
        public static interface AsyncClosingFunction3<V1, V2, V3, U> {
            public ClosingFuture<U> apply(DeferredCloser var1, @ParametricNullness V1 var2, @ParametricNullness V2 var3, @ParametricNullness V3 var4) throws Exception;
        }

        @FunctionalInterface
        public static interface ClosingFunction3<V1, V2, V3, U> {
            @ParametricNullness
            public U apply(DeferredCloser var1, @ParametricNullness V1 var2, @ParametricNullness V2 var3, @ParametricNullness V3 var4) throws Exception;
        }
    }

    public static final class Combiner2<V1, V2>
    extends Combiner {
        private final ClosingFuture<V1> future1;
        private final ClosingFuture<V2> future2;

        private Combiner2(ClosingFuture<V1> future1, ClosingFuture<V2> future2) {
            super(true, ImmutableList.of(future1, future2));
            this.future1 = future1;
            this.future2 = future2;
        }

        public <U> ClosingFuture<U> call(final ClosingFunction2<V1, V2, U> function, Executor executor) {
            return this.call(new Combiner.CombiningCallable<U>(){

                @Override
                @ParametricNullness
                public U call(DeferredCloser closer, Peeker peeker) throws Exception {
                    return function.apply(closer, peeker.getDone(future1), peeker.getDone(future2));
                }

                public String toString() {
                    return function.toString();
                }
            }, executor);
        }

        public <U> ClosingFuture<U> callAsync(final AsyncClosingFunction2<V1, V2, U> function, Executor executor) {
            return this.callAsync(new Combiner.AsyncCombiningCallable<U>(){

                @Override
                public ClosingFuture<U> call(DeferredCloser closer, Peeker peeker) throws Exception {
                    return function.apply(closer, peeker.getDone(future1), peeker.getDone(future2));
                }

                public String toString() {
                    return function.toString();
                }
            }, executor);
        }

        @FunctionalInterface
        public static interface AsyncClosingFunction2<V1, V2, U> {
            public ClosingFuture<U> apply(DeferredCloser var1, @ParametricNullness V1 var2, @ParametricNullness V2 var3) throws Exception;
        }

        @FunctionalInterface
        public static interface ClosingFunction2<V1, V2, U> {
            @ParametricNullness
            public U apply(DeferredCloser var1, @ParametricNullness V1 var2, @ParametricNullness V2 var3) throws Exception;
        }
    }

    @DoNotMock(value="Use ClosingFuture.whenAllSucceed() or .whenAllComplete() instead.")
    public static class Combiner {
        private final CloseableList closeables = new CloseableList();
        private final boolean allMustSucceed;
        protected final ImmutableList<ClosingFuture<?>> inputs;

        private Combiner(boolean allMustSucceed, Iterable<? extends ClosingFuture<?>> inputs) {
            this.allMustSucceed = allMustSucceed;
            this.inputs = ImmutableList.copyOf(inputs);
            for (ClosingFuture<?> input : inputs) {
                ((ClosingFuture)input).becomeSubsumedInto(this.closeables);
            }
        }

        public <V> ClosingFuture<V> call(final CombiningCallable<V> combiningCallable, Executor executor) {
            Callable callable = new Callable<V>(){

                @Override
                @ParametricNullness
                public V call() throws Exception {
                    return new Peeker(inputs).call(combiningCallable, closeables);
                }

                public String toString() {
                    return combiningCallable.toString();
                }
            };
            ClosingFuture derived = new ClosingFuture(this.futureCombiner().call(callable, executor));
            derived.closeables.add(this.closeables, MoreExecutors.directExecutor());
            return derived;
        }

        public <V> ClosingFuture<V> callAsync(final AsyncCombiningCallable<V> combiningCallable, Executor executor) {
            AsyncCallable asyncCallable = new AsyncCallable<V>(){

                @Override
                public ListenableFuture<V> call() throws Exception {
                    return new Peeker(inputs).callAsync(combiningCallable, closeables);
                }

                public String toString() {
                    return combiningCallable.toString();
                }
            };
            ClosingFuture derived = new ClosingFuture(this.futureCombiner().callAsync(asyncCallable, executor));
            derived.closeables.add(this.closeables, MoreExecutors.directExecutor());
            return derived;
        }

        private Futures.FutureCombiner<@Nullable Object> futureCombiner() {
            return this.allMustSucceed ? Futures.whenAllSucceed(this.inputFutures()) : Futures.whenAllComplete(this.inputFutures());
        }

        private ImmutableList<FluentFuture<?>> inputFutures() {
            return FluentIterable.from(this.inputs).transform(future -> ((ClosingFuture)future).future).toList();
        }

        @FunctionalInterface
        public static interface AsyncCombiningCallable<V> {
            public ClosingFuture<V> call(DeferredCloser var1, Peeker var2) throws Exception;
        }

        @FunctionalInterface
        public static interface CombiningCallable<V> {
            @ParametricNullness
            public V call(DeferredCloser var1, Peeker var2) throws Exception;
        }
    }

    public static final class Peeker {
        private final ImmutableList<ClosingFuture<?>> futures;
        private volatile boolean beingCalled;

        private Peeker(ImmutableList<ClosingFuture<?>> futures) {
            this.futures = Preconditions.checkNotNull(futures);
        }

        @ParametricNullness
        public final <D> D getDone(ClosingFuture<D> closingFuture) throws ExecutionException {
            Preconditions.checkState(this.beingCalled);
            Preconditions.checkArgument(this.futures.contains(closingFuture));
            return (D)Futures.getDone(((ClosingFuture)closingFuture).future);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @ParametricNullness
        private <V> V call(Combiner.CombiningCallable<V> combiner, CloseableList closeables) throws Exception {
            this.beingCalled = true;
            CloseableList newCloseables = new CloseableList();
            try {
                V v = combiner.call(newCloseables.closer, this);
                return v;
            }
            finally {
                closeables.add(newCloseables, MoreExecutors.directExecutor());
                this.beingCalled = false;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private <V> FluentFuture<V> callAsync(Combiner.AsyncCombiningCallable<V> combiner, CloseableList closeables) throws Exception {
            this.beingCalled = true;
            CloseableList newCloseables = new CloseableList();
            try {
                ClosingFuture<V> closingFuture = combiner.call(newCloseables.closer, this);
                ((ClosingFuture)closingFuture).becomeSubsumedInto(closeables);
                FluentFuture fluentFuture = ((ClosingFuture)closingFuture).future;
                return fluentFuture;
            }
            finally {
                closeables.add(newCloseables, MoreExecutors.directExecutor());
                this.beingCalled = false;
            }
        }
    }

    @FunctionalInterface
    public static interface ValueAndCloserConsumer<V> {
        public void accept(ValueAndCloser<V> var1);
    }

    public static final class ValueAndCloser<V> {
        private final ClosingFuture<? extends V> closingFuture;

        ValueAndCloser(ClosingFuture<? extends V> closingFuture) {
            this.closingFuture = Preconditions.checkNotNull(closingFuture);
        }

        @ParametricNullness
        public V get() throws ExecutionException {
            return Futures.getDone(((ClosingFuture)this.closingFuture).future);
        }

        public void closeAsync() {
            ((ClosingFuture)this.closingFuture).close();
        }
    }

    @FunctionalInterface
    public static interface AsyncClosingFunction<T, U> {
        public ClosingFuture<U> apply(DeferredCloser var1, @ParametricNullness T var2) throws Exception;
    }

    @FunctionalInterface
    public static interface ClosingFunction<T, U> {
        @ParametricNullness
        public U apply(DeferredCloser var1, @ParametricNullness T var2) throws Exception;
    }

    @FunctionalInterface
    public static interface AsyncClosingCallable<V> {
        public ClosingFuture<V> call(DeferredCloser var1) throws Exception;
    }

    @FunctionalInterface
    public static interface ClosingCallable<V> {
        @ParametricNullness
        public V call(DeferredCloser var1) throws Exception;
    }

    public static final class DeferredCloser {
        @RetainedWith
        private final CloseableList list;

        DeferredCloser(CloseableList list) {
            this.list = list;
        }

        @ParametricNullness
        @CanIgnoreReturnValue
        public <C> C eventuallyClose(@ParametricNullness C closeable, Executor closingExecutor) {
            Preconditions.checkNotNull(closingExecutor);
            if (closeable != null) {
                this.list.add((AutoCloseable)closeable, closingExecutor);
            }
            return closeable;
        }
    }
}

