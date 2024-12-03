/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.jmx;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.instance.EndpointQualifier;
import com.hazelcast.internal.jmx.HazelcastMBean;
import com.hazelcast.internal.jmx.ManagedAnnotation;
import com.hazelcast.internal.jmx.ManagedDescription;
import com.hazelcast.internal.jmx.ManagementService;
import com.hazelcast.nio.EndpointManager;
import com.hazelcast.nio.NetworkingService;
import com.hazelcast.util.MapUtil;
import java.util.Map;

@ManagedDescription(value="HazelcastInstance.NetworkingService")
public class NetworkingServiceMBean
extends HazelcastMBean<NetworkingService> {
    private static final int PROPERTY_COUNT = 3;

    public NetworkingServiceMBean(HazelcastInstance hazelcastInstance, NetworkingService ns, ManagementService service) {
        super(ns, service);
        Map<String, String> properties = MapUtil.createHashMap(3);
        properties.put("type", ManagementService.quote("HazelcastInstance.NetworkingService"));
        properties.put("instance", ManagementService.quote(hazelcastInstance.getName()));
        properties.put("name", ManagementService.quote(hazelcastInstance.getName()));
        this.setObjectName(properties);
    }

    public NetworkingService getNetworkingService() {
        return (NetworkingService)this.managedObject;
    }

    @ManagedAnnotation(value="clientConnectionCount")
    @ManagedDescription(value="Current number of client connections")
    public int getCurrentClientConnections() {
        EndpointManager cem = this.getNetworkingService().getEndpointManager(EndpointQualifier.CLIENT);
        if (cem == null) {
            return -1;
        }
        return cem.getActiveConnections().size();
    }

    @ManagedAnnotation(value="activeConnectionCount")
    @ManagedDescription(value="Current number of active connections")
    public int getActiveConnectionCount() {
        return this.getNetworkingService().getAggregateEndpointManager().getActiveConnections().size();
    }

    @ManagedAnnotation(value="connectionCount")
    @ManagedDescription(value="Current number of connections")
    public int getConnectionCount() {
        return this.getNetworkingService().getAggregateEndpointManager().getConnections().size();
    }
}

