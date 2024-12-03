/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.event.api.AsynchronousPreferred
 */
package com.atlassian.tenancy.api.event;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.event.api.AsynchronousPreferred;
import com.atlassian.tenancy.api.Tenant;

@ExperimentalApi
@AsynchronousPreferred
@Deprecated
public class TenantArrivedEvent {
    private final Tenant tenant;

    public TenantArrivedEvent(Tenant tenant) {
        this.tenant = tenant;
    }

    @Deprecated
    public Tenant getTenant() {
        return this.tenant;
    }
}

