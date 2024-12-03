/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.configuration;

import org.apache.axis.Handler;
import org.apache.axis.SimpleTargetedChain;
import org.apache.axis.configuration.SimpleProvider;
import org.apache.axis.transport.local.LocalResponder;
import org.apache.axis.transport.local.LocalSender;

public class BasicServerConfig
extends SimpleProvider {
    public BasicServerConfig() {
        LocalResponder h = new LocalResponder();
        SimpleTargetedChain transport = new SimpleTargetedChain(null, null, h);
        this.deployTransport("local", (Handler)transport);
        this.deployTransport("java", (Handler)new SimpleTargetedChain(new LocalSender()));
    }
}

