/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.session;

public class TooManyActiveSessionsException
extends IllegalStateException {
    private static final long serialVersionUID = 1L;
    private final int maxActiveSessions;

    public TooManyActiveSessionsException(String message, int maxActive) {
        super(message);
        this.maxActiveSessions = maxActive;
    }

    public int getMaxActiveSessions() {
        return this.maxActiveSessions;
    }
}

