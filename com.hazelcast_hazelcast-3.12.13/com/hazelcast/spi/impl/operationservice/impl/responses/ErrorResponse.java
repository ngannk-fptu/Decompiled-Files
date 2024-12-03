/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.operationservice.impl.responses;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.impl.operationservice.impl.responses.Response;
import java.io.IOException;

public class ErrorResponse
extends Response {
    private Throwable cause;

    public ErrorResponse() {
    }

    public ErrorResponse(Throwable cause, long callId, boolean urgent) {
        super(callId, urgent);
        this.cause = cause;
    }

    public Throwable getCause() {
        return this.cause;
    }

    @Override
    public int getId() {
        return 9;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        super.writeData(out);
        out.writeObject(this.cause);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        super.readData(in);
        this.cause = (Throwable)in.readObject();
    }

    public String toString() {
        return "ErrorResponse{callId=" + this.callId + ", urgent=" + this.urgent + ", cause=" + this.cause + '}';
    }
}

