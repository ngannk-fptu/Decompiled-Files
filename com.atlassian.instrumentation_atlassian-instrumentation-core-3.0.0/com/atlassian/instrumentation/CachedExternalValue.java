/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.tenancy.TenancyScope
 *  com.atlassian.annotations.tenancy.TenantAware
 *  io.atlassian.util.concurrent.LazyReference
 *  io.atlassian.util.concurrent.Timeout
 */
package com.atlassian.instrumentation;

import com.atlassian.annotations.tenancy.TenancyScope;
import com.atlassian.annotations.tenancy.TenantAware;
import com.atlassian.instrumentation.ExternalValue;
import com.atlassian.instrumentation.utils.dbc.Assertions;
import io.atlassian.util.concurrent.LazyReference;
import io.atlassian.util.concurrent.Timeout;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public abstract class CachedExternalValue
implements ExternalValue {
    private final long cacheTimeout;
    private final TimeUnit cacheTimeoutUnits;
    private final AtomicReference<ExpiringLongReference> calculatedValue = new AtomicReference();

    protected CachedExternalValue() {
        this(60L, TimeUnit.SECONDS);
    }

    protected CachedExternalValue(long cacheTimeout, TimeUnit cacheTimeoutUnits) {
        Assertions.notNegative("cacheTimeout", cacheTimeout);
        Assertions.notNull("timeUnit", cacheTimeoutUnits);
        this.cacheTimeout = cacheTimeout;
        this.cacheTimeoutUnits = cacheTimeoutUnits;
        this.calculatedValue.set(new ExpiringLongReference());
    }

    protected abstract long computeValue();

    @Override
    public long getValue() {
        ExpiringLongReference value;
        while ((value = this.calculatedValue.get()).isExpired()) {
            this.calculatedValue.compareAndSet(value, new ExpiringLongReference());
        }
        return (Long)value.get();
    }

    @TenantAware(value=TenancyScope.TENANTLESS)
    private class ExpiringLongReference
    extends LazyReference<Long> {
        private final Timeout timeout;

        private ExpiringLongReference() {
            this.timeout = Timeout.getNanosTimeout((long)CachedExternalValue.this.cacheTimeout, (TimeUnit)CachedExternalValue.this.cacheTimeoutUnits);
        }

        boolean isExpired() {
            return this.timeout.isExpired();
        }

        protected Long create() throws Exception {
            return CachedExternalValue.this.computeValue();
        }
    }
}

