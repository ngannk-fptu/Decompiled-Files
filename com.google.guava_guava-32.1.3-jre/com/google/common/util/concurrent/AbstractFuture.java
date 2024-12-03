/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.util.concurrent.internal.InternalFutureFailureAccess
 *  com.google.common.util.concurrent.internal.InternalFutures
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 *  com.google.errorprone.annotations.ForOverride
 *  com.google.j2objc.annotations.ReflectionSupport
 *  com.google.j2objc.annotations.ReflectionSupport$Level
 *  javax.annotation.CheckForNull
 */
package com.google.common.util.concurrent;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.util.concurrent.DirectExecutor;
import com.google.common.util.concurrent.ElementTypesAreNonnullByDefault;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.NullnessCasts;
import com.google.common.util.concurrent.OverflowAvoidingLockSupport;
import com.google.common.util.concurrent.ParametricNullness;
import com.google.common.util.concurrent.internal.InternalFutureFailureAccess;
import com.google.common.util.concurrent.internal.InternalFutures;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.ForOverride;
import com.google.j2objc.annotations.ReflectionSupport;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.concurrent.locks.LockSupport;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.CheckForNull;
import sun.misc.Unsafe;

@ElementTypesAreNonnullByDefault
@GwtCompatible(emulated=true)
@ReflectionSupport(value=ReflectionSupport.Level.FULL)
public abstract class AbstractFuture<V>
extends InternalFutureFailureAccess
implements ListenableFuture<V> {
    static final boolean GENERATE_CANCELLATION_CAUSES;
    private static final Logger log;
    private static final long SPIN_THRESHOLD_NANOS = 1000L;
    private static final AtomicHelper ATOMIC_HELPER;
    private static final Object NULL;
    @CheckForNull
    private volatile Object value;
    @CheckForNull
    private volatile Listener listeners;
    @CheckForNull
    private volatile Waiter waiters;

    /*
     * Unable to fully structure code
     */
    private void removeWaiter(Waiter node) {
        node.thread = null;
        block0: while (true) {
            pred = null;
            curr = this.waiters;
            if (curr == Waiter.TOMBSTONE) {
                return;
            }
            while (curr != null) {
                succ = curr.next;
                if (curr.thread != null) {
                    pred = curr;
                } else if (pred != null) {
                    pred.next = succ;
                    if (pred.thread == null) {
                        continue block0;
                    }
                } else {
                    if (AbstractFuture.ATOMIC_HELPER.casWaiters(this, curr, succ)) ** break;
                    continue block0;
                }
                curr = succ;
            }
            break;
        }
    }

    protected AbstractFuture() {
    }

    @Override
    @ParametricNullness
    @CanIgnoreReturnValue
    public V get(long timeout, TimeUnit unit) throws InterruptedException, TimeoutException, ExecutionException {
        long endNanos;
        Object localValue;
        long remainingNanos;
        block15: {
            long timeoutNanos;
            remainingNanos = timeoutNanos = unit.toNanos(timeout);
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            localValue = this.value;
            if (localValue != null & !(localValue instanceof SetFuture)) {
                return this.getDoneValue(localValue);
            }
            long l = endNanos = remainingNanos > 0L ? System.nanoTime() + remainingNanos : 0L;
            if (remainingNanos >= 1000L) {
                Waiter oldHead = this.waiters;
                if (oldHead != Waiter.TOMBSTONE) {
                    Waiter node = new Waiter();
                    do {
                        node.setNext(oldHead);
                        if (!ATOMIC_HELPER.casWaiters(this, oldHead, node)) continue;
                        do {
                            OverflowAvoidingLockSupport.parkNanos(this, remainingNanos);
                            if (Thread.interrupted()) {
                                this.removeWaiter(node);
                                throw new InterruptedException();
                            }
                            localValue = this.value;
                            if (!(localValue != null & !(localValue instanceof SetFuture))) continue;
                            return this.getDoneValue(localValue);
                        } while ((remainingNanos = endNanos - System.nanoTime()) >= 1000L);
                        this.removeWaiter(node);
                        break block15;
                    } while ((oldHead = this.waiters) != Waiter.TOMBSTONE);
                }
                return this.getDoneValue(Objects.requireNonNull(this.value));
            }
        }
        while (remainingNanos > 0L) {
            localValue = this.value;
            if (localValue != null & !(localValue instanceof SetFuture)) {
                return this.getDoneValue(localValue);
            }
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            remainingNanos = endNanos - System.nanoTime();
        }
        String futureToString = this.toString();
        String unitString = unit.toString().toLowerCase(Locale.ROOT);
        String message = "Waited " + timeout + " " + unit.toString().toLowerCase(Locale.ROOT);
        if (remainingNanos + 1000L < 0L) {
            boolean shouldShowExtraNanos;
            message = message + " (plus ";
            long overWaitNanos = -remainingNanos;
            long overWaitUnits = unit.convert(overWaitNanos, TimeUnit.NANOSECONDS);
            long overWaitLeftoverNanos = overWaitNanos - unit.toNanos(overWaitUnits);
            boolean bl = shouldShowExtraNanos = overWaitUnits == 0L || overWaitLeftoverNanos > 1000L;
            if (overWaitUnits > 0L) {
                message = message + overWaitUnits + " " + unitString;
                if (shouldShowExtraNanos) {
                    message = message + ",";
                }
                message = message + " ";
            }
            if (shouldShowExtraNanos) {
                message = message + overWaitLeftoverNanos + " nanoseconds ";
            }
            message = message + "delay)";
        }
        if (this.isDone()) {
            throw new TimeoutException(message + " but future completed as timeout expired");
        }
        throw new TimeoutException(message + " for " + futureToString);
    }

    @Override
    @ParametricNullness
    @CanIgnoreReturnValue
    public V get() throws InterruptedException, ExecutionException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
        Object localValue = this.value;
        if (localValue != null & !(localValue instanceof SetFuture)) {
            return this.getDoneValue(localValue);
        }
        Waiter oldHead = this.waiters;
        if (oldHead != Waiter.TOMBSTONE) {
            Waiter node = new Waiter();
            do {
                node.setNext(oldHead);
                if (!ATOMIC_HELPER.casWaiters(this, oldHead, node)) continue;
                do {
                    LockSupport.park(this);
                    if (!Thread.interrupted()) continue;
                    this.removeWaiter(node);
                    throw new InterruptedException();
                } while (!((localValue = this.value) != null & !(localValue instanceof SetFuture)));
                return this.getDoneValue(localValue);
            } while ((oldHead = this.waiters) != Waiter.TOMBSTONE);
        }
        return this.getDoneValue(Objects.requireNonNull(this.value));
    }

    @ParametricNullness
    private V getDoneValue(Object obj) throws ExecutionException {
        if (obj instanceof Cancellation) {
            throw AbstractFuture.cancellationExceptionWithCause("Task was cancelled.", ((Cancellation)obj).cause);
        }
        if (obj instanceof Failure) {
            throw new ExecutionException(((Failure)obj).exception);
        }
        if (obj == NULL) {
            return (V)NullnessCasts.uncheckedNull();
        }
        Object asV = obj;
        return (V)asV;
    }

    @Override
    public boolean isDone() {
        Object localValue = this.value;
        return localValue != null & !(localValue instanceof SetFuture);
    }

    @Override
    public boolean isCancelled() {
        Object localValue = this.value;
        return localValue instanceof Cancellation;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    @CanIgnoreReturnValue
    public boolean cancel(boolean mayInterruptIfRunning) {
        Object localValue = this.value;
        boolean rValue = false;
        if (!(localValue == null | localValue instanceof SetFuture)) return rValue;
        Cancellation valueToSet = GENERATE_CANCELLATION_CAUSES ? new Cancellation(mayInterruptIfRunning, new CancellationException("Future.cancel() was called.")) : Objects.requireNonNull(mayInterruptIfRunning ? Cancellation.CAUSELESS_INTERRUPTED : Cancellation.CAUSELESS_CANCELLED);
        AbstractFuture abstractFuture = this;
        while (true) {
            if (ATOMIC_HELPER.casValue(abstractFuture, localValue, valueToSet)) {
                rValue = true;
                AbstractFuture.complete(abstractFuture, mayInterruptIfRunning);
                if (!(localValue instanceof SetFuture)) return rValue;
                ListenableFuture futureToPropagateTo = ((SetFuture)localValue).future;
                if (futureToPropagateTo instanceof Trusted) {
                    AbstractFuture trusted = (AbstractFuture)futureToPropagateTo;
                    localValue = trusted.value;
                    if (!(localValue == null | localValue instanceof SetFuture)) return rValue;
                    abstractFuture = trusted;
                    continue;
                }
                futureToPropagateTo.cancel(mayInterruptIfRunning);
                return rValue;
            }
            localValue = abstractFuture.value;
            if (!(localValue instanceof SetFuture)) return rValue;
        }
    }

    protected void interruptTask() {
    }

    protected final boolean wasInterrupted() {
        Object localValue = this.value;
        return localValue instanceof Cancellation && ((Cancellation)localValue).wasInterrupted;
    }

    @Override
    public void addListener(Runnable listener, Executor executor) {
        Listener oldHead;
        Preconditions.checkNotNull(listener, "Runnable was null.");
        Preconditions.checkNotNull(executor, "Executor was null.");
        if (!this.isDone() && (oldHead = this.listeners) != Listener.TOMBSTONE) {
            Listener newNode = new Listener(listener, executor);
            do {
                newNode.next = oldHead;
                if (!ATOMIC_HELPER.casListeners(this, oldHead, newNode)) continue;
                return;
            } while ((oldHead = this.listeners) != Listener.TOMBSTONE);
        }
        AbstractFuture.executeListener(listener, executor);
    }

    @CanIgnoreReturnValue
    protected boolean set(@ParametricNullness V value) {
        Object valueToSet;
        Object object = valueToSet = value == null ? NULL : value;
        if (ATOMIC_HELPER.casValue(this, null, valueToSet)) {
            AbstractFuture.complete(this, false);
            return true;
        }
        return false;
    }

    @CanIgnoreReturnValue
    protected boolean setException(Throwable throwable) {
        Failure valueToSet = new Failure(Preconditions.checkNotNull(throwable));
        if (ATOMIC_HELPER.casValue(this, null, valueToSet)) {
            AbstractFuture.complete(this, false);
            return true;
        }
        return false;
    }

    @CanIgnoreReturnValue
    protected boolean setFuture(ListenableFuture<? extends V> future) {
        Preconditions.checkNotNull(future);
        Object localValue = this.value;
        if (localValue == null) {
            if (future.isDone()) {
                Object value = AbstractFuture.getFutureValue(future);
                if (ATOMIC_HELPER.casValue(this, null, value)) {
                    AbstractFuture.complete(this, false);
                    return true;
                }
                return false;
            }
            SetFuture<? extends V> valueToSet = new SetFuture<V>(this, future);
            if (ATOMIC_HELPER.casValue(this, null, valueToSet)) {
                try {
                    future.addListener(valueToSet, DirectExecutor.INSTANCE);
                }
                catch (Error | RuntimeException t) {
                    Failure failure;
                    try {
                        failure = new Failure(t);
                    }
                    catch (Error | RuntimeException oomMostLikely) {
                        failure = Failure.FALLBACK_INSTANCE;
                    }
                    boolean bl = ATOMIC_HELPER.casValue(this, valueToSet, failure);
                }
                return true;
            }
            localValue = this.value;
        }
        if (localValue instanceof Cancellation) {
            future.cancel(((Cancellation)localValue).wasInterrupted);
        }
        return false;
    }

    private static Object getFutureValue(ListenableFuture<?> future) {
        boolean wasCancelled;
        Throwable throwable;
        if (future instanceof Trusted) {
            Object v = ((AbstractFuture)future).value;
            if (v instanceof Cancellation) {
                Cancellation c = (Cancellation)v;
                if (c.wasInterrupted) {
                    v = c.cause != null ? new Cancellation(false, c.cause) : Cancellation.CAUSELESS_CANCELLED;
                }
            }
            return Objects.requireNonNull(v);
        }
        if (future instanceof InternalFutureFailureAccess && (throwable = InternalFutures.tryInternalFastPathGetFailure((InternalFutureFailureAccess)((InternalFutureFailureAccess)future))) != null) {
            return new Failure(throwable);
        }
        if (!GENERATE_CANCELLATION_CAUSES & (wasCancelled = future.isCancelled())) {
            return Objects.requireNonNull(Cancellation.CAUSELESS_CANCELLED);
        }
        try {
            Object v = AbstractFuture.getUninterruptibly(future);
            if (wasCancelled) {
                return new Cancellation(false, new IllegalArgumentException("get() did not throw CancellationException, despite reporting isCancelled() == true: " + future));
            }
            return v == null ? NULL : v;
        }
        catch (ExecutionException exception) {
            if (wasCancelled) {
                return new Cancellation(false, new IllegalArgumentException("get() did not throw CancellationException, despite reporting isCancelled() == true: " + future, exception));
            }
            return new Failure(exception.getCause());
        }
        catch (CancellationException cancellation) {
            if (!wasCancelled) {
                return new Failure(new IllegalArgumentException("get() threw CancellationException, despite reporting isCancelled() == false: " + future, cancellation));
            }
            return new Cancellation(false, cancellation);
        }
        catch (Error | RuntimeException t) {
            return new Failure(t);
        }
    }

    @ParametricNullness
    private static <V> V getUninterruptibly(Future<V> future) throws ExecutionException {
        boolean interrupted = false;
        while (true) {
            try {
                V v = future.get();
                return v;
            }
            catch (InterruptedException e) {
                interrupted = true;
                continue;
            }
            break;
        }
        finally {
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private static void complete(AbstractFuture<?> param, boolean callInterruptTask) {
        AbstractFuture<Object> future = param;
        Listener next = null;
        block0: while (true) {
            super.releaseWaiters();
            if (callInterruptTask) {
                future.interruptTask();
                callInterruptTask = false;
            }
            future.afterDone();
            next = super.clearListeners(next);
            future = null;
            while (next != null) {
                Listener curr = next;
                next = next.next;
                Runnable task = Objects.requireNonNull(curr.task);
                if (task instanceof SetFuture) {
                    Object valueToSet;
                    SetFuture setFuture = (SetFuture)task;
                    future = setFuture.owner;
                    if (future.value != setFuture || !ATOMIC_HELPER.casValue(future, setFuture, valueToSet = AbstractFuture.getFutureValue(setFuture.future))) continue;
                    continue block0;
                }
                AbstractFuture.executeListener(task, Objects.requireNonNull(curr.executor));
            }
            break;
        }
    }

    @ForOverride
    protected void afterDone() {
    }

    @CheckForNull
    protected final Throwable tryInternalFastPathGetFailure() {
        Object obj;
        if (this instanceof Trusted && (obj = this.value) instanceof Failure) {
            return ((Failure)obj).exception;
        }
        return null;
    }

    final void maybePropagateCancellationTo(@CheckForNull Future<?> related) {
        if (related != null & this.isCancelled()) {
            related.cancel(this.wasInterrupted());
        }
    }

    private void releaseWaiters() {
        Waiter head;
        Waiter currentWaiter = head = ATOMIC_HELPER.gasWaiters(this, Waiter.TOMBSTONE);
        while (currentWaiter != null) {
            currentWaiter.unpark();
            currentWaiter = currentWaiter.next;
        }
    }

    @CheckForNull
    private Listener clearListeners(@CheckForNull Listener onto) {
        Listener head = ATOMIC_HELPER.gasListeners(this, Listener.TOMBSTONE);
        Listener reversedList = onto;
        while (head != null) {
            Listener tmp = head;
            head = head.next;
            tmp.next = reversedList;
            reversedList = tmp;
        }
        return reversedList;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (this.getClass().getName().startsWith("com.google.common.util.concurrent.")) {
            builder.append(this.getClass().getSimpleName());
        } else {
            builder.append(this.getClass().getName());
        }
        builder.append('@').append(Integer.toHexString(System.identityHashCode(this))).append("[status=");
        if (this.isCancelled()) {
            builder.append("CANCELLED");
        } else if (this.isDone()) {
            this.addDoneString(builder);
        } else {
            this.addPendingString(builder);
        }
        return builder.append("]").toString();
    }

    @CheckForNull
    protected String pendingToString() {
        if (this instanceof ScheduledFuture) {
            return "remaining delay=[" + ((ScheduledFuture)((Object)this)).getDelay(TimeUnit.MILLISECONDS) + " ms]";
        }
        return null;
    }

    private void addPendingString(StringBuilder builder) {
        int truncateLength = builder.length();
        builder.append("PENDING");
        Object localValue = this.value;
        if (localValue instanceof SetFuture) {
            builder.append(", setFuture=[");
            this.appendUserObject(builder, ((SetFuture)localValue).future);
            builder.append("]");
        } else {
            String pendingDescription;
            try {
                pendingDescription = Strings.emptyToNull(this.pendingToString());
            }
            catch (RuntimeException | StackOverflowError e) {
                pendingDescription = "Exception thrown from implementation: " + e.getClass();
            }
            if (pendingDescription != null) {
                builder.append(", info=[").append(pendingDescription).append("]");
            }
        }
        if (this.isDone()) {
            builder.delete(truncateLength, builder.length());
            this.addDoneString(builder);
        }
    }

    private void addDoneString(StringBuilder builder) {
        try {
            V value = AbstractFuture.getUninterruptibly(this);
            builder.append("SUCCESS, result=[");
            this.appendResultObject(builder, value);
            builder.append("]");
        }
        catch (ExecutionException e) {
            builder.append("FAILURE, cause=[").append(e.getCause()).append("]");
        }
        catch (CancellationException e) {
            builder.append("CANCELLED");
        }
        catch (RuntimeException e) {
            builder.append("UNKNOWN, cause=[").append(e.getClass()).append(" thrown from get()]");
        }
    }

    private void appendResultObject(StringBuilder builder, @CheckForNull Object o) {
        if (o == null) {
            builder.append("null");
        } else if (o == this) {
            builder.append("this future");
        } else {
            builder.append(o.getClass().getName()).append("@").append(Integer.toHexString(System.identityHashCode(o)));
        }
    }

    private void appendUserObject(StringBuilder builder, @CheckForNull Object o) {
        try {
            if (o == this) {
                builder.append("this future");
            } else {
                builder.append(o);
            }
        }
        catch (RuntimeException | StackOverflowError e) {
            builder.append("Exception thrown from implementation: ").append(e.getClass());
        }
    }

    private static void executeListener(Runnable runnable, Executor executor) {
        try {
            executor.execute(runnable);
        }
        catch (RuntimeException e) {
            log.log(Level.SEVERE, "RuntimeException while executing runnable " + runnable + " with executor " + executor, e);
        }
    }

    private static CancellationException cancellationExceptionWithCause(String message, @CheckForNull Throwable cause) {
        CancellationException exception = new CancellationException(message);
        exception.initCause(cause);
        return exception;
    }

    static {
        AtomicHelper helper;
        boolean generateCancellationCauses;
        try {
            generateCancellationCauses = Boolean.parseBoolean(System.getProperty("guava.concurrent.generate_cancellation_cause", "false"));
        }
        catch (SecurityException e) {
            generateCancellationCauses = false;
        }
        GENERATE_CANCELLATION_CAUSES = generateCancellationCauses;
        log = Logger.getLogger(AbstractFuture.class.getName());
        Throwable thrownUnsafeFailure = null;
        Throwable thrownAtomicReferenceFieldUpdaterFailure = null;
        try {
            helper = new UnsafeAtomicHelper();
        }
        catch (Error | RuntimeException unsafeFailure) {
            thrownUnsafeFailure = unsafeFailure;
            try {
                helper = new SafeAtomicHelper(AtomicReferenceFieldUpdater.newUpdater(Waiter.class, Thread.class, "thread"), AtomicReferenceFieldUpdater.newUpdater(Waiter.class, Waiter.class, "next"), AtomicReferenceFieldUpdater.newUpdater(AbstractFuture.class, Waiter.class, "waiters"), AtomicReferenceFieldUpdater.newUpdater(AbstractFuture.class, Listener.class, "listeners"), AtomicReferenceFieldUpdater.newUpdater(AbstractFuture.class, Object.class, "value"));
            }
            catch (Error | RuntimeException atomicReferenceFieldUpdaterFailure) {
                thrownAtomicReferenceFieldUpdaterFailure = atomicReferenceFieldUpdaterFailure;
                helper = new SynchronizedHelper();
            }
        }
        ATOMIC_HELPER = helper;
        Class<LockSupport> ensureLoaded = LockSupport.class;
        if (thrownAtomicReferenceFieldUpdaterFailure != null) {
            log.log(Level.SEVERE, "UnsafeAtomicHelper is broken!", thrownUnsafeFailure);
            log.log(Level.SEVERE, "SafeAtomicHelper is broken!", thrownAtomicReferenceFieldUpdaterFailure);
        }
        NULL = new Object();
    }

    private static final class SynchronizedHelper
    extends AtomicHelper {
        private SynchronizedHelper() {
        }

        @Override
        void putThread(Waiter waiter, Thread newValue) {
            waiter.thread = newValue;
        }

        @Override
        void putNext(Waiter waiter, @CheckForNull Waiter newValue) {
            waiter.next = newValue;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        boolean casWaiters(AbstractFuture<?> future, @CheckForNull Waiter expect, @CheckForNull Waiter update) {
            AbstractFuture<?> abstractFuture = future;
            synchronized (abstractFuture) {
                if (((AbstractFuture)future).waiters == expect) {
                    ((AbstractFuture)future).waiters = update;
                    return true;
                }
                return false;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        boolean casListeners(AbstractFuture<?> future, @CheckForNull Listener expect, Listener update) {
            AbstractFuture<?> abstractFuture = future;
            synchronized (abstractFuture) {
                if (((AbstractFuture)future).listeners == expect) {
                    ((AbstractFuture)future).listeners = update;
                    return true;
                }
                return false;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        Listener gasListeners(AbstractFuture<?> future, Listener update) {
            AbstractFuture<?> abstractFuture = future;
            synchronized (abstractFuture) {
                Listener old = ((AbstractFuture)future).listeners;
                if (old != update) {
                    ((AbstractFuture)future).listeners = update;
                }
                return old;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        Waiter gasWaiters(AbstractFuture<?> future, Waiter update) {
            AbstractFuture<?> abstractFuture = future;
            synchronized (abstractFuture) {
                Waiter old = ((AbstractFuture)future).waiters;
                if (old != update) {
                    ((AbstractFuture)future).waiters = update;
                }
                return old;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        boolean casValue(AbstractFuture<?> future, @CheckForNull Object expect, Object update) {
            AbstractFuture<?> abstractFuture = future;
            synchronized (abstractFuture) {
                if (((AbstractFuture)future).value == expect) {
                    ((AbstractFuture)future).value = update;
                    return true;
                }
                return false;
            }
        }
    }

    private static final class SafeAtomicHelper
    extends AtomicHelper {
        final AtomicReferenceFieldUpdater<Waiter, Thread> waiterThreadUpdater;
        final AtomicReferenceFieldUpdater<Waiter, Waiter> waiterNextUpdater;
        final AtomicReferenceFieldUpdater<AbstractFuture, Waiter> waitersUpdater;
        final AtomicReferenceFieldUpdater<AbstractFuture, Listener> listenersUpdater;
        final AtomicReferenceFieldUpdater<AbstractFuture, Object> valueUpdater;

        SafeAtomicHelper(AtomicReferenceFieldUpdater<Waiter, Thread> waiterThreadUpdater, AtomicReferenceFieldUpdater<Waiter, Waiter> waiterNextUpdater, AtomicReferenceFieldUpdater<AbstractFuture, Waiter> waitersUpdater, AtomicReferenceFieldUpdater<AbstractFuture, Listener> listenersUpdater, AtomicReferenceFieldUpdater<AbstractFuture, Object> valueUpdater) {
            this.waiterThreadUpdater = waiterThreadUpdater;
            this.waiterNextUpdater = waiterNextUpdater;
            this.waitersUpdater = waitersUpdater;
            this.listenersUpdater = listenersUpdater;
            this.valueUpdater = valueUpdater;
        }

        @Override
        void putThread(Waiter waiter, Thread newValue) {
            this.waiterThreadUpdater.lazySet(waiter, newValue);
        }

        @Override
        void putNext(Waiter waiter, @CheckForNull Waiter newValue) {
            this.waiterNextUpdater.lazySet(waiter, newValue);
        }

        @Override
        boolean casWaiters(AbstractFuture<?> future, @CheckForNull Waiter expect, @CheckForNull Waiter update) {
            return this.waitersUpdater.compareAndSet(future, expect, update);
        }

        @Override
        boolean casListeners(AbstractFuture<?> future, @CheckForNull Listener expect, Listener update) {
            return this.listenersUpdater.compareAndSet(future, expect, update);
        }

        @Override
        Listener gasListeners(AbstractFuture<?> future, Listener update) {
            return this.listenersUpdater.getAndSet(future, update);
        }

        @Override
        Waiter gasWaiters(AbstractFuture<?> future, Waiter update) {
            return this.waitersUpdater.getAndSet(future, update);
        }

        @Override
        boolean casValue(AbstractFuture<?> future, @CheckForNull Object expect, Object update) {
            return this.valueUpdater.compareAndSet(future, expect, update);
        }
    }

    private static final class UnsafeAtomicHelper
    extends AtomicHelper {
        static final Unsafe UNSAFE;
        static final long LISTENERS_OFFSET;
        static final long WAITERS_OFFSET;
        static final long VALUE_OFFSET;
        static final long WAITER_THREAD_OFFSET;
        static final long WAITER_NEXT_OFFSET;

        private UnsafeAtomicHelper() {
        }

        @Override
        void putThread(Waiter waiter, Thread newValue) {
            UNSAFE.putObject(waiter, WAITER_THREAD_OFFSET, newValue);
        }

        @Override
        void putNext(Waiter waiter, @CheckForNull Waiter newValue) {
            UNSAFE.putObject(waiter, WAITER_NEXT_OFFSET, newValue);
        }

        @Override
        boolean casWaiters(AbstractFuture<?> future, @CheckForNull Waiter expect, @CheckForNull Waiter update) {
            return UNSAFE.compareAndSwapObject(future, WAITERS_OFFSET, expect, update);
        }

        @Override
        boolean casListeners(AbstractFuture<?> future, @CheckForNull Listener expect, Listener update) {
            return UNSAFE.compareAndSwapObject(future, LISTENERS_OFFSET, expect, update);
        }

        @Override
        Listener gasListeners(AbstractFuture<?> future, Listener update) {
            return (Listener)UNSAFE.getAndSetObject(future, LISTENERS_OFFSET, update);
        }

        @Override
        Waiter gasWaiters(AbstractFuture<?> future, Waiter update) {
            return (Waiter)UNSAFE.getAndSetObject(future, WAITERS_OFFSET, update);
        }

        @Override
        boolean casValue(AbstractFuture<?> future, @CheckForNull Object expect, Object update) {
            return UNSAFE.compareAndSwapObject(future, VALUE_OFFSET, expect, update);
        }

        static {
            Unsafe unsafe = null;
            try {
                unsafe = Unsafe.getUnsafe();
            }
            catch (SecurityException tryReflectionInstead) {
                try {
                    unsafe = AccessController.doPrivileged(new PrivilegedExceptionAction<Unsafe>(){

                        @Override
                        public Unsafe run() throws Exception {
                            Class<Unsafe> k = Unsafe.class;
                            for (Field f : k.getDeclaredFields()) {
                                f.setAccessible(true);
                                Object x = f.get(null);
                                if (!k.isInstance(x)) continue;
                                return (Unsafe)k.cast(x);
                            }
                            throw new NoSuchFieldError("the Unsafe");
                        }
                    });
                }
                catch (PrivilegedActionException e) {
                    throw new RuntimeException("Could not initialize intrinsics", e.getCause());
                }
            }
            try {
                Class<AbstractFuture> abstractFuture = AbstractFuture.class;
                WAITERS_OFFSET = unsafe.objectFieldOffset(abstractFuture.getDeclaredField("waiters"));
                LISTENERS_OFFSET = unsafe.objectFieldOffset(abstractFuture.getDeclaredField("listeners"));
                VALUE_OFFSET = unsafe.objectFieldOffset(abstractFuture.getDeclaredField("value"));
                WAITER_THREAD_OFFSET = unsafe.objectFieldOffset(Waiter.class.getDeclaredField("thread"));
                WAITER_NEXT_OFFSET = unsafe.objectFieldOffset(Waiter.class.getDeclaredField("next"));
                UNSAFE = unsafe;
            }
            catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
            catch (RuntimeException e) {
                throw e;
            }
        }
    }

    private static abstract class AtomicHelper {
        private AtomicHelper() {
        }

        abstract void putThread(Waiter var1, Thread var2);

        abstract void putNext(Waiter var1, @CheckForNull Waiter var2);

        abstract boolean casWaiters(AbstractFuture<?> var1, @CheckForNull Waiter var2, @CheckForNull Waiter var3);

        abstract boolean casListeners(AbstractFuture<?> var1, @CheckForNull Listener var2, Listener var3);

        abstract Waiter gasWaiters(AbstractFuture<?> var1, Waiter var2);

        abstract Listener gasListeners(AbstractFuture<?> var1, Listener var2);

        abstract boolean casValue(AbstractFuture<?> var1, @CheckForNull Object var2, Object var3);
    }

    private static final class SetFuture<V>
    implements Runnable {
        final AbstractFuture<V> owner;
        final ListenableFuture<? extends V> future;

        SetFuture(AbstractFuture<V> owner, ListenableFuture<? extends V> future) {
            this.owner = owner;
            this.future = future;
        }

        @Override
        public void run() {
            if (((AbstractFuture)this.owner).value != this) {
                return;
            }
            Object valueToSet = AbstractFuture.getFutureValue(this.future);
            if (ATOMIC_HELPER.casValue(this.owner, this, valueToSet)) {
                AbstractFuture.complete((AbstractFuture)this.owner, false);
            }
        }
    }

    private static final class Cancellation {
        @CheckForNull
        static final Cancellation CAUSELESS_INTERRUPTED;
        @CheckForNull
        static final Cancellation CAUSELESS_CANCELLED;
        final boolean wasInterrupted;
        @CheckForNull
        final Throwable cause;

        Cancellation(boolean wasInterrupted, @CheckForNull Throwable cause) {
            this.wasInterrupted = wasInterrupted;
            this.cause = cause;
        }

        static {
            if (GENERATE_CANCELLATION_CAUSES) {
                CAUSELESS_CANCELLED = null;
                CAUSELESS_INTERRUPTED = null;
            } else {
                CAUSELESS_CANCELLED = new Cancellation(false, null);
                CAUSELESS_INTERRUPTED = new Cancellation(true, null);
            }
        }
    }

    private static final class Failure {
        static final Failure FALLBACK_INSTANCE = new Failure(new Throwable("Failure occurred while trying to finish a future."){

            @Override
            public synchronized Throwable fillInStackTrace() {
                return this;
            }
        });
        final Throwable exception;

        Failure(Throwable exception) {
            this.exception = Preconditions.checkNotNull(exception);
        }
    }

    private static final class Listener {
        static final Listener TOMBSTONE = new Listener();
        @CheckForNull
        final Runnable task;
        @CheckForNull
        final Executor executor;
        @CheckForNull
        Listener next;

        Listener(Runnable task, Executor executor) {
            this.task = task;
            this.executor = executor;
        }

        Listener() {
            this.task = null;
            this.executor = null;
        }
    }

    private static final class Waiter {
        static final Waiter TOMBSTONE = new Waiter(false);
        @CheckForNull
        volatile Thread thread;
        @CheckForNull
        volatile Waiter next;

        Waiter(boolean unused) {
        }

        Waiter() {
            ATOMIC_HELPER.putThread(this, Thread.currentThread());
        }

        void setNext(@CheckForNull Waiter next) {
            ATOMIC_HELPER.putNext(this, next);
        }

        void unpark() {
            Thread w = this.thread;
            if (w != null) {
                this.thread = null;
                LockSupport.unpark(w);
            }
        }
    }

    static abstract class TrustedFuture<V>
    extends AbstractFuture<V>
    implements Trusted<V> {
        TrustedFuture() {
        }

        @Override
        @ParametricNullness
        @CanIgnoreReturnValue
        public final V get() throws InterruptedException, ExecutionException {
            return super.get();
        }

        @Override
        @ParametricNullness
        @CanIgnoreReturnValue
        public final V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            return super.get(timeout, unit);
        }

        @Override
        public final boolean isDone() {
            return super.isDone();
        }

        @Override
        public final boolean isCancelled() {
            return super.isCancelled();
        }

        @Override
        public final void addListener(Runnable listener, Executor executor) {
            super.addListener(listener, executor);
        }

        @Override
        @CanIgnoreReturnValue
        public final boolean cancel(boolean mayInterruptIfRunning) {
            return super.cancel(mayInterruptIfRunning);
        }
    }

    static interface Trusted<V>
    extends ListenableFuture<V> {
    }
}

