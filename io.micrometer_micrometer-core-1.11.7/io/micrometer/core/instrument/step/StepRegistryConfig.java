/*
 * Decompiled with CFR 0.152.
 */
package io.micrometer.core.instrument.step;

import io.micrometer.core.instrument.config.validate.Validated;
import io.micrometer.core.instrument.push.PushRegistryConfig;

public interface StepRegistryConfig
extends PushRegistryConfig {
    public static Validated<?> validate(StepRegistryConfig config) {
        return PushRegistryConfig.validate(config);
    }
}

