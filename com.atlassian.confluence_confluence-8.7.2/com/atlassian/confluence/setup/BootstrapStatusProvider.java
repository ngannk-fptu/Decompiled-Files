/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.ApplicationConfiguration
 *  com.atlassian.config.setup.SetupPersister
 */
package com.atlassian.confluence.setup;

import com.atlassian.config.ApplicationConfiguration;
import com.atlassian.config.setup.SetupPersister;
import java.sql.Connection;

public interface BootstrapStatusProvider {
    public boolean isSetupComplete();

    public boolean isBootstrapped();

    public Object getProperty(String var1);

    public String getHibernateDialect();

    public String getWebAppContextPath();

    public boolean isWebAppContextPathSet();

    public ApplicationConfiguration getApplicationConfig();

    public SetupPersister getSetupPersister();

    public String getApplicationHome();

    public String getFilePathProperty(String var1);

    public boolean databaseContainsExistingData(Connection var1);
}

