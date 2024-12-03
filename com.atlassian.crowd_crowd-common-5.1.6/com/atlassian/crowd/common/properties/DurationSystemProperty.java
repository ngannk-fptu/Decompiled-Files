/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.crowd.common.properties;

import com.atlassian.crowd.common.properties.AbstractSystemProperty;
import java.time.Duration;
import java.time.temporal.TemporalUnit;
import javax.annotation.Nonnull;

public class DurationSystemProperty
extends AbstractSystemProperty<Duration> {
    private final TemporalUnit temporalUnit;

    public DurationSystemProperty(@Nonnull String propertyName, @Nonnull TemporalUnit temporalUnit, long defaultValue) {
        super(propertyName, Duration.of(defaultValue, temporalUnit));
        this.temporalUnit = temporalUnit;
    }

    @Override
    public Duration fromString(@Nonnull String stringValue) {
        return Duration.of(Long.parseLong(stringValue), this.temporalUnit);
    }

    @Override
    public void setValue(@Nonnull Duration value) {
        System.setProperty(this.propertyName, String.valueOf(value.toNanos() / this.temporalUnit.getDuration().toNanos()));
    }
}

