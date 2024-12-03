/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.nimbusds.oauth2.sdk.SerializeException
 *  com.nimbusds.oauth2.sdk.auth.ClientAuthentication
 *  com.nimbusds.oauth2.sdk.auth.ClientAuthenticationMethod
 *  com.nimbusds.oauth2.sdk.http.HTTPRequest
 *  com.nimbusds.oauth2.sdk.http.HTTPRequest$Method
 *  com.nimbusds.oauth2.sdk.id.ClientID
 *  com.nimbusds.oauth2.sdk.util.URLUtils
 */
package com.microsoft.aad.msal4j;

import com.microsoft.aad.msal4j.HTTPContentType;
import com.nimbusds.oauth2.sdk.SerializeException;
import com.nimbusds.oauth2.sdk.auth.ClientAuthentication;
import com.nimbusds.oauth2.sdk.auth.ClientAuthenticationMethod;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.util.URLUtils;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

class ClientAuthenticationPost
extends ClientAuthentication {
    protected ClientAuthenticationPost(ClientAuthenticationMethod method, ClientID clientID) {
        super(method, clientID);
    }

    public Set<String> getFormParameterNames() {
        return Collections.unmodifiableSet(new HashSet<String>(Arrays.asList("client_assertion", "client_assertion_type", "client_id")));
    }

    Map<String, List<String>> toParameters() {
        HashMap<String, List<String>> params = new HashMap<String, List<String>>();
        params.put("client_id", Collections.singletonList(this.getClientID().getValue()));
        return params;
    }

    public void applyTo(HTTPRequest httpRequest) throws SerializeException {
        if (httpRequest.getMethod() != HTTPRequest.Method.POST) {
            throw new SerializeException("The HTTP request method must be POST");
        }
        String ct = String.valueOf(httpRequest.getEntityContentType());
        if (ct == null) {
            throw new SerializeException("Missing HTTP Content-Type header");
        }
        if (!ct.equals(HTTPContentType.ApplicationURLEncoded.contentType)) {
            throw new SerializeException("The HTTP Content-Type header must be " + HTTPContentType.ApplicationURLEncoded.contentType);
        }
        Map params = httpRequest.getQueryParameters();
        params.putAll(this.toParameters());
        String queryString = URLUtils.serializeParameters((Map)params);
        httpRequest.setQuery(queryString);
    }
}

