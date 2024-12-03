/*
 * Decompiled with CFR 0.152.
 */
package brave.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public abstract class WrappingExecutorService
implements ExecutorService {
    protected WrappingExecutorService() {
    }

    protected abstract ExecutorService delegate();

    protected abstract <R> Callable<R> wrap(Callable<R> var1);

    protected abstract Runnable wrap(Runnable var1);

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return this.delegate().awaitTermination(timeout, unit);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return this.delegate().invokeAll(this.wrap(tasks));
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        return this.delegate().invokeAll(this.wrap(tasks), timeout, unit);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        return this.delegate().invokeAny(this.wrap(tasks));
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return this.delegate().invokeAny(this.wrap(tasks), timeout, unit);
    }

    @Override
    public boolean isShutdown() {
        return this.delegate().isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return this.delegate().isTerminated();
    }

    @Override
    public void shutdown() {
        this.delegate().shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        return this.delegate().shutdownNow();
    }

    @Override
    public void execute(Runnable task) {
        this.delegate().execute(this.wrap(task));
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return this.delegate().submit(this.wrap(task));
    }

    @Override
    public Future<?> submit(Runnable task) {
        return this.delegate().submit(this.wrap(task));
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        return this.delegate().submit(this.wrap(task), result);
    }

    <T> Collection<? extends Callable<T>> wrap(Collection<? extends Callable<T>> tasks) {
        ArrayList<Callable<T>> result = new ArrayList<Callable<T>>(tasks.size());
        for (Callable<T> task : tasks) {
            result.add(this.wrap(task));
        }
        return result;
    }
}

