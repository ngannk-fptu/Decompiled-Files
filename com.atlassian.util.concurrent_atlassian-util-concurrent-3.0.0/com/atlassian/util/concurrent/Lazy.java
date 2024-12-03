/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Predicate
 */
package com.atlassian.util.concurrent;

import com.atlassian.util.concurrent.Expiring;
import com.atlassian.util.concurrent.LazyReference;
import com.atlassian.util.concurrent.Supplier;
import com.atlassian.util.concurrent.Timeout;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import java.util.concurrent.TimeUnit;

public final class Lazy {
    public static <T> Supplier<T> supplier(Supplier<T> factory) {
        return new Strong<T>(factory);
    }

    public static <T> Supplier<T> timeToLive(Supplier<T> factory, final long time, final TimeUnit unit) {
        return new Expiring<T>(factory, new Supplier<Predicate<Void>>(){

            @Override
            public Predicate<Void> get() {
                return new TimeToLive(Timeout.getNanosTimeout(time, unit));
            }
        });
    }

    public static <T> Supplier<T> timeToIdle(Supplier<T> factory, final long time, final TimeUnit unit) {
        return new Expiring<T>(factory, new Supplier<Predicate<Void>>(){

            @Override
            public Predicate<Void> get() {
                return new TimeToIdle(Timeout.timeoutFactory(time, unit, Timeout.TimeSuppliers.NANOS));
            }
        });
    }

    static final class TimeToIdle
    implements Predicate<Void> {
        private volatile Timeout lastAccess;
        private final Supplier<Timeout> timeout;

        TimeToIdle(Supplier<Timeout> timeout) {
            this.timeout = (Supplier)Preconditions.checkNotNull(timeout);
            this.lastAccess = timeout.get();
        }

        public boolean apply(Void input) {
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

        public boolean apply(Void input) {
            return !this.timeout.isExpired();
        }
    }

    static final class Strong<T>
    extends LazyReference<T> {
        volatile Supplier<T> supplier;

        Strong(Supplier<T> supplier) {
            this.supplier = (Supplier)Preconditions.checkNotNull(supplier);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
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

