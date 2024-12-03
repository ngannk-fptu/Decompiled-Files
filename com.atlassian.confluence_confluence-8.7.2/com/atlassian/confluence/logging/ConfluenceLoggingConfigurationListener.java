/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.util.BootstrapUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.slf4j.bridge.SLF4JBridgeHandler
 *  org.springframework.context.ApplicationEvent
 *  org.springframework.context.ApplicationListener
 *  org.springframework.context.event.ContextRefreshedEvent
 */
package com.atlassian.confluence.logging;

import com.atlassian.config.util.BootstrapUtils;
import com.atlassian.confluence.impl.logging.LogAppenderController;
import com.atlassian.confluence.setup.BootstrapContextInitialisedEvent;
import com.atlassian.confluence.setup.BootstrapManager;
import java.security.AccessControlException;
import java.util.Optional;
import java.util.logging.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

public class ConfluenceLoggingConfigurationListener
implements ApplicationListener {
    private static final Logger log = LoggerFactory.getLogger(ConfluenceLoggingConfigurationListener.class);
    private final BootstrapManager bootstrapManager;
    private String logFileName;
    private String appenderName;

    ConfluenceLoggingConfigurationListener(BootstrapManager bootstrapManager) {
        this.bootstrapManager = bootstrapManager;
    }

    @Deprecated
    public ConfluenceLoggingConfigurationListener() {
        this((BootstrapManager)BootstrapUtils.getBootstrapManager());
    }

    public void setLogFileName(String file) {
        this.logFileName = file;
    }

    public String getLogFileName() {
        return this.logFileName;
    }

    public void setAppenderName(String name) {
        this.appenderName = name;
    }

    public String getAppenderName() {
        return this.appenderName;
    }

    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        if (applicationEvent instanceof ContextRefreshedEvent) {
            this.setupLogLevelForWebResources();
        }
        if (applicationEvent instanceof BootstrapContextInitialisedEvent) {
            assert (BootstrapUtils.getBootstrapManager().isBootstrapped()) : "BootstrapContextInitialisedEvent raised before Bootstrap is complete";
            LogAppenderController.reconfigureAppendersWithLogDirectory(this.bootstrapManager);
            this.initSlf4j();
        }
    }

    private void setupLogLevelForWebResources() {
        Optional<ClassLoader> tomcatClassLoader = this.getTomcatClassLoader();
        if (tomcatClassLoader.isPresent()) {
            try {
                ClassLoader threadClassLoader = Thread.currentThread().getContextClassLoader();
                Thread.currentThread().setContextClassLoader(tomcatClassLoader.get());
                java.util.logging.Logger cacheLogger = java.util.logging.Logger.getLogger("org.apache.catalina.webresources.Cache");
                cacheLogger.setLevel(Level.SEVERE);
                Thread.currentThread().setContextClassLoader(threadClassLoader);
            }
            catch (AccessControlException e) {
                log.warn(e.getMessage());
            }
        }
    }

    private Optional<ClassLoader> getTomcatClassLoader() {
        try {
            return Optional.of(Class.forName("org.apache.catalina.webresources.Cache").getClassLoader());
        }
        catch (ClassNotFoundException e) {
            return Optional.empty();
        }
    }

    private void initSlf4j() {
        if (!SLF4JBridgeHandler.isInstalled()) {
            SLF4JBridgeHandler.install();
        }
    }
}

