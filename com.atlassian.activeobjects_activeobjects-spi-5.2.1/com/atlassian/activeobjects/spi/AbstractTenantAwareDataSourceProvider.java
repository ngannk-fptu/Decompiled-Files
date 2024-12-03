/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.tenancy.api.Tenant
 *  javax.annotation.Nonnull
 */
package com.atlassian.activeobjects.spi;

import com.atlassian.activeobjects.spi.DatabaseType;
import com.atlassian.activeobjects.spi.TenantAwareDataSourceProvider;
import com.atlassian.tenancy.api.Tenant;
import javax.annotation.Nonnull;

@Deprecated
public abstract class AbstractTenantAwareDataSourceProvider
implements TenantAwareDataSourceProvider {
    @Override
    @Nonnull
    public DatabaseType getDatabaseType(@Nonnull Tenant tenant) {
        return DatabaseType.UNKNOWN;
    }

    @Override
    public String getSchema(@Nonnull Tenant tenant) {
        return null;
    }
}

