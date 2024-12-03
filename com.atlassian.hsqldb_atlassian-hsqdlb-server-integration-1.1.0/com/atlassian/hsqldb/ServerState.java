/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.concurrent.ThreadSafe
 *  org.hsqldb.server.Server
 */
package com.atlassian.hsqldb;

import javax.annotation.concurrent.ThreadSafe;
import org.hsqldb.server.Server;

@ThreadSafe
public enum ServerState {
    ONLINE(1),
    OPENING(4),
    CLOSING(8),
    SHUTDOWN(16);

    private final int value;

    private ServerState(int value) {
        this.value = value;
    }

    public static ServerState forServer(Server server) {
        int snapshot = server.getState();
        for (ServerState state : ServerState.values()) {
            if (state.value != snapshot) continue;
            return state;
        }
        throw new IllegalStateException("unknown server state " + snapshot);
    }

    public String toString() {
        return "state " + this.name().toLowerCase();
    }
}

