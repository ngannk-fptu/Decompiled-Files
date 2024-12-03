/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.configuration;

import org.apache.axis.AxisProperties;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.EngineConfigurationFactory;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.configuration.FileProvider;
import org.apache.commons.logging.Log;

public class EngineConfigurationFactoryDefault
implements EngineConfigurationFactory {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$configuration$EngineConfigurationFactoryDefault == null ? (class$org$apache$axis$configuration$EngineConfigurationFactoryDefault = EngineConfigurationFactoryDefault.class$("org.apache.axis.configuration.EngineConfigurationFactoryDefault")) : class$org$apache$axis$configuration$EngineConfigurationFactoryDefault).getName());
    public static final String OPTION_CLIENT_CONFIG_FILE = "axis.ClientConfigFile";
    public static final String OPTION_SERVER_CONFIG_FILE = "axis.ServerConfigFile";
    protected static final String CLIENT_CONFIG_FILE = "client-config.wsdd";
    protected static final String SERVER_CONFIG_FILE = "server-config.wsdd";
    protected String clientConfigFile = AxisProperties.getProperty("axis.ClientConfigFile", "client-config.wsdd");
    protected String serverConfigFile = AxisProperties.getProperty("axis.ServerConfigFile", "server-config.wsdd");
    static /* synthetic */ Class class$org$apache$axis$configuration$EngineConfigurationFactoryDefault;

    public static EngineConfigurationFactory newFactory(Object param) {
        if (param != null) {
            return null;
        }
        return new EngineConfigurationFactoryDefault();
    }

    protected EngineConfigurationFactoryDefault() {
    }

    public EngineConfiguration getClientEngineConfig() {
        return new FileProvider(this.clientConfigFile);
    }

    public EngineConfiguration getServerEngineConfig() {
        return new FileProvider(this.serverConfigFile);
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

