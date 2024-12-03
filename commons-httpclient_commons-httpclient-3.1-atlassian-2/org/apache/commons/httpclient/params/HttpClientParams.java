/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.httpclient.params;

import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.params.HttpParams;

public class HttpClientParams
extends HttpMethodParams {
    public static final String CONNECTION_MANAGER_TIMEOUT = "http.connection-manager.timeout";
    public static final String CONNECTION_MANAGER_CLASS = "http.connection-manager.class";
    public static final String PREEMPTIVE_AUTHENTICATION = "http.authentication.preemptive";
    public static final String REJECT_RELATIVE_REDIRECT = "http.protocol.reject-relative-redirect";
    public static final String MAX_REDIRECTS = "http.protocol.max-redirects";
    public static final String ALLOW_CIRCULAR_REDIRECTS = "http.protocol.allow-circular-redirects";
    private static final String[] PROTOCOL_STRICTNESS_PARAMETERS = new String[]{"http.protocol.reject-relative-redirect", "http.protocol.allow-circular-redirects"};

    public HttpClientParams() {
    }

    public HttpClientParams(HttpParams defaults) {
        super(defaults);
    }

    public long getConnectionManagerTimeout() {
        return this.getLongParameter(CONNECTION_MANAGER_TIMEOUT, 0L);
    }

    public void setConnectionManagerTimeout(long timeout) {
        this.setLongParameter(CONNECTION_MANAGER_TIMEOUT, timeout);
    }

    public Class getConnectionManagerClass() {
        return (Class)this.getParameter(CONNECTION_MANAGER_CLASS);
    }

    public void setConnectionManagerClass(Class clazz) {
        this.setParameter(CONNECTION_MANAGER_CLASS, clazz);
    }

    public boolean isAuthenticationPreemptive() {
        return this.getBooleanParameter(PREEMPTIVE_AUTHENTICATION, false);
    }

    public void setAuthenticationPreemptive(boolean value) {
        this.setBooleanParameter(PREEMPTIVE_AUTHENTICATION, value);
    }

    @Override
    public void makeStrict() {
        super.makeStrict();
        this.setParameters(PROTOCOL_STRICTNESS_PARAMETERS, Boolean.TRUE);
    }

    @Override
    public void makeLenient() {
        super.makeLenient();
        this.setParameters(PROTOCOL_STRICTNESS_PARAMETERS, Boolean.FALSE);
    }
}

