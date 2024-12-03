/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.config.bootstrap;

import com.atlassian.config.ApplicationConfiguration;
import com.atlassian.config.ConfigurationException;
import com.atlassian.config.bootstrap.BootstrapException;
import com.atlassian.config.db.DatabaseDetails;
import com.atlassian.config.db.HibernateConfig;
import com.atlassian.config.db.HibernateConfigurator;
import com.atlassian.config.setup.SetupPersister;
import java.sql.Connection;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;

public interface AtlassianBootstrapManager {
    public boolean isBootstrapped();

    public Object getProperty(String var1);

    public void setProperty(String var1, Object var2);

    public boolean isPropertyTrue(String var1);

    public void removeProperty(String var1);

    public String getString(String var1);

    public String getFilePathProperty(String var1);

    public Collection getPropertyKeys();

    public Map getPropertiesWithPrefix(String var1);

    public String getBuildNumber();

    public void setBuildNumber(String var1);

    public boolean isApplicationHomeValid();

    public Properties getHibernateProperties();

    public void save() throws ConfigurationException;

    public boolean isSetupComplete();

    public void setSetupComplete(boolean var1);

    public String getOperation();

    public void setOperation(String var1);

    public void bootstrapDatasource(String var1, String var2) throws BootstrapException;

    public SetupPersister getSetupPersister();

    public ApplicationConfiguration getApplicationConfig();

    public String getApplicationHome();

    public String getConfiguredApplicationHome();

    public String getBootstrapFailureReason();

    public void init() throws BootstrapException;

    public void publishConfiguration();

    public void bootstrapDatabase(DatabaseDetails var1, boolean var2) throws BootstrapException;

    public HibernateConfigurator getHibernateConfigurator();

    public void setHibernateConfigurator(HibernateConfigurator var1);

    public HibernateConfig getHibernateConfig();

    public Connection getTestDatasourceConnection(String var1) throws BootstrapException;

    public boolean databaseContainsExistingData(Connection var1);

    public Connection getTestDatabaseConnection(DatabaseDetails var1) throws BootstrapException;
}

