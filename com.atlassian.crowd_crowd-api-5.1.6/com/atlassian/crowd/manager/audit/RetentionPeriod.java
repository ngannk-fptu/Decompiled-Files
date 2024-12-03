/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.manager.audit;

import java.time.Period;
import java.util.Optional;
import java.util.stream.Stream;

public enum RetentionPeriod {
    ONE_MONTH(Period.ofMonths(1)),
    THREE_MONTHS(Period.ofMonths(3)),
    SIX_MONTHS(Period.ofMonths(6)),
    UNLIMITED(null);

    private final Period retentionPeriod;

    private RetentionPeriod(Period retentionPeriod) {
        this.retentionPeriod = retentionPeriod;
    }

    public static RetentionPeriod ofMonths(Integer months) {
        if (months == null) {
            return UNLIMITED;
        }
        return Stream.of(RetentionPeriod.values()).filter(v -> v.retentionPeriod.equals(Period.ofMonths(months))).findAny().orElseThrow(() -> new IllegalArgumentException("Invalid number of months: " + months));
    }

    public Optional<Period> get() {
        return Optional.ofNullable(this.retentionPeriod);
    }
}

