/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.nimbusds.common.contenttype.ContentType
 *  com.nimbusds.jwt.JWT
 *  com.nimbusds.jwt.JWTClaimsSet
 */
package com.nimbusds.oauth2.sdk;

import com.nimbusds.common.contenttype.ContentType;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.oauth2.sdk.AuthorizationErrorResponse;
import com.nimbusds.oauth2.sdk.AuthorizationSuccessResponse;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.Response;
import com.nimbusds.oauth2.sdk.ResponseMode;
import com.nimbusds.oauth2.sdk.SerializeException;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.oauth2.sdk.id.State;
import com.nimbusds.oauth2.sdk.jarm.JARMUtils;
import com.nimbusds.oauth2.sdk.jarm.JARMValidator;
import com.nimbusds.oauth2.sdk.util.MultivaluedMapUtils;
import com.nimbusds.oauth2.sdk.util.StringUtils;
import com.nimbusds.oauth2.sdk.util.URIUtils;
import com.nimbusds.oauth2.sdk.util.URLUtils;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

public abstract class AuthorizationResponse
implements Response {
    private final URI redirectURI;
    private final State state;
    private final Issuer issuer;
    private final JWT jwtResponse;
    private final ResponseMode rm;

    protected AuthorizationResponse(URI redirectURI, State state, Issuer issuer, ResponseMode rm) {
        if (redirectURI == null) {
            throw new IllegalArgumentException("The redirection URI must not be null");
        }
        this.redirectURI = redirectURI;
        this.jwtResponse = null;
        this.state = state;
        this.issuer = issuer;
        this.rm = rm;
    }

    protected AuthorizationResponse(URI redirectURI, JWT jwtResponse, ResponseMode rm) {
        if (redirectURI == null) {
            throw new IllegalArgumentException("The redirection URI must not be null");
        }
        this.redirectURI = redirectURI;
        if (jwtResponse == null) {
            throw new IllegalArgumentException("The JWT response must not be null");
        }
        this.jwtResponse = jwtResponse;
        this.state = null;
        this.issuer = null;
        this.rm = rm;
    }

    public URI getRedirectionURI() {
        return this.redirectURI;
    }

    public State getState() {
        return this.state;
    }

    public Issuer getIssuer() {
        return this.issuer;
    }

    public JWT getJWTResponse() {
        return this.jwtResponse;
    }

    public ResponseMode getResponseMode() {
        return this.rm;
    }

    public abstract ResponseMode impliedResponseMode();

    public abstract Map<String, List<String>> toParameters();

    public URI toURI() {
        ResponseMode rm = this.impliedResponseMode();
        StringBuilder sb = new StringBuilder(this.getRedirectionURI().toString());
        String serializedParameters = URLUtils.serializeParameters(this.toParameters());
        if (StringUtils.isNotBlank(serializedParameters)) {
            if (ResponseMode.QUERY.equals(rm) || ResponseMode.QUERY_JWT.equals(rm)) {
                if (!this.getRedirectionURI().toString().endsWith("?")) {
                    if (StringUtils.isBlank(this.getRedirectionURI().getRawQuery())) {
                        sb.append('?');
                    } else {
                        sb.append('&');
                    }
                }
            } else if (ResponseMode.FRAGMENT.equals(rm) || ResponseMode.FRAGMENT_JWT.equals(rm)) {
                sb.append('#');
            } else {
                throw new SerializeException("The (implied) response mode must be query or fragment");
            }
            sb.append(serializedParameters);
        }
        try {
            return new URI(sb.toString());
        }
        catch (URISyntaxException e) {
            throw new SerializeException("Couldn't serialize response: " + e.getMessage(), e);
        }
    }

    @Override
    public HTTPResponse toHTTPResponse() {
        if (ResponseMode.FORM_POST.equals(this.rm)) {
            throw new SerializeException("The response mode must not be form_post");
        }
        HTTPResponse response = new HTTPResponse(302);
        response.setLocation(this.toURI());
        return response;
    }

    public HTTPRequest toHTTPRequest() {
        if (!ResponseMode.FORM_POST.equals(this.rm) && !ResponseMode.FORM_POST_JWT.equals(this.rm)) {
            throw new SerializeException("The response mode must be form_post or form_post.jwt");
        }
        HTTPRequest request = new HTTPRequest(HTTPRequest.Method.POST, this.getRedirectionURI());
        request.setEntityContentType(ContentType.APPLICATION_URLENCODED);
        request.setQuery(URLUtils.serializeParameters(this.toParameters()));
        return request;
    }

    public AuthorizationSuccessResponse toSuccessResponse() {
        return (AuthorizationSuccessResponse)this;
    }

    public AuthorizationErrorResponse toErrorResponse() {
        return (AuthorizationErrorResponse)this;
    }

    public static AuthorizationResponse parse(URI redirectURI, Map<String, List<String>> params) throws ParseException {
        return AuthorizationResponse.parse(redirectURI, params, null);
    }

    public static AuthorizationResponse parse(URI redirectURI, Map<String, List<String>> params, JARMValidator jarmValidator) throws ParseException {
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
            return AuthorizationErrorResponse.parse(redirectURI, workParams);
        }
        if (StringUtils.isNotBlank(jwtResponseString)) {
            boolean likelyError = JARMUtils.impliesAuthorizationErrorResponse(jwtResponseString);
            if (likelyError) {
                return AuthorizationErrorResponse.parse(redirectURI, workParams);
            }
            return AuthorizationSuccessResponse.parse(redirectURI, workParams);
        }
        return AuthorizationSuccessResponse.parse(redirectURI, workParams);
    }

    public static AuthorizationResponse parse(URI uri) throws ParseException {
        return AuthorizationResponse.parse(URIUtils.getBaseURI(uri), AuthorizationResponse.parseResponseParameters(uri));
    }

    public static AuthorizationResponse parse(URI uri, JARMValidator jarmValidator) throws ParseException {
        if (jarmValidator == null) {
            throw new IllegalArgumentException("The JARM validator must not be null");
        }
        return AuthorizationResponse.parse(URIUtils.getBaseURI(uri), AuthorizationResponse.parseResponseParameters(uri), jarmValidator);
    }

    public static AuthorizationResponse parse(HTTPResponse httpResponse) throws ParseException {
        URI location = httpResponse.getLocation();
        if (location == null) {
            throw new ParseException("Missing redirection URI / HTTP Location header");
        }
        return AuthorizationResponse.parse(location);
    }

    public static AuthorizationResponse parse(HTTPResponse httpResponse, JARMValidator jarmValidator) throws ParseException {
        URI location = httpResponse.getLocation();
        if (location == null) {
            throw new ParseException("Missing redirection URI / HTTP Location header");
        }
        return AuthorizationResponse.parse(location, jarmValidator);
    }

    public static AuthorizationResponse parse(HTTPRequest httpRequest) throws ParseException {
        return AuthorizationResponse.parse(httpRequest.getURI(), AuthorizationResponse.parseResponseParameters(httpRequest));
    }

    public static AuthorizationResponse parse(HTTPRequest httpRequest, JARMValidator jarmValidator) throws ParseException {
        if (jarmValidator == null) {
            throw new IllegalArgumentException("The JARM validator must not be null");
        }
        return AuthorizationResponse.parse(httpRequest.getURI(), AuthorizationResponse.parseResponseParameters(httpRequest), jarmValidator);
    }

    public static Map<String, List<String>> parseResponseParameters(URI uri) throws ParseException {
        if (uri.getRawFragment() != null) {
            return URLUtils.parseParameters(uri.getRawFragment());
        }
        if (uri.getRawQuery() != null) {
            return URLUtils.parseParameters(uri.getRawQuery());
        }
        throw new ParseException("Missing URI fragment or query string");
    }

    public static Map<String, List<String>> parseResponseParameters(HTTPRequest httpRequest) throws ParseException {
        if (httpRequest.getQuery() != null) {
            return URLUtils.parseParameters(httpRequest.getQuery());
        }
        if (httpRequest.getFragment() != null) {
            return URLUtils.parseParameters(httpRequest.getFragment());
        }
        throw new ParseException("Missing URI fragment, query string or post body");
    }
}

