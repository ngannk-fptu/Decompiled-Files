/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.nimbusds.common.contenttype.ContentType
 */
package com.nimbusds.oauth2.sdk.auth;

import com.nimbusds.common.contenttype.ContentType;
import com.nimbusds.oauth2.sdk.SerializeException;
import com.nimbusds.oauth2.sdk.auth.ClientAuthentication;
import com.nimbusds.oauth2.sdk.auth.ClientAuthenticationMethod;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.util.URLUtils;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.net.ssl.SSLSocketFactory;

public abstract class TLSClientAuthentication
extends ClientAuthentication {
    protected final X509Certificate certificate;
    private final SSLSocketFactory sslSocketFactory;

    protected TLSClientAuthentication(ClientAuthenticationMethod method, ClientID clientID, SSLSocketFactory sslSocketFactory) {
        super(method, clientID);
        this.sslSocketFactory = sslSocketFactory;
        this.certificate = null;
    }

    protected TLSClientAuthentication(ClientAuthenticationMethod method, ClientID clientID, X509Certificate certificate) {
        super(method, clientID);
        this.sslSocketFactory = null;
        this.certificate = certificate;
    }

    public SSLSocketFactory getSSLSocketFactory() {
        return this.sslSocketFactory;
    }

    public X509Certificate getClientX509Certificate() {
        return this.certificate;
    }

    @Override
    public Set<String> getFormParameterNames() {
        return Collections.singleton("client_id");
    }

    @Override
    public void applyTo(HTTPRequest httpRequest) {
        if (httpRequest.getMethod() != HTTPRequest.Method.POST) {
            throw new SerializeException("The HTTP request method must be POST");
        }
        ContentType ct = httpRequest.getEntityContentType();
        if (ct == null) {
            throw new SerializeException("Missing HTTP Content-Type header");
        }
        if (!ct.matches(ContentType.APPLICATION_JSON)) {
            if (ct.matches(ContentType.APPLICATION_URLENCODED)) {
                Map<String, List<String>> params = httpRequest.getQueryParameters();
                params.put("client_id", Collections.singletonList(this.getClientID().getValue()));
                String queryString = URLUtils.serializeParameters(params);
                httpRequest.setQuery(queryString);
            } else {
                throw new SerializeException("The HTTP Content-Type header must be " + ContentType.APPLICATION_URLENCODED);
            }
        }
        httpRequest.setSSLSocketFactory(this.sslSocketFactory);
    }
}

