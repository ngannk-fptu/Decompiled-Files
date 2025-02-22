/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi;

public class CallStatus {
    public static final int DONE_RESPONSE_ORDINAL = 0;
    public static final int DONE_VOID_ORDINAL = 1;
    public static final int WAIT_ORDINAL = 2;
    public static final int OFFLOAD_ORDINAL = 3;
    public static final CallStatus DONE_RESPONSE = new CallStatus(0);
    public static final CallStatus DONE_VOID = new CallStatus(1);
    public static final CallStatus WAIT = new CallStatus(2);
    private final int ordinal;

    protected CallStatus(int ordinal) {
        this.ordinal = ordinal;
    }

    public int ordinal() {
        return this.ordinal;
    }
}

