/*
 * Decompiled with CFR 0.152.
 */
package zipkin2.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;
import zipkin2.Call;
import zipkin2.Callback;
import zipkin2.internal.Nullable;

public abstract class AggregateCall<I, O>
extends Call.Base<O> {
    final Logger log = Logger.getLogger(this.getClass().getName());
    final List<Call<I>> delegate;

    public static Call<Void> newVoidCall(List<Call<Void>> calls) {
        if (calls.isEmpty()) {
            throw new IllegalArgumentException("calls were empty");
        }
        if (calls.size() == 1) {
            return calls.get(0);
        }
        return new AggregateVoidCall(calls);
    }

    protected AggregateCall(List<Call<I>> delegate) {
        assert (!delegate.isEmpty()) : "do not create empty aggregate calls";
        assert (delegate.size() > 1) : "do not create single-element aggregates";
        this.delegate = delegate;
    }

    protected abstract O newOutput();

    protected abstract void append(I var1, O var2);

    protected abstract boolean isEmpty(O var1);

    protected O finish(O output) {
        return output;
    }

    @Override
    protected O doExecute() throws IOException {
        int length = this.delegate.size();
        Throwable firstError = null;
        O result = this.newOutput();
        for (int i = 0; i < length; ++i) {
            Call<I> call = this.delegate.get(i);
            try {
                this.append(call.execute(), result);
                continue;
            }
            catch (Throwable e) {
                if (firstError == null) {
                    firstError = e;
                    continue;
                }
                if (!this.log.isLoggable(Level.INFO)) continue;
                this.log.log(Level.INFO, "error from " + call, e);
            }
        }
        if (firstError == null) {
            return this.finish(result);
        }
        if (firstError instanceof Error) {
            throw (Error)firstError;
        }
        if (firstError instanceof RuntimeException) {
            throw (RuntimeException)firstError;
        }
        throw (IOException)firstError;
    }

    @Override
    protected void doEnqueue(Callback<O> callback) {
        int length = this.delegate.size();
        AtomicInteger remaining = new AtomicInteger(length);
        AtomicReference<Throwable> firstError = new AtomicReference<Throwable>();
        O result = this.newOutput();
        for (int i = 0; i < length; ++i) {
            Call<I> call = this.delegate.get(i);
            call.enqueue(new CountdownCallback(call, remaining, firstError, result, callback));
        }
    }

    @Override
    protected void doCancel() {
        int length = this.delegate.size();
        for (int i = 0; i < length; ++i) {
            this.delegate.get(i).cancel();
        }
    }

    protected final List<Call<I>> cloneCalls() {
        int length = this.delegate.size();
        ArrayList<Call<I>> result = new ArrayList<Call<I>>(length);
        for (int i = 0; i < length; ++i) {
            result.add((Call<I>)this.delegate.get(i).clone());
        }
        return result;
    }

    public final List<Call<I>> delegate() {
        return this.delegate;
    }

    public String toString() {
        return "AggregateCall{" + this.delegate + "}";
    }

    class CountdownCallback
    implements Callback<I> {
        final Call<I> call;
        final AtomicInteger remaining;
        final AtomicReference<Throwable> firstError;
        @Nullable
        final O result;
        final Callback<O> callback;

        CountdownCallback(Call<I> call, AtomicInteger remaining, AtomicReference<Throwable> firstError, O result, Callback<O> callback) {
            this.call = call;
            this.remaining = remaining;
            this.firstError = firstError;
            this.result = result;
            this.callback = callback;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void onSuccess(I value) {
            Callback callback = this.callback;
            synchronized (callback) {
                AggregateCall.this.append(value, this.result);
                if (this.remaining.decrementAndGet() > 0) {
                    return;
                }
                Throwable error = this.firstError.get();
                if (error != null) {
                    this.callback.onError(error);
                } else {
                    this.callback.onSuccess(AggregateCall.this.finish(this.result));
                }
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public synchronized void onError(Throwable throwable) {
            if (AggregateCall.this.log.isLoggable(Level.INFO)) {
                AggregateCall.this.log.log(Level.INFO, "error from " + this.call, throwable);
            }
            Callback callback = this.callback;
            synchronized (callback) {
                this.firstError.compareAndSet(null, throwable);
                if (this.remaining.decrementAndGet() > 0) {
                    return;
                }
                this.callback.onError(this.firstError.get());
            }
        }
    }

    static final class AggregateVoidCall
    extends AggregateCall<Void, Void> {
        volatile boolean empty = true;

        AggregateVoidCall(List<Call<Void>> calls) {
            super(calls);
        }

        @Override
        protected Void newOutput() {
            return null;
        }

        @Override
        protected void append(Void input, Void output) {
            this.empty = false;
        }

        @Override
        protected boolean isEmpty(Void output) {
            return this.empty;
        }

        @Override
        public AggregateVoidCall clone() {
            return new AggregateVoidCall(this.cloneCalls());
        }
    }
}

