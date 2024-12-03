/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.ForOverride
 *  javax.annotation.CheckForNull
 */
package com.google.common.util.concurrent;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.AsyncFunction;
import com.google.common.util.concurrent.ElementTypesAreNonnullByDefault;
import com.google.common.util.concurrent.FluentFuture;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ParametricNullness;
import com.google.common.util.concurrent.Platform;
import com.google.errorprone.annotations.ForOverride;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible
abstract class AbstractTransformFuture<I, O, F, T>
extends FluentFuture.TrustedFuture<O>
implements Runnable {
    @CheckForNull
    ListenableFuture<? extends I> inputFuture;
    @CheckForNull
    F function;

    static <I, O> ListenableFuture<O> create(ListenableFuture<I> input, AsyncFunction<? super I, ? extends O> function, Executor executor) {
        Preconditions.checkNotNull(executor);
        AsyncTransformFuture<? super I, ? extends O> output = new AsyncTransformFuture<I, O>(input, function);
        input.addListener(output, MoreExecutors.rejectionPropagatingExecutor(executor, output));
        return output;
    }

    static <I, O> ListenableFuture<O> create(ListenableFuture<I> input, Function<? super I, ? extends O> function, Executor executor) {
        Preconditions.checkNotNull(function);
        TransformFuture<? super I, ? extends O> output = new TransformFuture<I, O>(input, function);
        input.addListener(output, MoreExecutors.rejectionPropagatingExecutor(executor, output));
        return output;
    }

    AbstractTransformFuture(ListenableFuture<? extends I> inputFuture, F function) {
        this.inputFuture = Preconditions.checkNotNull(inputFuture);
        this.function = Preconditions.checkNotNull(function);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public final void run() {
        T transformResult;
        I sourceResult;
        ListenableFuture<I> localInputFuture = this.inputFuture;
        F localFunction = this.function;
        if (this.isCancelled() | localInputFuture == null | localFunction == null) {
            return;
        }
        this.inputFuture = null;
        if (localInputFuture.isCancelled()) {
            boolean unused = this.setFuture(localInputFuture);
            return;
        }
        try {
            sourceResult = Futures.getDone(localInputFuture);
        }
        catch (CancellationException e) {
            this.cancel(false);
            return;
        }
        catch (ExecutionException e) {
            this.setException(e.getCause());
            return;
        }
        catch (RuntimeException e) {
            this.setException(e);
            return;
        }
        catch (Error e) {
            this.setException(e);
            return;
        }
        try {
            transformResult = this.doTransform(localFunction, sourceResult);
        }
        catch (Throwable t) {
            Platform.restoreInterruptIfIsInterruptedException(t);
            this.setException(t);
            return;
        }
        finally {
            this.function = null;
        }
        this.setResult(transformResult);
    }

    @ParametricNullness
    @ForOverride
    abstract T doTransform(F var1, @ParametricNullness I var2) throws Exception;

    @ForOverride
    abstract void setResult(@ParametricNullness T var1);

    @Override
    protected final void afterDone() {
        this.maybePropagateCancellationTo(this.inputFuture);
        this.inputFuture = null;
        this.function = null;
    }

    @Override
    @CheckForNull
    protected String pendingToString() {
        ListenableFuture<? extends I> localInputFuture = this.inputFuture;
        F localFunction = this.function;
        String superString = super.pendingToString();
        String resultString = "";
        if (localInputFuture != null) {
            resultString = "inputFuture=[" + localInputFuture + "], ";
        }
        if (localFunction != null) {
            return resultString + "function=[" + localFunction + "]";
        }
        if (superString != null) {
            return resultString + superString;
        }
        return null;
    }

    private static final class TransformFuture<I, O>
    extends AbstractTransformFuture<I, O, Function<? super I, ? extends O>, O> {
        TransformFuture(ListenableFuture<? extends I> inputFuture, Function<? super I, ? extends O> function) {
            super(inputFuture, function);
        }

        @Override
        @ParametricNullness
        O doTransform(Function<? super I, ? extends O> function, @ParametricNullness I input) {
            return function.apply(input);
        }

        @Override
        void setResult(@ParametricNullness O result) {
            this.set(result);
        }
    }

    private static final class AsyncTransformFuture<I, O>
    extends AbstractTransformFuture<I, O, AsyncFunction<? super I, ? extends O>, ListenableFuture<? extends O>> {
        AsyncTransformFuture(ListenableFuture<? extends I> inputFuture, AsyncFunction<? super I, ? extends O> function) {
            super(inputFuture, function);
        }

        @Override
        ListenableFuture<? extends O> doTransform(AsyncFunction<? super I, ? extends O> function, @ParametricNullness I input) throws Exception {
            ListenableFuture<? extends O> outputFuture = function.apply(input);
            Preconditions.checkNotNull(outputFuture, "AsyncFunction.apply returned null instead of a Future. Did you mean to return immediateFuture(null)? %s", function);
            return outputFuture;
        }

        @Override
        void setResult(ListenableFuture<? extends O> result) {
            this.setFuture(result);
        }
    }
}

