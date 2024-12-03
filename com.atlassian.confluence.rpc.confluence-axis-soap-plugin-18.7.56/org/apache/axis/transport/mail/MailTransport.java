/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.transport.mail;

import org.apache.axis.AxisEngine;
import org.apache.axis.MessageContext;
import org.apache.axis.client.Call;
import org.apache.axis.client.Transport;

public class MailTransport
extends Transport {
    public MailTransport() {
        this.transportName = "mail";
    }

    public void setupMessageContextImpl(MessageContext mc, Call call, AxisEngine engine) {
    }
}

