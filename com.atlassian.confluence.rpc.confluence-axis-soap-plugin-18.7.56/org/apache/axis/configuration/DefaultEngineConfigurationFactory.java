/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.configuration;

import org.apache.axis.EngineConfiguration;
import org.apache.axis.EngineConfigurationFactory;
import org.apache.axis.configuration.EngineConfigurationFactoryFinder;

public class DefaultEngineConfigurationFactory
implements EngineConfigurationFactory {
    protected final EngineConfigurationFactory factory;

    protected DefaultEngineConfigurationFactory(EngineConfigurationFactory factory) {
        this.factory = factory;
    }

    public DefaultEngineConfigurationFactory() {
        this(EngineConfigurationFactoryFinder.newFactory());
    }

    public EngineConfiguration getClientEngineConfig() {
        return this.factory == null ? null : this.factory.getClientEngineConfig();
    }

    public EngineConfiguration getServerEngineConfig() {
        return this.factory == null ? null : this.factory.getServerEngineConfig();
    }
}

