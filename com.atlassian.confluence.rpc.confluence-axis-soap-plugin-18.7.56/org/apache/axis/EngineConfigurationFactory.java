/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis;

import org.apache.axis.EngineConfiguration;

public interface EngineConfigurationFactory {
    public static final String SYSTEM_PROPERTY_NAME = "axis.EngineConfigFactory";

    public EngineConfiguration getClientEngineConfig();

    public EngineConfiguration getServerEngineConfig();
}

