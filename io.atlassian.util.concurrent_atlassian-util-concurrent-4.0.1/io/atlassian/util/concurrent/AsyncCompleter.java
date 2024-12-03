/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  net.jcip.annotations.ThreadSafe
 */
package io.atlassian.util.concurrent;

import io.atlassian.util.concurrent.ExceptionPolicy;
import io.atlassian.util.concurrent.Executors;
import io.atlassian.util.concurrent.Functions;
import io.atlassian.util.concurrent.Lazy;
import io.atlassian.util.concurrent.NotNull;
import io.atlassian.util.concurrent.RuntimeExecutionException;
import io.atlassian.util.concurrent.RuntimeInterruptedException;
import io.atlassian.util.concurrent.Timeout;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Spliterator;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nonnull;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public final class AsyncCompleter {
    private final Executor executor;
    private final ExceptionPolicy policy;
    private final ExecutorCompletionServiceFactory completionServiceFactory;
    private final CompletionServiceDecorator completionServiceDecorator;

    AsyncCompleter(Executor executor, ExceptionPolicy policy, ExecutorCompletionServiceFactory completionServiceFactory, CompletionServiceDecorator completionServiceDecorator) {
        this.executor = Objects.requireNonNull(executor, "executor");
        this.policy = Objects.requireNonNull(policy, "policy");
        this.completionServiceFactory = Objects.requireNonNull(completionServiceFactory, "completionServiceFactory");
        this.completionServiceDecorator = completionServiceDecorator;
    }

    public <T> Iterable<T> invokeAll(Iterable<? extends Callable<T>> callables) {
        return this.invokeAllTasks(callables, new BlockingAccessor());
    }

    public <T> Iterable<T> invokeAll(Iterable<? extends Callable<T>> callables, long time, TimeUnit unit) {
        return this.invokeAllTasks(callables, new TimeoutAccessor(Timeout.getNanosTimeout(time, unit)));
    }

    <T> Iterable<T> invokeAllTasks(Iterable<? extends Callable<T>> callables, Accessor<T> accessor) {
        CompletionService apply = this.completionServiceDecorator.apply(this.completionServiceFactory.create().apply(this.executor));
        final List lazyAsyncSuppliers = StreamSupport.stream(callables.spliterator(), false).map(new AsyncCompletionFunction(apply, accessor)).collect(Collectors.toList());
        return new Iterable<T>(){

            private Stream<T> newStream() {
                return lazyAsyncSuppliers.stream().map(AsyncCompleter.this.policy.handler()).map(Functions.fromSupplier()).filter(x -> x != null);
            }

            @Override
            public Iterator<T> iterator() {
                return this.newStream().iterator();
            }

            @Override
            public Spliterator<T> spliterator() {
                return this.newStream().spliterator();
            }
        };
    }

    static class IdentityCheckedCompletionService<T>
    implements CompletionService<T> {
        private final CompletionService<T> delegate;
        private final Collection<Future<T>> futures = new ConcurrentLinkedQueue<Future<T>>();

        IdentityCheckedCompletionService(CompletionService<T> delegate) {
            this.delegate = delegate;
        }

        Future<T> add(Future<T> f) {
            this.futures.add(f);
            return f;
        }

        Future<T> check(Future<T> f) {
            if (!this.futures.remove(f)) {
                throw new IllegalArgumentException("Expected the future to be in the list of registered futures");
            }
            return f;
        }

        @Override
        @Nonnull
        public Future<T> submit(@Nonnull Callable<T> task) {
            return this.add(this.delegate.submit(task));
        }

        @Override
        @Nonnull
        public Future<T> submit(@Nonnull Runnable task, T result) {
            return this.add(this.delegate.submit(task, result));
        }

        @Override
        public Future<T> take() throws InterruptedException {
            return this.check(this.delegate.take());
        }

        @Override
        public Future<T> poll() {
            return this.check(this.delegate.poll());
        }

        @Override
        public Future<T> poll(long timeout, @Nonnull TimeUnit unit) throws InterruptedException {
            return this.check(this.delegate.poll(timeout, unit));
        }
    }

    static interface CompletionServiceDecorator {
        public <T> CompletionService<T> apply(CompletionService<T> var1);

        public static class IdentityChecker
        implements CompletionServiceDecorator {
            @Override
            public <T> CompletionService<T> apply(CompletionService<T> delegate) {
                return new IdentityCheckedCompletionService<T>(delegate);
            }
        }

        public static enum Identity implements CompletionServiceDecorator
        {
            INSTANCE;


            @Override
            public <T> CompletionService<T> apply(CompletionService<T> acc) {
                return acc;
            }
        }
    }

    static final class ExecutorCompletionServiceFunction<T>
    implements Function<Executor, CompletionService<T>> {
        ExecutorCompletionServiceFunction() {
        }

        @Override
        public CompletionService<T> apply(Executor executor) {
            return new ExecutorCompletionService(executor);
        }
    }

    static final class DefaultExecutorCompletionServiceFactory
    implements ExecutorCompletionServiceFactory {
        DefaultExecutorCompletionServiceFactory() {
        }

        @Override
        public <T> Function<Executor, CompletionService<T>> create() {
            return new ExecutorCompletionServiceFunction();
        }
    }

    static final class BlockingAccessor<T>
    implements Accessor<T> {
        BlockingAccessor() {
        }

        @Override
        public T apply(CompletionService<T> completionService) {
            try {
                return completionService.take().get();
            }
            catch (InterruptedException e) {
                throw new RuntimeInterruptedException(e);
            }
            catch (ExecutionException e) {
                throw new RuntimeExecutionException(e);
            }
        }

        @Override
        public void register(Future<T> f) {
        }
    }

    static final class TimeoutAccessor<T>
    implements Accessor<T> {
        private final Timeout timeout;
        private final Collection<Future<T>> futures = new ConcurrentLinkedQueue<Future<T>>();

        TimeoutAccessor(Timeout timeout) {
            this.timeout = timeout;
        }

        @Override
        public T apply(CompletionService<T> completionService) {
            try {
                Future<T> future = completionService.poll(this.timeout.getTime(), this.timeout.getUnit());
                if (future == null) {
                    this.cancelRemaining();
                    throw this.timeout.getTimeoutException();
                }
                this.futures.remove(future);
                return future.get();
            }
            catch (InterruptedException e) {
                throw new RuntimeInterruptedException(e);
            }
            catch (ExecutionException e) {
                throw new RuntimeExecutionException(e);
            }
        }

        @Override
        public void register(Future<T> f) {
            this.futures.add(f);
        }

        private void cancelRemaining() {
            for (Future<T> f : this.futures) {
                f.cancel(true);
            }
            this.futures.clear();
        }
    }

    static interface Accessor<T>
    extends Function<CompletionService<T>, T> {
        public void register(Future<T> var1);
    }

    private static class AsyncCompletionFunction<T>
    implements Function<Callable<T>, Supplier<T>> {
        private final CompletionService<T> completionService;
        private final Accessor<T> accessor;
        private final Supplier<T> nextCompleteItem = new Supplier<T>(){

            @Override
            public T get() {
                return accessor.apply(completionService);
            }
        };

        AsyncCompletionFunction(CompletionService<T> completionService, Accessor<T> accessor) {
            this.completionService = completionService;
            this.accessor = accessor;
        }

        @Override
        public Supplier<T> apply(Callable<T> task) {
            this.accessor.register(this.completionService.submit(task));
            return Lazy.supplier(this.nextCompleteItem::get);
        }
    }

    public static interface ExecutorCompletionServiceFactory {
        public <T> Function<Executor, CompletionService<T>> create();
    }

    public static class Builder {
        Executor executor;
        ExceptionPolicy policy = ExceptionPolicy.Policies.THROW;
        ExecutorCompletionServiceFactory completionServiceFactory = new DefaultExecutorCompletionServiceFactory();
        CompletionServiceDecorator completionServiceDecorator = CompletionServiceDecorator.Identity.INSTANCE;

        public Builder(@NotNull Executor executor) {
            this.executor = Objects.requireNonNull(executor, "executor");
        }

        public Builder ignoreExceptions() {
            return this.handleExceptions(ExceptionPolicy.Policies.IGNORE_EXCEPTIONS);
        }

        public Builder handleExceptions(ExceptionPolicy policy) {
            this.policy = policy;
            return this;
        }

        public Builder completionServiceFactory(ExecutorCompletionServiceFactory completionServiceFactory) {
            this.completionServiceFactory = Objects.requireNonNull(completionServiceFactory, "completionServiceFactory");
            return this;
        }

        public Builder checkCompletionServiceFutureIdentity() {
            this.completionServiceDecorator = new CompletionServiceDecorator.IdentityChecker();
            return this;
        }

        public AsyncCompleter limitParallelExecutionTo(int limit) {
            return new AsyncCompleter(Executors.limited(this.executor, limit), this.policy, this.completionServiceFactory, this.completionServiceDecorator);
        }

        public AsyncCompleter build() {
            return new AsyncCompleter(this.executor, this.policy, this.completionServiceFactory, this.completionServiceDecorator);
        }
    }
}

