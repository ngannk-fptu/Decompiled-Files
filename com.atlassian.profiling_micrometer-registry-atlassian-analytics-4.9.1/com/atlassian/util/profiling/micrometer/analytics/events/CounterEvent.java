/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.core.instrument.Counter
 *  io.micrometer.core.instrument.FunctionCounter
 *  io.micrometer.core.instrument.Meter
 *  javax.annotation.Nonnull
 *  org.apache.commons.lang3.builder.EqualsBuilder
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.util.profiling.micrometer.analytics.events;

import com.atlassian.util.profiling.micrometer.analytics.events.AbstractMeterEvent;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.FunctionCounter;
import io.micrometer.core.instrument.Meter;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class CounterEvent
extends AbstractMeterEvent {
    private final double count;

    public CounterEvent(@Nonnull Counter counter) {
        super((Meter)counter);
        this.count = counter.count();
    }

    public CounterEvent(FunctionCounter counter) {
        super((Meter)counter);
        this.count = counter.count();
    }

    public double getCount() {
        return this.count;
    }

    @Override
    public String getType() {
        return "counter";
    }

    @Override
    public String toString() {
        return new ToStringBuilder((Object)this).appendSuper(super.toString()).append("count", this.count).toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        CounterEvent that = (CounterEvent)o;
        return new EqualsBuilder().appendSuper(super.equals(o)).append(this.count, that.count).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).appendSuper(super.hashCode()).append(this.count).toHashCode();
    }
}

