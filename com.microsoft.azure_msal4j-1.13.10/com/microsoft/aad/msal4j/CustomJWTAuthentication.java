/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.nimbusds.common.contenttype.ContentType
 *  com.nimbusds.oauth2.sdk.SerializeException
 *  com.nimbusds.oauth2.sdk.auth.ClientAuthentication
 *  com.nimbusds.oauth2.sdk.auth.ClientAuthenticationMethod
 *  com.nimbusds.oauth2.sdk.http.HTTPRequest
 *  com.nimbusds.oauth2.sdk.http.HTTPRequest$Method
 *  com.nimbusds.oauth2.sdk.id.ClientID
 *  com.nimbusds.oauth2.sdk.util.URLUtils
 */
package com.microsoft.aad.msal4j;

import com.microsoft.aad.msal4j.ClientAssertion;
import com.nimbusds.common.contenttype.ContentType;
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

public class CustomJWTAuthentication
extends ClientAuthentication {
    private ClientAssertion clientAssertion;

    protected CustomJWTAuthentication(ClientAuthenticationMethod method, ClientAssertion clientAssertion, ClientID clientID) {
        super(method, clientID);
        this.clientAssertion = clientAssertion;
    }

    public Set<String> getFormParameterNames() {
        return Collections.unmodifiableSet(new HashSet<String>(Arrays.asList("client_assertion", "client_assertion_type", "client_id")));
    }

    public void applyTo(HTTPRequest httpRequest) {
        if (httpRequest.getMethod() != HTTPRequest.Method.POST) {
            throw new SerializeException("The HTTP request method must be POST");
        }
        ContentType ct = httpRequest.getEntityContentType();
        if (ct == null) {
            throw new SerializeException("Missing HTTP Content-Type header");
        }
        if (!ct.matches(ContentType.APPLICATION_URLENCODED)) {
            throw new SerializeException("The HTTP Content-Type header must be " + ContentType.APPLICATION_URLENCODED);
        }
        Map params = httpRequest.getQueryParameters();
        params.putAll(this.toParameters());
        String queryString = URLUtils.serializeParameters((Map)params);
        httpRequest.setQuery(queryString);
    }

    public Map<String, List<String>> toParameters() {
        HashMap<String, List<String>> params = new HashMap<String, List<String>>();
        try {
            params.put("client_assertion", Collections.singletonList(this.clientAssertion.assertion()));
        }
        catch (IllegalStateException var3) {
            throw new SerializeException("Couldn't serialize JWT to a client assertion string: " + var3.getMessage(), (Throwable)var3);
        }
        params.put("client_assertion_type", Collections.singletonList("urn:ietf:params:oauth:client-assertion-type:jwt-bearer"));
        params.put("client_id", Collections.singletonList(this.getClientID().getValue()));
        return params;
    }
}

