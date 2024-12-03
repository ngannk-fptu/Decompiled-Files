/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicSpi
 *  com.atlassian.tenancy.api.Tenant
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.activeobjects.spi;

import com.atlassian.activeobjects.spi.DataSourceProvider;
import com.atlassian.activeobjects.spi.DatabaseType;
import com.atlassian.annotations.PublicSpi;
import com.atlassian.tenancy.api.Tenant;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.sql.DataSource;

@ParametersAreNonnullByDefault
@Deprecated
@PublicSpi
public interface TenantAwareDataSourceProvider
extends DataSourceProvider {
    @Nonnull
    @Deprecated
    public DataSource getDataSource(Tenant var1);

    @Nonnull
    @Deprecated
    public DatabaseType getDatabaseType(Tenant var1);

    @Nullable
    @Deprecated
    public String getSchema(Tenant var1);

    @Override
    default public String getSchema() {
        return this.getSchema(null);
    }

    @Override
    default public DatabaseType getDatabaseType() {
        return this.getDatabaseType(null);
    }

    @Override
    default public DataSource getDataSource() {
        return this.getDataSource(null);
    }
}

