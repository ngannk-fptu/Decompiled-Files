/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.jmx.export.metadata;

import org.springframework.jmx.export.metadata.AbstractJmxAttribute;
import org.springframework.jmx.support.MetricType;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class ManagedMetric
extends AbstractJmxAttribute {
    @Nullable
    private String category;
    @Nullable
    private String displayName;
    private MetricType metricType = MetricType.GAUGE;
    private int persistPeriod = -1;
    @Nullable
    private String persistPolicy;
    @Nullable
    private String unit;

    public void setCategory(@Nullable String category) {
        this.category = category;
    }

    @Nullable
    public String getCategory() {
        return this.category;
    }

    public void setDisplayName(@Nullable String displayName) {
        this.displayName = displayName;
    }

    @Nullable
    public String getDisplayName() {
        return this.displayName;
    }

    public void setMetricType(MetricType metricType) {
        Assert.notNull((Object)((Object)metricType), (String)"MetricType must not be null");
        this.metricType = metricType;
    }

    public MetricType getMetricType() {
        return this.metricType;
    }

    public void setPersistPeriod(int persistPeriod) {
        this.persistPeriod = persistPeriod;
    }

    public int getPersistPeriod() {
        return this.persistPeriod;
    }

    public void setPersistPolicy(@Nullable String persistPolicy) {
        this.persistPolicy = persistPolicy;
    }

    @Nullable
    public String getPersistPolicy() {
        return this.persistPolicy;
    }

    public void setUnit(@Nullable String unit) {
        this.unit = unit;
    }

    @Nullable
    public String getUnit() {
        return this.unit;
    }
}

