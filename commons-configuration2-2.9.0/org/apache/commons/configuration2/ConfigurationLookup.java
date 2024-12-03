/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2;

import org.apache.commons.configuration2.ImmutableConfiguration;
import org.apache.commons.configuration2.interpol.Lookup;

public class ConfigurationLookup
implements Lookup {
    private final ImmutableConfiguration configuration;

    public ConfigurationLookup(ImmutableConfiguration config) {
        if (config == null) {
            throw new IllegalArgumentException("Configuration must not be null!");
        }
        this.configuration = config;
    }

    public ImmutableConfiguration getConfiguration() {
        return this.configuration;
    }

    @Override
    public Object lookup(String variable) {
        return this.getConfiguration().getProperty(variable);
    }
}

