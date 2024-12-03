/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.channel.Channel
 *  io.netty.channel.ChannelFuture
 *  io.netty.channel.ChannelPromise
 *  io.netty.channel.EventLoop
 *  io.netty.channel.EventLoopGroup
 *  io.netty.util.concurrent.EventExecutor
 *  io.netty.util.concurrent.Future
 *  io.netty.util.concurrent.ScheduledFuture
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.http.nio.netty.internal;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.ScheduledFuture;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import software.amazon.awssdk.annotations.SdkInternalApi;

@SdkInternalApi
public abstract class DelegatingEventLoopGroup
implements EventLoopGroup {
    private final EventLoopGroup delegate;

    protected DelegatingEventLoopGroup(EventLoopGroup delegate) {
        this.delegate = delegate;
    }

    public EventLoopGroup getDelegate() {
        return this.delegate;
    }

    public boolean isShuttingDown() {
        return this.delegate.isShuttingDown();
    }

    public io.netty.util.concurrent.Future<?> shutdownGracefully() {
        return this.shutdownGracefully(2L, 15L, TimeUnit.SECONDS);
    }

    public io.netty.util.concurrent.Future<?> shutdownGracefully(long quietPeriod, long timeout, TimeUnit unit) {
        return this.delegate.shutdownGracefully(quietPeriod, timeout, unit);
    }

    public io.netty.util.concurrent.Future<?> terminationFuture() {
        return this.delegate.terminationFuture();
    }

    public void shutdown() {
        this.delegate.shutdown();
    }

    public List<Runnable> shutdownNow() {
        return this.delegate.shutdownNow();
    }

    public boolean isShutdown() {
        return this.delegate.isShutdown();
    }

    public boolean isTerminated() {
        return this.delegate.isTerminated();
    }

    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return this.delegate.awaitTermination(timeout, unit);
    }

    public EventLoop next() {
        return this.delegate.next();
    }

    public Iterator<EventExecutor> iterator() {
        return this.delegate.iterator();
    }

    public io.netty.util.concurrent.Future<?> submit(Runnable task) {
        return this.delegate.submit(task);
    }

    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return this.delegate.invokeAll(tasks);
    }

    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        return this.delegate.invokeAll(tasks, timeout, unit);
    }

    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        return (T)this.delegate.invokeAny(tasks);
    }

    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return (T)this.delegate.invokeAny(tasks, timeout, unit);
    }

    public <T> io.netty.util.concurrent.Future<T> submit(Runnable task, T result) {
        return this.delegate.submit(task, result);
    }

    public <T> io.netty.util.concurrent.Future<T> submit(Callable<T> task) {
        return this.delegate.submit(task);
    }

    public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
        return this.delegate.schedule(command, delay, unit);
    }

    public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
        return this.delegate.schedule(callable, delay, unit);
    }

    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
        return this.delegate.scheduleAtFixedRate(command, initialDelay, period, unit);
    }

    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
        return this.delegate.scheduleWithFixedDelay(command, initialDelay, delay, unit);
    }

    public ChannelFuture register(Channel channel) {
        return this.delegate.register(channel);
    }

    public ChannelFuture register(ChannelPromise promise) {
        return this.delegate.register(promise);
    }

    public ChannelFuture register(Channel channel, ChannelPromise promise) {
        return this.delegate.register(channel, promise);
    }

    public void execute(Runnable command) {
        this.delegate.execute(command);
    }
}

