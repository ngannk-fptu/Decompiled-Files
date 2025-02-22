/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi;

import com.hazelcast.nio.Address;
import com.hazelcast.nio.Connection;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.annotation.PrivateApi;

@PrivateApi
public final class OperationAccessor {
    private OperationAccessor() {
    }

    public static void setCallerAddress(Operation op, Address caller) {
        op.setCallerAddress(caller);
    }

    public static void setConnection(Operation op, Connection connection) {
        op.setConnection(connection);
    }

    public static void setCallId(Operation op, long callId) {
        op.setCallId(callId);
    }

    public static boolean deactivate(Operation op) {
        return op.deactivate();
    }

    public static boolean hasActiveInvocation(Operation op) {
        return op.isActive();
    }

    public static void setInvocationTime(Operation op, long invocationTime) {
        op.setInvocationTime(invocationTime);
    }

    public static void setCallTimeout(Operation op, long callTimeout) {
        op.setCallTimeout(callTimeout);
    }
}

