/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.transport.http;

import java.net.Socket;

public class SocketHolder {
    private Socket value = null;

    public SocketHolder(Socket value) {
        this.value = value;
    }

    public Socket getSocket() {
        return this.value;
    }

    public void setSocket(Socket value) {
        this.value = value;
    }
}

