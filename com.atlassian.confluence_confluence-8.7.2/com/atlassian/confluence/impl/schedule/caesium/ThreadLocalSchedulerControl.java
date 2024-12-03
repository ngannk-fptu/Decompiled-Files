/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.tenancy.TenancyScope
 *  com.atlassian.annotations.tenancy.TenantAware
 */
package com.atlassian.confluence.impl.schedule.caesium;

import com.atlassian.annotations.tenancy.TenancyScope;
import com.atlassian.annotations.tenancy.TenantAware;

public class ThreadLocalSchedulerControl {
    @TenantAware(value=TenancyScope.TENANTLESS)
    private static final ThreadLocal<Object> schedulerDisabledThreadLocal = new ThreadLocal();
    private static final ThreadLocalSchedulerControl instance = new ThreadLocalSchedulerControl();

    public static ThreadLocalSchedulerControl getInstance() {
        return instance;
    }

    public void suspend() {
        schedulerDisabledThreadLocal.set(new Object());
    }

    public void resume() {
        schedulerDisabledThreadLocal.remove();
    }

    public boolean schedulerEnabled() {
        return schedulerDisabledThreadLocal.get() == null;
    }

    public boolean schedulerDisabled() {
        return !this.schedulerEnabled();
    }
}

