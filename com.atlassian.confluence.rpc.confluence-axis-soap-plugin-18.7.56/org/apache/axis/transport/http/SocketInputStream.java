/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.transport.http;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class SocketInputStream
extends FilterInputStream {
    protected volatile boolean closed = false;
    Socket socket = null;

    private SocketInputStream() {
        super(null);
    }

    public SocketInputStream(InputStream is, Socket socket) {
        super(is);
        this.socket = socket;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void close() throws IOException {
        SocketInputStream socketInputStream = this;
        synchronized (socketInputStream) {
            if (this.closed) {
                return;
            }
            this.closed = true;
        }
        this.in.close();
        this.in = null;
        this.socket.close();
        this.socket = null;
    }
}

