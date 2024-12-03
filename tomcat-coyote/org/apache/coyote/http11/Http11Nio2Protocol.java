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
import org.apache.tomcat.util.net.Nio2Channel;
import org.apache.tomcat.util.net.Nio2Endpoint;

public class Http11Nio2Protocol
extends AbstractHttp11JsseProtocol<Nio2Channel> {
    private static final Log log = LogFactory.getLog(Http11Nio2Protocol.class);

    public Http11Nio2Protocol() {
        this(new Nio2Endpoint());
    }

    public Http11Nio2Protocol(Nio2Endpoint endpoint) {
        super(endpoint);
    }

    @Override
    protected Log getLog() {
        return log;
    }

    @Override
    protected String getNamePrefix() {
        if (this.isSSLEnabled()) {
            return "https-" + this.getSslImplementationShortName() + "-nio2";
        }
        return "http-nio2";
    }
}

