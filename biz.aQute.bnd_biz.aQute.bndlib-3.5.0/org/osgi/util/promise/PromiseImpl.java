/*
 * Decompiled with CFR 0.152.
 */
package org.osgi.util.promise;

import java.lang.reflect.InvocationTargetException;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import org.osgi.util.function.Function;
import org.osgi.util.function.Predicate;
import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Success;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class PromiseImpl<T>
implements Promise<T> {
    private final ConcurrentLinkedQueue<Runnable> callbacks;
    private final CountDownLatch resolved;
    private T value;
    private Throwable fail;

    PromiseImpl() {
        this.callbacks = new ConcurrentLinkedQueue();
        this.resolved = new CountDownLatch(1);
    }

    PromiseImpl(T v, Throwable f) {
        this.value = v;
        this.fail = f;
        this.callbacks = new ConcurrentLinkedQueue();
        this.resolved = new CountDownLatch(0);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void resolve(T v, Throwable f) {
        CountDownLatch countDownLatch = this.resolved;
        synchronized (countDownLatch) {
            if (this.resolved.getCount() == 0L) {
                throw new IllegalStateException("Already resolved");
            }
            this.value = v;
            this.fail = f;
            this.resolved.countDown();
        }
        this.notifyCallbacks();
    }

    private void notifyCallbacks() {
        if (this.resolved.getCount() != 0L) {
            return;
        }
        Runnable callback = this.callbacks.poll();
        while (callback != null) {
            try {
                callback.run();
            }
            catch (Throwable t) {
                Logger.logCallbackException(t);
            }
            callback = this.callbacks.poll();
        }
    }

    @Override
    public boolean isDone() {
        return this.resolved.getCount() == 0L;
    }

    @Override
    public T getValue() throws InvocationTargetException, InterruptedException {
        this.resolved.await();
        if (this.fail == null) {
            return this.value;
        }
        throw new InvocationTargetException(this.fail);
    }

    @Override
    public Throwable getFailure() throws InterruptedException {
        this.resolved.await();
        return this.fail;
    }

    @Override
    public Promise<T> onResolve(Runnable callback) {
        this.callbacks.offer(callback);
        this.notifyCallbacks();
        return this;
    }

    @Override
    public <R> Promise<R> then(Success<? super T, ? extends R> success, Failure failure) {
        PromiseImpl<T> chained = new PromiseImpl<T>();
        this.onResolve(new Then<R>(chained, success, failure));
        return chained;
    }

    @Override
    public <R> Promise<R> then(Success<? super T, ? extends R> success) {
        return this.then(success, null);
    }

    Promise<Void> resolveWith(Promise<? extends T> with) {
        PromiseImpl<Void> chained = new PromiseImpl<Void>();
        ResolveWith resolveWith = new ResolveWith(chained);
        with.then(resolveWith, resolveWith);
        return chained;
    }

    @Override
    public Promise<T> filter(Predicate<? super T> predicate) {
        return this.then(new Filter<T>(predicate));
    }

    @Override
    public <R> Promise<R> map(Function<? super T, ? extends R> mapper) {
        return this.then(new Map<T, R>(mapper));
    }

    @Override
    public <R> Promise<R> flatMap(Function<? super T, Promise<? extends R>> mapper) {
        return this.then(new FlatMap<T, R>(mapper));
    }

    @Override
    public Promise<T> recover(Function<Promise<?>, ? extends T> recovery) {
        PromiseImpl<T> chained = new PromiseImpl<T>();
        Recover<? extends T> recover = new Recover<T>(chained, recovery);
        this.then(recover, recover);
        return chained;
    }

    @Override
    public Promise<T> recoverWith(Function<Promise<?>, Promise<? extends T>> recovery) {
        PromiseImpl<T> chained = new PromiseImpl<T>();
        RecoverWith<? extends T> recoverWith = new RecoverWith<T>(chained, recovery);
        this.then(recoverWith, recoverWith);
        return chained;
    }

    @Override
    public Promise<T> fallbackTo(Promise<? extends T> fallback) {
        PromiseImpl<T> chained = new PromiseImpl<T>();
        FallbackTo<? extends T> fallbackTo = new FallbackTo<T>(chained, fallback);
        this.then(fallbackTo, fallbackTo);
        return chained;
    }

    static <V> V requireNonNull(V value) {
        if (value != null) {
            return value;
        }
        throw new NullPointerException();
    }

    private static final class Logger {
        private static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(PromiseImpl.class.getName());

        private Logger() {
        }

        static void logCallbackException(Throwable t) {
            LOGGER.log(Level.WARNING, "Exception from Promise callback", t);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static final class FallbackTo<T>
    implements Success<T, Void>,
    Failure {
        private final PromiseImpl<T> chained;
        private final Promise<? extends T> fallback;

        FallbackTo(PromiseImpl<T> chained, Promise<? extends T> fallback) {
            this.chained = chained;
            this.fallback = PromiseImpl.requireNonNull(fallback);
        }

        @Override
        public Promise<Void> call(Promise<T> resolved) throws Exception {
            T value;
            try {
                value = resolved.getValue();
            }
            catch (Throwable e) {
                this.chained.resolve(null, e);
                return null;
            }
            this.chained.resolve(value, null);
            return null;
        }

        @Override
        public void fail(Promise<?> resolved) throws Exception {
            Throwable failure;
            try {
                failure = resolved.getFailure();
            }
            catch (Throwable e) {
                this.chained.resolve(null, e);
                return;
            }
            this.fallback.onResolve(new Chain<T>(this.chained, this.fallback, failure));
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static final class RecoverWith<T>
    implements Success<T, Void>,
    Failure {
        private final PromiseImpl<T> chained;
        private final Function<Promise<?>, Promise<? extends T>> recovery;

        RecoverWith(PromiseImpl<T> chained, Function<Promise<?>, Promise<? extends T>> recovery) {
            this.chained = chained;
            this.recovery = PromiseImpl.requireNonNull(recovery);
        }

        @Override
        public Promise<Void> call(Promise<T> resolved) throws Exception {
            T value;
            try {
                value = resolved.getValue();
            }
            catch (Throwable e) {
                this.chained.resolve(null, e);
                return null;
            }
            this.chained.resolve(value, null);
            return null;
        }

        @Override
        public void fail(Promise<?> resolved) throws Exception {
            Throwable failure;
            Promise<T> recovered;
            try {
                recovered = this.recovery.apply(resolved);
                failure = resolved.getFailure();
            }
            catch (Throwable e) {
                this.chained.resolve(null, e);
                return;
            }
            if (recovered == null) {
                this.chained.resolve(null, failure);
            } else {
                recovered.onResolve(new Chain<T>(this.chained, recovered));
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static final class Recover<T>
    implements Success<T, Void>,
    Failure {
        private final PromiseImpl<T> chained;
        private final Function<Promise<?>, ? extends T> recovery;

        Recover(PromiseImpl<T> chained, Function<Promise<?>, ? extends T> recovery) {
            this.chained = chained;
            this.recovery = PromiseImpl.requireNonNull(recovery);
        }

        @Override
        public Promise<Void> call(Promise<T> resolved) throws Exception {
            T value;
            try {
                value = resolved.getValue();
            }
            catch (Throwable e) {
                this.chained.resolve(null, e);
                return null;
            }
            this.chained.resolve(value, null);
            return null;
        }

        @Override
        public void fail(Promise<?> resolved) throws Exception {
            Throwable failure;
            T recovered;
            try {
                recovered = this.recovery.apply(resolved);
                failure = resolved.getFailure();
            }
            catch (Throwable e) {
                this.chained.resolve(null, e);
                return;
            }
            if (recovered == null) {
                this.chained.resolve(null, failure);
            } else {
                this.chained.resolve(recovered, null);
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static final class FlatMap<T, R>
    implements Success<T, R> {
        private final Function<? super T, Promise<? extends R>> mapper;

        FlatMap(Function<? super T, Promise<? extends R>> mapper) {
            this.mapper = PromiseImpl.requireNonNull(mapper);
        }

        @Override
        public Promise<R> call(Promise<T> resolved) throws Exception {
            return this.mapper.apply(resolved.getValue());
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static final class Map<T, R>
    implements Success<T, R> {
        private final Function<? super T, ? extends R> mapper;

        Map(Function<? super T, ? extends R> mapper) {
            this.mapper = PromiseImpl.requireNonNull(mapper);
        }

        @Override
        public Promise<R> call(Promise<T> resolved) throws Exception {
            return new PromiseImpl<R>(this.mapper.apply(resolved.getValue()), null);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static final class Filter<T>
    implements Success<T, T> {
        private final Predicate<? super T> predicate;

        Filter(Predicate<? super T> predicate) {
            this.predicate = PromiseImpl.requireNonNull(predicate);
        }

        @Override
        public Promise<T> call(Promise<T> resolved) throws Exception {
            if (this.predicate.test(resolved.getValue())) {
                return resolved;
            }
            throw new NoSuchElementException();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private final class ResolveWith
    implements Success<T, Void>,
    Failure {
        private final PromiseImpl<Void> chained;

        ResolveWith(PromiseImpl<Void> chained) {
            this.chained = chained;
        }

        @Override
        public Promise<Void> call(Promise<T> with) throws Exception {
            try {
                PromiseImpl.this.resolve(with.getValue(), null);
            }
            catch (Throwable e) {
                this.chained.resolve(null, e);
                return null;
            }
            this.chained.resolve(null, null);
            return null;
        }

        @Override
        public void fail(Promise<?> with) throws Exception {
            try {
                PromiseImpl.this.resolve(null, with.getFailure());
            }
            catch (Throwable e) {
                this.chained.resolve(null, e);
                return;
            }
            this.chained.resolve(null, null);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static final class Chain<R>
    implements Runnable {
        private final PromiseImpl<R> chained;
        private final Promise<? extends R> promise;
        private final Throwable failure;

        Chain(PromiseImpl<R> chained, Promise<? extends R> promise) {
            this.chained = chained;
            this.promise = promise;
            this.failure = null;
        }

        Chain(PromiseImpl<R> chained, Promise<? extends R> promise, Throwable failure) {
            this.chained = chained;
            this.promise = promise;
            this.failure = failure;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void run() {
            Throwable f;
            Object value = null;
            boolean interrupted = Thread.interrupted();
            try {
                f = this.promise.getFailure();
                if (f == null) {
                    value = this.promise.getValue();
                } else if (this.failure != null) {
                    f = this.failure;
                }
            }
            catch (Throwable e) {
                f = e;
            }
            finally {
                if (interrupted) {
                    Thread.currentThread().interrupt();
                }
            }
            this.chained.resolve(value, f);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private final class Then<R>
    implements Runnable {
        private final PromiseImpl<R> chained;
        private final Success<T, ? extends R> success;
        private final Failure failure;

        Then(PromiseImpl<R> chained, Success<? super T, ? extends R> success, Failure failure) {
            this.chained = chained;
            this.success = success;
            this.failure = failure;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void run() {
            Throwable f;
            boolean interrupted = Thread.interrupted();
            try {
                f = PromiseImpl.this.getFailure();
            }
            catch (Throwable e) {
                f = e;
            }
            finally {
                if (interrupted) {
                    Thread.currentThread().interrupt();
                }
            }
            if (f != null) {
                if (this.failure != null) {
                    try {
                        this.failure.fail(PromiseImpl.this);
                    }
                    catch (Throwable e) {
                        f = e;
                    }
                }
                this.chained.resolve(null, f);
                return;
            }
            Promise<R> returned = null;
            if (this.success != null) {
                try {
                    returned = this.success.call(PromiseImpl.this);
                }
                catch (Throwable e) {
                    this.chained.resolve(null, e);
                    return;
                }
            }
            if (returned == null) {
                this.chained.resolve(null, null);
            } else {
                returned.onResolve(new Chain<R>(this.chained, returned));
            }
        }
    }
}

