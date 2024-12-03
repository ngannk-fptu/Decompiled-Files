/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  javax.annotation.Nonnull
 */
package com.atlassian.tenancy.api;

import com.atlassian.annotations.PublicApi;
import com.atlassian.tenancy.api.Tenant;
import javax.annotation.Nonnull;

@Deprecated
@PublicApi
public interface TenantContext {
    @Nonnull
    @Deprecated
    public Tenant getCurrentTenant();
}

