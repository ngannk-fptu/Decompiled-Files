/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  io.micrometer.core.instrument.step.StepRegistryConfig
 */
package com.atlassian.util.profiling.micrometer.analytics;

import com.atlassian.annotations.Internal;
import io.micrometer.core.instrument.step.StepRegistryConfig;

@Internal
public interface AnalyticsRegistryConfig
extends StepRegistryConfig {
    public static final AnalyticsRegistryConfig DEFAULT = System::getProperty;

    default public String prefix() {
        return "profiling.analytics";
    }

    default public boolean logInactive() {
        String v = this.get(this.prefix() + ".sendInactive");
        return Boolean.parseBoolean(v);
    }
}

