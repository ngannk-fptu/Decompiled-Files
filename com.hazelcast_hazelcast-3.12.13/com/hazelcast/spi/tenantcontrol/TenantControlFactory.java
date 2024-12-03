/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.tenantcontrol;

import com.hazelcast.spi.annotation.Beta;
import com.hazelcast.spi.impl.tenantcontrol.NoopTenantControlFactory;
import com.hazelcast.spi.tenantcontrol.DestroyEventContext;
import com.hazelcast.spi.tenantcontrol.TenantControl;

@Beta
public interface TenantControlFactory {
    public static final TenantControlFactory NOOP_TENANT_CONTROL_FACTORY = new NoopTenantControlFactory();

    public TenantControl saveCurrentTenant(DestroyEventContext var1);
}

