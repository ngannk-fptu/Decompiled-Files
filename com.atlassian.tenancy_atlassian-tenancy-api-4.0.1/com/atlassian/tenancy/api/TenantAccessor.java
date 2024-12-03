/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 */
package com.atlassian.tenancy.api;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.tenancy.api.Tenant;
import com.atlassian.tenancy.api.TenantUnavailableException;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Callable;

@ExperimentalApi
@Deprecated
public interface TenantAccessor {
    @Deprecated
    public Iterable<Tenant> getAvailableTenants();

    @Deprecated
    public <T> T asTenant(Tenant var1, Callable<T> var2) throws TenantUnavailableException, InvocationTargetException;
}

