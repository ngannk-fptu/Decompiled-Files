/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.client.urlconnection;

import java.security.NoSuchAlgorithmException;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

public class HTTPSProperties {
    public static final String PROPERTY_HTTPS_PROPERTIES = "com.sun.jersey.client.impl.urlconnection.httpsProperties";
    private HostnameVerifier hostnameVerifier = null;
    private SSLContext sslContext = null;

    public HTTPSProperties() throws NoSuchAlgorithmException {
        this(null, SSLContext.getInstance("SSL"));
    }

    public HTTPSProperties(HostnameVerifier hv) throws NoSuchAlgorithmException {
        this(hv, SSLContext.getInstance("SSL"));
    }

    public HTTPSProperties(HostnameVerifier hv, SSLContext c) {
        if (c == null) {
            throw new IllegalArgumentException("SSLContext must not be null");
        }
        this.hostnameVerifier = hv;
        this.sslContext = c;
    }

    public HostnameVerifier getHostnameVerifier() {
        return this.hostnameVerifier;
    }

    public SSLContext getSSLContext() {
        return this.sslContext;
    }

    public void setConnection(HttpsURLConnection connection) {
        if (this.hostnameVerifier != null) {
            connection.setHostnameVerifier(this.hostnameVerifier);
        }
        connection.setSSLSocketFactory(this.sslContext.getSocketFactory());
    }
}

