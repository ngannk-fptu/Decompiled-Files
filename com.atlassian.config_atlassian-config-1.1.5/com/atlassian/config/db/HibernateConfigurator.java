/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.config.db;

import com.atlassian.config.ConfigurationException;
import com.atlassian.config.db.DatabaseDetails;

public interface HibernateConfigurator {
    public static final String DATABASE_TYPE_EMBEDDED = "embedded";
    public static final String DATABASE_TYPE_STANDARD = "standard";
    public static final String DATABASE_TYPE_DATASOURCE = "datasource";

    public void configureDatabase(DatabaseDetails var1, boolean var2) throws ConfigurationException;

    public void configureDatasource(String var1, String var2) throws ConfigurationException;

    public void unconfigureDatabase();
}

