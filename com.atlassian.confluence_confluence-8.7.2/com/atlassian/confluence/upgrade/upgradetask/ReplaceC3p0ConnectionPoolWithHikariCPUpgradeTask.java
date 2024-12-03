/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.ApplicationConfig
 *  com.atlassian.confluence.impl.hibernate.DelegatingHikariConnectionProvider
 *  com.atlassian.confluence.upgrade.AbstractUpgradeTask
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.upgrade.upgradetask;

import com.atlassian.config.ApplicationConfig;
import com.atlassian.confluence.impl.hibernate.DelegatingHikariConnectionProvider;
import com.atlassian.confluence.upgrade.AbstractUpgradeTask;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReplaceC3p0ConnectionPoolWithHikariCPUpgradeTask
extends AbstractUpgradeTask {
    private static final Logger log = LoggerFactory.getLogger(ReplaceC3p0ConnectionPoolWithHikariCPUpgradeTask.class);
    private static final String BUILD_NUMBER = "8803";
    static final String[] PREDEFINED_CONNECTION_POOL_PROPERTIES = new String[]{"hibernate.connection.provider_class", "hibernate.connection.datasource", "hibernate.dbcp.maxActive", "hibernate.proxool.xml"};
    private final ApplicationConfig applicationConfig;

    public ReplaceC3p0ConnectionPoolWithHikariCPUpgradeTask(ApplicationConfig applicationConfig) {
        this.applicationConfig = applicationConfig;
    }

    public String getBuildNumber() {
        return BUILD_NUMBER;
    }

    public boolean runOnSpaceImport() {
        return false;
    }

    public boolean breaksBackwardCompatibility() {
        return false;
    }

    public String getShortDescription() {
        return "Removes config for C3p0 connection pool provider for hibernate and replaces it with HikariCP by replacing config in confluence.cfg.xml (CONFSRVDEV-20791)";
    }

    public void doUpgrade() throws Exception {
        try {
            for (String predefinedConnectionPoolProperty : PREDEFINED_CONNECTION_POOL_PROPERTIES) {
                if (!this.hasValidProperty(predefinedConnectionPoolProperty)) continue;
                log.info("{} is already configured, we don't want to force switch to Hikari in this case.", (Object)predefinedConnectionPoolProperty);
                return;
            }
            int idleTimeout = this.hasValidProperty("hibernate.c3p0.timeout") ? this.applicationConfig.getIntegerProperty((Object)"hibernate.c3p0.timeout") * 1000 : 30000;
            this.setProperty("hibernate.hikari.idleTimeout", String.valueOf(idleTimeout));
            this.setProperty("hibernate.hikari.maximumPoolSize", this.getProperty("hibernate.c3p0.max_size", "60"));
            this.setProperty("hibernate.hikari.minimumIdle", this.getProperty("hibernate.c3p0.min_size", "20"));
            this.setProperty("hibernate.hikari.registerMbeans", "true");
            this.setProperty("spring.datasource.hikari.registerMbeans", "true");
            this.setProperty("hibernate.connection.autocommit", "false");
            this.setProperty("hibernate.connection.provider_class", DelegatingHikariConnectionProvider.class.getName());
            this.applicationConfig.save();
            log.info("Successfully saved applicationConfig.");
        }
        catch (Exception ex) {
            log.error("Unable to update Connection pool provider to use HikariCP", (Throwable)ex);
            throw ex;
        }
    }

    private boolean hasValidProperty(String propName) {
        Object val = this.applicationConfig.getProperty((Object)propName);
        return val != null && (!(val instanceof String) || !StringUtils.isBlank((CharSequence)((String)val)));
    }

    private String getProperty(String propName, String defaultVal) {
        return this.hasValidProperty(propName) ? this.applicationConfig.getProperty((Object)propName).toString() : defaultVal;
    }

    private void setProperty(String newName, Object value) {
        this.setProperty(null, newName, value);
    }

    private void setProperty(String oldName, String newName, Object value) {
        if (this.hasValidProperty(newName)) {
            log.warn("Config already has a defined setting for this key: {}={}. Not resetting it to {}.", new Object[]{newName, this.applicationConfig.getProperty((Object)newName), value});
            return;
        }
        if (oldName != null) {
            log.warn("Inferring {} from existing property {}={}", new Object[]{newName, oldName, value});
        } else {
            log.warn("Setting new config property {}={}", (Object)newName, value);
        }
        this.applicationConfig.setProperty((Object)newName, value);
    }
}

