/*
 * Decompiled with CFR 0.152.
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

    @Override
    public boolean isShuttingDown() {
        return this.delegate.isShuttingDown();
    }

    @Override
    public io.netty.util.concurrent.Future<?> shutdownGracefully() {
        return this.shutdownGracefully(2L, 15L, TimeUnit.SECONDS);
    }

    @Override
    public io.netty.util.concurrent.Future<?> shutdownGracefully(long quietPeriod, long timeout, TimeUnit unit) {
        return this.delegate.shutdownGracefully(quietPeriod, timeout, unit);
    }

    @Override
    public io.netty.util.concurrent.Future<?> terminationFuture() {
        return this.delegate.terminationFuture();
    }

    @Override
    public void shutdown() {
        this.delegate.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        return this.delegate.shutdownNow();
    }

    @Override
    public boolean isShutdown() {
        return this.delegate.isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return this.delegate.isTerminated();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return this.delegate.awaitTermination(timeout, unit);
    }

    @Override
    public EventLoop next() {
        return this.delegate.next();
    }

    @Override
    public Iterator<EventExecutor> iterator() {
        return this.delegate.iterator();
    }

    @Override
    public io.netty.util.concurrent.Future<?> submit(Runnable task) {
        return this.delegate.submit(task);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return this.delegate.invokeAll(tasks);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        return this.delegate.invokeAll(tasks, timeout, unit);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        return this.delegate.invokeAny(tasks);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return this.delegate.invokeAny(tasks, timeout, unit);
    }

    @Override
    public <T> io.netty.util.concurrent.Future<T> submit(Runnable task, T result) {
        return this.delegate.submit(task, result);
    }

    @Override
    public <T> io.netty.util.concurrent.Future<T> submit(Callable<T> task) {
        return this.delegate.submit(task);
    }

    @Override
    public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
        return this.delegate.schedule(command, delay, unit);
    }

    @Override
    public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
        return this.delegate.schedule(callable, delay, unit);
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
        return this.delegate.scheduleAtFixedRate(command, initialDelay, period, unit);
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
        return this.delegate.scheduleWithFixedDelay(command, initialDelay, delay, unit);
    }

    @Override
    public ChannelFuture register(Channel channel) {
        return this.delegate.register(channel);
    }

    @Override
    public ChannelFuture register(ChannelPromise promise) {
        return this.delegate.register(promise);
    }

    @Override
    public ChannelFuture register(Channel channel, ChannelPromise promise) {
        return this.delegate.register(channel, promise);
    }

    @Override
    public void execute(Runnable command) {
        this.delegate.execute(command);
    }
}

