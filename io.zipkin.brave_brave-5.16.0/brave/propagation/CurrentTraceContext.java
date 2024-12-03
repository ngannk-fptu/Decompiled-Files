/*
 * Decompiled with CFR 0.152.
 */
package brave.propagation;

import brave.internal.Nullable;
import brave.internal.WrappingExecutorService;
import brave.propagation.SamplingFlags;
import brave.propagation.ThreadLocalCurrentTraceContext;
import brave.propagation.TraceContext;
import java.io.Closeable;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

public abstract class CurrentTraceContext {
    final ScopeDecorator[] scopeDecorators;

    @Nullable
    public abstract TraceContext get();

    public abstract Scope newScope(@Nullable TraceContext var1);

    protected CurrentTraceContext() {
        this.scopeDecorators = new ScopeDecorator[0];
    }

    protected CurrentTraceContext(Builder builder) {
        this.scopeDecorators = builder.scopeDecorators.toArray(new ScopeDecorator[0]);
    }

    protected Scope decorateScope(@Nullable TraceContext context, Scope scope) {
        for (ScopeDecorator scopeDecorator : this.scopeDecorators) {
            scope = scopeDecorator.decorateScope(context, scope);
        }
        return scope;
    }

    public Scope maybeScope(@Nullable TraceContext context) {
        TraceContext current = this.get();
        if (CurrentTraceContext.equals(current, context)) {
            return this.decorateScope(context, Scope.NOOP);
        }
        return this.newScope(context);
    }

    public <C> Callable<C> wrap(final Callable<C> task) {
        final TraceContext invocationContext = this.get();
        class CurrentTraceContextCallable
        implements Callable<C> {
            CurrentTraceContextCallable() {
            }

            @Override
            public C call() throws Exception {
                Object v;
                block5: {
                    Scope scope = CurrentTraceContext.this.maybeScope(invocationContext);
                    try {
                        v = task.call();
                        if (scope == null) break block5;
                        scope.close();
                    }
                    catch (Throwable throwable) {
                        if (scope != null) {
                            try {
                                scope.close();
                            }
                            catch (Throwable throwable2) {
                            }
                        }
                        throw throwable;
                    }
                }
                return v;
            }
        }
        return new CurrentTraceContextCallable();
    }

    public Runnable wrap(final Runnable task) {
        final TraceContext invocationContext = this.get();
        class CurrentTraceContextRunnable
        implements Runnable {
            CurrentTraceContextRunnable() {
            }

            @Override
            public void run() {
                Scope scope = CurrentTraceContext.this.maybeScope(invocationContext);
                try {
                    task.run();
                    if (scope != null) {
                        scope.close();
                    }
                }
                catch (Throwable throwable) {
                    if (scope != null) {
                        try {
                            scope.close();
                        }
                        catch (Throwable throwable2) {
                        }
                    }
                    throw throwable;
                }
            }
        }
        return new CurrentTraceContextRunnable();
    }

    public Executor executor(final Executor delegate) {
        class CurrentTraceContextExecutor
        implements Executor {
            CurrentTraceContextExecutor() {
            }

            @Override
            public void execute(Runnable task) {
                delegate.execute(CurrentTraceContext.this.wrap(task));
            }
        }
        return new CurrentTraceContextExecutor();
    }

    public ExecutorService executorService(final ExecutorService delegate) {
        class CurrentTraceContextExecutorService
        extends WrappingExecutorService {
            CurrentTraceContextExecutorService() {
            }

            @Override
            protected ExecutorService delegate() {
                return delegate;
            }

            protected <C> Callable<C> wrap(Callable<C> task) {
                return CurrentTraceContext.this.wrap(task);
            }

            @Override
            protected Runnable wrap(Runnable task) {
                return CurrentTraceContext.this.wrap(task);
            }
        }
        return new CurrentTraceContextExecutorService();
    }

    static boolean equals(@Nullable TraceContext a, @Nullable TraceContext b) {
        return a == null ? b == null : a.equals(b);
    }

    static {
        SamplingFlags.DEBUG.toString();
    }

    public static final class Default
    extends ThreadLocalCurrentTraceContext {
        static final InheritableThreadLocal<TraceContext> INHERITABLE = new InheritableThreadLocal();

        public static CurrentTraceContext create() {
            return ThreadLocalCurrentTraceContext.create();
        }

        public static CurrentTraceContext inheritable() {
            return new Default();
        }

        Default() {
            super(new ThreadLocalCurrentTraceContext.Builder(INHERITABLE));
        }
    }

    public static interface ScopeDecorator {
        public static final ScopeDecorator NOOP = new ScopeDecorator(){

            @Override
            public Scope decorateScope(TraceContext context, Scope scope) {
                return scope;
            }

            public String toString() {
                return "NoopScopeDecorator";
            }
        };

        public Scope decorateScope(@Nullable TraceContext var1, Scope var2);
    }

    public static interface Scope
    extends Closeable {
        public static final Scope NOOP = new Scope(){

            @Override
            public void close() {
            }

            public String toString() {
                return "NoopScope";
            }
        };

        @Override
        public void close();
    }

    public static abstract class Builder {
        ArrayList<ScopeDecorator> scopeDecorators = new ArrayList();

        public Builder addScopeDecorator(ScopeDecorator scopeDecorator) {
            if (scopeDecorator == null) {
                throw new NullPointerException("scopeDecorator == null");
            }
            if (scopeDecorator == ScopeDecorator.NOOP) {
                return this;
            }
            this.scopeDecorators.add(scopeDecorator);
            return this;
        }

        public abstract CurrentTraceContext build();
    }
}

