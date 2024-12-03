/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.nimbusds.jwt.JWT
 *  com.nimbusds.jwt.JWTParser
 *  net.jcip.annotations.Immutable
 */
package com.nimbusds.oauth2.sdk;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.oauth2.sdk.AuthorizationResponse;
import com.nimbusds.oauth2.sdk.ErrorObject;
import com.nimbusds.oauth2.sdk.ErrorResponse;
import com.nimbusds.oauth2.sdk.OAuth2Error;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.ResponseMode;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.oauth2.sdk.id.State;
import com.nimbusds.oauth2.sdk.util.MultivaluedMapUtils;
import com.nimbusds.oauth2.sdk.util.StringUtils;
import com.nimbusds.oauth2.sdk.util.URIUtils;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.jcip.annotations.Immutable;

@Immutable
public class AuthorizationErrorResponse
extends AuthorizationResponse
implements ErrorResponse {
    private static final Set<ErrorObject> stdErrors = new HashSet<ErrorObject>();
    private final ErrorObject error;

    public static Set<ErrorObject> getStandardErrors() {
        return Collections.unmodifiableSet(stdErrors);
    }

    public AuthorizationErrorResponse(URI redirectURI, ErrorObject error, State state, ResponseMode rm) {
        this(redirectURI, error, state, null, rm);
    }

    public AuthorizationErrorResponse(URI redirectURI, ErrorObject error, State state, Issuer issuer, ResponseMode rm) {
        super(redirectURI, state, issuer, rm);
        if (error == null) {
            throw new IllegalArgumentException("The error must not be null");
        }
        this.error = error;
    }

    public AuthorizationErrorResponse(URI redirectURI, JWT jwtResponse, ResponseMode rm) {
        super(redirectURI, jwtResponse, rm);
        this.error = null;
    }

    @Override
    public boolean indicatesSuccess() {
        return false;
    }

    @Override
    public ErrorObject getErrorObject() {
        return this.error;
    }

    @Override
    public ResponseMode impliedResponseMode() {
        return this.getResponseMode() != null ? this.getResponseMode() : ResponseMode.QUERY;
    }

    @Override
    public Map<String, List<String>> toParameters() {
        HashMap<String, List<String>> params = new HashMap<String, List<String>>();
        if (this.getJWTResponse() != null) {
            params.put("response", Collections.singletonList(this.getJWTResponse().serialize()));
            return params;
        }
        params.putAll(this.getErrorObject().toParameters());
        if (this.getState() != null) {
            params.put("state", Collections.singletonList(this.getState().getValue()));
        }
        if (this.getIssuer() != null) {
            params.put("iss", Collections.singletonList(this.getIssuer().getValue()));
        }
        return params;
    }

    public static AuthorizationErrorResponse parse(URI redirectURI, Map<String, List<String>> params) throws ParseException {
        String responseString = MultivaluedMapUtils.getFirstValue(params, "response");
        if (responseString != null) {
            JWT jwtResponse;
            try {
                jwtResponse = JWTParser.parse((String)responseString);
            }
            catch (java.text.ParseException e) {
                throw new ParseException("Invalid JWT response: " + e.getMessage(), e);
            }
            return new AuthorizationErrorResponse(redirectURI, jwtResponse, ResponseMode.JWT);
        }
        ErrorObject error = ErrorObject.parse(params);
        if (StringUtils.isBlank(error.getCode())) {
            throw new ParseException("Missing error code");
        }
        error = error.setHTTPStatusCode(302);
        State state = State.parse(MultivaluedMapUtils.getFirstValue(params, "state"));
        Issuer issuer = Issuer.parse(MultivaluedMapUtils.getFirstValue(params, "iss"));
        return new AuthorizationErrorResponse(redirectURI, error, state, issuer, null);
    }

    public static AuthorizationErrorResponse parse(URI uri) throws ParseException {
        return AuthorizationErrorResponse.parse(URIUtils.getBaseURI(uri), AuthorizationErrorResponse.parseResponseParameters(uri));
    }

    public static AuthorizationErrorResponse parse(HTTPResponse httpResponse) throws ParseException {
        URI location = httpResponse.getLocation();
        if (location == null) {
            throw new ParseException("Missing redirection URL / HTTP Location header");
        }
        return AuthorizationErrorResponse.parse(location);
    }

    public static AuthorizationErrorResponse parse(HTTPRequest httpRequest) throws ParseException {
        return AuthorizationErrorResponse.parse(httpRequest.getURI(), AuthorizationErrorResponse.parseResponseParameters(httpRequest));
    }

    static {
        stdErrors.add(OAuth2Error.INVALID_REQUEST);
        stdErrors.add(OAuth2Error.UNAUTHORIZED_CLIENT);
        stdErrors.add(OAuth2Error.ACCESS_DENIED);
        stdErrors.add(OAuth2Error.UNSUPPORTED_RESPONSE_TYPE);
        stdErrors.add(OAuth2Error.INVALID_SCOPE);
        stdErrors.add(OAuth2Error.SERVER_ERROR);
        stdErrors.add(OAuth2Error.TEMPORARILY_UNAVAILABLE);
    }
}

