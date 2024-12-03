/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ObjectUtils
 */
package org.springframework.data.geo;

import java.io.Serializable;
import org.springframework.data.domain.Range;
import org.springframework.data.geo.Metric;
import org.springframework.data.geo.Metrics;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

public final class Distance
implements Serializable,
Comparable<Distance> {
    private static final long serialVersionUID = 2460886201934027744L;
    private final double value;
    private final Metric metric;

    public Distance(double value) {
        this(value, Metrics.NEUTRAL);
    }

    public Distance(double value, Metric metric) {
        Assert.notNull((Object)metric, (String)"Metric must not be null!");
        this.value = value;
        this.metric = metric;
    }

    public static Range<Distance> between(Distance min, Distance max) {
        return Range.from(Range.Bound.inclusive(min)).to(Range.Bound.inclusive(max));
    }

    public static Range<Distance> between(double minValue, Metric minMetric, double maxValue, Metric maxMetric) {
        return Distance.between(new Distance(minValue, minMetric), new Distance(maxValue, maxMetric));
    }

    public double getNormalizedValue() {
        return this.value / this.metric.getMultiplier();
    }

    public String getUnit() {
        return this.metric.getAbbreviation();
    }

    public Distance add(Distance other) {
        Assert.notNull((Object)other, (String)"Distance to add must not be null!");
        double newNormalizedValue = this.getNormalizedValue() + other.getNormalizedValue();
        return new Distance(newNormalizedValue * this.metric.getMultiplier(), this.metric);
    }

    public Distance add(Distance other, Metric metric) {
        Assert.notNull((Object)other, (String)"Distance to must not be null!");
        Assert.notNull((Object)metric, (String)"Result metric must not be null!");
        double newLeft = this.getNormalizedValue() * metric.getMultiplier();
        double newRight = other.getNormalizedValue() * metric.getMultiplier();
        return new Distance(newLeft + newRight, metric);
    }

    public Distance in(Metric metric) {
        Assert.notNull((Object)metric, (String)"Metric must not be null!");
        return this.metric.equals(metric) ? this : new Distance(this.getNormalizedValue() * metric.getMultiplier(), metric);
    }

    @Override
    public int compareTo(@Nullable Distance that) {
        if (that == null) {
            return 1;
        }
        double difference = this.getNormalizedValue() - that.getNormalizedValue();
        return difference == 0.0 ? 0 : (difference > 0.0 ? 1 : -1);
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(this.value);
        if (this.metric != Metrics.NEUTRAL) {
            builder.append(" ").append(this.metric.toString());
        }
        return builder.toString();
    }

    public double getValue() {
        return this.value;
    }

    public Metric getMetric() {
        return this.metric;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Distance)) {
            return false;
        }
        Distance distance = (Distance)o;
        if (this.value != distance.value) {
            return false;
        }
        return ObjectUtils.nullSafeEquals((Object)this.metric, (Object)distance.metric);
    }

    public int hashCode() {
        long temp = Double.doubleToLongBits(this.value);
        int result = (int)(temp ^ temp >>> 32);
        result = 31 * result + ObjectUtils.nullSafeHashCode((Object)this.metric);
        return result;
    }
}

