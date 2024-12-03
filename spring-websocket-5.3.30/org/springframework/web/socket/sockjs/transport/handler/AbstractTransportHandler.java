/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.web.socket.sockjs.transport.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.socket.sockjs.transport.SockJsServiceConfig;
import org.springframework.web.socket.sockjs.transport.TransportHandler;

public abstract class AbstractTransportHandler
implements TransportHandler {
    protected final Log logger = LogFactory.getLog(this.getClass());
    @Nullable
    private SockJsServiceConfig serviceConfig;

    @Override
    public void initialize(SockJsServiceConfig serviceConfig) {
        this.serviceConfig = serviceConfig;
    }

    public SockJsServiceConfig getServiceConfig() {
        Assert.state((this.serviceConfig != null ? 1 : 0) != 0, (String)"No SockJsServiceConfig available");
        return this.serviceConfig;
    }
}

