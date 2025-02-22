/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.mapreduce.impl.notification;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import java.io.IOException;

public abstract class MapReduceNotification
implements IdentifiedDataSerializable {
    private String name;
    private String jobId;

    public MapReduceNotification() {
    }

    public MapReduceNotification(String name, String jobId) {
        this.name = name;
        this.jobId = jobId;
    }

    public String getName() {
        return this.name;
    }

    public String getJobId() {
        return this.jobId;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.name);
        out.writeUTF(this.jobId);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.name = in.readUTF();
        this.jobId = in.readUTF();
    }

    public String toString() {
        return "MapReduceNotification{name='" + this.name + '\'' + ", jobId='" + this.jobId + '\'' + '}';
    }
}

