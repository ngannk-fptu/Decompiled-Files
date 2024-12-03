/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.jmx;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.internal.jmx.HazelcastMBean;
import com.hazelcast.internal.jmx.ManagedAnnotation;
import com.hazelcast.internal.jmx.ManagedDescription;
import com.hazelcast.internal.jmx.ManagementService;
import com.hazelcast.spi.ProxyService;
import com.hazelcast.util.MapUtil;
import java.util.Map;

@ManagedDescription(value="HazelcastInstance.ProxyService")
public class ProxyServiceMBean
extends HazelcastMBean<ProxyService> {
    private static final int INITIAL_CAPACITY = 3;

    public ProxyServiceMBean(HazelcastInstance hazelcastInstance, ProxyService proxyService, ManagementService service) {
        super(proxyService, service);
        Map<String, String> properties = MapUtil.createHashMap(3);
        properties.put("type", ManagementService.quote("HazelcastInstance.ProxyService"));
        properties.put("name", ManagementService.quote("proxyService" + hazelcastInstance.getName()));
        properties.put("instance", ManagementService.quote(hazelcastInstance.getName()));
        this.setObjectName(properties);
    }

    @ManagedAnnotation(value="proxyCount")
    @ManagedDescription(value="The number proxies")
    public int getProxyCount() {
        return ((ProxyService)this.managedObject).getProxyCount();
    }
}

