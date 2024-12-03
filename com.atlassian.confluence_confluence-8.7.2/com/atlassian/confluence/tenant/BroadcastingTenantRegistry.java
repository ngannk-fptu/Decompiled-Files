/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.tenancy.api.Tenant
 *  com.atlassian.tenancy.api.event.TenantArrivedEvent
 */
package com.atlassian.confluence.tenant;

import com.atlassian.confluence.tenant.TenantRegistry;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.tenancy.api.Tenant;
import com.atlassian.tenancy.api.event.TenantArrivedEvent;

@Deprecated(forRemoval=true)
public class BroadcastingTenantRegistry
implements TenantRegistry {
    private final TenantRegistry delegate;
    private final EventPublisher eventPublisher;

    public BroadcastingTenantRegistry(TenantRegistry delegate, EventPublisher eventPublisher) {
        this.delegate = delegate;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public boolean addTenant(Tenant tenant) {
        if (this.delegate.addTenant(tenant)) {
            this.eventPublisher.publish((Object)new TenantArrivedEvent(tenant));
            return true;
        }
        return false;
    }

    @Override
    public boolean removeTenant(Tenant tenant) {
        return this.delegate.removeTenant(tenant);
    }

    @Override
    public boolean isTenantRegistered(Tenant tenant) {
        return this.delegate.isTenantRegistered(tenant);
    }

    @Override
    public boolean isRegistryVacant() {
        return this.delegate.isRegistryVacant();
    }
}

