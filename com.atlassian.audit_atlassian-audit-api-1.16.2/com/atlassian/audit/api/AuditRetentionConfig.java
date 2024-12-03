/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.audit.api;

import java.time.Period;
import java.util.Objects;
import javax.annotation.Nonnull;

public class AuditRetentionConfig {
    public static final Period DEFAULT_RETENTION_PERIOD = Period.ofYears(3);
    private final Period period;

    public AuditRetentionConfig(@Nonnull Period period) {
        this.period = Objects.requireNonNull(period, "period");
    }

    @Nonnull
    public Period getPeriod() {
        return this.period;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AuditRetentionConfig that = (AuditRetentionConfig)o;
        return this.period.equals(that.period);
    }

    public int hashCode() {
        return this.period.hashCode();
    }
}

