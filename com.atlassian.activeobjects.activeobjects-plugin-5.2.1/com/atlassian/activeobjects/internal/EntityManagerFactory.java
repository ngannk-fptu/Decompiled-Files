/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.spi.DatabaseType
 */
package com.atlassian.activeobjects.internal;

import com.atlassian.activeobjects.config.ActiveObjectsConfiguration;
import com.atlassian.activeobjects.spi.DatabaseType;
import javax.sql.DataSource;
import net.java.ao.EntityManager;

public interface EntityManagerFactory {
    public EntityManager getEntityManager(DataSource var1, DatabaseType var2, String var3, ActiveObjectsConfiguration var4);
}

