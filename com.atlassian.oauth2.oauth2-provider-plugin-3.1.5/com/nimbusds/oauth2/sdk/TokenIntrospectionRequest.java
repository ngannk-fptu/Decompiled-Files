/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk;

import com.nimbusds.common.contenttype.ContentType;
import com.nimbusds.oauth2.sdk.AbstractOptionallyAuthenticatedRequest;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.SerializeException;
import com.nimbusds.oauth2.sdk.auth.ClientAuthentication;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.oauth2.sdk.token.RefreshToken;
import com.nimbusds.oauth2.sdk.token.Token;
import com.nimbusds.oauth2.sdk.token.TypelessAccessToken;
import com.nimbusds.oauth2.sdk.util.MultivaluedMapUtils;
import com.nimbusds.oauth2.sdk.util.URLUtils;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.jcip.annotations.Immutable;
import net.minidev.json.JSONObject;

@Immutable
public class TokenIntrospectionRequest
extends AbstractOptionallyAuthenticatedRequest {
    private final Token token;
    private final AccessToken clientAuthz;
    private final Map<String, List<String>> customParams;

    public TokenIntrospectionRequest(URI uri, Token token) {
        this(uri, token, null);
    }

    public TokenIntrospectionRequest(URI uri, Token token, Map<String, List<String>> customParams) {
        super(uri, null);
        if (token == null) {
            throw new IllegalArgumentException("The token must not be null");
        }
        this.token = token;
        this.clientAuthz = null;
        this.customParams = customParams != null ? customParams : Collections.emptyMap();
    }

    public TokenIntrospectionRequest(URI uri, ClientAuthentication clientAuth, Token token) {
        this(uri, clientAuth, token, null);
    }

    public TokenIntrospectionRequest(URI uri, ClientAuthentication clientAuth, Token token, Map<String, List<String>> customParams) {
        super(uri, clientAuth);
        if (token == null) {
            throw new IllegalArgumentException("The token must not be null");
        }
        this.token = token;
        this.clientAuthz = null;
        this.customParams = customParams != null ? customParams : Collections.emptyMap();
    }

    public TokenIntrospectionRequest(URI uri, AccessToken clientAuthz, Token token) {
        this(uri, clientAuthz, token, null);
    }

    public TokenIntrospectionRequest(URI uri, AccessToken clientAuthz, Token token, Map<String, List<String>> customParams) {
        super(uri, null);
        if (token == null) {
            throw new IllegalArgumentException("The token must not be null");
        }
        this.token = token;
        this.clientAuthz = clientAuthz;
        this.customParams = customParams != null ? customParams : Collections.emptyMap();
    }

    public AccessToken getClientAuthorization() {
        return this.clientAuthz;
    }

    public Token getToken() {
        return this.token;
    }

    public Map<String, List<String>> getCustomParameters() {
        return this.customParams;
    }

    @Override
    public HTTPRequest toHTTPRequest() {
        if (this.getEndpointURI() == null) {
            throw new SerializeException("The endpoint URI is not specified");
        }
        HTTPRequest httpRequest = new HTTPRequest(HTTPRequest.Method.POST, this.getEndpointURI());
        httpRequest.setEntityContentType(ContentType.APPLICATION_URLENCODED);
        HashMap<String, List<String>> params = new HashMap<String, List<String>>();
        params.put("token", Collections.singletonList(this.token.getValue()));
        if (this.token instanceof AccessToken) {
            params.put("token_type_hint", Collections.singletonList("access_token"));
        } else if (this.token instanceof RefreshToken) {
            params.put("token_type_hint", Collections.singletonList("refresh_token"));
        }
        params.putAll(this.customParams);
        httpRequest.setQuery(URLUtils.serializeParameters(params));
        if (this.getClientAuthentication() != null) {
            this.getClientAuthentication().applyTo(httpRequest);
        }
        if (this.clientAuthz != null) {
            httpRequest.setAuthorization(this.clientAuthz.toAuthorizationHeader());
        }
        return httpRequest;
    }

    public static TokenIntrospectionRequest parse(HTTPRequest httpRequest) throws ParseException {
        httpRequest.ensureMethod(HTTPRequest.Method.POST);
        httpRequest.ensureEntityContentType(ContentType.APPLICATION_URLENCODED);
        Map<String, List<String>> params = httpRequest.getQueryParameters();
        final String tokenValue = MultivaluedMapUtils.removeAndReturnFirstValue(params, "token");
        if (tokenValue == null || tokenValue.isEmpty()) {
            throw new ParseException("Missing required token parameter");
        }
        Token token = null;
        String tokenTypeHint = MultivaluedMapUtils.removeAndReturnFirstValue(params, "token_type_hint");
        if (tokenTypeHint == null) {
            token = new Token(){
                private static final long serialVersionUID = 8491102820261331059L;

                @Override
                public String getValue() {
                    return tokenValue;
                }

                @Override
                public Set<String> getParameterNames() {
                    return Collections.emptySet();
                }

                @Override
                public JSONObject toJSONObject() {
                    return new JSONObject();
                }

                @Override
                public boolean equals(Object other) {
                    return other instanceof Token && other.toString().equals(tokenValue);
                }
            };
        } else if (tokenTypeHint.equals("access_token")) {
            token = new TypelessAccessToken(tokenValue);
        } else if (tokenTypeHint.equals("refresh_token")) {
            token = new RefreshToken(tokenValue);
        }
        ClientAuthentication clientAuth = ClientAuthentication.parse(httpRequest);
        AccessToken clientAuthz = null;
        if (clientAuth == null && httpRequest.getAuthorization() != null) {
            clientAuthz = AccessToken.parse(httpRequest.getAuthorization());
        }
        URI uri = httpRequest.getURI();
        if (clientAuthz != null) {
            return new TokenIntrospectionRequest(uri, clientAuthz, token, params);
        }
        return new TokenIntrospectionRequest(uri, clientAuth, token, params);
    }
}

