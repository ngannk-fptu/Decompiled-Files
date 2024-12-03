/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.client;

import org.apache.axis.AxisEngine;
import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.client.Call;

public class Transport {
    public String transportName = null;
    public String url = null;

    public final void setupMessageContext(MessageContext context, Call message, AxisEngine engine) throws AxisFault {
        if (this.url != null) {
            context.setProperty("transport.url", this.url);
        }
        if (this.transportName != null) {
            context.setTransportName(this.transportName);
        }
        this.setupMessageContextImpl(context, message, engine);
    }

    public void setupMessageContextImpl(MessageContext context, Call message, AxisEngine engine) throws AxisFault {
    }

    public void processReturnedMessageContext(MessageContext context) {
    }

    public void setTransportName(String name) {
        this.transportName = name;
    }

    public String getTransportName() {
        return this.transportName;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

