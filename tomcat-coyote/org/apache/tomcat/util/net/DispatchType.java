/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.net;

import org.apache.tomcat.util.net.SocketEvent;

public enum DispatchType {
    NON_BLOCKING_READ(SocketEvent.OPEN_READ),
    NON_BLOCKING_WRITE(SocketEvent.OPEN_WRITE);

    private final SocketEvent status;

    private DispatchType(SocketEvent status) {
        this.status = status;
    }

    public SocketEvent getSocketStatus() {
        return this.status;
    }
}

