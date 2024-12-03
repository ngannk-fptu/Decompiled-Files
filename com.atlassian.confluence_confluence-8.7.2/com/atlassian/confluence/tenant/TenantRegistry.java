/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.tenancy.api.Tenant
 */
package com.atlassian.confluence.tenant;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.tenancy.api.Tenant;

@Deprecated(forRemoval=true)
@ExperimentalApi
public interface TenantRegistry {
    public boolean addTenant(Tenant var1);

    public boolean removeTenant(Tenant var1);

    public boolean isTenantRegistered(Tenant var1);

    public boolean isRegistryVacant();

    default public boolean hasTenant() {
        return !this.isRegistryVacant();
    }
}

