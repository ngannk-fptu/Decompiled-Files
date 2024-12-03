/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.oauth2.sdk.AuthorizationResponse;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.jarm.JARMUtils;
import com.nimbusds.oauth2.sdk.jarm.JARMValidator;
import com.nimbusds.oauth2.sdk.util.MultivaluedMapUtils;
import com.nimbusds.oauth2.sdk.util.StringUtils;
import com.nimbusds.oauth2.sdk.util.URIUtils;
import com.nimbusds.openid.connect.sdk.AuthenticationErrorResponse;
import com.nimbusds.openid.connect.sdk.AuthenticationResponse;
import com.nimbusds.openid.connect.sdk.AuthenticationSuccessResponse;
import java.net.URI;
import java.util.List;
import java.util.Map;

public class AuthenticationResponseParser {
    public static AuthenticationResponse parse(URI redirectURI, Map<String, List<String>> params) throws ParseException {
        return AuthenticationResponseParser.parse(redirectURI, params, null);
    }

    public static AuthenticationResponse parse(URI redirectURI, Map<String, List<String>> params, JARMValidator jarmValidator) throws ParseException {
        Map<String, List<String>> workParams = params;
        String jwtResponseString = MultivaluedMapUtils.getFirstValue(params, "response");
        if (jarmValidator != null) {
            if (StringUtils.isBlank(jwtResponseString)) {
                throw new ParseException("Missing JWT-secured (JARM) authorization response parameter");
            }
            try {
                JWTClaimsSet jwtClaimsSet = jarmValidator.validate(jwtResponseString);
                workParams = JARMUtils.toMultiValuedStringParameters(jwtClaimsSet);
            }
            catch (Exception e) {
                throw new ParseException("Invalid JWT-secured (JARM) authorization response: " + e.getMessage());
            }
        }
        if (StringUtils.isNotBlank(MultivaluedMapUtils.getFirstValue(workParams, "error"))) {
            return AuthenticationErrorResponse.parse(redirectURI, workParams);
        }
        if (StringUtils.isNotBlank(MultivaluedMapUtils.getFirstValue(workParams, "response"))) {
            boolean likelyError = JARMUtils.impliesAuthorizationErrorResponse(jwtResponseString);
            if (likelyError) {
                return AuthenticationErrorResponse.parse(redirectURI, workParams);
            }
            return AuthenticationSuccessResponse.parse(redirectURI, workParams);
        }
        return AuthenticationSuccessResponse.parse(redirectURI, workParams);
    }

    public static AuthenticationResponse parse(URI uri) throws ParseException {
        return AuthenticationResponseParser.parse(URIUtils.getBaseURI(uri), AuthorizationResponse.parseResponseParameters(uri));
    }

    public static AuthenticationResponse parse(URI uri, JARMValidator jarmValidator) throws ParseException {
        if (jarmValidator == null) {
            throw new IllegalArgumentException("The JARM validator must not be null");
        }
        return AuthenticationResponseParser.parse(URIUtils.getBaseURI(uri), AuthorizationResponse.parseResponseParameters(uri), jarmValidator);
    }

    public static AuthenticationResponse parse(HTTPResponse httpResponse) throws ParseException {
        URI location = httpResponse.getLocation();
        if (location == null) {
            throw new ParseException("Missing redirection URI / HTTP Location header");
        }
        return AuthenticationResponseParser.parse(location);
    }

    public static AuthenticationResponse parse(HTTPResponse httpResponse, JARMValidator jarmValidator) throws ParseException {
        URI location = httpResponse.getLocation();
        if (location == null) {
            throw new ParseException("Missing redirection URI / HTTP Location header");
        }
        return AuthenticationResponseParser.parse(location, jarmValidator);
    }

    public static AuthenticationResponse parse(HTTPRequest httpRequest) throws ParseException {
        return AuthenticationResponseParser.parse(httpRequest.getURI(), AuthorizationResponse.parseResponseParameters(httpRequest));
    }

    public static AuthenticationResponse parse(HTTPRequest httpRequest, JARMValidator jarmValidator) throws ParseException {
        if (jarmValidator == null) {
            throw new IllegalArgumentException("The JARM validator must not be null");
        }
        return AuthenticationResponseParser.parse(httpRequest.getURI(), AuthorizationResponse.parseResponseParameters(httpRequest), jarmValidator);
    }

    private AuthenticationResponseParser() {
    }
}

