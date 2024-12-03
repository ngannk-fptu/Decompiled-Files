/*
 * Decompiled with CFR 0.152.
 */
package io.atlassian.util.concurrent;

import io.atlassian.util.concurrent.Expiring;
import io.atlassian.util.concurrent.LazyReference;
import io.atlassian.util.concurrent.ResettableLazyReference;
import io.atlassian.util.concurrent.Timeout;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class Lazy {
    public static <T> Supplier<T> supplier(Supplier<T> factory) {
        return new Strong<T>(factory);
    }

    public static <T> Supplier<T> timeToLive(Supplier<T> factory, long time, TimeUnit unit) {
        return new Expiring<T>(factory, () -> new TimeToLive(Timeout.getNanosTimeout(time, unit)));
    }

    public static <T> Supplier<T> timeToIdle(Supplier<T> factory, long time, TimeUnit unit) {
        return new Expiring<T>(factory, () -> new TimeToIdle(Timeout.timeoutFactory(time, unit, Timeout.TimeSuppliers.NANOS)));
    }

    public static <T> ResettableLazyReference<T> resettable(final Supplier<T> supplier) {
        return new ResettableLazyReference<T>(){

            @Override
            protected T create() throws Exception {
                return supplier.get();
            }
        };
    }

    static final class TimeToIdle
    implements Predicate<Void> {
        private volatile Timeout lastAccess;
        private final Supplier<Timeout> timeout;

        TimeToIdle(Supplier<Timeout> timeout) {
            this.timeout = Objects.requireNonNull(timeout);
            this.lastAccess = timeout.get();
        }

        @Override
        public boolean test(Void input) {
            boolean alive;
            boolean bl = alive = !this.lastAccess.isExpired();
            if (alive) {
                this.lastAccess = this.timeout.get();
            }
            return alive;
        }
    }

    static final class TimeToLive
    implements Predicate<Void> {
        private final Timeout timeout;

        TimeToLive(Timeout timeout) {
            this.timeout = timeout;
        }

        @Override
        public boolean test(Void input) {
            return !this.timeout.isExpired();
        }
    }

    static final class Strong<T>
    extends LazyReference<T> {
        volatile Supplier<T> supplier;

        Strong(Supplier<T> supplier) {
            this.supplier = Objects.requireNonNull(supplier);
        }

        @Override
        protected T create() throws Exception {
            try {
                T t = this.supplier.get();
                return t;
            }
            finally {
                this.supplier = null;
            }
        }
    }
}

