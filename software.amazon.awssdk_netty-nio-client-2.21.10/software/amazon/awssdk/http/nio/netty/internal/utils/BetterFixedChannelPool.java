/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.channel.Channel
 *  io.netty.util.concurrent.DefaultPromise
 *  io.netty.util.concurrent.EventExecutor
 *  io.netty.util.concurrent.Future
 *  io.netty.util.concurrent.FutureListener
 *  io.netty.util.concurrent.GenericFutureListener
 *  io.netty.util.concurrent.GlobalEventExecutor
 *  io.netty.util.concurrent.Promise
 *  io.netty.util.internal.ObjectUtil
 *  io.netty.util.internal.ThrowableUtil
 *  software.amazon.awssdk.http.HttpMetric
 *  software.amazon.awssdk.metrics.MetricCollector
 */
package software.amazon.awssdk.http.nio.netty.internal.utils;

import io.netty.channel.Channel;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.util.concurrent.Promise;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.ThrowableUtil;
import java.nio.channels.ClosedChannelException;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import software.amazon.awssdk.http.HttpMetric;
import software.amazon.awssdk.http.nio.netty.internal.SdkChannelPool;
import software.amazon.awssdk.http.nio.netty.internal.utils.NettyUtils;
import software.amazon.awssdk.metrics.MetricCollector;

public class BetterFixedChannelPool
implements SdkChannelPool {
    private static final IllegalStateException FULL_EXCEPTION = (IllegalStateException)ThrowableUtil.unknownStackTrace((Throwable)new IllegalStateException("Too many outstanding acquire operations"), BetterFixedChannelPool.class, (String)"acquire0(...)");
    private static final TimeoutException TIMEOUT_EXCEPTION = (TimeoutException)ThrowableUtil.unknownStackTrace((Throwable)new TimeoutException("Acquire operation took longer than configured maximum time"), BetterFixedChannelPool.class, (String)"<init>(...)");
    static final IllegalStateException POOL_CLOSED_ON_RELEASE_EXCEPTION = (IllegalStateException)ThrowableUtil.unknownStackTrace((Throwable)new IllegalStateException("BetterFixedChannelPooled was closed"), BetterFixedChannelPool.class, (String)"release(...)");
    static final IllegalStateException POOL_CLOSED_ON_ACQUIRE_EXCEPTION = (IllegalStateException)ThrowableUtil.unknownStackTrace((Throwable)new IllegalStateException("BetterFixedChannelPooled was closed"), BetterFixedChannelPool.class, (String)"acquire0(...)");
    private final EventExecutor executor;
    private final long acquireTimeoutNanos;
    private final Runnable timeoutTask;
    private final SdkChannelPool delegateChannelPool;
    private final Queue<AcquireTask> pendingAcquireQueue = new ArrayDeque<AcquireTask>();
    private final int maxConnections;
    private final int maxPendingAcquires;
    private int acquiredChannelCount;
    private int pendingAcquireCount;
    private boolean closed;

    private BetterFixedChannelPool(Builder builder) {
        if (builder.maxConnections < 1) {
            throw new IllegalArgumentException("maxConnections: " + builder.maxConnections + " (expected: >= 1)");
        }
        if (builder.maxPendingAcquires < 1) {
            throw new IllegalArgumentException("maxPendingAcquires: " + builder.maxPendingAcquires + " (expected: >= 1)");
        }
        this.delegateChannelPool = builder.channelPool;
        this.executor = builder.executor;
        if (builder.action == null && builder.acquireTimeoutMillis == -1L) {
            this.timeoutTask = null;
            this.acquireTimeoutNanos = -1L;
        } else {
            if (builder.action == null && builder.acquireTimeoutMillis != -1L) {
                throw new NullPointerException("action");
            }
            if (builder.action != null && builder.acquireTimeoutMillis < 0L) {
                throw new IllegalArgumentException("acquireTimeoutMillis: " + builder.acquireTimeoutMillis + " (expected: >= 0)");
            }
            this.acquireTimeoutNanos = TimeUnit.MILLISECONDS.toNanos(builder.acquireTimeoutMillis);
            switch (builder.action) {
                case FAIL: {
                    this.timeoutTask = new TimeoutTask(){

                        @Override
                        public void onTimeout(AcquireTask task) {
                            task.promise.setFailure((Throwable)TIMEOUT_EXCEPTION);
                        }
                    };
                    break;
                }
                case NEW: {
                    this.timeoutTask = new TimeoutTask(){

                        @Override
                        public void onTimeout(AcquireTask task) {
                            task.acquired();
                            BetterFixedChannelPool.this.delegateChannelPool.acquire(task.promise);
                        }
                    };
                    break;
                }
                default: {
                    throw new Error();
                }
            }
        }
        this.maxConnections = builder.maxConnections;
        this.maxPendingAcquires = builder.maxPendingAcquires;
    }

    public Future<Channel> acquire() {
        return this.acquire((Promise<Channel>)new DefaultPromise(this.executor));
    }

    public Future<Channel> acquire(Promise<Channel> promise) {
        try {
            if (this.executor.inEventLoop()) {
                this.acquire0(promise);
            } else {
                this.executor.execute(() -> this.acquire0(promise));
            }
        }
        catch (Throwable cause) {
            promise.setFailure(cause);
        }
        return promise;
    }

    @Override
    public CompletableFuture<Void> collectChannelPoolMetrics(MetricCollector metrics) {
        CompletableFuture<Void> delegateMetricResult = this.delegateChannelPool.collectChannelPoolMetrics(metrics);
        CompletableFuture result = new CompletableFuture();
        NettyUtils.doInEventLoop(this.executor, () -> {
            try {
                metrics.reportMetric(HttpMetric.MAX_CONCURRENCY, (Object)this.maxConnections);
                metrics.reportMetric(HttpMetric.PENDING_CONCURRENCY_ACQUIRES, (Object)this.pendingAcquireCount);
                metrics.reportMetric(HttpMetric.LEASED_CONCURRENCY, (Object)this.acquiredChannelCount);
                result.complete(null);
            }
            catch (Throwable t) {
                result.completeExceptionally(t);
            }
        });
        return CompletableFuture.allOf(result, delegateMetricResult);
    }

    private void acquire0(Promise<Channel> promise) {
        assert (this.executor.inEventLoop());
        if (this.closed) {
            promise.setFailure((Throwable)POOL_CLOSED_ON_ACQUIRE_EXCEPTION);
            return;
        }
        if (this.acquiredChannelCount < this.maxConnections) {
            assert (this.acquiredChannelCount >= 0);
            Promise p = this.executor.newPromise();
            AcquireListener l = new AcquireListener(promise);
            l.acquired();
            p.addListener((GenericFutureListener)l);
            this.delegateChannelPool.acquire(p);
        } else {
            if (this.pendingAcquireCount >= this.maxPendingAcquires) {
                promise.setFailure((Throwable)FULL_EXCEPTION);
            } else {
                AcquireTask task = new AcquireTask(promise);
                if (this.pendingAcquireQueue.offer(task)) {
                    ++this.pendingAcquireCount;
                    if (this.timeoutTask != null) {
                        task.timeoutFuture = this.executor.schedule(this.timeoutTask, this.acquireTimeoutNanos, TimeUnit.NANOSECONDS);
                    }
                } else {
                    promise.setFailure((Throwable)FULL_EXCEPTION);
                }
            }
            assert (this.pendingAcquireCount > 0);
        }
    }

    public Future<Void> release(Channel channel) {
        return this.release(channel, (Promise<Void>)new DefaultPromise(this.executor));
    }

    public Future<Void> release(final Channel channel, final Promise<Void> promise) {
        ObjectUtil.checkNotNull(promise, (String)"promise");
        Promise p = this.executor.newPromise();
        this.delegateChannelPool.release(channel, p.addListener((GenericFutureListener)new FutureListener<Void>(){

            public void operationComplete(Future<Void> future) throws Exception {
                assert (BetterFixedChannelPool.this.executor.inEventLoop());
                if (BetterFixedChannelPool.this.closed) {
                    channel.close();
                    promise.setFailure((Throwable)POOL_CLOSED_ON_RELEASE_EXCEPTION);
                    return;
                }
                if (future.isSuccess()) {
                    BetterFixedChannelPool.this.decrementAndRunTaskQueue();
                    promise.setSuccess(null);
                } else {
                    Throwable cause = future.cause();
                    if (!(cause instanceof IllegalArgumentException)) {
                        BetterFixedChannelPool.this.decrementAndRunTaskQueue();
                    }
                    promise.setFailure(future.cause());
                }
            }
        }));
        return promise;
    }

    private void decrementAndRunTaskQueue() {
        --this.acquiredChannelCount;
        assert (this.acquiredChannelCount >= 0);
        this.runTaskQueue();
    }

    private void runTaskQueue() {
        AcquireTask task;
        while (this.acquiredChannelCount < this.maxConnections && (task = this.pendingAcquireQueue.poll()) != null) {
            ScheduledFuture<?> timeoutFuture = task.timeoutFuture;
            if (timeoutFuture != null) {
                timeoutFuture.cancel(false);
            }
            --this.pendingAcquireCount;
            task.acquired();
            this.delegateChannelPool.acquire(task.promise);
        }
        assert (this.pendingAcquireCount >= 0);
        assert (this.acquiredChannelCount >= 0);
    }

    public void close() {
        if (this.executor.inEventLoop()) {
            this.close0();
        } else {
            this.executor.submit(() -> this.close0()).awaitUninterruptibly();
        }
    }

    private void close0() {
        if (!this.closed) {
            AcquireTask task;
            this.closed = true;
            while ((task = this.pendingAcquireQueue.poll()) != null) {
                ScheduledFuture<?> f = task.timeoutFuture;
                if (f != null) {
                    f.cancel(false);
                }
                task.promise.setFailure((Throwable)new ClosedChannelException());
            }
            this.acquiredChannelCount = 0;
            this.pendingAcquireCount = 0;
            GlobalEventExecutor.INSTANCE.execute(() -> this.delegateChannelPool.close());
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private SdkChannelPool channelPool;
        private EventExecutor executor;
        private AcquireTimeoutAction action;
        private long acquireTimeoutMillis;
        private int maxConnections;
        private int maxPendingAcquires;

        private Builder() {
        }

        public Builder channelPool(SdkChannelPool channelPool) {
            this.channelPool = channelPool;
            return this;
        }

        public Builder executor(EventExecutor executor) {
            this.executor = executor;
            return this;
        }

        public Builder acquireTimeoutAction(AcquireTimeoutAction action) {
            this.action = action;
            return this;
        }

        public Builder acquireTimeoutMillis(long acquireTimeoutMillis) {
            this.acquireTimeoutMillis = acquireTimeoutMillis;
            return this;
        }

        public Builder maxConnections(int maxConnections) {
            this.maxConnections = maxConnections;
            return this;
        }

        public Builder maxPendingAcquires(int maxPendingAcquires) {
            this.maxPendingAcquires = maxPendingAcquires;
            return this;
        }

        public BetterFixedChannelPool build() {
            return new BetterFixedChannelPool(this);
        }
    }

    private class AcquireListener
    implements FutureListener<Channel> {
        private final Promise<Channel> originalPromise;
        protected boolean acquired;

        AcquireListener(Promise<Channel> originalPromise) {
            this.originalPromise = originalPromise;
        }

        public void operationComplete(Future<Channel> future) throws Exception {
            assert (BetterFixedChannelPool.this.executor.inEventLoop());
            if (BetterFixedChannelPool.this.closed) {
                if (future.isSuccess()) {
                    ((Channel)future.getNow()).close();
                }
                this.originalPromise.setFailure((Throwable)POOL_CLOSED_ON_ACQUIRE_EXCEPTION);
                return;
            }
            if (future.isSuccess()) {
                this.originalPromise.setSuccess(future.getNow());
            } else {
                if (this.acquired) {
                    BetterFixedChannelPool.this.decrementAndRunTaskQueue();
                } else {
                    BetterFixedChannelPool.this.runTaskQueue();
                }
                this.originalPromise.setFailure(future.cause());
            }
        }

        public void acquired() {
            if (this.acquired) {
                return;
            }
            BetterFixedChannelPool.this.acquiredChannelCount++;
            this.acquired = true;
        }
    }

    private abstract class TimeoutTask
    implements Runnable {
        private TimeoutTask() {
        }

        @Override
        public final void run() {
            AcquireTask task;
            assert (BetterFixedChannelPool.this.executor.inEventLoop());
            long nanoTime = System.nanoTime();
            while ((task = (AcquireTask)BetterFixedChannelPool.this.pendingAcquireQueue.peek()) != null && nanoTime - task.expireNanoTime >= 0L) {
                BetterFixedChannelPool.this.pendingAcquireQueue.remove();
                --BetterFixedChannelPool.this.pendingAcquireCount;
                this.onTimeout(task);
            }
        }

        public abstract void onTimeout(AcquireTask var1);
    }

    private final class AcquireTask
    extends AcquireListener {
        final Promise<Channel> promise;
        final long expireNanoTime;
        ScheduledFuture<?> timeoutFuture;

        public AcquireTask(Promise<Channel> promise) {
            super(promise);
            this.expireNanoTime = System.nanoTime() + BetterFixedChannelPool.this.acquireTimeoutNanos;
            this.promise = BetterFixedChannelPool.this.executor.newPromise().addListener((GenericFutureListener)this);
        }
    }

    public static enum AcquireTimeoutAction {
        NEW,
        FAIL;

    }
}

