/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.operationservice.impl;

final class InvocationConstant {
    static final Object HEARTBEAT_TIMEOUT = new InvocationConstant("Invocation::HEARTBEAT_TIMEOUT");
    static final Object CALL_TIMEOUT = new InvocationConstant("Invocation::CALL_TIMEOUT");
    static final Object INTERRUPTED = new InvocationConstant("Invocation::INTERRUPTED");
    static final Object VOID = new InvocationConstant("VOID");
    private String toString;

    private InvocationConstant(String toString) {
        this.toString = toString;
    }

    public String toString() {
        return this.toString;
    }
}

