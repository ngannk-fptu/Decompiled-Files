/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.core.instrument.LongTaskTimer
 *  io.micrometer.core.instrument.Meter
 *  javax.annotation.Nonnull
 *  org.apache.commons.lang3.builder.EqualsBuilder
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.util.profiling.micrometer.analytics.events;

import com.atlassian.util.profiling.micrometer.analytics.events.AbstractMeterEvent;
import io.micrometer.core.instrument.LongTaskTimer;
import io.micrometer.core.instrument.Meter;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class LongTaskTimerEvent
extends AbstractMeterEvent {
    private final int activeTasks;
    private final double duration;
    private final double max;

    public LongTaskTimerEvent(@Nonnull LongTaskTimer timer, @Nonnull TimeUnit unit) {
        super((Meter)timer);
        Objects.requireNonNull(unit);
        this.activeTasks = timer.activeTasks();
        this.duration = timer.duration(unit);
        this.max = timer.max(unit);
    }

    public int getActiveTasks() {
        return this.activeTasks;
    }

    public double getDuration() {
        return this.duration;
    }

    public double getMax() {
        return this.max;
    }

    @Override
    public String getType() {
        return "longTaskTimer";
    }

    @Override
    public String toString() {
        return new ToStringBuilder((Object)this).appendSuper(super.toString()).append("activeTasks", this.activeTasks).append("duration", this.duration).append("max", this.max).toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        LongTaskTimerEvent that = (LongTaskTimerEvent)o;
        return new EqualsBuilder().appendSuper(super.equals(o)).append(this.activeTasks, that.activeTasks).append(this.duration, that.duration).append(this.max, that.max).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).appendSuper(super.hashCode()).append(this.activeTasks).append(this.duration).append(this.max).toHashCode();
    }
}

