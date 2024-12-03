/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.tenantcontrol;

import com.hazelcast.spi.tenantcontrol.DestroyEventContext;
import com.hazelcast.spi.tenantcontrol.TenantControl;
import com.hazelcast.spi.tenantcontrol.TenantControlFactory;

public class NoopTenantControlFactory
implements TenantControlFactory {
    @Override
    public TenantControl saveCurrentTenant(DestroyEventContext event) {
        return TenantControl.NOOP_TENANT_CONTROL;
    }
}

