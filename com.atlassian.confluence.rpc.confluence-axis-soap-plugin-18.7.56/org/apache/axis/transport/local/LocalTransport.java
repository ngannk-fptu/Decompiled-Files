/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.transport.local;

import org.apache.axis.AxisEngine;
import org.apache.axis.MessageContext;
import org.apache.axis.client.Call;
import org.apache.axis.client.Transport;
import org.apache.axis.server.AxisServer;

public class LocalTransport
extends Transport {
    public static final String LOCAL_SERVER = "LocalTransport.AxisServer";
    public static final String REMOTE_SERVICE = "LocalTransport.RemoteService";
    private AxisServer server;
    private String remoteServiceName;

    public LocalTransport() {
        this.transportName = "local";
    }

    public LocalTransport(AxisServer server) {
        this.transportName = "local";
        this.server = server;
    }

    public void setRemoteService(String remoteServiceName) {
        this.remoteServiceName = remoteServiceName;
    }

    public void setupMessageContextImpl(MessageContext mc, Call call, AxisEngine engine) {
        if (this.server != null) {
            mc.setProperty(LOCAL_SERVER, this.server);
        }
        if (this.remoteServiceName != null) {
            mc.setProperty(REMOTE_SERVICE, this.remoteServiceName);
        }
    }
}

