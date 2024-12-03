/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2;

import java.util.HashMap;
import org.apache.commons.configuration2.MapConfiguration;

public class EnvironmentConfiguration
extends MapConfiguration {
    public EnvironmentConfiguration() {
        super(new HashMap<String, String>(System.getenv()));
    }

    @Override
    protected void addPropertyDirect(String key, Object value) {
        throw new UnsupportedOperationException("EnvironmentConfiguration is read-only!");
    }

    @Override
    protected void clearPropertyDirect(String key) {
        throw new UnsupportedOperationException("EnvironmentConfiguration is read-only!");
    }

    @Override
    protected void clearInternal() {
        throw new UnsupportedOperationException("EnvironmentConfiguration is read-only!");
    }
}

