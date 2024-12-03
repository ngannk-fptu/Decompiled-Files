/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.httpclient.params;

import org.apache.commons.httpclient.params.DefaultHttpParams;
import org.apache.commons.httpclient.params.HttpParams;

public class HostParams
extends DefaultHttpParams {
    public static final String DEFAULT_HEADERS = "http.default-headers";

    public HostParams() {
    }

    public HostParams(HttpParams defaults) {
        super(defaults);
    }

    public void setVirtualHost(String hostname) {
        this.setParameter("http.virtual-host", hostname);
    }

    public String getVirtualHost() {
        return (String)this.getParameter("http.virtual-host");
    }
}

