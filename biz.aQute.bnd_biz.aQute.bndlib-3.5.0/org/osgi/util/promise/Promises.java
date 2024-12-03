/*
 * Decompiled with CFR 0.152.
 */
package org.osgi.util.promise;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.osgi.util.promise.FailedPromisesException;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.PromiseImpl;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class Promises {
    private Promises() {
    }

    public static <T> Promise<T> resolved(T value) {
        return new PromiseImpl<T>(value, null);
    }

    public static <T> Promise<T> failed(Throwable failure) {
        return new PromiseImpl<Object>(null, PromiseImpl.requireNonNull(failure));
    }

    public static <T, S extends T> Promise<List<T>> all(Collection<Promise<S>> promises) {
        if (promises.isEmpty()) {
            ArrayList result = new ArrayList();
            return Promises.resolved(result);
        }
        ArrayList list = new ArrayList(promises);
        PromiseImpl chained = new PromiseImpl();
        All all = new All(chained, list);
        for (Promise promise : list) {
            promise.onResolve(all);
        }
        return chained;
    }

    public static <T> Promise<List<T>> all(Promise<? extends T> ... promises) {
        List list = Arrays.asList(promises);
        return Promises.all(list);
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static final class All<T>
    implements Runnable {
        private final PromiseImpl<List<T>> chained;
        private final List<Promise<? extends T>> promises;
        private final AtomicInteger promiseCount;

        All(PromiseImpl<List<T>> chained, List<Promise<? extends T>> promises) {
            this.chained = chained;
            this.promises = promises;
            this.promiseCount = new AtomicInteger(promises.size());
        }

        @Override
        public void run() {
            if (this.promiseCount.decrementAndGet() != 0) {
                return;
            }
            ArrayList<Object> result = new ArrayList<Object>(this.promises.size());
            ArrayList failed = new ArrayList(this.promises.size());
            Throwable cause = null;
            for (Promise<T> promise : this.promises) {
                Object value;
                Throwable failure;
                try {
                    failure = promise.getFailure();
                    value = failure != null ? null : (Object)promise.getValue();
                }
                catch (Throwable e) {
                    this.chained.resolve(null, e);
                    return;
                }
                if (failure != null) {
                    failed.add(promise);
                    if (cause != null) continue;
                    cause = failure;
                    continue;
                }
                result.add(value);
            }
            if (failed.isEmpty()) {
                this.chained.resolve(result, null);
            } else {
                this.chained.resolve(null, new FailedPromisesException(failed, cause));
            }
        }
    }
}

