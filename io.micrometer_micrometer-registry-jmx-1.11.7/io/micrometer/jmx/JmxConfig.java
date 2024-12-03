/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.core.instrument.dropwizard.DropwizardConfig
 */
package io.micrometer.jmx;

import io.micrometer.core.instrument.dropwizard.DropwizardConfig;

public interface JmxConfig
extends DropwizardConfig {
    public static final JmxConfig DEFAULT = k -> null;

    default public String prefix() {
        return "jmx";
    }

    default public String domain() {
        return "metrics";
    }
}

