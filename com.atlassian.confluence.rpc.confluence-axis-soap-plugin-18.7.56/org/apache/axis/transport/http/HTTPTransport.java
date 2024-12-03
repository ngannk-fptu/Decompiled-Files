/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.transport.http;

import org.apache.axis.AxisEngine;
import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.client.Call;
import org.apache.axis.client.Transport;

public class HTTPTransport
extends Transport {
    public static final String DEFAULT_TRANSPORT_NAME = "http";
    public static final String URL = "transport.url";
    private Object cookie;
    private Object cookie2;
    private String action;

    public HTTPTransport() {
        this.transportName = DEFAULT_TRANSPORT_NAME;
    }

    public HTTPTransport(String url, String action) {
        this.transportName = DEFAULT_TRANSPORT_NAME;
        this.url = url;
        this.action = action;
    }

    public void setupMessageContextImpl(MessageContext mc, Call call, AxisEngine engine) throws AxisFault {
        if (this.action != null) {
            mc.setUseSOAPAction(true);
            mc.setSOAPActionURI(this.action);
        }
        if (this.cookie != null) {
            mc.setProperty("Cookie", this.cookie);
        }
        if (this.cookie2 != null) {
            mc.setProperty("Cookie2", this.cookie2);
        }
        if (mc.getService() == null) {
            mc.setTargetService(mc.getSOAPActionURI());
        }
    }

    public void processReturnedMessageContext(MessageContext context) {
        this.cookie = context.getProperty("Cookie");
        this.cookie2 = context.getProperty("Cookie2");
    }
}

