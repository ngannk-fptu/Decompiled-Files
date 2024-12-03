/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.config.ApplicationConfiguration
 *  com.atlassian.config.ConfigurationException
 *  com.atlassian.config.bootstrap.AtlassianBootstrapManager
 *  com.atlassian.config.bootstrap.BootstrapException
 *  com.atlassian.config.db.DatabaseDetails
 *  com.atlassian.config.db.HibernateConfig
 *  com.atlassian.config.db.HibernateConfigurator
 *  com.atlassian.config.setup.SetupPersister
 *  com.atlassian.config.util.BootstrapUtils
 *  com.google.common.base.Suppliers
 */
package com.atlassian.confluence.impl.setup;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.config.ApplicationConfiguration;
import com.atlassian.config.ConfigurationException;
import com.atlassian.config.bootstrap.AtlassianBootstrapManager;
import com.atlassian.config.bootstrap.BootstrapException;
import com.atlassian.config.db.DatabaseDetails;
import com.atlassian.config.db.HibernateConfig;
import com.atlassian.config.db.HibernateConfigurator;
import com.atlassian.config.setup.SetupPersister;
import com.atlassian.config.util.BootstrapUtils;
import com.atlassian.confluence.impl.setup.ReadOnlyApplicationConfig;
import com.atlassian.confluence.impl.setup.ReadOnlySetupPersister;
import com.atlassian.confluence.setup.BootstrapManagerInternal;
import com.atlassian.confluence.setup.BootstrapStatusProvider;
import com.atlassian.confluence.setup.BootstrapStatusProviderException;
import com.atlassian.confluence.setup.SharedConfigurationMap;
import com.google.common.base.Suppliers;
import java.io.File;
import java.sql.Connection;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Supplier;

public class BootstrapStatusProviderImpl
implements BootstrapStatusProvider,
BootstrapManagerInternal {
    private static final Supplier<BootstrapStatusProvider> instance = Suppliers.memoize(BootstrapStatusProviderImpl::initialiseBootstrapStatusProvider);
    private final AtlassianBootstrapManager delegate;

    @VisibleForTesting
    BootstrapStatusProviderImpl(AtlassianBootstrapManager bootstrapManager) {
        this.delegate = bootstrapManager;
    }

    public static BootstrapStatusProvider getInstance() {
        return instance.get();
    }

    private static BootstrapStatusProvider initialiseBootstrapStatusProvider() {
        return new BootstrapStatusProviderImpl(BootstrapUtils.getBootstrapManager());
    }

    @Override
    public boolean isSetupComplete() {
        return this.delegate.isSetupComplete();
    }

    @Override
    public boolean isBootstrapped() {
        return this.delegate.isBootstrapped();
    }

    @Override
    public Object getProperty(String key) {
        return this.delegate.getProperty(key);
    }

    @Deprecated
    public void setProperty(String property, Object value) {
        throw new BootstrapStatusProviderException();
    }

    @Override
    public String getHibernateDialect() {
        return this.delegate.getString("hibernate.dialect");
    }

    @Override
    public String getWebAppContextPath() {
        return this.delegate.getString("confluence.webapp.context.path");
    }

    @Override
    public boolean isWebAppContextPathSet() {
        return this.getWebAppContextPath() != null;
    }

    @Override
    @Deprecated
    public File getSharedHome() {
        throw new BootstrapStatusProviderException();
    }

    @Override
    public ApplicationConfiguration getApplicationConfig() {
        return new ReadOnlyApplicationConfig(this.delegate.getApplicationConfig());
    }

    @Override
    public SetupPersister getSetupPersister() {
        return new ReadOnlySetupPersister(this.delegate.getSetupPersister());
    }

    @Deprecated
    public String getString(String key) {
        throw new BootstrapStatusProviderException();
    }

    @Override
    @Deprecated
    public String getConfluenceHome() {
        throw new BootstrapStatusProviderException();
    }

    @Override
    @Deprecated
    public File getLocalHome() {
        throw new BootstrapStatusProviderException();
    }

    @Override
    @Deprecated
    public void setConfluenceHome(String confluenceHome) {
        throw new BootstrapStatusProviderException();
    }

    @Override
    @Deprecated
    public File getConfiguredLocalHome() {
        throw new BootstrapStatusProviderException();
    }

    @Override
    @Deprecated
    public void setWebAppContextPath(String webAppContextPath) throws ConfigurationException {
        throw new BootstrapStatusProviderException();
    }

    @Override
    @Deprecated
    public void checkConfigurationOnStartup() throws BootstrapException {
        throw new BootstrapStatusProviderException();
    }

    @Override
    @Deprecated
    public void cleanupOnShutdown() {
        throw new BootstrapStatusProviderException();
    }

    @Override
    @Deprecated
    public Optional<String> getDataSourceName() {
        throw new BootstrapStatusProviderException();
    }

    @Override
    @Deprecated
    public void bootstrapSharedConfiguration(SharedConfigurationMap sharedConfig) throws BootstrapException {
        throw new BootstrapStatusProviderException();
    }

    @Deprecated
    public boolean isPropertyTrue(String var1) {
        throw new BootstrapStatusProviderException();
    }

    @Deprecated
    public void removeProperty(String var1) {
        throw new BootstrapStatusProviderException();
    }

    @Override
    public String getFilePathProperty(String var1) {
        return this.delegate.getFilePathProperty(var1);
    }

    @Deprecated
    public Collection getPropertyKeys() {
        throw new BootstrapStatusProviderException();
    }

    @Deprecated
    public Map getPropertiesWithPrefix(String var1) {
        throw new BootstrapStatusProviderException();
    }

    @Deprecated
    public String getBuildNumber() {
        throw new BootstrapStatusProviderException();
    }

    @Deprecated
    public void setBuildNumber(String var1) {
        throw new BootstrapStatusProviderException();
    }

    @Deprecated
    public boolean isApplicationHomeValid() {
        throw new BootstrapStatusProviderException();
    }

    @Deprecated
    public Properties getHibernateProperties() {
        throw new BootstrapStatusProviderException();
    }

    @Deprecated
    public void save() throws ConfigurationException {
        throw new BootstrapStatusProviderException();
    }

    @Deprecated
    public void setSetupComplete(boolean var1) {
        throw new BootstrapStatusProviderException();
    }

    @Deprecated
    public String getOperation() {
        throw new BootstrapStatusProviderException();
    }

    @Deprecated
    public void setOperation(String var1) {
        throw new BootstrapStatusProviderException();
    }

    @Deprecated
    public void bootstrapDatasource(String var1, String var2) throws BootstrapException {
        throw new BootstrapStatusProviderException();
    }

    @Override
    public String getApplicationHome() {
        return this.delegate.getApplicationHome();
    }

    @Override
    @Deprecated
    public String getConfiguredApplicationHome() {
        throw new BootstrapStatusProviderException();
    }

    @Deprecated
    public String getBootstrapFailureReason() {
        throw new BootstrapStatusProviderException();
    }

    @Deprecated
    public void init() throws BootstrapException {
        throw new BootstrapStatusProviderException();
    }

    @Deprecated
    public void publishConfiguration() {
        throw new BootstrapStatusProviderException();
    }

    @Deprecated
    public void bootstrapDatabase(DatabaseDetails var1, boolean var2) throws BootstrapException {
        throw new BootstrapStatusProviderException();
    }

    @Deprecated
    public HibernateConfigurator getHibernateConfigurator() {
        throw new BootstrapStatusProviderException();
    }

    @Deprecated
    public void setHibernateConfigurator(HibernateConfigurator var1) {
        throw new BootstrapStatusProviderException();
    }

    @Deprecated
    public HibernateConfig getHibernateConfig() {
        throw new BootstrapStatusProviderException();
    }

    @Deprecated
    public Connection getTestDatasourceConnection(String var1) throws BootstrapException {
        throw new BootstrapStatusProviderException();
    }

    @Override
    public boolean databaseContainsExistingData(Connection var1) {
        return this.delegate.databaseContainsExistingData(var1);
    }

    @Deprecated
    public Connection getTestDatabaseConnection(DatabaseDetails var1) throws BootstrapException {
        throw new BootstrapStatusProviderException();
    }

    @Override
    @Deprecated
    public Optional<DatabaseDetails> getDatabaseDetail(String database) throws ConfigurationException {
        throw new BootstrapStatusProviderException();
    }

    @Override
    @Deprecated
    public boolean performPersistenceUpgrade() {
        throw new BootstrapStatusProviderException();
    }
}

