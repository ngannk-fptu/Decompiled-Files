/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.base.Supplier
 *  com.google.common.base.Suppliers
 *  net.jcip.annotations.ThreadSafe
 */
package com.atlassian.util.concurrent;

import com.atlassian.util.concurrent.Assertions;
import com.atlassian.util.concurrent.ExceptionPolicy;
import com.atlassian.util.concurrent.LimitedExecutor;
import com.atlassian.util.concurrent.NotNull;
import com.atlassian.util.concurrent.RuntimeExecutionException;
import com.atlassian.util.concurrent.RuntimeInterruptedException;
import com.atlassian.util.concurrent.Timeout;
import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import net.jcip.annotations.ThreadSafe;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@ThreadSafe
public final class AsyncCompleter {
    private final Executor executor;
    private final ExceptionPolicy policy;
    private final ExecutorCompletionServiceFactory completionServiceFactory;

    AsyncCompleter(Executor executor, ExceptionPolicy policy, ExecutorCompletionServiceFactory completionServiceFactory) {
        this.executor = Assertions.notNull("executor", executor);
        this.policy = Assertions.notNull("policy", policy);
        this.completionServiceFactory = Assertions.notNull("completionServiceFactory", completionServiceFactory);
    }

    public <T> Iterable<T> invokeAll(Iterable<? extends Callable<T>> callables) {
        return this.invokeAllTasks(callables, new BlockingAccessor());
    }

    public <T> Iterable<T> invokeAll(Iterable<? extends Callable<T>> callables, long time, TimeUnit unit) {
        return this.invokeAllTasks(callables, new TimeoutAccessor(Timeout.getNanosTimeout(time, unit)));
    }

    /*
     * Exception decompiling
     */
    <T> Iterable<T> invokeAllTasks(Iterable<? extends Callable<T>> callables, Accessor<T> accessor) {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * java.lang.NullPointerException: Cannot invoke "org.benf.cfr.reader.bytecode.analysis.types.BindingSuperContainer.getBoundAssignable(org.benf.cfr.reader.bytecode.analysis.types.JavaGenericRefTypeInstance, org.benf.cfr.reader.bytecode.analysis.types.JavaGenericRefTypeInstance)" because "maybeBindingContainer" is null
         *     at org.benf.cfr.reader.bytecode.analysis.types.GenericTypeBinder.extractBaseBindings(GenericTypeBinder.java:125)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.rewriters.ExplicitTypeCallRewriter$InnerExplicitTypeCallRewriter.rewriteFunctionInvokation(ExplicitTypeCallRewriter.java:37)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.rewriters.ExplicitTypeCallRewriter$InnerExplicitTypeCallRewriter.rewriteExpression(ExplicitTypeCallRewriter.java:56)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.rewriters.ExpressionRewriterHelper.applyForwards(ExpressionRewriterHelper.java:12)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.StaticFunctionInvokation.applyExpressionRewriterToArgs(StaticFunctionInvokation.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.rewriters.ExplicitTypeCallRewriter.rewriteExpression(ExplicitTypeCallRewriter.java:71)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.CastExpression.applyExpressionRewriter(CastExpression.java:128)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.rewriters.AbstractExpressionRewriter.rewriteExpression(AbstractExpressionRewriter.java:14)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.rewriters.ExplicitTypeCallRewriter.rewriteExpression(ExplicitTypeCallRewriter.java:75)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.rewriters.ExpressionRewriterHelper.applyForwards(ExpressionRewriterHelper.java:12)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.StaticFunctionInvokation.applyExpressionRewriterToArgs(StaticFunctionInvokation.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.StaticFunctionInvokation.applyExpressionRewriter(StaticFunctionInvokation.java:90)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.rewriters.AbstractExpressionRewriter.rewriteExpression(AbstractExpressionRewriter.java:14)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.rewriters.ExplicitTypeCallRewriter.rewriteExpression(ExplicitTypeCallRewriter.java:75)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.statement.ReturnValueStatement.rewriteExpressions(ReturnValueStatement.java:62)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.rewrite(Op03SimpleStatement.java:479)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op3rewriters.Op03Rewriters.rewriteWith(Op03Rewriters.java:23)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:819)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    static final class ExecutorCompletionServiceFunction<T>
    implements Function<Executor, CompletionService<T>> {
        ExecutorCompletionServiceFunction() {
        }

        public CompletionService<T> apply(Executor executor) {
            return new ExecutorCompletionService(executor);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    static final class DefaultExecutorCompletionServiceFactory
    implements ExecutorCompletionServiceFactory {
        DefaultExecutorCompletionServiceFactory() {
        }

        @Override
        public <T> Function<Executor, CompletionService<T>> create() {
            return new ExecutorCompletionServiceFunction();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    static final class BlockingAccessor<T>
    implements Accessor<T> {
        BlockingAccessor() {
        }

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

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    static final class TimeoutAccessor<T>
    implements Accessor<T> {
        private final Timeout timeout;
        private final Collection<Future<T>> futures = new ConcurrentLinkedQueue<Future<T>>();

        TimeoutAccessor(Timeout timeout) {
            this.timeout = timeout;
        }

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

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    static interface Accessor<T>
    extends Function<CompletionService<T>, T> {
        public void register(Future<T> var1);
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class AsyncCompletionFunction<T>
    implements Function<Callable<T>, Supplier<T>> {
        private final CompletionService<T> completionService;
        private final Accessor<T> accessor;
        private final Supplier<T> nextCompleteItem = new Supplier<T>(){

            public T get() {
                return AsyncCompletionFunction.this.accessor.apply(AsyncCompletionFunction.this.completionService);
            }
        };

        AsyncCompletionFunction(CompletionService<T> completionService, Accessor<T> accessor) {
            this.completionService = completionService;
            this.accessor = accessor;
        }

        public Supplier<T> apply(Callable<T> task) {
            this.accessor.register(this.completionService.submit(task));
            return Suppliers.memoize(this.nextCompleteItem);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static interface ExecutorCompletionServiceFactory {
        public <T> Function<Executor, CompletionService<T>> create();
    }

    public static class Builder {
        Executor executor;
        ExceptionPolicy policy = ExceptionPolicy.Policies.THROW;
        ExecutorCompletionServiceFactory completionServiceFactory = new DefaultExecutorCompletionServiceFactory();

        public Builder(@NotNull Executor executor) {
            this.executor = Assertions.notNull("executor", executor);
        }

        public Builder ignoreExceptions() {
            return this.handleExceptions(ExceptionPolicy.Policies.IGNORE_EXCEPTIONS);
        }

        public Builder handleExceptions(ExceptionPolicy policy) {
            this.policy = policy;
            return this;
        }

        public Builder completionServiceFactory(ExecutorCompletionServiceFactory completionServiceFactory) {
            this.completionServiceFactory = Assertions.notNull("completionServiceFactory", completionServiceFactory);
            return this;
        }

        public AsyncCompleter limitParallelExecutionTo(int limit) {
            return new AsyncCompleter(new LimitedExecutor(this.executor, limit), this.policy, this.completionServiceFactory);
        }

        public AsyncCompleter build() {
            return new AsyncCompleter(this.executor, this.policy, this.completionServiceFactory);
        }
    }
}

