/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.Immutable
 */
package io.atlassian.util.concurrent;

import io.atlassian.util.concurrent.Awaitable;
import io.atlassian.util.concurrent.RuntimeTimeoutException;
import io.atlassian.util.concurrent.TimedOutException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;
import net.jcip.annotations.Immutable;

@Immutable
public final class Timeout {
    private final long created;
    private final long timeoutPeriod;
    private final TimeSupplier supplier;

    public static Timeout getNanosTimeout(long time, TimeUnit unit) {
        return new Timeout(time, unit, TimeSuppliers.NANOS);
    }

    public static Timeout getMillisTimeout(long time, TimeUnit unit) {
        return new Timeout(time, unit, TimeSuppliers.MILLIS);
    }

    public static Supplier<Timeout> timeoutFactory(long time, TimeUnit unit, TimeSupplier supplier) {
        return () -> new Timeout(time, unit, supplier);
    }

    Timeout(long time, TimeUnit unit, TimeSupplier supplier) {
        this.created = supplier.currentTime();
        this.supplier = supplier;
        this.timeoutPeriod = this.supplier.precision().convert(time, unit);
    }

    public long getTime() {
        return this.created + this.timeoutPeriod - this.supplier.currentTime();
    }

    public TimeUnit getUnit() {
        return this.supplier.precision();
    }

    public boolean isExpired() {
        return this.getTime() <= 0L;
    }

    public long getTimeoutPeriod() {
        return this.timeoutPeriod;
    }

    void await(Awaitable waitable) throws TimeoutException, InterruptedException {
        if (!waitable.await(this.getTime(), this.getUnit())) {
            this.throwTimeoutException();
        }
    }

    public void throwTimeoutException() throws TimedOutException {
        throw new TimedOutException(this.timeoutPeriod, this.getUnit());
    }

    public RuntimeTimeoutException getTimeoutException() {
        return new RuntimeTimeoutException(this.timeoutPeriod, this.getUnit());
    }

    static enum TimeSuppliers implements TimeSupplier
    {
        NANOS{

            @Override
            public long currentTime() {
                return System.nanoTime();
            }

            @Override
            public TimeUnit precision() {
                return TimeUnit.NANOSECONDS;
            }
        }
        ,
        MILLIS{

            @Override
            public long currentTime() {
                return System.currentTimeMillis();
            }

            @Override
            public TimeUnit precision() {
                return TimeUnit.MILLISECONDS;
            }
        };

    }

    public static interface TimeSupplier {
        public long currentTime();

        public TimeUnit precision();
    }
}

