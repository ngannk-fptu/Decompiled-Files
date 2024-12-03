/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.wan;

import com.hazelcast.config.WanAcknowledgeType;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.wan.ReplicationEventObject;
import com.hazelcast.wan.impl.WanDataSerializerHook;
import java.io.IOException;

public class WanReplicationEvent
implements IdentifiedDataSerializable {
    private String serviceName;
    private ReplicationEventObject eventObject;
    private transient WanAcknowledgeType acknowledgeType;

    public WanReplicationEvent() {
    }

    public WanReplicationEvent(String serviceName, ReplicationEventObject eventObject) {
        this.serviceName = serviceName;
        this.eventObject = eventObject;
    }

    public String getServiceName() {
        return this.serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public ReplicationEventObject getEventObject() {
        return this.eventObject;
    }

    public void setEventObject(ReplicationEventObject eventObject) {
        this.eventObject = eventObject;
    }

    public WanAcknowledgeType getAcknowledgeType() {
        return this.acknowledgeType;
    }

    public void setAcknowledgeType(WanAcknowledgeType acknowledgeType) {
        this.acknowledgeType = acknowledgeType;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.serviceName);
        out.writeObject(this.eventObject);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.serviceName = in.readUTF();
        this.eventObject = (ReplicationEventObject)in.readObject();
    }

    @Override
    public int getFactoryId() {
        return WanDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 0;
    }
}

