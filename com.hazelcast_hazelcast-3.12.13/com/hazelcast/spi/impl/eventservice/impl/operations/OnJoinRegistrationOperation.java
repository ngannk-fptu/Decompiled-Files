/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.eventservice.impl.operations;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.spi.impl.SpiDataSerializerHook;
import com.hazelcast.spi.impl.eventservice.impl.EventServiceImpl;
import com.hazelcast.spi.impl.eventservice.impl.Registration;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class OnJoinRegistrationOperation
extends Operation
implements IdentifiedDataSerializable {
    private Collection<Registration> registrations;

    public OnJoinRegistrationOperation() {
    }

    public OnJoinRegistrationOperation(Collection<Registration> registrations) {
        this.registrations = registrations;
    }

    @Override
    public void run() throws Exception {
        if (this.registrations == null || this.registrations.size() <= 0) {
            return;
        }
        NodeEngineImpl nodeEngine = (NodeEngineImpl)this.getNodeEngine();
        EventServiceImpl eventService = (EventServiceImpl)nodeEngine.getEventService();
        for (Registration reg : this.registrations) {
            eventService.handleRegistration(reg);
        }
    }

    @Override
    public boolean returnsResponse() {
        return false;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        int len = this.registrations != null ? this.registrations.size() : 0;
        out.writeInt(len);
        if (len > 0) {
            for (Registration reg : this.registrations) {
                reg.writeData(out);
            }
        }
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        int len = in.readInt();
        if (len > 0) {
            this.registrations = new ArrayList<Registration>(len);
            for (int i = 0; i < len; ++i) {
                Registration reg = new Registration();
                this.registrations.add(reg);
                reg.readData(in);
            }
        }
    }

    @Override
    public int getFactoryId() {
        return SpiDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 11;
    }
}

