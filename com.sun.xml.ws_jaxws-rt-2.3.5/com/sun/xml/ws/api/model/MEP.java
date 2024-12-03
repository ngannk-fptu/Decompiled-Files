/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.api.model;

public enum MEP {
    REQUEST_RESPONSE(false),
    ONE_WAY(false),
    ASYNC_POLL(true),
    ASYNC_CALLBACK(true);

    public final boolean isAsync;

    private MEP(boolean async) {
        this.isAsync = async;
    }

    public final boolean isOneWay() {
        return this == ONE_WAY;
    }
}

