/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicSpi
 *  javax.annotation.Nullable
 */
package com.atlassian.activeobjects.spi;

import com.atlassian.activeobjects.spi.DatabaseType;
import com.atlassian.annotations.PublicSpi;
import javax.annotation.Nullable;
import javax.sql.DataSource;

@PublicSpi
public interface DataSourceProvider {
    public DataSource getDataSource();

    public DatabaseType getDatabaseType();

    @Nullable
    public String getSchema();
}

