/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.coyote.http11;

import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.net.AprEndpoint;

@Deprecated
public class Http11AprProtocol
extends AbstractHttp11Protocol<Long> {
    private static final Log log = LogFactory.getLog(Http11AprProtocol.class);

    public Http11AprProtocol() {
        this(new AprEndpoint());
    }

    public Http11AprProtocol(AprEndpoint endpoint) {
        super(endpoint);
    }

    @Override
    protected Log getLog() {
        return log;
    }

    @Override
    public boolean isAprRequired() {
        return true;
    }

    public int getPollTime() {
        return ((AprEndpoint)this.getEndpoint()).getPollTime();
    }

    public void setPollTime(int pollTime) {
        ((AprEndpoint)this.getEndpoint()).setPollTime(pollTime);
    }

    public int getSendfileSize() {
        return ((AprEndpoint)this.getEndpoint()).getSendfileSize();
    }

    public void setSendfileSize(int sendfileSize) {
        ((AprEndpoint)this.getEndpoint()).setSendfileSize(sendfileSize);
    }

    public boolean getDeferAccept() {
        return ((AprEndpoint)this.getEndpoint()).getDeferAccept();
    }

    public void setDeferAccept(boolean deferAccept) {
        ((AprEndpoint)this.getEndpoint()).setDeferAccept(deferAccept);
    }

    @Override
    protected String getNamePrefix() {
        if (this.isSSLEnabled()) {
            return "https-openssl-apr";
        }
        return "http-apr";
    }
}

