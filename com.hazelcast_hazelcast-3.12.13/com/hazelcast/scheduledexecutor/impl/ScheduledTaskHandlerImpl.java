/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.scheduledexecutor.impl;

import com.hazelcast.nio.Address;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.scheduledexecutor.ScheduledTaskHandler;
import com.hazelcast.scheduledexecutor.impl.ScheduledExecutorDataSerializerHook;
import java.io.IOException;
import java.net.UnknownHostException;

public final class ScheduledTaskHandlerImpl
extends ScheduledTaskHandler {
    private static final String URN_BASE = "urn:hzScheduledTaskHandler:";
    private static final char DESC_SEP = '\u0000';
    private static final int URN_PARTS = 4;
    private Address address;
    private int partitionId;
    private String schedulerName;
    private String taskName;

    public ScheduledTaskHandlerImpl() {
    }

    private ScheduledTaskHandlerImpl(int partitionId, String schedulerName, String taskName) {
        this.partitionId = partitionId;
        this.schedulerName = schedulerName;
        this.taskName = taskName;
        this.address = null;
    }

    private ScheduledTaskHandlerImpl(Address address, String schedulerName, String taskName) {
        this.address = address;
        this.schedulerName = schedulerName;
        this.taskName = taskName;
        this.partitionId = -1;
    }

    @Override
    public Address getAddress() {
        return this.address;
    }

    @Override
    public int getPartitionId() {
        return this.partitionId;
    }

    @Override
    public String getSchedulerName() {
        return this.schedulerName;
    }

    @Override
    public String getTaskName() {
        return this.taskName;
    }

    @Override
    public boolean isAssignedToPartition() {
        return this.address == null;
    }

    @Override
    public boolean isAssignedToMember() {
        return this.address != null;
    }

    @Override
    public String toUrn() {
        return URN_BASE + (this.address == null ? "-" : this.address.getHost() + ":" + String.valueOf(this.address.getPort())) + '\u0000' + String.valueOf(this.partitionId) + '\u0000' + this.schedulerName + '\u0000' + this.taskName;
    }

    @Override
    public int getFactoryId() {
        return ScheduledExecutorDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 1;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.toUrn());
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        ScheduledTaskHandler handler = ScheduledTaskHandlerImpl.of(in.readUTF());
        this.address = handler.getAddress();
        this.partitionId = handler.getPartitionId();
        this.schedulerName = handler.getSchedulerName();
        this.taskName = handler.getTaskName();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ScheduledTaskHandlerImpl that = (ScheduledTaskHandlerImpl)o;
        if (this.partitionId != that.partitionId) {
            return false;
        }
        if (this.address != null ? !this.address.equals(that.address) : that.address != null) {
            return false;
        }
        if (!this.schedulerName.equals(that.schedulerName)) {
            return false;
        }
        return this.taskName.equals(that.taskName);
    }

    public int hashCode() {
        int result = this.address != null ? this.address.hashCode() : 0;
        result = 31 * result + this.partitionId;
        result = 31 * result + this.schedulerName.hashCode();
        result = 31 * result + this.taskName.hashCode();
        return result;
    }

    public String toString() {
        return "ScheduledTaskHandler{address=" + this.address + ", partitionId=" + this.partitionId + ", schedulerName='" + this.schedulerName + '\'' + ", taskName='" + this.taskName + '\'' + '}';
    }

    void setAddress(Address address) {
        this.address = address;
    }

    public static ScheduledTaskHandler of(Address addr, String schedulerName, String taskName) {
        return new ScheduledTaskHandlerImpl(addr, schedulerName, taskName);
    }

    public static ScheduledTaskHandler of(int partitionId, String schedulerName, String taskName) {
        return new ScheduledTaskHandlerImpl(partitionId, schedulerName, taskName);
    }

    public static ScheduledTaskHandler of(String urn) {
        if (!urn.startsWith(URN_BASE)) {
            throw new IllegalArgumentException("Wrong urn format.");
        }
        String[] parts = (urn = urn.replace(URN_BASE, "")).split(String.valueOf('\u0000'));
        if (parts.length != 4) {
            throw new IllegalArgumentException("Wrong urn format.");
        }
        Address addr = null;
        if (!"-".equals(parts[0])) {
            int lastColonIx = parts[0].lastIndexOf(58);
            String host = parts[0].substring(0, lastColonIx);
            int port = Integer.parseInt(parts[0].substring(lastColonIx + 1));
            try {
                addr = new Address(host, port);
            }
            catch (UnknownHostException e) {
                throw new IllegalArgumentException("Wrong urn format.", e);
            }
        }
        int partitionId = Integer.parseInt(parts[1]);
        String scheduler = parts[2];
        String task = parts[3];
        return addr != null ? new ScheduledTaskHandlerImpl(addr, scheduler, task) : new ScheduledTaskHandlerImpl(partitionId, scheduler, task);
    }
}

