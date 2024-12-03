/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.ConfigurationException
 *  com.atlassian.config.bootstrap.AtlassianBootstrapManager
 *  com.atlassian.config.bootstrap.BootstrapException
 */
package com.atlassian.confluence.setup;

import com.atlassian.config.ConfigurationException;
import com.atlassian.config.bootstrap.AtlassianBootstrapManager;
import com.atlassian.config.bootstrap.BootstrapException;
import com.atlassian.confluence.setup.ConfluenceBootstrapConstants;
import com.atlassian.confluence.setup.SharedConfigurationMap;
import java.io.File;
import java.util.Optional;

public interface BootstrapManager
extends AtlassianBootstrapManager,
ConfluenceBootstrapConstants {
    public static final String JWT_PRIVATE_KEY = "jwt.private.key";
    public static final String JWT_PUBLIC_KEY = "jwt.public.key";
    public static final String JWT_KEY_LENGTH = "jwt.key.length";
    public static final String SYNCHRONY_SERVICE_AUTHTOKEN = "synchrony.service.authtoken";

    @Deprecated
    public String getConfluenceHome();

    @Deprecated
    public String getApplicationHome();

    @Deprecated
    public File getSharedHome();

    @Deprecated
    public File getLocalHome();

    @Deprecated
    public void setConfluenceHome(String var1) throws ConfigurationException;

    @Deprecated
    public File getConfiguredLocalHome();

    @Deprecated
    public String getConfiguredApplicationHome();

    @Deprecated
    public String getWebAppContextPath();

    @Deprecated
    public void setWebAppContextPath(String var1) throws ConfigurationException;

    @Deprecated
    public boolean isWebAppContextPathSet();

    public void checkConfigurationOnStartup() throws BootstrapException;

    public void cleanupOnShutdown();

    public Optional<String> getDataSourceName();

    public String getHibernateDialect();

    @Deprecated
    public void bootstrapSharedConfiguration(SharedConfigurationMap var1) throws BootstrapException;

    @Deprecated
    public String getFilePathProperty(String var1);
}

