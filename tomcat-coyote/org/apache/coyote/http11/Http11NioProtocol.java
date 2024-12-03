/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.coyote.http11;

import org.apache.coyote.http11.AbstractHttp11JsseProtocol;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.net.NioChannel;
import org.apache.tomcat.util.net.NioEndpoint;

public class Http11NioProtocol
extends AbstractHttp11JsseProtocol<NioChannel> {
    private static final Log log = LogFactory.getLog(Http11NioProtocol.class);

    public Http11NioProtocol() {
        this(new NioEndpoint());
    }

    public Http11NioProtocol(NioEndpoint endpoint) {
        super(endpoint);
    }

    @Override
    protected Log getLog() {
        return log;
    }

    @Deprecated
    public void setPollerThreadCount(int count) {
    }

    @Deprecated
    public int getPollerThreadCount() {
        return 1;
    }

    public void setSelectorTimeout(long timeout) {
        ((NioEndpoint)this.getEndpoint()).setSelectorTimeout(timeout);
    }

    public long getSelectorTimeout() {
        return ((NioEndpoint)this.getEndpoint()).getSelectorTimeout();
    }

    public void setPollerThreadPriority(int threadPriority) {
        ((NioEndpoint)this.getEndpoint()).setPollerThreadPriority(threadPriority);
    }

    public int getPollerThreadPriority() {
        return ((NioEndpoint)this.getEndpoint()).getPollerThreadPriority();
    }

    @Override
    protected String getNamePrefix() {
        if (this.isSSLEnabled()) {
            return "https-" + this.getSslImplementationShortName() + "-nio";
        }
        return "http-nio";
    }
}

