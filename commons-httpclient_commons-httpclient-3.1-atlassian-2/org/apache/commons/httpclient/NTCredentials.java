/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.httpclient;

import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.util.LangUtils;

public class NTCredentials
extends UsernamePasswordCredentials {
    private String domain;
    private String host;

    public NTCredentials() {
    }

    public NTCredentials(String userName, String password, String host, String domain) {
        super(userName, password);
        if (domain == null) {
            throw new IllegalArgumentException("Domain may not be null");
        }
        this.domain = domain;
        if (host == null) {
            throw new IllegalArgumentException("Host may not be null");
        }
        this.host = host;
    }

    public void setDomain(String domain) {
        if (domain == null) {
            throw new IllegalArgumentException("Domain may not be null");
        }
        this.domain = domain;
    }

    public String getDomain() {
        return this.domain;
    }

    public void setHost(String host) {
        if (host == null) {
            throw new IllegalArgumentException("Host may not be null");
        }
        this.host = host;
    }

    public String getHost() {
        return this.host;
    }

    @Override
    public String toString() {
        StringBuffer sbResult = new StringBuffer(super.toString());
        sbResult.append("@");
        sbResult.append(this.host);
        sbResult.append(".");
        sbResult.append(this.domain);
        return sbResult.toString();
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = LangUtils.hashCode(hash, this.host);
        hash = LangUtils.hashCode(hash, this.domain);
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (this == o) {
            return true;
        }
        if (super.equals(o) && o instanceof NTCredentials) {
            NTCredentials that = (NTCredentials)o;
            return LangUtils.equals(this.domain, that.domain) && LangUtils.equals(this.host, that.host);
        }
        return false;
    }
}

