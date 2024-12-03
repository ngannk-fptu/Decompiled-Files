/*
 * Decompiled with CFR 0.152.
 */
package io.micrometer.core.instrument.simple;

import io.micrometer.core.instrument.config.MeterRegistryConfig;
import io.micrometer.core.instrument.config.MeterRegistryConfigValidator;
import io.micrometer.core.instrument.config.validate.PropertyValidator;
import io.micrometer.core.instrument.config.validate.Validated;
import io.micrometer.core.instrument.simple.CountingMode;
import java.time.Duration;

public interface SimpleConfig
extends MeterRegistryConfig {
    public static final SimpleConfig DEFAULT = k -> null;

    @Override
    default public String prefix() {
        return "simple";
    }

    default public Duration step() {
        return PropertyValidator.getDuration(this, "step").orElse(Duration.ofMinutes(1L));
    }

    default public CountingMode mode() {
        return PropertyValidator.getEnum(this, CountingMode.class, "mode").orElse(CountingMode.CUMULATIVE);
    }

    @Override
    default public Validated<?> validate() {
        return MeterRegistryConfigValidator.checkAll(this, MeterRegistryConfigValidator.check("step", SimpleConfig::step), MeterRegistryConfigValidator.check("mode", SimpleConfig::mode));
    }
}

