/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.nimbusds.common.contenttype.ContentType
 *  com.nimbusds.jwt.JWT
 *  com.nimbusds.jwt.JWTParser
 *  com.nimbusds.jwt.PlainJWT
 *  net.jcip.annotations.Immutable
 */
package com.nimbusds.openid.connect.sdk;

import com.nimbusds.common.contenttype.ContentType;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.jwt.PlainJWT;
import com.nimbusds.oauth2.sdk.AbstractRequest;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.SerializeException;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.util.MultivaluedMapUtils;
import com.nimbusds.oauth2.sdk.util.URIUtils;
import com.nimbusds.oauth2.sdk.util.URLUtils;
import java.net.URI;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.jcip.annotations.Immutable;

@Immutable
public class BackChannelLogoutRequest
extends AbstractRequest {
    private final JWT logoutToken;

    public BackChannelLogoutRequest(URI uri, JWT logoutToken) {
        super(uri);
        if (logoutToken == null) {
            throw new IllegalArgumentException("The logout token must not be null");
        }
        if (logoutToken instanceof PlainJWT) {
            throw new IllegalArgumentException("The logout token must not be unsecured (plain)");
        }
        this.logoutToken = logoutToken;
    }

    public JWT getLogoutToken() {
        return this.logoutToken;
    }

    public Map<String, List<String>> toParameters() {
        LinkedHashMap<String, List<String>> params = new LinkedHashMap<String, List<String>>();
        try {
            params.put("logout_token", Collections.singletonList(this.logoutToken.serialize()));
        }
        catch (IllegalStateException e) {
            throw new SerializeException("Couldn't serialize logout token: " + e.getMessage(), e);
        }
        return params;
    }

    @Override
    public HTTPRequest toHTTPRequest() {
        if (this.getEndpointURI() == null) {
            throw new SerializeException("The endpoint URI is not specified");
        }
        HTTPRequest httpRequest = new HTTPRequest(HTTPRequest.Method.POST, this.getEndpointURI());
        httpRequest.setEntityContentType(ContentType.APPLICATION_URLENCODED);
        httpRequest.setQuery(URLUtils.serializeParameters(this.toParameters()));
        return httpRequest;
    }

    public static BackChannelLogoutRequest parse(Map<String, List<String>> params) throws ParseException {
        return BackChannelLogoutRequest.parse(null, params);
    }

    public static BackChannelLogoutRequest parse(URI uri, Map<String, List<String>> params) throws ParseException {
        JWT logoutToken;
        String logoutTokenString = MultivaluedMapUtils.getFirstValue(params, "logout_token");
        if (logoutTokenString == null) {
            throw new ParseException("Missing logout_token parameter");
        }
        try {
            logoutToken = JWTParser.parse((String)logoutTokenString);
        }
        catch (java.text.ParseException e) {
            throw new ParseException("Invalid logout token: " + e.getMessage(), e);
        }
        try {
            return new BackChannelLogoutRequest(uri, logoutToken);
        }
        catch (IllegalArgumentException e) {
            throw new ParseException(e.getMessage(), e);
        }
    }

    public static BackChannelLogoutRequest parse(HTTPRequest httpRequest) throws ParseException {
        if (!HTTPRequest.Method.POST.equals((Object)httpRequest.getMethod())) {
            throw new ParseException("HTTP POST required");
        }
        String query = httpRequest.getQuery();
        if (query == null) {
            throw new ParseException("Missing URI query string");
        }
        Map<String, List<String>> params = URLUtils.parseParameters(query);
        return BackChannelLogoutRequest.parse(URIUtils.getBaseURI(httpRequest.getURI()), params);
    }
}

