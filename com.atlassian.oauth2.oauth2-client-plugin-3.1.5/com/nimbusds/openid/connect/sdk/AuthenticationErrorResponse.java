/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk;

import com.nimbusds.jwt.JWT;
import com.nimbusds.oauth2.sdk.AuthorizationErrorResponse;
import com.nimbusds.oauth2.sdk.ErrorObject;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.ResponseMode;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.oauth2.sdk.id.State;
import com.nimbusds.openid.connect.sdk.AuthenticationResponse;
import com.nimbusds.openid.connect.sdk.AuthenticationSuccessResponse;
import com.nimbusds.openid.connect.sdk.OIDCError;
import java.net.URI;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.jcip.annotations.Immutable;

@Immutable
public class AuthenticationErrorResponse
extends AuthorizationErrorResponse
implements AuthenticationResponse {
    private static final Set<ErrorObject> stdErrors = new HashSet<ErrorObject>();

    public static Set<ErrorObject> getStandardErrors() {
        return Collections.unmodifiableSet(stdErrors);
    }

    public AuthenticationErrorResponse(URI redirectURI, ErrorObject error, State state, ResponseMode rm) {
        this(redirectURI, error, state, null, rm);
    }

    public AuthenticationErrorResponse(URI redirectURI, ErrorObject error, State state, Issuer issuer, ResponseMode rm) {
        super(redirectURI, error, state, issuer, rm);
    }

    public AuthenticationErrorResponse(URI redirectURI, JWT jwtResponse, ResponseMode rm) {
        super(redirectURI, jwtResponse, rm);
    }

    @Override
    public AuthenticationSuccessResponse toSuccessResponse() {
        throw new ClassCastException("Cannot cast to AuthenticationSuccessResponse");
    }

    @Override
    public AuthenticationErrorResponse toErrorResponse() {
        return this;
    }

    private static AuthenticationErrorResponse toAuthenticationErrorResponse(AuthorizationErrorResponse errorResponse) {
        if (errorResponse.getJWTResponse() != null) {
            return new AuthenticationErrorResponse(errorResponse.getRedirectionURI(), errorResponse.getJWTResponse(), errorResponse.getResponseMode());
        }
        return new AuthenticationErrorResponse(errorResponse.getRedirectionURI(), errorResponse.getErrorObject(), errorResponse.getState(), errorResponse.getIssuer(), errorResponse.getResponseMode());
    }

    public static AuthenticationErrorResponse parse(URI redirectURI, Map<String, List<String>> params) throws ParseException {
        return AuthenticationErrorResponse.toAuthenticationErrorResponse(AuthorizationErrorResponse.parse(redirectURI, params));
    }

    public static AuthenticationErrorResponse parse(URI uri) throws ParseException {
        return AuthenticationErrorResponse.toAuthenticationErrorResponse(AuthorizationErrorResponse.parse(uri));
    }

    public static AuthenticationErrorResponse parse(HTTPResponse httpResponse) throws ParseException {
        return AuthenticationErrorResponse.toAuthenticationErrorResponse(AuthorizationErrorResponse.parse(httpResponse));
    }

    public static AuthenticationErrorResponse parse(HTTPRequest httpRequest) throws ParseException {
        return AuthenticationErrorResponse.parse(httpRequest.getURI(), AuthenticationErrorResponse.parseResponseParameters(httpRequest));
    }

    static {
        stdErrors.addAll(AuthorizationErrorResponse.getStandardErrors());
        stdErrors.add(OIDCError.INTERACTION_REQUIRED);
        stdErrors.add(OIDCError.LOGIN_REQUIRED);
        stdErrors.add(OIDCError.ACCOUNT_SELECTION_REQUIRED);
        stdErrors.add(OIDCError.CONSENT_REQUIRED);
        stdErrors.add(OIDCError.UNMET_AUTHENTICATION_REQUIREMENTS);
        stdErrors.add(OIDCError.REGISTRATION_NOT_SUPPORTED);
    }
}

