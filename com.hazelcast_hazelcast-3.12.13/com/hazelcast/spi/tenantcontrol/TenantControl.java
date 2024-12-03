/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.tenantcontrol;

import com.hazelcast.spi.annotation.Beta;
import com.hazelcast.spi.impl.tenantcontrol.NoopTenantControl;
import java.io.Closeable;
import java.io.Serializable;

@Beta
public interface TenantControl
extends Serializable {
    public static final TenantControl NOOP_TENANT_CONTROL = new NoopTenantControl();

    public Closeable setTenant(boolean var1);

    public void unregister();
}

