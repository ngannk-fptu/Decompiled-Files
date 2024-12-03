/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.tenancy.api.Tenant
 *  com.atlassian.tenancy.api.TenantAccessor
 *  com.atlassian.tenancy.api.TenantContext
 *  com.atlassian.tenancy.api.TenantUnavailableException
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.tenant;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.confluence.tenant.TenantRegistry;
import com.atlassian.tenancy.api.Tenant;
import com.atlassian.tenancy.api.TenantAccessor;
import com.atlassian.tenancy.api.TenantContext;
import com.atlassian.tenancy.api.TenantUnavailableException;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
@Deprecated(forRemoval=true)
public class VolatileSingleTenantRegistry
implements TenantRegistry,
TenantAccessor,
TenantContext {
    private static Logger log = LoggerFactory.getLogger(VolatileSingleTenantRegistry.class);
    private static final ThreadLocal<Tenant> tenantContext = new ThreadLocal();
    private final AtomicReference<Tenant> tenant = new AtomicReference();

    public Iterable<Tenant> getAvailableTenants() {
        Tenant tenant = this.tenant.get();
        return tenant == null ? ImmutableList.of() : ImmutableList.of((Object)tenant);
    }

    public <T> T asTenant(Tenant tenant, Callable<T> callback) throws TenantUnavailableException, InvocationTargetException {
        Preconditions.checkNotNull((Object)tenant);
        Preconditions.checkNotNull(callback);
        if (this.isTenantRegistered(tenant)) {
            Tenant previousTenant = tenantContext.get();
            try {
                tenantContext.set(tenant);
                T t = callback.call();
                return t;
            }
            catch (Exception e) {
                throw new InvocationTargetException(e);
            }
            finally {
                tenantContext.set(previousTenant);
            }
        }
        throw new TenantUnavailableException();
    }

    @Override
    public boolean addTenant(Tenant tenant) {
        Preconditions.checkNotNull((Object)tenant);
        boolean added = this.tenant.compareAndSet(null, tenant);
        if (added) {
            log.info(tenant + " added");
        }
        return added;
    }

    @Override
    public boolean removeTenant(Tenant tenant) {
        Preconditions.checkNotNull((Object)tenant);
        boolean removed = this.tenant.compareAndSet(tenant, null);
        if (removed) {
            log.info(tenant + " removed");
        }
        return removed;
    }

    @Override
    public boolean isTenantRegistered(Tenant tenant) {
        Preconditions.checkNotNull((Object)tenant);
        return this.tenant.compareAndSet(tenant, tenant);
    }

    @Override
    public boolean isRegistryVacant() {
        return this.tenant.get() == null;
    }

    public @NonNull Tenant getCurrentTenant() {
        Tenant tenant = tenantContext.get();
        Preconditions.checkState((tenant != null ? 1 : 0) != 0, (Object)"No current tenant");
        return tenant;
    }
}

