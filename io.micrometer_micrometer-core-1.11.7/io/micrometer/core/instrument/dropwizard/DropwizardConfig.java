/*
 * Decompiled with CFR 0.152.
 */
package io.micrometer.core.instrument.dropwizard;

import io.micrometer.core.instrument.config.MeterRegistryConfig;
import io.micrometer.core.instrument.config.MeterRegistryConfigValidator;
import io.micrometer.core.instrument.config.validate.PropertyValidator;
import io.micrometer.core.instrument.config.validate.Validated;
import java.time.Duration;

public interface DropwizardConfig
extends MeterRegistryConfig {
    default public Duration step() {
        return PropertyValidator.getDuration(this, "step").orElse(Duration.ofMinutes(1L));
    }

    @Override
    default public Validated<?> validate() {
        return DropwizardConfig.validate(this);
    }

    public static Validated<?> validate(DropwizardConfig config) {
        return MeterRegistryConfigValidator.checkAll(config, MeterRegistryConfigValidator.check("step", DropwizardConfig::step));
    }
}

