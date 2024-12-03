/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package io.atlassian.util.concurrent;

import io.atlassian.util.concurrent.ExecutorSubmitter;
import io.atlassian.util.concurrent.LimitedExecutor;
import io.atlassian.util.concurrent.Promise;
import io.atlassian.util.concurrent.Promises;
import io.atlassian.util.concurrent.Suppliers;
import io.atlassian.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.function.Supplier;
import javax.annotation.Nonnull;

public final class Executors {
    public static Executor limited(Executor delegate, int limit) {
        return new LimitedExecutor(delegate, limit);
    }

    public static ExecutorSubmitter submitter(Executor delegate) {
        return new DefaultSubmitter(delegate);
    }

    private Executors() {
        throw new AssertionError((Object)"cannot instantiate!");
    }

    static class DefaultSubmitter
    implements ExecutorSubmitter {
        private final Executor executor;

        DefaultSubmitter(Executor executor) {
            this.executor = executor;
        }

        @Override
        public void execute(@Nonnull Runnable command) {
            this.executor.execute(command);
        }

        @Override
        public <T> Promise<T> submit(Callable<T> callable) {
            CallableRunner<T> runner = new CallableRunner<T>(callable);
            this.executor.execute(runner);
            return runner.get();
        }

        @Override
        public <T> Promise<T> submitSupplier(Supplier<T> supplier) {
            return this.submit(Suppliers.toCallable(supplier));
        }

        static class CallableRunner<T>
        implements Runnable,
        Supplier<Promise<T>> {
            final Callable<T> task;
            final Promises.SettablePromise<T> promise = Promises.settablePromise();
            final AtomicReference<State> state = new AtomicReference<State>(State.WAITING);

            CallableRunner(Callable<T> taskToRun) {
                this.task = taskToRun;
                this.promise.fail(t -> {
                    if (this.promise.isCancelled()) {
                        this.state.set(State.FINISHED);
                    }
                });
            }

            @Override
            public void run() {
                block4: {
                    if (this.state.compareAndSet(State.WAITING, State.RUNNING)) {
                        try {
                            T value = this.task.call();
                            if (this.state.compareAndSet(State.RUNNING, State.FINISHED)) {
                                this.promise.set(value);
                            }
                        }
                        catch (Exception ex) {
                            if (!this.state.compareAndSet(State.RUNNING, State.FINISHED)) break block4;
                            this.promise.exception(ex);
                        }
                    }
                }
            }

            @Override
            public Promise<T> get() {
                return this.promise;
            }

            static enum State {
                WAITING,
                RUNNING,
                FINISHED;

            }
        }
    }
}

