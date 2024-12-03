/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk.auth;

import com.nimbusds.common.contenttype.ContentType;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.SerializeException;
import com.nimbusds.oauth2.sdk.auth.ClientAuthenticationMethod;
import com.nimbusds.oauth2.sdk.auth.PlainClientSecret;
import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.util.MultivaluedMapUtils;
import com.nimbusds.oauth2.sdk.util.URLUtils;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.jcip.annotations.Immutable;

@Immutable
public final class ClientSecretPost
extends PlainClientSecret {
    public ClientSecretPost(ClientID clientID, Secret secret) {
        super(ClientAuthenticationMethod.CLIENT_SECRET_POST, clientID, secret);
    }

    @Override
    public Set<String> getFormParameterNames() {
        return Collections.unmodifiableSet(new HashSet<String>(Arrays.asList("client_id", "client_secret")));
    }

    public Map<String, List<String>> toParameters() {
        HashMap<String, List<String>> params = new HashMap<String, List<String>>();
        params.put("client_id", Collections.singletonList(this.getClientID().getValue()));
        params.put("client_secret", Collections.singletonList(this.getClientSecret().getValue()));
        return params;
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
        if (!ct.matches(ContentType.APPLICATION_URLENCODED)) {
            throw new SerializeException("The HTTP Content-Type header must be " + ContentType.APPLICATION_URLENCODED);
        }
        Map<String, List<String>> params = httpRequest.getQueryParameters();
        params.putAll(this.toParameters());
        String queryString = URLUtils.serializeParameters(params);
        httpRequest.setQuery(queryString);
    }

    public static ClientSecretPost parse(Map<String, List<String>> params) throws ParseException {
        String clientIDString = MultivaluedMapUtils.getFirstValue(params, "client_id");
        if (clientIDString == null) {
            throw new ParseException("Malformed client secret post authentication: Missing client_id parameter");
        }
        String secretValue = MultivaluedMapUtils.getFirstValue(params, "client_secret");
        if (secretValue == null) {
            throw new ParseException("Malformed client secret post authentication: Missing client_secret parameter");
        }
        return new ClientSecretPost(new ClientID(clientIDString), new Secret(secretValue));
    }

    public static ClientSecretPost parse(String paramsString) throws ParseException {
        Map<String, List<String>> params = URLUtils.parseParameters(paramsString);
        return ClientSecretPost.parse(params);
    }

    public static ClientSecretPost parse(HTTPRequest httpRequest) throws ParseException {
        httpRequest.ensureMethod(HTTPRequest.Method.POST);
        httpRequest.ensureEntityContentType(ContentType.APPLICATION_URLENCODED);
        return ClientSecretPost.parse(httpRequest.getQueryParameters());
    }
}

