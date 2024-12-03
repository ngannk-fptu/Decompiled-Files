/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.nimbusds.jwt.JWT
 *  com.nimbusds.jwt.JWTParser
 *  net.jcip.annotations.Immutable
 *  net.minidev.json.JSONObject
 */
package com.nimbusds.oauth2.sdk;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.oauth2.sdk.AuthorizationCode;
import com.nimbusds.oauth2.sdk.AuthorizationResponse;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.ResponseMode;
import com.nimbusds.oauth2.sdk.ResponseType;
import com.nimbusds.oauth2.sdk.SuccessResponse;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.oauth2.sdk.id.State;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.oauth2.sdk.util.MultivaluedMapUtils;
import com.nimbusds.oauth2.sdk.util.URIUtils;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.jcip.annotations.Immutable;
import net.minidev.json.JSONObject;

@Immutable
public class AuthorizationSuccessResponse
extends AuthorizationResponse
implements SuccessResponse {
    private final AuthorizationCode code;
    private final AccessToken accessToken;

    public AuthorizationSuccessResponse(URI redirectURI, AuthorizationCode code, AccessToken accessToken, State state, ResponseMode rm) {
        this(redirectURI, code, accessToken, state, null, rm);
    }

    public AuthorizationSuccessResponse(URI redirectURI, AuthorizationCode code, AccessToken accessToken, State state, Issuer issuer, ResponseMode rm) {
        super(redirectURI, state, issuer, rm);
        this.code = code;
        this.accessToken = accessToken;
    }

    public AuthorizationSuccessResponse(URI redirectURI, JWT jwtResponse, ResponseMode rm) {
        super(redirectURI, jwtResponse, rm);
        this.code = null;
        this.accessToken = null;
    }

    @Override
    public boolean indicatesSuccess() {
        return true;
    }

    public ResponseType impliedResponseType() {
        ResponseType rt = new ResponseType();
        if (this.code != null) {
            rt.add(ResponseType.Value.CODE);
        }
        if (this.accessToken != null) {
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
        if (this.accessToken != null) {
            return ResponseMode.FRAGMENT;
        }
        return ResponseMode.QUERY;
    }

    public AuthorizationCode getAuthorizationCode() {
        return this.code;
    }

    public AccessToken getAccessToken() {
        return this.accessToken;
    }

    @Override
    public Map<String, List<String>> toParameters() {
        HashMap<String, List<String>> params = new HashMap<String, List<String>>();
        if (this.getJWTResponse() != null) {
            params.put("response", Collections.singletonList(this.getJWTResponse().serialize()));
            return params;
        }
        if (this.code != null) {
            params.put("code", Collections.singletonList(this.code.getValue()));
        }
        if (this.accessToken != null) {
            for (Map.Entry entry : this.accessToken.toJSONObject().entrySet()) {
                params.put((String)entry.getKey(), Collections.singletonList(entry.getValue().toString()));
            }
        }
        if (this.getState() != null) {
            params.put("state", Collections.singletonList(this.getState().getValue()));
        }
        if (this.getIssuer() != null) {
            params.put("iss", Collections.singletonList(this.getIssuer().getValue()));
        }
        return params;
    }

    public static AuthorizationSuccessResponse parse(URI redirectURI, Map<String, List<String>> params) throws ParseException {
        String responseString = MultivaluedMapUtils.getFirstValue(params, "response");
        if (responseString != null) {
            JWT jwtResponse;
            try {
                jwtResponse = JWTParser.parse((String)responseString);
            }
            catch (java.text.ParseException e) {
                throw new ParseException("Invalid JWT response: " + e.getMessage(), e);
            }
            return new AuthorizationSuccessResponse(redirectURI, jwtResponse, ResponseMode.JWT);
        }
        AuthorizationCode code = null;
        if (params.get("code") != null) {
            code = new AuthorizationCode(MultivaluedMapUtils.getFirstValue(params, "code"));
        }
        AccessToken accessToken = null;
        if (params.get("access_token") != null) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.putAll(MultivaluedMapUtils.toSingleValuedMap(params));
            accessToken = AccessToken.parse(jsonObject);
        }
        State state = State.parse(MultivaluedMapUtils.getFirstValue(params, "state"));
        Issuer issuer = Issuer.parse(MultivaluedMapUtils.getFirstValue(params, "iss"));
        return new AuthorizationSuccessResponse(redirectURI, code, accessToken, state, issuer, null);
    }

    public static AuthorizationSuccessResponse parse(URI uri) throws ParseException {
        return AuthorizationSuccessResponse.parse(URIUtils.getBaseURI(uri), AuthorizationSuccessResponse.parseResponseParameters(uri));
    }

    public static AuthorizationSuccessResponse parse(HTTPResponse httpResponse) throws ParseException {
        URI location = httpResponse.getLocation();
        if (location == null) {
            throw new ParseException("Missing redirection URL / HTTP Location header");
        }
        return AuthorizationSuccessResponse.parse(location);
    }

    public static AuthorizationSuccessResponse parse(HTTPRequest httpRequest) throws ParseException {
        return AuthorizationSuccessResponse.parse(httpRequest.getURI(), AuthorizationSuccessResponse.parseResponseParameters(httpRequest));
    }
}

