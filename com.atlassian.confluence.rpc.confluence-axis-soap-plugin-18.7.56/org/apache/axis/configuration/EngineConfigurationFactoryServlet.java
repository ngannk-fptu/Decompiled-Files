/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletConfig
 *  javax.servlet.ServletContext
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.configuration;

import java.io.File;
import java.io.InputStream;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import org.apache.axis.AxisProperties;
import org.apache.axis.ConfigurationException;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.EngineConfigurationFactory;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.configuration.EngineConfigurationFactoryDefault;
import org.apache.axis.configuration.FileProvider;
import org.apache.axis.utils.ClassUtils;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;

public class EngineConfigurationFactoryServlet
extends EngineConfigurationFactoryDefault {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$configuration$EngineConfigurationFactoryServlet == null ? (class$org$apache$axis$configuration$EngineConfigurationFactoryServlet = EngineConfigurationFactoryServlet.class$("org.apache.axis.configuration.EngineConfigurationFactoryServlet")) : class$org$apache$axis$configuration$EngineConfigurationFactoryServlet).getName());
    private ServletConfig cfg;
    static /* synthetic */ Class class$org$apache$axis$configuration$EngineConfigurationFactoryServlet;
    static /* synthetic */ Class class$org$apache$axis$server$AxisServer;

    public static EngineConfigurationFactory newFactory(Object param) {
        return param instanceof ServletConfig ? new EngineConfigurationFactoryServlet((ServletConfig)param) : null;
    }

    protected EngineConfigurationFactoryServlet(ServletConfig conf) {
        this.cfg = conf;
    }

    public EngineConfiguration getServerEngineConfig() {
        return EngineConfigurationFactoryServlet.getServerEngineConfig(this.cfg);
    }

    private static EngineConfiguration getServerEngineConfig(ServletConfig cfg) {
        ServletContext ctx = cfg.getServletContext();
        String configFile = cfg.getInitParameter("axis.ServerConfigFile");
        if (configFile == null) {
            configFile = AxisProperties.getProperty("axis.ServerConfigFile");
        }
        if (configFile == null) {
            configFile = "server-config.wsdd";
        }
        String appWebInfPath = "/WEB-INF";
        FileProvider config = null;
        String realWebInfPath = ctx.getRealPath(appWebInfPath);
        if (realWebInfPath == null || !new File(realWebInfPath, configFile).exists()) {
            String name = appWebInfPath + "/" + configFile;
            InputStream is = ctx.getResourceAsStream(name);
            if (is != null) {
                config = new FileProvider(is);
            }
            if (config == null) {
                log.error((Object)Messages.getMessage("servletEngineWebInfError03", name));
            }
        }
        if (config == null && realWebInfPath != null) {
            try {
                config = new FileProvider(realWebInfPath, configFile);
            }
            catch (ConfigurationException e) {
                log.error((Object)Messages.getMessage("servletEngineWebInfError00"), (Throwable)e);
            }
        }
        if (config == null) {
            log.warn((Object)Messages.getMessage("servletEngineWebInfWarn00"));
            try {
                InputStream is = ClassUtils.getResourceAsStream(class$org$apache$axis$server$AxisServer == null ? (class$org$apache$axis$server$AxisServer = EngineConfigurationFactoryServlet.class$("org.apache.axis.server.AxisServer")) : class$org$apache$axis$server$AxisServer, "server-config.wsdd");
                config = new FileProvider(is);
            }
            catch (Exception e) {
                log.error((Object)Messages.getMessage("servletEngineWebInfError02"), (Throwable)e);
            }
        }
        return config;
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

