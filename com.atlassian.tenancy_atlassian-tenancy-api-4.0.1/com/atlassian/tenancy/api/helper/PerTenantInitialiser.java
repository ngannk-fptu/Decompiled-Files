/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.tenancy.api.helper;

import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.tenancy.api.TenantAccessor;
import com.atlassian.tenancy.api.event.TenantArrivedEvent;
import com.atlassian.tenancy.api.helper.Failure;
import com.atlassian.tenancy.api.helper.TenantAccessors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public class PerTenantInitialiser {
    private static final Logger log = LoggerFactory.getLogger(PerTenantInitialiser.class);
    private final EventPublisher eventPublisher;
    private final TenantAccessor tenantAccessor;
    private final Runnable tenantSetupProcedure;

    public PerTenantInitialiser(EventPublisher eventPublisher, TenantAccessor tenantAccessor, Runnable tenantSetupProcedure) {
        this.eventPublisher = eventPublisher;
        this.tenantAccessor = tenantAccessor;
        this.tenantSetupProcedure = tenantSetupProcedure;
    }

    @Deprecated
    public void init() {
        this.eventPublisher.register((Object)this);
        for (Failure failure : TenantAccessors.forEachTenant(this.tenantAccessor, this.tenantSetupProcedure)) {
            log.error("Setup for tenant " + failure.getTenant() + " failed: " + failure.getException(), (Throwable)failure.getException());
        }
    }

    @EventListener
    @Deprecated
    public void onTenantArrived(TenantArrivedEvent event) {
        try {
            this.tenantSetupProcedure.run();
        }
        catch (RuntimeException e) {
            log.error("Setup for tenant " + event.getTenant() + " failed: " + e, (Throwable)e);
        }
    }

    @Deprecated
    public void destroy() {
        this.eventPublisher.unregister((Object)this);
    }
}

