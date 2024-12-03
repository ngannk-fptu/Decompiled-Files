/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.oauth2.common.properties;

import com.atlassian.oauth2.common.properties.AbstractSystemProperty;
import com.google.common.base.Preconditions;
import java.time.Duration;
import java.time.temporal.TemporalUnit;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DurationSystemProperty
extends AbstractSystemProperty<Duration> {
    private static final Logger log = LoggerFactory.getLogger(DurationSystemProperty.class);
    private final TemporalUnit temporalUnit;
    private final Long maxAllowedValue;

    public DurationSystemProperty(@Nonnull String propertyName, @Nonnull TemporalUnit temporalUnit, long defaultValue) {
        this(propertyName, temporalUnit, defaultValue, null);
    }

    public DurationSystemProperty(@Nonnull String propertyName, @Nonnull TemporalUnit temporalUnit, long defaultValue, @Nullable Long maxAllowedValue) {
        super(propertyName, Duration.of(defaultValue, temporalUnit));
        Preconditions.checkArgument((maxAllowedValue == null || maxAllowedValue >= defaultValue ? 1 : 0) != 0, (Object)("Max allowed value [" + maxAllowedValue + "] must be null or equal to or above default value [" + defaultValue + "]"));
        this.temporalUnit = temporalUnit;
        this.maxAllowedValue = maxAllowedValue;
    }

    @Override
    @Nonnull
    public Duration getValue() {
        return Optional.ofNullable(System.getProperty(this.propertyName)).flatMap(this::tryParseToLong).flatMap(this::checkMaxValueBound).map(value -> Duration.of(value, this.temporalUnit)).orElse((Duration)this.defaultValue);
    }

    private Optional<Long> tryParseToLong(String systemPropValue) {
        try {
            return Optional.of(Long.parseLong(systemPropValue));
        }
        catch (NumberFormatException e) {
            log.warn("System property [" + this.propertyName + "] was not in the expected number format", (Throwable)e);
            return Optional.empty();
        }
    }

    private Optional<Long> checkMaxValueBound(Long systemPropValue) {
        if (this.isWithinMaxValueBound(systemPropValue)) {
            return Optional.of(systemPropValue);
        }
        log.warn("Using [" + this.maxAllowedValue + "] as system property cannot be greater than [" + systemPropValue + "]");
        return Optional.empty();
    }

    private boolean isWithinMaxValueBound(long systemPropValue) {
        return this.maxAllowedValue == null || systemPropValue <= this.maxAllowedValue;
    }

    @Override
    public void setValue(@Nonnull Duration value) {
        System.setProperty(this.propertyName, String.valueOf(value.toNanos() / this.temporalUnit.getDuration().toNanos()));
    }
}

