/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.cluster.impl;

import com.hazelcast.nio.Address;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import java.io.IOException;

public class BindMessage
implements IdentifiedDataSerializable {
    private Address localAddress;
    private Address targetAddress;
    private boolean reply;

    public BindMessage() {
    }

    public BindMessage(Address localAddress, Address targetAddress, boolean reply) {
        this.localAddress = localAddress;
        this.targetAddress = targetAddress;
        this.reply = reply;
    }

    public Address getLocalAddress() {
        return this.localAddress;
    }

    public Address getTargetAddress() {
        return this.targetAddress;
    }

    public boolean shouldReply() {
        return this.reply;
    }

    @Override
    public int getFactoryId() {
        return 0;
    }

    @Override
    public int getId() {
        return 5;
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.localAddress = new Address();
        this.localAddress.readData(in);
        boolean hasTarget = in.readBoolean();
        if (hasTarget) {
            this.targetAddress = new Address();
            this.targetAddress.readData(in);
        }
        this.reply = in.readBoolean();
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        this.localAddress.writeData(out);
        boolean hasTarget = this.targetAddress != null;
        out.writeBoolean(hasTarget);
        if (hasTarget) {
            this.targetAddress.writeData(out);
        }
        out.writeBoolean(this.reply);
    }

    public String toString() {
        return "Bind " + this.localAddress;
    }
}

