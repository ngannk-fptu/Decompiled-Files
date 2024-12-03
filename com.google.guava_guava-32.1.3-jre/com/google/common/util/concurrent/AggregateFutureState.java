/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.j2objc.annotations.ReflectionSupport
 *  com.google.j2objc.annotations.ReflectionSupport$Level
 *  javax.annotation.CheckForNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.google.common.util.concurrent;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.AbstractFuture;
import com.google.common.util.concurrent.ElementTypesAreNonnullByDefault;
import com.google.j2objc.annotations.ReflectionSupport;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.CheckForNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@ElementTypesAreNonnullByDefault
@GwtCompatible(emulated=true)
@ReflectionSupport(value=ReflectionSupport.Level.FULL)
abstract class AggregateFutureState<OutputT>
extends AbstractFuture.TrustedFuture<OutputT> {
    @CheckForNull
    private volatile Set<Throwable> seenExceptions = null;
    private volatile int remaining;
    private static final AtomicHelper ATOMIC_HELPER;
    private static final Logger log;

    AggregateFutureState(int remainingFutures) {
        this.remaining = remainingFutures;
    }

    final Set<Throwable> getOrInitSeenExceptions() {
        Set<Throwable> seenExceptionsLocal = this.seenExceptions;
        if (seenExceptionsLocal == null) {
            seenExceptionsLocal = Sets.newConcurrentHashSet();
            this.addInitialException(seenExceptionsLocal);
            ATOMIC_HELPER.compareAndSetSeenExceptions(this, null, seenExceptionsLocal);
            seenExceptionsLocal = Objects.requireNonNull(this.seenExceptions);
        }
        return seenExceptionsLocal;
    }

    abstract void addInitialException(Set<Throwable> var1);

    final int decrementRemainingAndGet() {
        return ATOMIC_HELPER.decrementAndGetRemainingCount(this);
    }

    final void clearSeenExceptions() {
        this.seenExceptions = null;
    }

    static {
        AtomicHelper helper;
        log = Logger.getLogger(AggregateFutureState.class.getName());
        Throwable thrownReflectionFailure = null;
        try {
            helper = new SafeAtomicHelper(AtomicReferenceFieldUpdater.newUpdater(AggregateFutureState.class, Set.class, "seenExceptions"), AtomicIntegerFieldUpdater.newUpdater(AggregateFutureState.class, "remaining"));
        }
        catch (Error | RuntimeException reflectionFailure) {
            thrownReflectionFailure = reflectionFailure;
            helper = new SynchronizedAtomicHelper();
        }
        ATOMIC_HELPER = helper;
        if (thrownReflectionFailure != null) {
            log.log(Level.SEVERE, "SafeAtomicHelper is broken!", thrownReflectionFailure);
        }
    }

    private static final class SynchronizedAtomicHelper
    extends AtomicHelper {
        private SynchronizedAtomicHelper() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        void compareAndSetSeenExceptions(AggregateFutureState<?> state, @CheckForNull Set<Throwable> expect, Set<Throwable> update) {
            AggregateFutureState<?> aggregateFutureState = state;
            synchronized (aggregateFutureState) {
                if (((AggregateFutureState)state).seenExceptions == expect) {
                    ((AggregateFutureState)state).seenExceptions = update;
                }
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        int decrementAndGetRemainingCount(AggregateFutureState<?> state) {
            AggregateFutureState<?> aggregateFutureState = state;
            synchronized (aggregateFutureState) {
                return --((AggregateFutureState)state).remaining;
            }
        }
    }

    private static final class SafeAtomicHelper
    extends AtomicHelper {
        final AtomicReferenceFieldUpdater<AggregateFutureState<?>, @Nullable Set<Throwable>> seenExceptionsUpdater;
        final AtomicIntegerFieldUpdater<AggregateFutureState<?>> remainingCountUpdater;

        SafeAtomicHelper(AtomicReferenceFieldUpdater seenExceptionsUpdater, AtomicIntegerFieldUpdater remainingCountUpdater) {
            this.seenExceptionsUpdater = seenExceptionsUpdater;
            this.remainingCountUpdater = remainingCountUpdater;
        }

        @Override
        void compareAndSetSeenExceptions(AggregateFutureState<?> state, @CheckForNull Set<Throwable> expect, Set<Throwable> update) {
            this.seenExceptionsUpdater.compareAndSet(state, expect, update);
        }

        @Override
        int decrementAndGetRemainingCount(AggregateFutureState<?> state) {
            return this.remainingCountUpdater.decrementAndGet(state);
        }
    }

    private static abstract class AtomicHelper {
        private AtomicHelper() {
        }

        abstract void compareAndSetSeenExceptions(AggregateFutureState<?> var1, @CheckForNull Set<Throwable> var2, Set<Throwable> var3);

        abstract int decrementAndGetRemainingCount(AggregateFutureState<?> var1);
    }
}

