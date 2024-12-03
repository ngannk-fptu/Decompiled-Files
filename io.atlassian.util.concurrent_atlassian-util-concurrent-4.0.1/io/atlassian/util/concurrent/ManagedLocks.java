/*
 * Decompiled with CFR 0.152.
 */
package io.atlassian.util.concurrent;

import io.atlassian.util.concurrent.Functions;
import io.atlassian.util.concurrent.ManagedLock;
import io.atlassian.util.concurrent.NotNull;
import io.atlassian.util.concurrent.WeakMemoizer;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;
import java.util.function.Supplier;

public class ManagedLocks {
    @NotNull
    static final Supplier<Lock> lockFactory = ReentrantLock::new;
    @NotNull
    static Supplier<ReadWriteLock> readWriteLockFactory = ReentrantReadWriteLock::new;

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
        return ManagedLocks.weakManagedLockFactory(stripeFunction, lockFactory);
    }

    @NotNull
    public static <T> Function<T, ManagedLock> weakManagedLockFactory() {
        return ManagedLocks.weakManagedLockFactory(Function.identity());
    }

    @NotNull
    public static <T, D> Function<T, ManagedLock.ReadWrite> weakReadWriteManagedLockFactory(@NotNull Function<T, D> stripeFunction, @NotNull Supplier<ReadWriteLock> lockFactory) {
        Objects.requireNonNull(stripeFunction, "stripeFunction");
        Function readWriteManagedLockFactory = Functions.fromSupplier(ManagedLocks.managedReadWriteLockFactory(lockFactory));
        WeakMemoizer locks = WeakMemoizer.weakMemoizer(readWriteManagedLockFactory);
        return input -> (ManagedLock.ReadWrite)locks.apply(stripeFunction.apply(input));
    }

    @NotNull
    public static <T, D> Function<T, ManagedLock.ReadWrite> weakReadWriteManagedLockFactory(Function<T, D> stripeFunction) {
        return ManagedLocks.weakReadWriteManagedLockFactory(stripeFunction, readWriteLockFactory);
    }

    @NotNull
    public static <T> Function<T, ManagedLock.ReadWrite> weakReadWriteManagedLockFactory() {
        return ManagedLocks.weakReadWriteManagedLockFactory(Function.identity());
    }

    @NotNull
    static Supplier<ManagedLock> managedLockFactory(@NotNull Supplier<Lock> supplier) {
        Objects.requireNonNull(supplier, "supplier");
        return () -> new ManagedLockImpl((Lock)supplier.get());
    }

    @NotNull
    static Supplier<ManagedLock.ReadWrite> managedReadWriteLockFactory(@NotNull Supplier<ReadWriteLock> supplier) {
        Objects.requireNonNull(supplier, "supplier");
        return () -> new ReadWriteManagedLock((ReadWriteLock)supplier.get());
    }

    private ManagedLocks() {
        throw new AssertionError((Object)"cannot instantiate!");
    }

    static class ManagedLockImpl
    implements ManagedLock {
        private final Lock lock;

        ManagedLockImpl(@NotNull Lock lock) {
            this.lock = Objects.requireNonNull(lock, "lock");
        }

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

    static class ManagedFactory<T, D>
    implements Function<T, ManagedLock> {
        private final Function<D, ManagedLock> lockResolver;
        private final Function<T, D> stripeFunction;

        static <T, D> ManagedFactory<T, D> managedFactory(Function<D, ManagedLock> lockResolver, Function<T, D> stripeFunction) {
            return new ManagedFactory<T, D>(lockResolver, stripeFunction);
        }

        ManagedFactory(Function<D, ManagedLock> lockResolver, Function<T, D> stripeFunction) {
            this.lockResolver = Objects.requireNonNull(lockResolver, "lockResolver");
            this.stripeFunction = Objects.requireNonNull(stripeFunction, "stripeFunction");
        }

        @Override
        public ManagedLock apply(T descriptor) {
            return this.lockResolver.apply(this.stripeFunction.apply(descriptor));
        }
    }

    static class ReadWriteManagedLock
    implements ManagedLock.ReadWrite {
        private final ManagedLock read;
        private final ManagedLock write;

        ReadWriteManagedLock(ReadWriteLock lock) {
            Objects.requireNonNull(lock, "lock");
            this.read = new ManagedLockImpl(lock.readLock());
            this.write = new ManagedLockImpl(lock.writeLock());
        }

        @Override
        public ManagedLock read() {
            return this.read;
        }

        @Override
        public ManagedLock write() {
            return this.write;
        }
    }
}

