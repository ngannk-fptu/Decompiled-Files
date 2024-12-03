/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.spi.DatabaseType
 *  javax.annotation.Nonnull
 */
package com.atlassian.activeobjects.internal;

import com.atlassian.activeobjects.spi.DatabaseType;
import javax.annotation.Nonnull;
import javax.sql.DataSource;
import net.java.ao.DatabaseProvider;

public interface DatabaseProviderFactory {
    @Nonnull
    public DatabaseProvider getDatabaseProvider(DataSource var1, DatabaseType var2, String var3);
}

