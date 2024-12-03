/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.servicemanager;

import com.hazelcast.spi.SharedService;
import com.hazelcast.spi.impl.servicemanager.ServiceInfo;
import java.util.List;

public interface ServiceManager {
    public ServiceInfo getServiceInfo(String var1);

    public List<ServiceInfo> getServiceInfos(Class var1);

    public <T> T getService(String var1);

    public <S> List<S> getServices(Class<S> var1);

    public <T extends SharedService> T getSharedService(String var1);
}

