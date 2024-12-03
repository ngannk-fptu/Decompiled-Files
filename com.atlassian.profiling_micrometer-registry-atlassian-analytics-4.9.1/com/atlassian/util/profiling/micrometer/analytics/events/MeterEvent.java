/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.core.instrument.Meter
 *  javax.annotation.Nonnull
 *  org.apache.commons.lang3.builder.EqualsBuilder
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.util.profiling.micrometer.analytics.events;

import com.atlassian.util.profiling.micrometer.analytics.events.AbstractMeterEvent;
import io.micrometer.core.instrument.Meter;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class MeterEvent
extends AbstractMeterEvent {
    private Double activeTasks;
    private Double count;
    private Double duration;
    private Double max;
    private Double total;
    private Double unknown;
    private Double value;

    public MeterEvent(@Nonnull Meter meter) {
        super(meter);
        meter.measure().forEach(m -> {
            switch (m.getStatistic()) {
                case MAX: {
                    this.max = m.getValue();
                    break;
                }
                case TOTAL: 
                case TOTAL_TIME: {
                    this.total = m.getValue();
                    break;
                }
                case COUNT: {
                    this.count = m.getValue();
                    break;
                }
                case VALUE: {
                    this.value = m.getValue();
                    break;
                }
                case DURATION: {
                    this.duration = m.getValue();
                    break;
                }
                case ACTIVE_TASKS: {
                    this.activeTasks = m.getValue();
                    break;
                }
                default: {
                    this.unknown = m.getValue();
                }
            }
        });
    }

    public Double getActiveTasks() {
        return this.activeTasks;
    }

    public Double getCount() {
        return this.count;
    }

    public Double getDuration() {
        return this.duration;
    }

    public Double getMax() {
        return this.max;
    }

    public Double getTotal() {
        return this.total;
    }

    public Double getUnknown() {
        return this.unknown;
    }

    public Double getValue() {
        return this.value;
    }

    @Override
    public String getType() {
        return "meter";
    }

    @Override
    public String toString() {
        return new ToStringBuilder((Object)this).appendSuper(super.toString()).append("activeTasks", (Object)this.activeTasks).append("count", (Object)this.count).append("duration", (Object)this.duration).append("max", (Object)this.max).append("total", (Object)this.total).append("unknown", (Object)this.unknown).append("value", (Object)this.value).toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        MeterEvent that = (MeterEvent)o;
        return new EqualsBuilder().appendSuper(super.equals(o)).append((Object)this.activeTasks, (Object)that.activeTasks).append((Object)this.count, (Object)that.count).append((Object)this.duration, (Object)that.duration).append((Object)this.max, (Object)that.max).append((Object)this.total, (Object)that.total).append((Object)this.unknown, (Object)that.unknown).append((Object)this.value, (Object)that.value).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).appendSuper(super.hashCode()).append((Object)this.activeTasks).append((Object)this.count).append((Object)this.duration).append((Object)this.max).append((Object)this.total).append((Object)this.unknown).append((Object)this.value).toHashCode();
    }
}

