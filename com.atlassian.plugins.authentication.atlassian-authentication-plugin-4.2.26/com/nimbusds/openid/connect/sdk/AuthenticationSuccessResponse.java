/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.oauth2.sdk.AuthorizationCode;
import com.nimbusds.oauth2.sdk.AuthorizationSuccessResponse;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.ResponseMode;
import com.nimbusds.oauth2.sdk.ResponseType;
import com.nimbusds.oauth2.sdk.SerializeException;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.oauth2.sdk.id.State;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.oauth2.sdk.util.MultivaluedMapUtils;
import com.nimbusds.oauth2.sdk.util.StringUtils;
import com.nimbusds.oauth2.sdk.util.URIUtils;
import com.nimbusds.openid.connect.sdk.AuthenticationErrorResponse;
import com.nimbusds.openid.connect.sdk.AuthenticationResponse;
import com.nimbusds.openid.connect.sdk.OIDCResponseTypeValue;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import net.jcip.annotations.Immutable;

@Immutable
public class AuthenticationSuccessResponse
extends AuthorizationSuccessResponse
implements AuthenticationResponse {
    private final JWT idToken;
    private final State sessionState;

    public AuthenticationSuccessResponse(URI redirectURI, AuthorizationCode code, JWT idToken, AccessToken accessToken, State state, State sessionState, ResponseMode rm) {
        this(redirectURI, code, idToken, accessToken, state, sessionState, null, rm);
    }

    public AuthenticationSuccessResponse(URI redirectURI, AuthorizationCode code, JWT idToken, AccessToken accessToken, State state, State sessionState, Issuer issuer, ResponseMode rm) {
        super(redirectURI, code, accessToken, state, issuer, rm);
        this.idToken = idToken;
        this.sessionState = sessionState;
    }

    public AuthenticationSuccessResponse(URI redirectURI, JWT jwtResponse, ResponseMode rm) {
        super(redirectURI, jwtResponse, rm);
        this.idToken = null;
        this.sessionState = null;
    }

    @Override
    public ResponseType impliedResponseType() {
        ResponseType rt = new ResponseType();
        if (this.getAuthorizationCode() != null) {
            rt.add(ResponseType.Value.CODE);
        }
        if (this.getIDToken() != null) {
            rt.add(OIDCResponseTypeValue.ID_TOKEN);
        }
        if (this.getAccessToken() != null) {
            rt.add(ResponseType.Value.TOKEN);
        }
        return rt;
    }

    @Override
    public ResponseMode impliedResponseMode() {
        if (this.getResponseMode() != null) {
            return this.getResponseMode();
        }
        if (this.getJWTResponse() != null) {
            return ResponseMode.JWT;
        }
        if (this.getAccessToken() != null || this.getIDToken() != null) {
            return ResponseMode.FRAGMENT;
        }
        return ResponseMode.QUERY;
    }

    public JWT getIDToken() {
        return this.idToken;
    }

    public State getSessionState() {
        return this.sessionState;
    }

    @Override
    public Map<String, List<String>> toParameters() {
        Map<String, List<String>> params = super.toParameters();
        if (this.getJWTResponse() != null) {
            return params;
        }
        if (this.idToken != null) {
            try {
                params.put("id_token", Collections.singletonList(this.idToken.serialize()));
            }
            catch (IllegalStateException e) {
                throw new SerializeException("Couldn't serialize ID token: " + e.getMessage(), e);
            }
        }
        if (this.sessionState != null) {
            params.put("session_state", Collections.singletonList(this.sessionState.getValue()));
        }
        return params;
    }

    @Override
    public AuthenticationSuccessResponse toSuccessResponse() {
        return this;
    }

    @Override
    public AuthenticationErrorResponse toErrorResponse() {
        throw new ClassCastException("Cannot cast to AuthenticationErrorResponse");
    }

    public static AuthenticationSuccessResponse parse(URI redirectURI, Map<String, List<String>> params) throws ParseException {
        AuthorizationSuccessResponse asr = AuthorizationSuccessResponse.parse(redirectURI, params);
        if (asr.getJWTResponse() != null) {
            return new AuthenticationSuccessResponse(redirectURI, asr.getJWTResponse(), asr.getResponseMode());
        }
        String idTokenString = MultivaluedMapUtils.getFirstValue(params, "id_token");
        JWT idToken = null;
        if (idTokenString != null) {
            try {
                idToken = JWTParser.parse(idTokenString);
            }
            catch (java.text.ParseException e) {
                throw new ParseException("Invalid ID Token JWT: " + e.getMessage(), e);
            }
        }
        State sessionState = null;
        if (StringUtils.isNotBlank(MultivaluedMapUtils.getFirstValue(params, "session_state"))) {
            sessionState = new State(MultivaluedMapUtils.getFirstValue(params, "session_state"));
        }
        return new AuthenticationSuccessResponse(redirectURI, asr.getAuthorizationCode(), idToken, asr.getAccessToken(), asr.getState(), sessionState, asr.getIssuer(), null);
    }

    public static AuthenticationSuccessResponse parse(URI uri) throws ParseException {
        return AuthenticationSuccessResponse.parse(URIUtils.getBaseURI(uri), AuthenticationSuccessResponse.parseResponseParameters(uri));
    }

    public static AuthenticationSuccessResponse parse(HTTPResponse httpResponse) throws ParseException {
        URI location = httpResponse.getLocation();
        if (location == null) {
            throw new ParseException("Missing redirection URI / HTTP Location header");
        }
        return AuthenticationSuccessResponse.parse(location);
    }

    public static AuthenticationSuccessResponse parse(HTTPRequest httpRequest) throws ParseException {
        return AuthenticationSuccessResponse.parse(httpRequest.getURI(), AuthenticationSuccessResponse.parseResponseParameters(httpRequest));
    }
}

