/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.core.instrument.Gauge
 *  io.micrometer.core.instrument.Meter
 *  io.micrometer.core.instrument.TimeGauge
 *  javax.annotation.Nonnull
 *  org.apache.commons.lang3.builder.EqualsBuilder
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.util.profiling.micrometer.analytics.events;

import com.atlassian.util.profiling.micrometer.analytics.events.AbstractMeterEvent;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.TimeGauge;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class GaugeEvent
extends AbstractMeterEvent {
    private final double value;

    public GaugeEvent(@Nonnull Gauge gauge) {
        super((Meter)gauge);
        this.value = gauge.value();
    }

    public GaugeEvent(TimeGauge gauge, TimeUnit unit) {
        super((Meter)gauge);
        this.value = gauge.value(unit);
    }

    public double getValue() {
        return this.value;
    }

    @Override
    public String getType() {
        return "gauge";
    }

    @Override
    public String toString() {
        return new ToStringBuilder((Object)this).appendSuper(super.toString()).append("value", this.value).toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        GaugeEvent that = (GaugeEvent)o;
        return new EqualsBuilder().appendSuper(super.equals(o)).append(this.value, that.value).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).appendSuper(super.hashCode()).append(this.value).toHashCode();
    }
}

