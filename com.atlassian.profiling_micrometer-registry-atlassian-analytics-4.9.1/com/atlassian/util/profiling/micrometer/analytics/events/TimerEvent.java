/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.core.instrument.Meter
 *  io.micrometer.core.instrument.Timer
 *  javax.annotation.Nonnull
 *  org.apache.commons.lang3.builder.EqualsBuilder
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.util.profiling.micrometer.analytics.events;

import com.atlassian.util.profiling.micrometer.analytics.events.AbstractMeterEvent;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Timer;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class TimerEvent
extends AbstractMeterEvent {
    private final long count;
    private final double max;
    private final double mean;
    private final double total;

    public TimerEvent(@Nonnull Timer timer, @Nonnull TimeUnit unit) {
        super((Meter)timer);
        this.total = timer.totalTime(Objects.requireNonNull(unit));
        this.count = timer.count();
        this.mean = timer.mean(unit);
        this.max = timer.max(unit);
    }

    public long getCount() {
        return this.count;
    }

    public double getMax() {
        return this.max;
    }

    public double getMean() {
        return this.mean;
    }

    public double getTotal() {
        return this.total;
    }

    @Override
    public String getType() {
        return "timer";
    }

    @Override
    public String toString() {
        return new ToStringBuilder((Object)this).appendSuper(super.toString()).append("count", this.count).append("max", this.max).append("mean", this.mean).append("total", this.total).toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        TimerEvent that = (TimerEvent)o;
        return new EqualsBuilder().appendSuper(super.equals(o)).append(this.count, that.count).append(this.max, that.max).append(this.mean, that.mean).append(this.total, that.total).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).appendSuper(super.hashCode()).append(this.count).append(this.max).append(this.mean).append(this.total).toHashCode();
    }
}

