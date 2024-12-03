/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.bootstrap.AtlassianBootstrapManager
 *  com.atlassian.config.util.BootstrapUtils
 *  com.atlassian.confluence.upgrade.AmpsOverridesManager
 *  com.atlassian.confluence.upgrade.UpgradeError
 *  com.atlassian.confluence.upgrade.UpgradeException
 *  com.atlassian.confluence.upgrade.UpgradeManager
 *  com.atlassian.johnson.Johnson
 *  com.atlassian.johnson.JohnsonEventContainer
 *  com.atlassian.plugin.SplitStartupPluginSystemLifecycle
 *  com.atlassian.spring.container.ComponentNotFoundException
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.util.profiling.Ticker
 *  com.google.common.annotations.VisibleForTesting
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletContextEvent
 *  javax.servlet.ServletContextListener
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugin;

import com.atlassian.config.bootstrap.AtlassianBootstrapManager;
import com.atlassian.config.util.BootstrapUtils;
import com.atlassian.confluence.internal.health.JohnsonEventPredicates;
import com.atlassian.confluence.internal.health.JohnsonEventType;
import com.atlassian.confluence.setup.DefaultSetupPersister;
import com.atlassian.confluence.setup.johnson.JohnsonUtils;
import com.atlassian.confluence.tenant.SystemTenant;
import com.atlassian.confluence.upgrade.AmpsOverridesManager;
import com.atlassian.confluence.upgrade.UpgradeError;
import com.atlassian.confluence.upgrade.UpgradeException;
import com.atlassian.confluence.upgrade.UpgradeGate;
import com.atlassian.confluence.upgrade.UpgradeManager;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.profiling.TimedAnalytics;
import com.atlassian.johnson.Johnson;
import com.atlassian.johnson.JohnsonEventContainer;
import com.atlassian.plugin.SplitStartupPluginSystemLifecycle;
import com.atlassian.spring.container.ComponentNotFoundException;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.util.profiling.Ticker;
import com.google.common.annotations.VisibleForTesting;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PluginFrameworkContextListener
implements ServletContextListener {
    private static final Logger log = LoggerFactory.getLogger(PluginFrameworkContextListener.class);

    public void contextInitialized(ServletContextEvent servletContextEvent) {
        log.debug("PluginFrameworkContextListener contextInitialized called");
        if (!this.isDatabaseConfigured()) {
            log.info("Database is not yet configured. Not starting full plugin system.");
            return;
        }
        if (this.isMigration()) {
            log.info("Confluence is being migrated. Not starting full plugin system.");
            if (this.isSpringContainerSetup()) {
                this.getSystemTenant().arrived();
            }
            return;
        }
        if (this.hasUpgradeErrors()) {
            log.warn("Not starting full plugin system due to upgrade");
            return;
        }
        try (Ticker t = TimedAnalytics.timedAnalytics().start("confluence.profiling.startup.plugin-system");){
            log.info("Initialising plugin system");
            SplitStartupPluginSystemLifecycle pluginSystemLifecycle = this.getPluginSystemLifecycle();
            pluginSystemLifecycle.earlyStartup();
            this.getSystemTenant().arrived();
            this.runAmpsOverrides();
            this.launchUpgrades(servletContextEvent);
            pluginSystemLifecycle.lateStartup();
            log.debug("PluginFrameworkContextListener contextInitialized completing successfully");
        }
        catch (Exception e) {
            log.error("Error initialising plugin manager: " + e.getMessage(), (Throwable)e);
        }
    }

    private void runAmpsOverrides() {
        if (!GeneralUtil.isSetupComplete()) {
            log.debug("Overrides not needed as server has not been set up yet");
            return;
        }
        if (this.hasStartupError()) {
            log.debug("Overrides not attempted as Confluence cannot start up");
            return;
        }
        log.debug("Running AMPS overrides");
        AmpsOverridesManager ampsOverridesManager = (AmpsOverridesManager)ContainerManager.getComponent((String)"ampsOverridesManager");
        ampsOverridesManager.doOverride();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void launchUpgrades(ServletContextEvent servletContextEvent) {
        log.debug("PluginFrameworkContextListener#launchUpgrades called");
        if (this.hasLicenseInconsistencyError()) {
            log.debug("Upgrades not attempted due to expired license");
            return;
        }
        if (this.hasLicenseIncompatibleError()) {
            log.debug("Upgrades not attempted due to incompatible license");
            return;
        }
        if (this.hasStartupError()) {
            log.debug("Upgrades not attempted as Confluence cannot start up");
            return;
        }
        UpgradeManager upgradeManager = (UpgradeManager)ContainerManager.getComponent((String)"upgradeManager");
        JohnsonEventContainer agentJohnson = Johnson.getEventContainer((ServletContext)servletContextEvent.getServletContext());
        try (Ticker t = TimedAnalytics.timedAnalytics().start("confluence.profiling.startup.upgrade");){
            upgradeManager.upgrade(agentJohnson);
        }
        catch (UpgradeException e) {
            try {
                log.error("Upgrade failed, application will not start: " + e.getMessage(), (Throwable)e);
                ((UpgradeGate)ContainerManager.getComponent((String)"upgradeGate")).setPluginDependentUpgradeComplete(false);
            }
            catch (Throwable throwable) {
                List errors = upgradeManager.getErrors();
                if (errors != null && !errors.isEmpty()) {
                    log.error("{} errors were encountered during upgrade:", (Object)errors.size());
                    int i = 1;
                    for (UpgradeError error : errors) {
                        log.error("{}: {}", (Object)i++, (Object)(error.getError() != null ? error.getError().getMessage() : error.getMessage()));
                    }
                }
                throw throwable;
            }
            List errors = upgradeManager.getErrors();
            if (errors != null && !errors.isEmpty()) {
                log.error("{} errors were encountered during upgrade:", (Object)errors.size());
                int i = 1;
                for (UpgradeError error : errors) {
                    log.error("{}: {}", (Object)i++, (Object)(error.getError() != null ? error.getError().getMessage() : error.getMessage()));
                }
            }
        }
        List errors = upgradeManager.getErrors();
        if (errors != null && !errors.isEmpty()) {
            log.error("{} errors were encountered during upgrade:", (Object)errors.size());
            int i = 1;
            for (UpgradeError error : errors) {
                log.error("{}: {}", (Object)i++, (Object)(error.getError() != null ? error.getError().getMessage() : error.getMessage()));
            }
        }
        log.debug("PluginFrameworkContextListener#launchUpgrades completed successfully");
    }

    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        try {
            if (!Boolean.getBoolean("skip.plugin.system.shutdown")) {
                SplitStartupPluginSystemLifecycle pluginSystemLifecycle = this.getPluginSystemLifecycle();
                log.info("ServletContext about to be shut down; shutting down plugin framework");
                pluginSystemLifecycle.shutdown();
            } else {
                log.info("Skipping Plugin System Shutdown due to skip.plugin.system.shutdown sysprop.");
            }
        }
        catch (ComponentNotFoundException ex) {
            log.warn("No Plugin System found during ServletContext shutdown");
        }
        catch (IllegalStateException ex) {
            log.warn("Failed to shut down plugin system during ServletContext shutdown: {}", (Object)ex.getMessage());
        }
    }

    private boolean isDatabaseConfigured() {
        AtlassianBootstrapManager bootstrapManager = BootstrapUtils.getBootstrapManager();
        return bootstrapManager != null && bootstrapManager.getHibernateConfig() != null && bootstrapManager.getHibernateConfig().isHibernateSetup();
    }

    private boolean isMigration() {
        String setupType = BootstrapUtils.getBootstrapManager().getApplicationConfig().getSetupType();
        return DefaultSetupPersister.MIGRATION_SETUP_TYPES.contains(setupType);
    }

    @VisibleForTesting
    boolean hasUpgradeErrors() {
        return JohnsonUtils.eventExists(JohnsonEventPredicates.blocksStartupButNotLicenseEvents());
    }

    @VisibleForTesting
    boolean hasStartupError() {
        return JohnsonUtils.eventExists(JohnsonEventPredicates.hasType(JohnsonEventType.STARTUP));
    }

    @VisibleForTesting
    boolean hasLicenseInconsistencyError() {
        return JohnsonUtils.eventExists(JohnsonEventPredicates.hasType(JohnsonEventType.LICENSE_INCONSISTENCY));
    }

    @VisibleForTesting
    boolean hasLicenseIncompatibleError() {
        return JohnsonUtils.eventExists(JohnsonEventPredicates.hasType(JohnsonEventType.LICENSE_INCOMPATIBLE));
    }

    private boolean isSpringContainerSetup() {
        return ContainerManager.isContainerSetup();
    }

    private SplitStartupPluginSystemLifecycle getPluginSystemLifecycle() {
        return (SplitStartupPluginSystemLifecycle)ContainerManager.getComponent((String)"pluginManager");
    }

    private SystemTenant getSystemTenant() {
        return (SystemTenant)ContainerManager.getComponent((String)"systemTenant");
    }
}

