/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  io.micrometer.core.instrument.Meter
 *  javax.annotation.Nonnull
 *  org.apache.commons.lang3.builder.EqualsBuilder
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.util.profiling.micrometer.analytics.events;

import com.atlassian.analytics.api.annotations.EventName;
import io.micrometer.core.instrument.Meter;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public abstract class AbstractMeterEvent {
    private final String name;
    private final Map<String, String> tags;

    public AbstractMeterEvent(@Nonnull Meter meter) {
        this.name = Objects.requireNonNull(meter).getId().getName();
        this.tags = meter.getId().getTags().stream().filter(t -> !Objects.equals("atl-analytics", t.getKey())).collect(Collectors.toMap(t -> t.getKey(), t -> t.getValue()));
    }

    public String getName() {
        return this.name;
    }

    @Nonnull
    public Map<String, String> getTags() {
        return this.tags;
    }

    @Nonnull
    public abstract String getType();

    @EventName
    @Nonnull
    public String getAnalyticsName() {
        return "profiling.metric";
    }

    public String toString() {
        return new ToStringBuilder((Object)this).append("name", (Object)this.name).append("tags", this.tags).toString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AbstractMeterEvent that = (AbstractMeterEvent)o;
        return new EqualsBuilder().append((Object)this.name, (Object)that.name).append(this.tags, that.tags).isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder(17, 37).append((Object)this.name).append(this.tags).toHashCode();
    }
}

