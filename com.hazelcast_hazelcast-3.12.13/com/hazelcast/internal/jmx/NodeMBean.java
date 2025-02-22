/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.jmx;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.instance.Node;
import com.hazelcast.internal.jmx.HazelcastMBean;
import com.hazelcast.internal.jmx.ManagedAnnotation;
import com.hazelcast.internal.jmx.ManagedDescription;
import com.hazelcast.internal.jmx.ManagementService;
import com.hazelcast.nio.Address;
import com.hazelcast.util.MapUtil;
import java.util.Map;

@ManagedDescription(value="HazelcastInstance.Node")
public class NodeMBean
extends HazelcastMBean<Node> {
    private static final int INITIAL_CAPACITY = 3;

    public NodeMBean(HazelcastInstance hazelcastInstance, Node node, ManagementService service) {
        super(node, service);
        Map<String, String> properties = MapUtil.createHashMap(3);
        properties.put("type", ManagementService.quote("HazelcastInstance.Node"));
        properties.put("name", ManagementService.quote("node" + node.address));
        properties.put("instance", ManagementService.quote(hazelcastInstance.getName()));
        this.setObjectName(properties);
    }

    @ManagedAnnotation(value="address")
    @ManagedDescription(value="Address of the node")
    public String getName() {
        return ((Node)this.managedObject).address.toString();
    }

    @ManagedAnnotation(value="masterAddress")
    @ManagedDescription(value="The master address of the cluster")
    public String getMasterAddress() {
        Address masterAddress = ((Node)this.managedObject).getMasterAddress();
        return masterAddress == null ? null : masterAddress.toString();
    }
}

