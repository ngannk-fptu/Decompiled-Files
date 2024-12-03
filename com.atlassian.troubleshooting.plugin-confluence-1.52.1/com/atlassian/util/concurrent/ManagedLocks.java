/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.util.concurrent;

import com.atlassian.util.concurrent.Assertions;
import com.atlassian.util.concurrent.Function;
import com.atlassian.util.concurrent.Functions;
import com.atlassian.util.concurrent.ManagedLock;
import com.atlassian.util.concurrent.NotNull;
import com.atlassian.util.concurrent.Supplier;
import com.atlassian.util.concurrent.WeakMemoizer;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ManagedLocks {
    @NotNull
    public static ManagedLock manage(@NotNull Lock lock) {
        return new ManagedLockImpl(lock);
    }

    @NotNull
    public static ManagedLock newManagedLock() {
        return ManagedLocks.manage(new ReentrantLock());
    }

    @NotNull
    public static ManagedLock.ReadWrite manageReadWrite(@NotNull ReadWriteLock lock) {
        return new ReadWriteManagedLock(lock);
    }

    @NotNull
    public static <T, D> Function<T, ManagedLock> weakManagedLockFactory(@NotNull Function<T, D> stripeFunction, @NotNull Supplier<Lock> lockFactory) {
        Function lockFunction = Functions.fromSupplier(ManagedLocks.managedLockFactory(lockFactory));
        return ManagedFactory.managedFactory(WeakMemoizer.weakMemoizer(lockFunction), stripeFunction);
    }

    @NotNull
    public static <T, D> Function<T, ManagedLock> weakManagedLockFactory(@NotNull Function<T, D> stripeFunction) {
        return ManagedLocks.weakManagedLockFactory(stripeFunction, ManagedLocks.lockFactory());
    }

    @NotNull
    public static <T> Function<T, ManagedLock> weakManagedLockFactory() {
        return ManagedLocks.weakManagedLockFactory(Functions.identity());
    }

    @NotNull
    public static <T, D> Function<T, ManagedLock.ReadWrite> weakReadWriteManagedLockFactory(final @NotNull Function<T, D> stripeFunction, @NotNull Supplier<ReadWriteLock> lockFactory) {
        Assertions.notNull("stripeFunction", stripeFunction);
        Function readWriteManagedLockFactory = Functions.fromSupplier(ManagedLocks.managedReadWriteLockFactory(lockFactory));
        final WeakMemoizer locks = WeakMemoizer.weakMemoizer(readWriteManagedLockFactory);
        return new Function<T, ManagedLock.ReadWrite>(){

            @Override
            public ManagedLock.ReadWrite get(T input) {
                return (ManagedLock.ReadWrite)locks.get(stripeFunction.get(input));
            }
        };
    }

    @NotNull
    public static <T, D> Function<T, ManagedLock.ReadWrite> weakReadWriteManagedLockFactory(Function<T, D> stripeFunction) {
        return ManagedLocks.weakReadWriteManagedLockFactory(stripeFunction, ManagedLocks.readWriteLockFactory());
    }

    @NotNull
    public static <T> Function<T, ManagedLock.ReadWrite> weakReadWriteManagedLockFactory() {
        return ManagedLocks.weakReadWriteManagedLockFactory(Functions.identity());
    }

    @NotNull
    static Supplier<Lock> lockFactory() {
        return new Supplier<Lock>(){

            @Override
            public Lock get() {
                return new ReentrantLock();
            }
        };
    }

    @NotNull
    static Supplier<ReadWriteLock> readWriteLockFactory() {
        return new Supplier<ReadWriteLock>(){

            @Override
            public ReadWriteLock get() {
                return new ReentrantReadWriteLock();
            }
        };
    }

    @NotNull
    static Supplier<ManagedLock> managedLockFactory(final @NotNull Supplier<Lock> supplier) {
        Assertions.notNull("supplier", supplier);
        return new Supplier<ManagedLock>(){

            @Override
            public ManagedLock get() {
                return new ManagedLockImpl((Lock)supplier.get());
            }
        };
    }

    @NotNull
    static Supplier<ManagedLock.ReadWrite> managedReadWriteLockFactory(final @NotNull Supplier<ReadWriteLock> supplier) {
        Assertions.notNull("supplier", supplier);
        return new Supplier<ManagedLock.ReadWrite>(){

            @Override
            public ManagedLock.ReadWrite get() {
                return new ReadWriteManagedLock((ReadWriteLock)supplier.get());
            }
        };
    }

    private ManagedLocks() {
        throw new AssertionError((Object)"cannot instantiate!");
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    static class ManagedLockImpl
    implements ManagedLock {
        private final Lock lock;

        ManagedLockImpl(@NotNull Lock lock) {
            this.lock = Assertions.notNull("lock", lock);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public <R> R withLock(Supplier<R> supplier) {
            this.lock.lock();
            try {
                R r = supplier.get();
                return r;
            }
            finally {
                this.lock.unlock();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public <R> R withLock(Callable<R> callable) throws Exception {
            this.lock.lock();
            try {
                R r = callable.call();
                return r;
            }
            finally {
                this.lock.unlock();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void withLock(Runnable runnable) {
            this.lock.lock();
            try {
                runnable.run();
            }
            finally {
                this.lock.unlock();
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    static class ManagedFactory<T, D>
    implements Function<T, ManagedLock> {
        private final Function<D, ManagedLock> lockResolver;
        private final Function<T, D> stripeFunction;

        static final <T, D> ManagedFactory<T, D> managedFactory(Function<D, ManagedLock> lockResolver, Function<T, D> stripeFunction) {
            return new ManagedFactory<T, D>(lockResolver, stripeFunction);
        }

        ManagedFactory(Function<D, ManagedLock> lockResolver, Function<T, D> stripeFunction) {
            this.lockResolver = Assertions.notNull("lockResolver", lockResolver);
            this.stripeFunction = Assertions.notNull("stripeFunction", stripeFunction);
        }

        @Override
        public ManagedLock get(T descriptor) {
            return this.lockResolver.get(this.stripeFunction.get(descriptor));
        }
    }

    static class ReadWriteManagedLock
    implements ManagedLock.ReadWrite {
        private final ManagedLock read;
        private final ManagedLock write;

        ReadWriteManagedLock(ReadWriteLock lock) {
            Assertions.notNull("lock", lock);
            this.read = new ManagedLockImpl(lock.readLock());
            this.write = new ManagedLockImpl(lock.writeLock());
        }

        public ManagedLock read() {
            return this.read;
        }

        public ManagedLock write() {
            return this.write;
        }
    }
}

