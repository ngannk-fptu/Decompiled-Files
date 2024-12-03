/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.transport.java;

import org.apache.axis.AxisEngine;
import org.apache.axis.MessageContext;
import org.apache.axis.client.Call;
import org.apache.axis.client.Transport;

public class JavaTransport
extends Transport {
    public JavaTransport() {
        this.transportName = "java";
    }

    public void setupMessageContextImpl(MessageContext mc, Call call, AxisEngine engine) {
    }
}

