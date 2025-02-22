/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.internal.util.concurrent;

import com.hazelcast.internal.util.concurrent.ConcurrentConveyorException;
import com.hazelcast.internal.util.concurrent.QueuedPipe;
import com.hazelcast.util.concurrent.BackoffIdleStrategy;
import com.hazelcast.util.concurrent.IdleStrategy;
import com.hazelcast.util.function.Predicate;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

public class ConcurrentConveyor<E> {
    public static final int SUBMIT_SPIN_COUNT = 1000;
    public static final int SUBMIT_YIELD_COUNT = 200;
    public static final long SUBMIT_MAX_PARK_MICROS = 200L;
    public static final IdleStrategy SUBMIT_IDLER = new BackoffIdleStrategy(1000L, 200L, 1L, TimeUnit.MICROSECONDS.toNanos(200L));
    private static final Throwable REGULAR_DEPARTURE = ConcurrentConveyor.regularDeparture();
    private final QueuedPipe<E>[] queues;
    private final E submitterGoneItem;
    private volatile boolean backpressure;
    private volatile Thread drainer;
    private volatile Throwable drainerDepartureCause;
    private volatile int liveQueueCount;

    ConcurrentConveyor(E submitterGoneItem, QueuedPipe<E> ... queues) {
        if (queues.length == 0) {
            throw new IllegalArgumentException("No concurrent queues supplied");
        }
        this.submitterGoneItem = submitterGoneItem;
        this.queues = this.validateAndCopy(queues);
        this.liveQueueCount = queues.length;
    }

    private QueuedPipe<E>[] validateAndCopy(QueuedPipe<E>[] queues) {
        QueuedPipe[] safeCopy = new QueuedPipe[queues.length];
        for (int i = 0; i < queues.length; ++i) {
            if (queues[i] == null) {
                throw new IllegalArgumentException("Queue at index " + i + " is null");
            }
            safeCopy[i] = queues[i];
        }
        return safeCopy;
    }

    public static <E1> ConcurrentConveyor<E1> concurrentConveyor(E1 submitterGoneItem, QueuedPipe<E1> ... queues) {
        return new ConcurrentConveyor<E1>(submitterGoneItem, queues);
    }

    public final E submitterGoneItem() {
        return this.submitterGoneItem;
    }

    public final int queueCount() {
        return this.queues.length;
    }

    public final int liveQueueCount() {
        return this.liveQueueCount;
    }

    public final QueuedPipe<E> queue(int index) {
        return this.queues[index];
    }

    @SuppressFBWarnings(value={"VO_VOLATILE_INCREMENT"}, justification="liveQueueCount is updated only by the drainer thread")
    public final boolean removeQueue(int index) {
        boolean didRemove = this.queues[index] != null;
        this.queues[index] = null;
        --this.liveQueueCount;
        return didRemove;
    }

    public final boolean offer(int queueIndex, E item) {
        return this.offer(this.queues[queueIndex], item);
    }

    public final boolean offer(Queue<E> queue, E item) throws ConcurrentConveyorException {
        if (queue.offer(item)) {
            return true;
        }
        this.checkDrainerGone();
        this.unparkDrainer();
        return false;
    }

    public final void submit(Queue<E> queue, E item) throws ConcurrentConveyorException {
        long idleCount = 0L;
        while (!queue.offer(item)) {
            SUBMIT_IDLER.idle(idleCount);
            this.checkDrainerGone();
            this.unparkDrainer();
            ConcurrentConveyor.checkInterrupted();
            ++idleCount;
        }
        idleCount = 0L;
        while (this.backpressure) {
            SUBMIT_IDLER.idle(idleCount);
            ConcurrentConveyor.checkInterrupted();
            ++idleCount;
        }
    }

    public final int drainTo(Collection<? super E> drain) {
        return this.drain(this.queues[0], drain, Integer.MAX_VALUE);
    }

    public final int drainTo(int queueIndex, Collection<? super E> drain) {
        return this.drain(this.queues[queueIndex], drain, Integer.MAX_VALUE);
    }

    public final int drain(int queueIndex, Predicate<? super E> itemHandler) {
        return this.queues[queueIndex].drain(itemHandler);
    }

    public final int drainTo(Collection<? super E> drain, int limit) {
        return this.drain(this.queues[0], drain, limit);
    }

    public final int drainTo(int queueIndex, Collection<? super E> drain, int limit) {
        return this.drain(this.queues[queueIndex], drain, limit);
    }

    public final void drainerArrived() {
        this.drainerDepartureCause = null;
        this.drainer = Thread.currentThread();
    }

    public final void drainerFailed(Throwable t) {
        if (t == null) {
            throw new NullPointerException("ConcurrentConveyor.drainerFailed(null)");
        }
        this.drainer = null;
        this.drainerDepartureCause = t;
    }

    public final void drainerDone() {
        this.drainer = null;
        this.drainerDepartureCause = REGULAR_DEPARTURE;
    }

    public final boolean isDrainerGone() {
        return this.drainerDepartureCause != null;
    }

    public final void checkDrainerGone() {
        Throwable cause = this.drainerDepartureCause;
        if (cause == REGULAR_DEPARTURE) {
            throw new ConcurrentConveyorException("Queue drainer has already left");
        }
        ConcurrentConveyor.propagateDrainerFailure(cause);
    }

    public final void awaitDrainerGone() {
        long i = 0L;
        while (!this.isDrainerGone()) {
            SUBMIT_IDLER.idle(i);
            ++i;
        }
        ConcurrentConveyor.propagateDrainerFailure(this.drainerDepartureCause);
    }

    public final void backpressureOn() {
        this.backpressure = true;
    }

    public final void backpressureOff() {
        this.backpressure = false;
    }

    private int drain(QueuedPipe<E> q, Collection<? super E> drain, int limit) {
        return q.drainTo(drain, limit);
    }

    private void unparkDrainer() {
        Thread drainer = this.drainer;
        if (drainer != null) {
            LockSupport.unpark(drainer);
        }
    }

    private static void propagateDrainerFailure(Throwable cause) {
        if (cause != null && cause != REGULAR_DEPARTURE) {
            throw new ConcurrentConveyorException("Queue drainer failed", cause);
        }
    }

    private static void checkInterrupted() throws ConcurrentConveyorException {
        if (Thread.currentThread().isInterrupted()) {
            throw new ConcurrentConveyorException("Thread interrupted");
        }
    }

    private static ConcurrentConveyorException regularDeparture() {
        ConcurrentConveyorException e = new ConcurrentConveyorException("Regular departure");
        e.setStackTrace(new StackTraceElement[0]);
        return e;
    }
}

