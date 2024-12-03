/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.eventservice.impl.operations;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.impl.eventservice.impl.EventServiceImpl;
import com.hazelcast.spi.impl.eventservice.impl.Registration;
import com.hazelcast.spi.impl.eventservice.impl.operations.AbstractRegistrationOperation;
import java.io.IOException;

public class RegistrationOperation
extends AbstractRegistrationOperation {
    private Registration registration;
    private boolean response;

    public RegistrationOperation() {
    }

    public RegistrationOperation(Registration registration, int memberListVersion) {
        super(memberListVersion);
        this.registration = registration;
    }

    @Override
    protected void runInternal() throws Exception {
        EventServiceImpl eventService = (EventServiceImpl)this.getNodeEngine().getEventService();
        this.response = eventService.handleRegistration(this.registration);
    }

    @Override
    public Object getResponse() {
        return this.response;
    }

    @Override
    protected void writeInternalImpl(ObjectDataOutput out) throws IOException {
        this.registration.writeData(out);
    }

    @Override
    protected void readInternalImpl(ObjectDataInput in) throws IOException {
        this.registration = new Registration();
        this.registration.readData(in);
    }

    @Override
    public int getId() {
        return 12;
    }
}

