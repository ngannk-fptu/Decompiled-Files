/*
 * Decompiled with CFR 0.152.
 */
package org.apache.coyote.http11;

import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.apache.tomcat.util.net.AbstractJsseEndpoint;
import org.apache.tomcat.util.net.openssl.OpenSSLImplementation;

public abstract class AbstractHttp11JsseProtocol<S>
extends AbstractHttp11Protocol<S> {
    public AbstractHttp11JsseProtocol(AbstractJsseEndpoint<S, ?> endpoint) {
        super(endpoint);
    }

    @Override
    protected AbstractJsseEndpoint<S, ?> getEndpoint() {
        return (AbstractJsseEndpoint)super.getEndpoint();
    }

    protected String getSslImplementationShortName() {
        if (OpenSSLImplementation.class.getName().equals(this.getSslImplementationName())) {
            return "openssl";
        }
        if (this.getSslImplementationName() != null && this.getSslImplementationName().endsWith(".panama.OpenSSLImplementation")) {
            return "opensslffm";
        }
        return "jsse";
    }

    public String getSslImplementationName() {
        return ((AbstractJsseEndpoint)this.getEndpoint()).getSslImplementationName();
    }

    public void setSslImplementationName(String s) {
        ((AbstractJsseEndpoint)this.getEndpoint()).setSslImplementationName(s);
    }

    public int getSniParseLimit() {
        return ((AbstractJsseEndpoint)this.getEndpoint()).getSniParseLimit();
    }

    public void setSniParseLimit(int sniParseLimit) {
        ((AbstractJsseEndpoint)this.getEndpoint()).setSniParseLimit(sniParseLimit);
    }
}

