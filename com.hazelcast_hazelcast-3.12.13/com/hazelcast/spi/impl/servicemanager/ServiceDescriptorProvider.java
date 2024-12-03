/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.servicemanager;

import com.hazelcast.spi.impl.servicemanager.ServiceDescriptor;

public interface ServiceDescriptorProvider {
    public ServiceDescriptor[] createServiceDescriptors();
}

