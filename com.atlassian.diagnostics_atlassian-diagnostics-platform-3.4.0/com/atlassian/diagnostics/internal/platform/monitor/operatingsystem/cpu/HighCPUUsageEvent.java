/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.diagnostics.internal.platform.monitor.operatingsystem.cpu;

import java.util.Objects;

public class HighCPUUsageEvent {
    private final double percentage;

    public HighCPUUsageEvent(double percentage) {
        this.percentage = percentage;
    }

    public double getPercentage() {
        return this.percentage;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        HighCPUUsageEvent that = (HighCPUUsageEvent)o;
        return Double.compare(that.percentage, this.percentage) == 0;
    }

    public int hashCode() {
        return Objects.hash(this.percentage);
    }
}

