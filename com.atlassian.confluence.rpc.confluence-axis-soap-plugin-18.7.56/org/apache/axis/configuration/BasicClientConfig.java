/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.configuration;

import org.apache.axis.Handler;
import org.apache.axis.SimpleTargetedChain;
import org.apache.axis.configuration.SimpleProvider;
import org.apache.axis.transport.http.HTTPSender;
import org.apache.axis.transport.java.JavaSender;
import org.apache.axis.transport.local.LocalSender;

public class BasicClientConfig
extends SimpleProvider {
    public BasicClientConfig() {
        this.deployTransport("java", (Handler)new SimpleTargetedChain(new JavaSender()));
        this.deployTransport("local", (Handler)new SimpleTargetedChain(new LocalSender()));
        this.deployTransport("http", (Handler)new SimpleTargetedChain(new HTTPSender()));
    }
}

