/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.util.concurrent;

import com.atlassian.util.concurrent.Function;
import com.atlassian.util.concurrent.Functions;
import com.atlassian.util.concurrent.LockManager;
import com.atlassian.util.concurrent.ManagedLock;
import com.atlassian.util.concurrent.ManagedLocks;
import com.atlassian.util.concurrent.Supplier;
import com.atlassian.util.concurrent.WeakMemoizer;
import java.util.concurrent.Callable;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@Deprecated
public class LockManagers {
    @Deprecated
    public static <T> LockManager<T> weakLockManager() {
        return LockManagers.weakLockManager(Functions.identity());
    }

    @Deprecated
    public static <T, D> LockManager<T> weakLockManager(Function<T, D> stripeFunction) {
        Function lockFactory = Functions.fromSupplier(ManagedLocks.managedLockFactory(ManagedLocks.lockFactory()));
        return Manager.createManager(stripeFunction, WeakMemoizer.weakMemoizer(lockFactory));
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    static class Manager<T, D>
    implements LockManager<T> {
        private final Function<D, ManagedLock> lockFactory;
        private final Function<T, D> stripeFunction;

        static final <T, D> Manager<T, D> createManager(Function<T, D> stripeFunction, Function<D, ManagedLock> lockFactory) {
            return new Manager<T, D>(stripeFunction, lockFactory);
        }

        Manager(Function<T, D> stripeFunction, Function<D, ManagedLock> lockFactory) {
            this.stripeFunction = stripeFunction;
            this.lockFactory = lockFactory;
        }

        @Override
        public <R> R withLock(T descriptor, Supplier<R> supplier) {
            return this.lockFactory.get(this.stripeFunction.get(descriptor)).withLock(supplier);
        }

        @Override
        public <R> R withLock(T descriptor, Callable<R> callable) throws Exception {
            return this.lockFactory.get(this.stripeFunction.get(descriptor)).withLock(callable);
        }

        @Override
        public void withLock(T descriptor, Runnable runnable) {
            this.lockFactory.get(this.stripeFunction.get(descriptor)).withLock(runnable);
        }
    }
}

