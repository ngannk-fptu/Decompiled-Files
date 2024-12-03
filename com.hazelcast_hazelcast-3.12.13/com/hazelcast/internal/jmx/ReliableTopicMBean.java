/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.jmx;

import com.hazelcast.config.Config;
import com.hazelcast.config.ReliableTopicConfig;
import com.hazelcast.internal.jmx.HazelcastMBean;
import com.hazelcast.internal.jmx.ManagedAnnotation;
import com.hazelcast.internal.jmx.ManagedDescription;
import com.hazelcast.internal.jmx.ManagementService;
import com.hazelcast.topic.impl.reliable.ReliableTopicProxy;

@ManagedDescription(value="ReliableTopic")
public class ReliableTopicMBean
extends HazelcastMBean<ReliableTopicProxy> {
    protected ReliableTopicMBean(ReliableTopicProxy managedObject, ManagementService service) {
        super(managedObject, service);
        this.objectName = service.createObjectName("ReliableTopic", managedObject.getName());
    }

    @ManagedAnnotation(value="localCreationTime")
    @ManagedDescription(value="the creation time of this reliable topic on this member")
    public long getLocalCreationTime() {
        return ((ReliableTopicProxy)this.managedObject).getLocalTopicStats().getCreationTime();
    }

    @ManagedAnnotation(value="localPublishOperationCount")
    @ManagedDescription(value=" the total number of published messages of this reliable topic on this member")
    public long getLocalPublishOperationCount() {
        return ((ReliableTopicProxy)this.managedObject).getLocalTopicStats().getPublishOperationCount();
    }

    @ManagedAnnotation(value="localReceiveOperationCount")
    @ManagedDescription(value="the total number of received messages of this reliable topic on this member")
    public long getLocalReceiveOperationCount() {
        return ((ReliableTopicProxy)this.managedObject).getLocalTopicStats().getReceiveOperationCount();
    }

    @ManagedAnnotation(value="name")
    @ManagedDescription(value="Name of the DistributedObject")
    public String getName() {
        return ((ReliableTopicProxy)this.managedObject).getName();
    }

    @ManagedAnnotation(value="config")
    public String getConfig() {
        Config config = this.service.instance.getConfig();
        ReliableTopicConfig topicConfig = config.findReliableTopicConfig(((ReliableTopicProxy)this.managedObject).getName());
        return topicConfig.toString();
    }
}

