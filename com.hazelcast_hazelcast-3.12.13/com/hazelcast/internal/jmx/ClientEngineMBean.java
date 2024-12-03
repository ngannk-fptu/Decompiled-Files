/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.jmx;

import com.hazelcast.client.impl.ClientEngine;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.internal.jmx.HazelcastMBean;
import com.hazelcast.internal.jmx.ManagedAnnotation;
import com.hazelcast.internal.jmx.ManagedDescription;
import com.hazelcast.internal.jmx.ManagementService;
import com.hazelcast.util.MapUtil;
import java.util.Map;

@ManagedDescription(value="HazelcastInstance.ClientEngine")
public class ClientEngineMBean
extends HazelcastMBean<ClientEngine> {
    private static final int PROPERTY_COUNT = 3;

    public ClientEngineMBean(HazelcastInstance hazelcastInstance, ClientEngine clientEngine, ManagementService service) {
        super(clientEngine, service);
        Map<String, String> properties = MapUtil.createHashMap(3);
        properties.put("type", ManagementService.quote("HazelcastInstance.ClientEngine"));
        properties.put("name", ManagementService.quote(hazelcastInstance.getName()));
        properties.put("instance", ManagementService.quote(hazelcastInstance.getName()));
        this.setObjectName(properties);
    }

    @ManagedAnnotation(value="clientEndpointCount")
    @ManagedDescription(value="The number of client endpoints")
    public int getClientEndpointCount() {
        return ((ClientEngine)this.managedObject).getClientEndpointCount();
    }
}

