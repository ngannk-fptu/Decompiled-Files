/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.tenancy.api.TenantContext
 */
package com.atlassian.activeobjects.spi;

import com.atlassian.activeobjects.spi.DataSourceProvider;
import com.atlassian.activeobjects.spi.DatabaseType;
import com.atlassian.activeobjects.spi.TenantAwareDataSourceProvider;
import com.atlassian.tenancy.api.TenantContext;
import java.util.Objects;
import javax.sql.DataSource;

@Deprecated
public class DataSourceProviderImpl
implements DataSourceProvider {
    private final TenantAwareDataSourceProvider tenantAwareDataSourceProvider;

    public DataSourceProviderImpl(TenantAwareDataSourceProvider tenantAwareDataSourceProvider, TenantContext tenantContext) {
        this.tenantAwareDataSourceProvider = Objects.requireNonNull(tenantAwareDataSourceProvider);
    }

    @Override
    public DataSource getDataSource() {
        return this.tenantAwareDataSourceProvider.getDataSource();
    }

    @Override
    public DatabaseType getDatabaseType() {
        return this.tenantAwareDataSourceProvider.getDatabaseType();
    }

    @Override
    public String getSchema() {
        return this.tenantAwareDataSourceProvider.getSchema();
    }
}

