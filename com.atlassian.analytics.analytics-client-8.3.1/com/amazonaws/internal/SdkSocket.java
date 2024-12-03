/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.amazonaws.internal;

import com.amazonaws.internal.DelegateSocket;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SdkSocket
extends DelegateSocket {
    private static final Log log = LogFactory.getLog(SdkSocket.class);

    public SdkSocket(Socket sock) {
        super(sock);
        if (log.isDebugEnabled()) {
            log.debug((Object)("created: " + this.endpoint()));
        }
    }

    private String endpoint() {
        return this.sock.getInetAddress() + ":" + this.sock.getPort();
    }

    @Override
    public void connect(SocketAddress endpoint) throws IOException {
        if (log.isDebugEnabled()) {
            log.debug((Object)("connecting to: " + endpoint));
        }
        this.sock.connect(endpoint);
        if (log.isDebugEnabled()) {
            log.debug((Object)("connected to: " + this.endpoint()));
        }
    }

    @Override
    public void connect(SocketAddress endpoint, int timeout) throws IOException {
        if (log.isDebugEnabled()) {
            log.debug((Object)("connecting to: " + endpoint));
        }
        this.sock.connect(endpoint, timeout);
        if (log.isDebugEnabled()) {
            log.debug((Object)("connected to: " + this.endpoint()));
        }
    }

    @Override
    public void close() throws IOException {
        if (log.isDebugEnabled()) {
            log.debug((Object)("closing " + this.endpoint()));
        }
        this.sock.close();
    }

    @Override
    public void shutdownInput() throws IOException {
        if (log.isDebugEnabled()) {
            log.debug((Object)("shutting down input of " + this.endpoint()));
        }
        this.sock.shutdownInput();
    }

    @Override
    public void shutdownOutput() throws IOException {
        if (log.isDebugEnabled()) {
            log.debug((Object)("shutting down output of " + this.endpoint()));
        }
        this.sock.shutdownOutput();
    }
}

