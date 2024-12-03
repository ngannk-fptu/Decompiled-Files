/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.tenancy.TenancyScope
 *  com.atlassian.annotations.tenancy.TenantAware
 *  io.atlassian.util.concurrent.LazyReference
 *  io.atlassian.util.concurrent.Timeout
 */
package com.atlassian.instrumentation.operations;

import com.atlassian.annotations.tenancy.TenancyScope;
import com.atlassian.annotations.tenancy.TenantAware;
import com.atlassian.instrumentation.operations.ExternalOpValue;
import com.atlassian.instrumentation.operations.OpSnapshot;
import com.atlassian.instrumentation.utils.dbc.Assertions;
import io.atlassian.util.concurrent.LazyReference;
import io.atlassian.util.concurrent.Timeout;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public abstract class CachedExternalOpValue
implements ExternalOpValue {
    private final long cacheTimeout;
    private final TimeUnit cacheTimeoutUnits;
    private final AtomicReference<ExpiringOpSnapshotReference> calculatedValue = new AtomicReference();

    protected CachedExternalOpValue() {
        this(60L, TimeUnit.SECONDS);
    }

    protected CachedExternalOpValue(long cacheTimeout, TimeUnit cacheTimeoutUnits) {
        Assertions.notNegative("cacheTimeout", cacheTimeout);
        Assertions.notNull("timeUnit", cacheTimeoutUnits);
        this.cacheTimeout = cacheTimeout;
        this.cacheTimeoutUnits = cacheTimeoutUnits;
        this.calculatedValue.set(new ExpiringOpSnapshotReference());
    }

    protected abstract OpSnapshot computeValue();

    @Override
    public OpSnapshot getSnapshot() {
        ExpiringOpSnapshotReference value;
        while ((value = this.calculatedValue.get()).isExpired()) {
            this.calculatedValue.compareAndSet(value, new ExpiringOpSnapshotReference());
        }
        return (OpSnapshot)value.get();
    }

    @TenantAware(value=TenancyScope.TENANTLESS)
    private class ExpiringOpSnapshotReference
    extends LazyReference<OpSnapshot> {
        private final Timeout timeout;

        private ExpiringOpSnapshotReference() {
            this.timeout = Timeout.getNanosTimeout((long)CachedExternalOpValue.this.cacheTimeout, (TimeUnit)CachedExternalOpValue.this.cacheTimeoutUnits);
        }

        boolean isExpired() {
            return this.timeout.isExpired();
        }

        protected OpSnapshot create() throws Exception {
            return CachedExternalOpValue.this.computeValue();
        }
    }
}

