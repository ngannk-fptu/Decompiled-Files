/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.ForOverride
 *  com.google.errorprone.annotations.OverridingMethodsMustInvokeSuper
 *  javax.annotation.CheckForNull
 */
package com.google.common.util.concurrent;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableCollection;
import com.google.common.util.concurrent.AggregateFutureState;
import com.google.common.util.concurrent.ElementTypesAreNonnullByDefault;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ParametricNullness;
import com.google.errorprone.annotations.ForOverride;
import com.google.errorprone.annotations.OverridingMethodsMustInvokeSuper;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible
abstract class AggregateFuture<InputT, OutputT>
extends AggregateFutureState<OutputT> {
    private static final Logger logger = Logger.getLogger(AggregateFuture.class.getName());
    @CheckForNull
    private ImmutableCollection<? extends ListenableFuture<? extends InputT>> futures;
    private final boolean allMustSucceed;
    private final boolean collectsValues;

    AggregateFuture(ImmutableCollection<? extends ListenableFuture<? extends InputT>> futures, boolean allMustSucceed, boolean collectsValues) {
        super(futures.size());
        this.futures = Preconditions.checkNotNull(futures);
        this.allMustSucceed = allMustSucceed;
        this.collectsValues = collectsValues;
    }

    @Override
    protected final void afterDone() {
        super.afterDone();
        ImmutableCollection<ListenableFuture<InputT>> localFutures = this.futures;
        this.releaseResources(ReleaseResourcesReason.OUTPUT_FUTURE_DONE);
        if (this.isCancelled() & localFutures != null) {
            boolean wasInterrupted = this.wasInterrupted();
            for (Future future : localFutures) {
                future.cancel(wasInterrupted);
            }
        }
    }

    @Override
    @CheckForNull
    protected final String pendingToString() {
        ImmutableCollection<? extends ListenableFuture<? extends InputT>> localFutures = this.futures;
        if (localFutures != null) {
            return "futures=" + localFutures;
        }
        return super.pendingToString();
    }

    final void init() {
        Objects.requireNonNull(this.futures);
        if (this.futures.isEmpty()) {
            this.handleAllCompleted();
            return;
        }
        if (this.allMustSucceed) {
            int i = 0;
            for (ListenableFuture listenableFuture : this.futures) {
                int n = i++;
                listenableFuture.addListener(() -> {
                    try {
                        if (future.isCancelled()) {
                            this.futures = null;
                            this.cancel(false);
                        } else {
                            this.collectValueFromNonCancelledFuture(index, future);
                        }
                    }
                    finally {
                        this.decrementCountAndMaybeComplete(null);
                    }
                }, MoreExecutors.directExecutor());
            }
        } else {
            ImmutableCollection<ListenableFuture<InputT>> localFutures = this.collectsValues ? this.futures : null;
            Runnable listener = () -> this.decrementCountAndMaybeComplete(localFutures);
            for (ListenableFuture listenableFuture : this.futures) {
                listenableFuture.addListener(listener, MoreExecutors.directExecutor());
            }
        }
    }

    private void handleException(Throwable throwable) {
        boolean firstTimeSeeingThisException;
        boolean completedWithFailure;
        Preconditions.checkNotNull(throwable);
        if (this.allMustSucceed && !(completedWithFailure = this.setException(throwable)) && (firstTimeSeeingThisException = AggregateFuture.addCausalChain(this.getOrInitSeenExceptions(), throwable))) {
            AggregateFuture.log(throwable);
            return;
        }
        if (throwable instanceof Error) {
            AggregateFuture.log(throwable);
        }
    }

    private static void log(Throwable throwable) {
        String message = throwable instanceof Error ? "Input Future failed with Error" : "Got more than one input Future failure. Logging failures after the first";
        logger.log(Level.SEVERE, message, throwable);
    }

    @Override
    final void addInitialException(Set<Throwable> seen) {
        Preconditions.checkNotNull(seen);
        if (!this.isCancelled()) {
            boolean bl = AggregateFuture.addCausalChain(seen, Objects.requireNonNull(this.tryInternalFastPathGetFailure()));
        }
    }

    private void collectValueFromNonCancelledFuture(int index, Future<? extends InputT> future) {
        try {
            this.collectOneValue(index, Futures.getDone(future));
        }
        catch (ExecutionException e) {
            this.handleException(e.getCause());
        }
        catch (Error | RuntimeException t) {
            this.handleException(t);
        }
    }

    private void decrementCountAndMaybeComplete(@CheckForNull ImmutableCollection<? extends Future<? extends InputT>> futuresIfNeedToCollectAtCompletion) {
        int newRemaining = this.decrementRemainingAndGet();
        Preconditions.checkState(newRemaining >= 0, "Less than 0 remaining futures");
        if (newRemaining == 0) {
            this.processCompleted(futuresIfNeedToCollectAtCompletion);
        }
    }

    private void processCompleted(@CheckForNull ImmutableCollection<? extends Future<? extends InputT>> futuresIfNeedToCollectAtCompletion) {
        if (futuresIfNeedToCollectAtCompletion != null) {
            int i = 0;
            for (Future future : futuresIfNeedToCollectAtCompletion) {
                if (!future.isCancelled()) {
                    this.collectValueFromNonCancelledFuture(i, future);
                }
                ++i;
            }
        }
        this.clearSeenExceptions();
        this.handleAllCompleted();
        this.releaseResources(ReleaseResourcesReason.ALL_INPUT_FUTURES_PROCESSED);
    }

    @ForOverride
    @OverridingMethodsMustInvokeSuper
    void releaseResources(ReleaseResourcesReason reason) {
        Preconditions.checkNotNull(reason);
        this.futures = null;
    }

    abstract void collectOneValue(int var1, @ParametricNullness InputT var2);

    abstract void handleAllCompleted();

    private static boolean addCausalChain(Set<Throwable> seen, Throwable param) {
        for (Throwable t = param; t != null; t = t.getCause()) {
            boolean firstTimeSeen = seen.add(t);
            if (firstTimeSeen) continue;
            return false;
        }
        return true;
    }

    static enum ReleaseResourcesReason {
        OUTPUT_FUTURE_DONE,
        ALL_INPUT_FUTURES_PROCESSED;

    }
}

