/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.tenantcontrol;

import com.hazelcast.spi.annotation.Beta;

@Beta
public interface DestroyEventContext<T> {
    public void destroy(T var1);

    public Class<? extends T> getContextType();

    public String getDistributedObjectName();

    public String getServiceName();
}

