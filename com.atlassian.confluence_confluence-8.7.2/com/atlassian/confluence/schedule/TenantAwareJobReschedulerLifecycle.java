/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.lifecycle.LifecycleContext
 *  com.atlassian.config.lifecycle.LifecycleItem
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.tenancy.api.TenantAccessor
 *  com.atlassian.tenancy.api.helper.PerTenantInitialiser
 */
package com.atlassian.confluence.schedule;

import com.atlassian.config.lifecycle.LifecycleContext;
import com.atlassian.config.lifecycle.LifecycleItem;
import com.atlassian.confluence.schedule.TenantAwareJobRescheduler;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.tenancy.api.TenantAccessor;
import com.atlassian.tenancy.api.helper.PerTenantInitialiser;

public class TenantAwareJobReschedulerLifecycle
implements LifecycleItem {
    private TenantAwareJobRescheduler tenantAwareJobRescheduler;
    private TenantAccessor tenantAccessor;
    private EventPublisher eventPublisher;
    private PerTenantInitialiser perTenantInitialiser;

    public void startup(LifecycleContext lifecycleContext) throws Exception {
        this.perTenantInitialiser = new PerTenantInitialiser(this.eventPublisher, this.tenantAccessor, this.tenantAwareJobRescheduler::rescheduleJobs);
        this.perTenantInitialiser.init();
    }

    public void shutdown(LifecycleContext lifecycleContext) throws Exception {
        if (this.perTenantInitialiser != null) {
            this.perTenantInitialiser.destroy();
            this.perTenantInitialiser = null;
        }
    }

    public void setTenantAwareJobRescheduler(TenantAwareJobRescheduler tenantAwareJobRescheduler) {
        this.tenantAwareJobRescheduler = tenantAwareJobRescheduler;
    }

    public void setTenantAccessor(TenantAccessor tenantAccessor) {
        this.tenantAccessor = tenantAccessor;
    }

    public void setEventPublisher(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }
}

