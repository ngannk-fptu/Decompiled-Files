/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.jmx;

import com.hazelcast.config.Config;
import com.hazelcast.config.TopicConfig;
import com.hazelcast.core.ITopic;
import com.hazelcast.internal.jmx.HazelcastMBean;
import com.hazelcast.internal.jmx.ManagedAnnotation;
import com.hazelcast.internal.jmx.ManagedDescription;
import com.hazelcast.internal.jmx.ManagementService;

@ManagedDescription(value="ITopic")
public class TopicMBean
extends HazelcastMBean<ITopic> {
    protected TopicMBean(ITopic managedObject, ManagementService service) {
        super(managedObject, service);
        this.objectName = service.createObjectName("ITopic", managedObject.getName());
    }

    @ManagedAnnotation(value="localCreationTime")
    @ManagedDescription(value="the creation time of this topic on this member")
    public long getLocalCreationTime() {
        return ((ITopic)this.managedObject).getLocalTopicStats().getCreationTime();
    }

    @ManagedAnnotation(value="localPublishOperationCount")
    @ManagedDescription(value=" the total number of published messages of this topic on this member")
    public long getLocalPublishOperationCount() {
        return ((ITopic)this.managedObject).getLocalTopicStats().getPublishOperationCount();
    }

    @ManagedAnnotation(value="localReceiveOperationCount")
    @ManagedDescription(value="the total number of received messages of this topic on this member")
    public long getLocalReceiveOperationCount() {
        return ((ITopic)this.managedObject).getLocalTopicStats().getReceiveOperationCount();
    }

    @ManagedAnnotation(value="name")
    @ManagedDescription(value="Name of the DistributedObject")
    public String getName() {
        return ((ITopic)this.managedObject).getName();
    }

    @ManagedAnnotation(value="config")
    public String getConfig() {
        Config config = this.service.instance.getConfig();
        TopicConfig topicConfig = config.findTopicConfig(((ITopic)this.managedObject).getName());
        return topicConfig.toString();
    }
}

