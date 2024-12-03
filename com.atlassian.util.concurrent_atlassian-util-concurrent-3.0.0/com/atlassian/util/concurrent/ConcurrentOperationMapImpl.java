/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.ThreadSafe
 */
package com.atlassian.util.concurrent;

import com.atlassian.util.concurrent.Assertions;
import com.atlassian.util.concurrent.ConcurrentOperationMap;
import com.atlassian.util.concurrent.Function;
import com.atlassian.util.concurrent.RuntimeInterruptedException;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class ConcurrentOperationMapImpl<K, R>
implements ConcurrentOperationMap<K, R> {
    private final ConcurrentMap<K, CallerRunsFuture<R>> map = new ConcurrentHashMap<K, CallerRunsFuture<R>>();
    private final Function<Callable<R>, CallerRunsFuture<R>> futureFactory;

    public ConcurrentOperationMapImpl() {
        this(new Function<Callable<R>, CallerRunsFuture<R>>(){

            @Override
            public CallerRunsFuture<R> get(Callable<R> input) {
                return new CallerRunsFuture(input);
            }
        });
    }

    ConcurrentOperationMapImpl(Function<Callable<R>, CallerRunsFuture<R>> futureFactory) {
        this.futureFactory = Assertions.notNull("futureFactory", futureFactory);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public R runOperation(K key, Callable<R> operation) throws ExecutionException {
        CallerRunsFuture future = (CallerRunsFuture)this.map.get(key);
        while (future == null) {
            this.map.putIfAbsent(key, this.futureFactory.get(operation));
            future = (CallerRunsFuture)this.map.get(key);
        }
        try {
            Object t = future.get();
            return (R)t;
        }
        finally {
            this.map.remove(key, future);
        }
    }

    static class CallerRunsFuture<T>
    extends FutureTask<T> {
        CallerRunsFuture(Callable<T> callable) {
            super(callable);
        }

        @Override
        public T get() throws ExecutionException {
            this.run();
            try {
                return (T)super.get();
            }
            catch (InterruptedException e) {
                throw new RuntimeInterruptedException(e);
            }
            catch (ExecutionException e) {
                Throwable cause = e.getCause();
                if (cause instanceof RuntimeException) {
                    throw (RuntimeException)cause;
                }
                if (cause instanceof Error) {
                    throw (Error)cause;
                }
                throw e;
            }
        }
    }
}

