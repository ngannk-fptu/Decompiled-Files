/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.tenancy.api.Tenant
 *  com.atlassian.tenancy.api.TenantAccessor
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.activeobjects.spi;

import com.atlassian.activeobjects.spi.CompatibilityTenantContext;
import com.atlassian.tenancy.api.Tenant;
import com.atlassian.tenancy.api.TenantAccessor;
import java.util.Objects;
import java.util.stream.StreamSupport;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Deprecated
public class CompatibilityTenantContextImpl
implements CompatibilityTenantContext {
    private final TenantAccessor tenantAccessor;

    public CompatibilityTenantContextImpl(@Nonnull TenantAccessor tenantAccessor) {
        this.tenantAccessor = Objects.requireNonNull(tenantAccessor);
    }

    @Nullable
    public Tenant getCurrentTenant() {
        return StreamSupport.stream(this.tenantAccessor.getAvailableTenants().spliterator(), false).findFirst().orElse(null);
    }
}

