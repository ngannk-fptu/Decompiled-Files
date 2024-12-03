/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.ConfigurationException
 *  com.atlassian.config.bootstrap.BootstrapException
 *  com.atlassian.config.db.DatabaseDetails
 *  com.atlassian.config.setup.SetupPersister
 */
package com.atlassian.confluence.impl.setup;

import com.atlassian.config.ConfigurationException;
import com.atlassian.config.bootstrap.BootstrapException;
import com.atlassian.config.db.DatabaseDetails;
import com.atlassian.config.setup.SetupPersister;
import com.atlassian.confluence.impl.setup.BootstrapConfigurer;
import com.atlassian.confluence.setup.BootstrapManager;
import java.sql.Connection;
import java.util.Objects;

public final class DelegatingBootstrapConfigurer
implements BootstrapConfigurer {
    private final BootstrapManager bootstrapManager;

    public DelegatingBootstrapConfigurer(BootstrapManager bootstrapManager) {
        this.bootstrapManager = Objects.requireNonNull(bootstrapManager);
    }

    @Override
    public void setConfluenceHome(String confluenceHome) throws ConfigurationException {
        this.bootstrapManager.setConfluenceHome(confluenceHome);
    }

    @Override
    public String getWebAppContextPath() {
        return this.bootstrapManager.getWebAppContextPath();
    }

    @Override
    public void setWebAppContextPath(String webAppContextPath) throws ConfigurationException {
        this.bootstrapManager.setWebAppContextPath(webAppContextPath);
    }

    @Override
    public boolean isWebAppContextPathSet() {
        return this.bootstrapManager.isWebAppContextPathSet();
    }

    @Override
    public void checkConfigurationOnStartup() throws BootstrapException {
        this.bootstrapManager.checkConfigurationOnStartup();
    }

    @Override
    public void cleanupOnShutdown() {
        this.bootstrapManager.cleanupOnShutdown();
    }

    @Override
    public void init() throws BootstrapException {
        this.bootstrapManager.init();
    }

    @Override
    public void publishConfiguration() {
        this.bootstrapManager.publishConfiguration();
    }

    @Override
    public void setProperty(String key, Object value) {
        this.bootstrapManager.setProperty(key, value);
    }

    @Override
    public void setSetupComplete(boolean complete) {
        this.bootstrapManager.setSetupComplete(complete);
    }

    @Override
    public void setBuildNumber(String buildNumber) {
        this.bootstrapManager.setBuildNumber(buildNumber);
    }

    @Override
    public void bootstrapDatasource(String datasourceName, String hibernateDialect) throws BootstrapException {
        this.bootstrapManager.bootstrapDatasource(datasourceName, hibernateDialect);
    }

    @Override
    public void bootstrapDatabase(DatabaseDetails dbDetails, boolean embedded) throws BootstrapException {
        this.bootstrapManager.bootstrapDatabase(dbDetails, embedded);
    }

    @Override
    public SetupPersister getSetupPersister() {
        return this.bootstrapManager.getSetupPersister();
    }

    @Override
    public boolean isBootstrapped() {
        return this.bootstrapManager.isBootstrapped();
    }

    @Override
    public void save() throws ConfigurationException {
        this.bootstrapManager.save();
    }

    @Override
    public Connection getTestDatasourceConnection(String datasourceName) throws BootstrapException {
        return this.bootstrapManager.getTestDatasourceConnection(datasourceName);
    }

    @Override
    public boolean isApplicationHomeValid() {
        return this.bootstrapManager.isApplicationHomeValid();
    }
}

