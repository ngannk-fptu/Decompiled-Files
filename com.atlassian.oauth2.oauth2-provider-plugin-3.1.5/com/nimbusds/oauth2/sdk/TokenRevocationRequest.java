/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk;

import com.nimbusds.common.contenttype.ContentType;
import com.nimbusds.oauth2.sdk.AbstractOptionallyIdentifiedRequest;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.SerializeException;
import com.nimbusds.oauth2.sdk.auth.ClientAuthentication;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.oauth2.sdk.token.RefreshToken;
import com.nimbusds.oauth2.sdk.token.Token;
import com.nimbusds.oauth2.sdk.token.TypelessAccessToken;
import com.nimbusds.oauth2.sdk.util.MultivaluedMapUtils;
import com.nimbusds.oauth2.sdk.util.StringUtils;
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
public final class TokenRevocationRequest
extends AbstractOptionallyIdentifiedRequest {
    private final Token token;

    public TokenRevocationRequest(URI uri, ClientAuthentication clientAuth, Token token) {
        super(uri, clientAuth);
        if (clientAuth == null) {
            throw new IllegalArgumentException("The client authentication must not be null");
        }
        if (token == null) {
            throw new IllegalArgumentException("The token must not be null");
        }
        this.token = token;
    }

    public TokenRevocationRequest(URI uri, ClientID clientID, Token token) {
        super(uri, clientID);
        if (clientID == null) {
            throw new IllegalArgumentException("The client ID must not be null");
        }
        if (token == null) {
            throw new IllegalArgumentException("The token must not be null");
        }
        this.token = token;
    }

    public Token getToken() {
        return this.token;
    }

    @Override
    public HTTPRequest toHTTPRequest() {
        if (this.getEndpointURI() == null) {
            throw new SerializeException("The endpoint URI is not specified");
        }
        HTTPRequest httpRequest = new HTTPRequest(HTTPRequest.Method.POST, this.getEndpointURI());
        httpRequest.setEntityContentType(ContentType.APPLICATION_URLENCODED);
        HashMap<String, List<String>> params = new HashMap<String, List<String>>();
        if (this.getClientID() != null) {
            params.put("client_id", Collections.singletonList(this.getClientID().getValue()));
        }
        params.put("token", Collections.singletonList(this.token.getValue()));
        if (this.token instanceof AccessToken) {
            params.put("token_type_hint", Collections.singletonList("access_token"));
        } else if (this.token instanceof RefreshToken) {
            params.put("token_type_hint", Collections.singletonList("refresh_token"));
        }
        httpRequest.setQuery(URLUtils.serializeParameters(params));
        if (this.getClientAuthentication() != null) {
            this.getClientAuthentication().applyTo(httpRequest);
        }
        return httpRequest;
    }

    public static TokenRevocationRequest parse(HTTPRequest httpRequest) throws ParseException {
        httpRequest.ensureMethod(HTTPRequest.Method.POST);
        httpRequest.ensureEntityContentType(ContentType.APPLICATION_URLENCODED);
        Map<String, List<String>> params = httpRequest.getQueryParameters();
        final String tokenValue = MultivaluedMapUtils.getFirstValue(params, "token");
        if (tokenValue == null || tokenValue.isEmpty()) {
            throw new ParseException("Missing required token parameter");
        }
        Token token = null;
        String tokenTypeHint = MultivaluedMapUtils.getFirstValue(params, "token_type_hint");
        if (tokenTypeHint == null) {
            token = new Token(){
                private static final long serialVersionUID = 8606135001277432930L;

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
        URI uri = httpRequest.getURI();
        ClientAuthentication clientAuth = ClientAuthentication.parse(httpRequest);
        if (clientAuth != null) {
            return new TokenRevocationRequest(uri, clientAuth, token);
        }
        String clientIDString = MultivaluedMapUtils.getFirstValue(params, "client_id");
        if (StringUtils.isBlank(clientIDString)) {
            throw new ParseException("Invalid token revocation request: No client authentication or client_id parameter found");
        }
        return new TokenRevocationRequest(uri, new ClientID(clientIDString), token);
    }
}

