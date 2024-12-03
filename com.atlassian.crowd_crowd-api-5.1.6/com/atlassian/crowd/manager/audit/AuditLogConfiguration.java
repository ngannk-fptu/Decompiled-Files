/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.manager.audit;

import com.atlassian.crowd.manager.audit.RetentionPeriod;
import java.util.Objects;

public class AuditLogConfiguration {
    private final RetentionPeriod retentionPeriod;

    public AuditLogConfiguration(RetentionPeriod retentionPeriod) {
        this.retentionPeriod = retentionPeriod;
    }

    public static AuditLogConfiguration defaultConfiguration() {
        return new AuditLogConfiguration(RetentionPeriod.SIX_MONTHS);
    }

    public RetentionPeriod getRetentionPeriod() {
        return this.retentionPeriod;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AuditLogConfiguration that = (AuditLogConfiguration)o;
        return this.retentionPeriod == that.retentionPeriod;
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.retentionPeriod});
    }
}

