/*
 * Decompiled with CFR 0.152.
 */
package zipkin2;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import zipkin2.Callback;

public abstract class Call<V>
implements Cloneable {
    public static <V> Call<V> create(V v) {
        return new Constant<V>(v);
    }

    public static <T> Call<List<T>> emptyList() {
        return Call.create(Collections.emptyList());
    }

    public final <R> Call<R> map(Mapper<V, R> mapper) {
        return new Mapping<R, V>(mapper, this);
    }

    public final <R> Call<R> flatMap(FlatMapper<V, R> flatMapper) {
        return new FlatMapping<R, V>(flatMapper, this);
    }

    public final Call<V> handleError(ErrorHandler<V> errorHandler) {
        return new ErrorHandling<V>(errorHandler, this);
    }

    public static void propagateIfFatal(Throwable t) {
        if (t instanceof VirtualMachineError) {
            throw (VirtualMachineError)t;
        }
        if (t instanceof ThreadDeath) {
            throw (ThreadDeath)t;
        }
        if (t instanceof LinkageError) {
            throw (LinkageError)t;
        }
    }

    public abstract V execute() throws IOException;

    public abstract void enqueue(Callback<V> var1);

    public abstract void cancel();

    public abstract boolean isCanceled();

    public abstract Call<V> clone();

    public static abstract class Base<V>
    extends Call<V> {
        volatile boolean canceled;
        boolean executed;

        protected Base() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public final V execute() throws IOException {
            Base base = this;
            synchronized (base) {
                if (this.executed) {
                    throw new IllegalStateException("Already Executed");
                }
                this.executed = true;
            }
            if (this.isCanceled()) {
                throw new IOException("Canceled");
            }
            return this.doExecute();
        }

        protected abstract V doExecute() throws IOException;

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public final void enqueue(Callback<V> callback) {
            Base base = this;
            synchronized (base) {
                if (this.executed) {
                    throw new IllegalStateException("Already Executed");
                }
                this.executed = true;
            }
            if (this.isCanceled()) {
                callback.onError(new IOException("Canceled"));
            } else {
                this.doEnqueue(callback);
            }
        }

        protected abstract void doEnqueue(Callback<V> var1);

        @Override
        public final void cancel() {
            this.canceled = true;
            this.doCancel();
        }

        protected void doCancel() {
        }

        @Override
        public final boolean isCanceled() {
            return this.canceled || this.doIsCanceled();
        }

        protected boolean doIsCanceled() {
            return false;
        }
    }

    static final class ErrorHandling<V>
    extends Base<V> {
        static final Object SENTINEL = new Object();
        final ErrorHandler<V> errorHandler;
        final Call<V> delegate;

        ErrorHandling(ErrorHandler<V> errorHandler, Call<V> delegate) {
            this.errorHandler = errorHandler;
            this.delegate = delegate;
        }

        @Override
        protected V doExecute() throws IOException {
            try {
                return this.delegate.execute();
            }
            catch (IOException e) {
                return this.handleError(e);
            }
            catch (RuntimeException e) {
                return this.handleError(e);
            }
            catch (Error e) {
                Call.propagateIfFatal(e);
                return this.handleError(e);
            }
        }

        <T extends Throwable> V handleError(T e) throws T {
            final AtomicReference<Object> ref = new AtomicReference<Object>(SENTINEL);
            this.errorHandler.onErrorReturn(e, new Callback<V>(){

                @Override
                public void onSuccess(V value) {
                    ref.set(value);
                }

                @Override
                public void onError(Throwable t) {
                }
            });
            Object result = ref.get();
            if (SENTINEL == result) {
                throw e;
            }
            return (V)result;
        }

        @Override
        protected void doEnqueue(final Callback<V> callback) {
            this.delegate.enqueue(new Callback<V>(){

                @Override
                public void onSuccess(V value) {
                    callback.onSuccess(value);
                }

                @Override
                public void onError(Throwable t) {
                    ErrorHandling.this.errorHandler.onErrorReturn(t, callback);
                }
            });
        }

        @Override
        protected void doCancel() {
            this.delegate.cancel();
        }

        public String toString() {
            return "ErrorHandling{call=" + this.delegate + ", errorHandler=" + this.errorHandler + "}";
        }

        @Override
        public Call<V> clone() {
            return new ErrorHandling<V>(this.errorHandler, this.delegate.clone());
        }
    }

    static final class FlatMapping<R, V>
    extends Base<R> {
        final FlatMapper<V, R> flatMapper;
        final Call<V> delegate;
        volatile Call<R> mapped;

        FlatMapping(FlatMapper<V, R> flatMapper, Call<V> delegate) {
            this.flatMapper = flatMapper;
            this.delegate = delegate;
        }

        @Override
        protected R doExecute() throws IOException {
            this.mapped = this.flatMapper.map(this.delegate.execute());
            return this.mapped.execute();
        }

        @Override
        protected void doEnqueue(final Callback<R> callback) {
            this.delegate.enqueue(new Callback<V>(){

                @Override
                public void onSuccess(V value) {
                    try {
                        FlatMapping.this.mapped = FlatMapping.this.flatMapper.map(value);
                        FlatMapping.this.mapped.enqueue(callback);
                    }
                    catch (Throwable t) {
                        Call.propagateIfFatal(t);
                        callback.onError(t);
                    }
                }

                @Override
                public void onError(Throwable t) {
                    callback.onError(t);
                }
            });
        }

        @Override
        protected void doCancel() {
            this.delegate.cancel();
            if (this.mapped != null) {
                this.mapped.cancel();
            }
        }

        public String toString() {
            return "FlatMapping{call=" + this.delegate + ", flatMapper=" + this.flatMapper + "}";
        }

        @Override
        public Call<R> clone() {
            return new FlatMapping<R, V>(this.flatMapper, this.delegate.clone());
        }
    }

    static final class Mapping<R, V>
    extends Base<R> {
        final Mapper<V, R> mapper;
        final Call<V> delegate;

        Mapping(Mapper<V, R> mapper, Call<V> delegate) {
            this.mapper = mapper;
            this.delegate = delegate;
        }

        @Override
        protected R doExecute() throws IOException {
            return this.mapper.map(this.delegate.execute());
        }

        @Override
        protected void doEnqueue(final Callback<R> callback) {
            this.delegate.enqueue(new Callback<V>(){

                @Override
                public void onSuccess(V value) {
                    try {
                        callback.onSuccess(Mapping.this.mapper.map(value));
                    }
                    catch (Throwable t) {
                        callback.onError(t);
                    }
                }

                @Override
                public void onError(Throwable t) {
                    callback.onError(t);
                }
            });
        }

        public String toString() {
            return "Mapping{call=" + this.delegate + ", mapper=" + this.mapper + "}";
        }

        @Override
        public Call<R> clone() {
            return new Mapping<R, V>(this.mapper, this.delegate.clone());
        }
    }

    static class Constant<V>
    extends Base<V> {
        final V v;

        Constant(V v) {
            this.v = v;
        }

        @Override
        protected V doExecute() {
            return this.v;
        }

        @Override
        protected void doEnqueue(Callback<V> callback) {
            callback.onSuccess(this.v);
        }

        @Override
        public Call<V> clone() {
            return new Constant<V>(this.v);
        }

        public String toString() {
            return "ConstantCall{value=" + this.v + "}";
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (o instanceof Constant) {
                Constant that = (Constant)o;
                return this.v == null ? that.v == null : this.v.equals(that.v);
            }
            return false;
        }

        public int hashCode() {
            int h = 1;
            h *= 1000003;
            return h ^= this.v == null ? 0 : this.v.hashCode();
        }
    }

    public static interface ErrorHandler<V> {
        public void onErrorReturn(Throwable var1, Callback<V> var2);
    }

    public static interface FlatMapper<V1, V2> {
        public Call<V2> map(V1 var1);
    }

    public static interface Mapper<V1, V2> {
        public V2 map(V1 var1);
    }
}

