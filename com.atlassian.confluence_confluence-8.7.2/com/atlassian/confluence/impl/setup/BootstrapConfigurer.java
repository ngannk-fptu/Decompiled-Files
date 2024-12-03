/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.ConfigurationException
 *  com.atlassian.config.bootstrap.BootstrapException
 *  com.atlassian.config.db.DatabaseDetails
 *  com.atlassian.config.setup.SetupPersister
 *  com.atlassian.config.util.BootstrapUtils
 */
package com.atlassian.confluence.impl.setup;

import com.atlassian.config.ConfigurationException;
import com.atlassian.config.bootstrap.BootstrapException;
import com.atlassian.config.db.DatabaseDetails;
import com.atlassian.config.setup.SetupPersister;
import com.atlassian.config.util.BootstrapUtils;
import com.atlassian.confluence.impl.setup.DelegatingBootstrapConfigurer;
import com.atlassian.confluence.setup.BootstrapManager;
import java.sql.Connection;

public interface BootstrapConfigurer {
    public void setConfluenceHome(String var1) throws ConfigurationException;

    public String getWebAppContextPath();

    public void setWebAppContextPath(String var1) throws ConfigurationException;

    public boolean isWebAppContextPathSet();

    public void checkConfigurationOnStartup() throws BootstrapException;

    public void cleanupOnShutdown();

    public void init() throws BootstrapException;

    public void publishConfiguration();

    public void setProperty(String var1, Object var2);

    public void setSetupComplete(boolean var1);

    public void setBuildNumber(String var1);

    public void bootstrapDatasource(String var1, String var2) throws BootstrapException;

    public void bootstrapDatabase(DatabaseDetails var1, boolean var2) throws BootstrapException;

    public SetupPersister getSetupPersister();

    public boolean isBootstrapped();

    public void save() throws ConfigurationException;

    public Connection getTestDatasourceConnection(String var1) throws BootstrapException;

    public boolean isApplicationHomeValid();

    public static BootstrapConfigurer getBootstrapConfigurer() {
        return new DelegatingBootstrapConfigurer((BootstrapManager)BootstrapUtils.getBootstrapManager());
    }
}

