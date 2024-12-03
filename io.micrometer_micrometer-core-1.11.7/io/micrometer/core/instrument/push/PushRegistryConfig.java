/*
 * Decompiled with CFR 0.152.
 */
package io.micrometer.core.instrument.push;

import io.micrometer.core.instrument.config.MeterRegistryConfig;
import io.micrometer.core.instrument.config.MeterRegistryConfigValidator;
import io.micrometer.core.instrument.config.validate.PropertyValidator;
import io.micrometer.core.instrument.config.validate.Validated;
import java.time.Duration;

public interface PushRegistryConfig
extends MeterRegistryConfig {
    default public Duration step() {
        return PropertyValidator.getDuration(this, "step").orElse(Duration.ofMinutes(1L));
    }

    default public boolean enabled() {
        return PropertyValidator.getBoolean(this, "enabled").orElse(true);
    }

    @Deprecated
    default public int numThreads() {
        return PropertyValidator.getInteger(this, "numThreads").orElse(2);
    }

    @Deprecated
    default public Duration connectTimeout() {
        return PropertyValidator.getDuration(this, "connectTimeout").orElse(Duration.ofSeconds(1L));
    }

    @Deprecated
    default public Duration readTimeout() {
        return PropertyValidator.getDuration(this, "readTimeout").orElse(Duration.ofSeconds(10L));
    }

    default public int batchSize() {
        return PropertyValidator.getInteger(this, "batchSize").orElse(10000);
    }

    @Override
    default public Validated<?> validate() {
        return PushRegistryConfig.validate(this);
    }

    public static Validated<?> validate(PushRegistryConfig config) {
        return MeterRegistryConfigValidator.checkAll(config, MeterRegistryConfigValidator.check("step", PushRegistryConfig::step), MeterRegistryConfigValidator.check("connectTimeout", PushRegistryConfig::connectTimeout), MeterRegistryConfigValidator.check("readTimeout", PushRegistryConfig::readTimeout), MeterRegistryConfigValidator.check("batchSize", PushRegistryConfig::batchSize), MeterRegistryConfigValidator.check("numThreads", PushRegistryConfig::numThreads));
    }
}

