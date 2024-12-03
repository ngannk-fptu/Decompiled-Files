/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk;

import com.nimbusds.common.contenttype.ContentType;
import com.nimbusds.oauth2.sdk.AbstractOptionallyAuthenticatedRequest;
import com.nimbusds.oauth2.sdk.AuthorizationRequest;
import com.nimbusds.oauth2.sdk.OAuth2Error;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.SerializeException;
import com.nimbusds.oauth2.sdk.auth.ClientAuthentication;
import com.nimbusds.oauth2.sdk.auth.ClientSecretBasic;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.util.MultivaluedMapUtils;
import com.nimbusds.oauth2.sdk.util.StringUtils;
import com.nimbusds.oauth2.sdk.util.URLUtils;
import com.nimbusds.openid.connect.sdk.AuthenticationRequest;
import com.nimbusds.openid.connect.sdk.op.AuthenticationRequestDetector;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import net.jcip.annotations.Immutable;

@Immutable
public class PushedAuthorizationRequest
extends AbstractOptionallyAuthenticatedRequest {
    private final AuthorizationRequest authzRequest;

    public PushedAuthorizationRequest(URI uri, ClientAuthentication clientAuth, AuthorizationRequest authzRequest) {
        super(uri, clientAuth);
        if (clientAuth == null) {
            throw new IllegalArgumentException("The client authentication must not be null");
        }
        if (authzRequest == null) {
            throw new IllegalArgumentException("The authorization request must not be null");
        }
        if (authzRequest.getRequestURI() != null) {
            throw new IllegalArgumentException("Authorization request_uri parameter not allowed");
        }
        this.authzRequest = authzRequest;
    }

    public PushedAuthorizationRequest(URI uri, AuthorizationRequest authzRequest) {
        super(uri, null);
        if (authzRequest == null) {
            throw new IllegalArgumentException("The authorization request must not be null");
        }
        if (authzRequest.getRequestURI() != null) {
            throw new IllegalArgumentException("Authorization request_uri parameter not allowed");
        }
        this.authzRequest = authzRequest;
    }

    public AuthorizationRequest getAuthorizationRequest() {
        return this.authzRequest;
    }

    @Override
    public HTTPRequest toHTTPRequest() {
        if (this.getEndpointURI() == null) {
            throw new SerializeException("The endpoint URI is not specified");
        }
        HTTPRequest httpRequest = new HTTPRequest(HTTPRequest.Method.POST, this.getEndpointURI());
        httpRequest.setEntityContentType(ContentType.APPLICATION_URLENCODED);
        if (this.getClientAuthentication() != null) {
            this.getClientAuthentication().applyTo(httpRequest);
        }
        Map<String, List<String>> params = httpRequest.getQueryParameters();
        params.putAll(this.authzRequest.toParameters());
        httpRequest.setQuery(URLUtils.serializeParameters(params));
        return httpRequest;
    }

    public static PushedAuthorizationRequest parse(HTTPRequest httpRequest) throws ParseException {
        AuthorizationRequest authzRequest;
        ClientAuthentication clientAuth;
        URI uri;
        try {
            uri = httpRequest.getURL().toURI();
        }
        catch (URISyntaxException e) {
            throw new ParseException(e.getMessage(), e);
        }
        httpRequest.ensureMethod(HTTPRequest.Method.POST);
        httpRequest.ensureEntityContentType(ContentType.APPLICATION_URLENCODED);
        try {
            clientAuth = ClientAuthentication.parse(httpRequest);
        }
        catch (ParseException e) {
            throw new ParseException(e.getMessage(), OAuth2Error.INVALID_REQUEST.appendDescription(": " + e.getMessage()));
        }
        Map<String, List<String>> params = httpRequest.getQueryParameters();
        if (clientAuth instanceof ClientSecretBasic && (StringUtils.isNotBlank(MultivaluedMapUtils.getFirstValue(params, "client_assertion")) || StringUtils.isNotBlank(MultivaluedMapUtils.getFirstValue(params, "client_assertion_type")))) {
            String msg = "Multiple conflicting client authentication methods found: Basic and JWT assertion";
            throw new ParseException(msg, OAuth2Error.INVALID_REQUEST.appendDescription(": " + msg));
        }
        if (!params.containsKey("client_id") && clientAuth != null) {
            params.put("client_id", Collections.singletonList(clientAuth.getClientID().getValue()));
        }
        if ((authzRequest = AuthenticationRequestDetector.isLikelyOpenID(params) ? AuthenticationRequest.parse(params) : AuthorizationRequest.parse(params)).getRequestURI() != null) {
            String msg = "Authorization request_uri parameter not allowed";
            throw new ParseException(msg, OAuth2Error.INVALID_REQUEST.appendDescription(": " + msg));
        }
        if (clientAuth != null) {
            return new PushedAuthorizationRequest(uri, clientAuth, authzRequest);
        }
        return new PushedAuthorizationRequest(uri, authzRequest);
    }
}

