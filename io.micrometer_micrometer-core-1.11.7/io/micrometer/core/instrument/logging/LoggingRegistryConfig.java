/*
 * Decompiled with CFR 0.152.
 */
package io.micrometer.core.instrument.logging;

import io.micrometer.core.instrument.step.StepRegistryConfig;

public interface LoggingRegistryConfig
extends StepRegistryConfig {
    public static final LoggingRegistryConfig DEFAULT = k -> null;

    @Override
    default public String prefix() {
        return "logging";
    }

    default public boolean logInactive() {
        String v = this.get(this.prefix() + ".logInactive");
        return Boolean.parseBoolean(v);
    }
}

