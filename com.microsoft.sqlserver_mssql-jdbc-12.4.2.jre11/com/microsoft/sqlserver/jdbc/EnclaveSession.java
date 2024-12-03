/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import java.util.concurrent.atomic.AtomicLong;

class EnclaveSession {
    private byte[] sessionID;
    private AtomicLong counter;
    private byte[] sessionSecret;

    EnclaveSession(byte[] cs, byte[] b) {
        this.sessionID = cs;
        this.sessionSecret = b;
        this.counter = new AtomicLong(0L);
    }

    byte[] getSessionID() {
        return this.sessionID;
    }

    byte[] getSessionSecret() {
        return this.sessionSecret;
    }

    long getCounter() {
        return this.counter.getAndIncrement();
    }
}

