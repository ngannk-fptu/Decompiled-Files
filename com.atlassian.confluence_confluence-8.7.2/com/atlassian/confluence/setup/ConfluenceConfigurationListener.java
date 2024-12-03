/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.ApplicationConfiguration
 *  com.atlassian.config.ConfigurationException
 *  com.atlassian.config.bootstrap.AtlassianBootstrapManager
 *  com.atlassian.config.bootstrap.BootstrapException
 *  com.atlassian.config.util.BootstrapUtils
 *  com.atlassian.threadlocal.BruteForceThreadLocalCleanup
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletContextEvent
 *  javax.servlet.ServletContextListener
 *  org.apache.commons.lang3.exception.ExceptionUtils
 *  org.apache.log4j.Level
 *  org.apache.log4j.LogManager
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.slf4j.bridge.SLF4JBridgeHandler
 *  org.springframework.context.ApplicationContext
 *  org.springframework.context.ApplicationEvent
 */
package com.atlassian.confluence.setup;

import com.atlassian.config.ApplicationConfiguration;
import com.atlassian.config.ConfigurationException;
import com.atlassian.config.bootstrap.AtlassianBootstrapManager;
import com.atlassian.config.bootstrap.BootstrapException;
import com.atlassian.config.util.BootstrapUtils;
import com.atlassian.confluence.internal.health.JohnsonEventLevel;
import com.atlassian.confluence.internal.health.JohnsonEventPredicates;
import com.atlassian.confluence.internal.health.JohnsonEventType;
import com.atlassian.confluence.license.LicenseService;
import com.atlassian.confluence.license.exception.LicenseException;
import com.atlassian.confluence.setup.BootstrapAppConfig;
import com.atlassian.confluence.setup.BootstrapContextInitialisedEvent;
import com.atlassian.confluence.setup.BuildInformation;
import com.atlassian.confluence.setup.ConfluenceAnnotationConfigApplicationContext;
import com.atlassian.confluence.setup.SetupContext;
import com.atlassian.confluence.setup.actions.ConfluenceSetupPersister;
import com.atlassian.confluence.setup.johnson.JohnsonUtils;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.threadlocal.BruteForceThreadLocalCleanup;
import java.lang.management.ManagementFactory;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;

public class ConfluenceConfigurationListener
implements ServletContextListener {
    private static final Logger log = LoggerFactory.getLogger(ConfluenceConfigurationListener.class);
    private static final Logger startupLog = LoggerFactory.getLogger((String)"com.atlassian.confluence.lifecycle");

    public void contextInitialized(ServletContextEvent event) {
        try {
            this.setStartupTime(event.getServletContext());
            startupLog.info("Starting Confluence " + BuildInformation.INSTANCE);
            startupLog.info("Using JVM {} {}", (Object)System.getProperty("java.vendor"), (Object)System.getProperty("java.version"));
            for (String jvmArg : ManagementFactory.getRuntimeMXBean().getInputArguments()) {
                startupLog.info("Using JVM argument {}", (Object)jvmArg);
            }
            this.initialiseBootstrapContext(event);
            ConfluenceSetupPersister setupPersister = (ConfluenceSetupPersister)BootstrapUtils.getBootstrapContext().getBean("setupPersister");
            LicenseService licenseService = (LicenseService)BootstrapUtils.getBootstrapContext().getBean("licenseService");
            ApplicationConfiguration applicationConfig = (ApplicationConfiguration)BootstrapUtils.getBootstrapContext().getBean("applicationConfig");
            setupPersister.resetCancelledMigration();
            AtlassianBootstrapManager bootstrapManager = BootstrapUtils.getBootstrapManager();
            if (!bootstrapManager.isSetupComplete()) {
                this.initialiseSetupContext(event);
            } else {
                try {
                    if (licenseService.isLicensedForDataCenter() && !setupPersister.isSetupTypeClustered() && this.isClusterSetupEnabled(applicationConfig)) {
                        this.disableClusterSetup(applicationConfig);
                        this.initialiseSetupContext(event);
                        setupPersister.convertToClusterMigration();
                    } else if (!licenseService.isLicensedForDataCenter() && setupPersister.isSetupTypeClustered()) {
                        this.initialiseSetupContext(event);
                        setupPersister.convertToStandaloneMigration();
                    } else if (!licenseService.isLicensedForDataCenterOrExempt() && !setupPersister.isSetupTypeClustered()) {
                        setupPersister.removeClusterSetupEntries();
                    }
                }
                catch (LicenseException licenseException) {
                    // empty catch block
                }
            }
            this.doStartupPropertyChecks();
        }
        catch (Exception e) {
            if (!JohnsonUtils.eventExists(JohnsonEventPredicates.blocksStartup())) {
                JohnsonUtils.raiseJohnsonEvent(JohnsonEventType.BOOTSTRAP, "Could not load bootstrap from environment", ExceptionUtils.getRootCauseMessage((Throwable)e), JohnsonEventLevel.FATAL);
            }
            log.error("An error was encountered while bootstrapping Confluence (see below): \n" + e.getMessage(), (Throwable)e);
        }
    }

    private void disableClusterSetup(ApplicationConfiguration applicationConfig) {
        applicationConfig.setProperty((Object)"cluster.setup.ready", false);
        try {
            applicationConfig.save();
        }
        catch (ConfigurationException e) {
            log.error("Error setting cluster setup to false.", (Throwable)e);
        }
    }

    private boolean isClusterSetupEnabled(ApplicationConfiguration applicationConfig) {
        Object isClusterSetupEnabled = applicationConfig.getProperty((Object)"cluster.setup.ready");
        return "true".equals(isClusterSetupEnabled);
    }

    private void initialiseSetupContext(ServletContextEvent event) {
        SetupContext.init(event.getServletContext());
    }

    private void doStartupPropertyChecks() {
        if (LogManager.getRootLogger().getLevel().equals((Object)Level.DEBUG) && !"true".equals(System.getProperty("confluence.ignore.debug.logging"))) {
            startupLog.error("***************************************************************************************************************");
            startupLog.error("The root log4j logger is set to DEBUG level. This may cause Confluence to run slowly.");
            startupLog.error("To disable this error message, start your appserver with the system property -Dconfluence.ignore.debug.logging=true");
            startupLog.error("***************************************************************************************************************");
        }
        if (Boolean.getBoolean("confluence.devmode")) {
            startupLog.info("Confluence is starting in DevMode. Developer/debugging options will be enabled");
        }
    }

    private void initialiseBootstrapContext(ServletContextEvent event) throws BootstrapException {
        ConfluenceAnnotationConfigApplicationContext bootstrapContext = new ConfluenceAnnotationConfigApplicationContext(event.getServletContext());
        bootstrapContext.register(new Class[]{BootstrapAppConfig.class});
        bootstrapContext.setAllowBeanDefinitionOverriding(false);
        bootstrapContext.refresh();
        BootstrapUtils.init((ApplicationContext)bootstrapContext, (ServletContext)event.getServletContext());
        BootstrapUtils.getBootstrapContext().publishEvent((ApplicationEvent)new BootstrapContextInitialisedEvent(this));
    }

    public void contextDestroyed(ServletContextEvent event) {
        startupLog.info("Stopping Confluence");
        SLF4JBridgeHandler.uninstall();
        SetupContext.destroy();
        BootstrapUtils.closeContext();
        BruteForceThreadLocalCleanup.cleanUp((ClassLoader)this.getClass().getClassLoader());
    }

    private void setStartupTime(ServletContext context) {
        Long currentTime = System.currentTimeMillis();
        if (context != null) {
            context.setAttribute("confluence_startup_time", (Object)currentTime);
        }
        GeneralUtil.setSystemStartupTime(currentTime);
    }
}

