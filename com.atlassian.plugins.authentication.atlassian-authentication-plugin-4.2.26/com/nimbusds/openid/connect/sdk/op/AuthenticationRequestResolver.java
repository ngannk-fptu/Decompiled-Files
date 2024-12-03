/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.op;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jose.util.ResourceRetriever;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.jwt.proc.JWTProcessor;
import com.nimbusds.oauth2.sdk.OAuth2Error;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.openid.connect.sdk.AuthenticationRequest;
import com.nimbusds.openid.connect.sdk.op.ResolveException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class AuthenticationRequestResolver<C extends SecurityContext> {
    private final JWTProcessor<C> jwtProcessor;
    private final ResourceRetriever jwtRetriever;

    public AuthenticationRequestResolver() {
        this.jwtProcessor = null;
        this.jwtRetriever = null;
    }

    public AuthenticationRequestResolver(JWTProcessor<C> jwtProcessor) {
        if (jwtProcessor == null) {
            throw new IllegalArgumentException("The JWT processor must not be null");
        }
        this.jwtProcessor = jwtProcessor;
        this.jwtRetriever = null;
    }

    public AuthenticationRequestResolver(JWTProcessor<C> jwtProcessor, ResourceRetriever jwtRetriever) {
        if (jwtProcessor == null) {
            throw new IllegalArgumentException("The JWT processor must not be null");
        }
        this.jwtProcessor = jwtProcessor;
        if (jwtRetriever == null) {
            throw new IllegalArgumentException("The JWT retriever must not be null");
        }
        this.jwtRetriever = jwtRetriever;
    }

    public JWTProcessor<C> getJWTProcessor() {
        return this.jwtProcessor;
    }

    public ResourceRetriever getJWTRetriever() {
        return this.jwtRetriever;
    }

    public static Map<String, List<String>> reformatClaims(JWTClaimsSet claimsSet) {
        Map<String, Object> claims = claimsSet.getClaims();
        HashMap<String, List<String>> reformattedClaims = new HashMap<String, List<String>>();
        for (Map.Entry<String, Object> entry : claims.entrySet()) {
            if (entry.getValue() == null) continue;
            reformattedClaims.put(entry.getKey(), Collections.singletonList(entry.getValue().toString()));
        }
        return Collections.unmodifiableMap(reformattedClaims);
    }

    public AuthenticationRequest resolve(AuthenticationRequest request, C securityContext) throws ResolveException, JOSEException {
        JWTClaimsSet jwtClaims;
        JWT jwt;
        if (!request.specifiesRequestObject()) {
            return request;
        }
        if (request.getRequestURI() != null) {
            if (this.jwtRetriever == null || this.jwtProcessor == null) {
                throw new ResolveException(OAuth2Error.REQUEST_URI_NOT_SUPPORTED, request);
            }
            try {
                jwt = JWTParser.parse(this.jwtRetriever.retrieveResource(request.getRequestURI().toURL()).getContent());
            }
            catch (MalformedURLException e) {
                throw new ResolveException(OAuth2Error.INVALID_REQUEST_URI.setDescription("Malformed URL"), request);
            }
            catch (IOException e) {
                throw new ResolveException("Couldn't retrieve request_uri: " + e.getMessage(), "Network error, check the request_uri", request, e);
            }
            catch (java.text.ParseException e) {
                throw new ResolveException(OAuth2Error.INVALID_REQUEST_URI.setDescription("Invalid JWT"), request);
            }
        } else {
            if (this.jwtProcessor == null) {
                throw new ResolveException(OAuth2Error.REQUEST_NOT_SUPPORTED, request);
            }
            jwt = request.getRequestObject();
        }
        try {
            jwtClaims = this.jwtProcessor.process(jwt, securityContext);
        }
        catch (BadJOSEException e) {
            throw new ResolveException("Invalid request object: " + e.getMessage(), "Bad JWT / signature / HMAC / encryption", request, e);
        }
        HashMap<String, List<String>> finalParams = new HashMap<String, List<String>>();
        finalParams.putAll(request.toParameters());
        finalParams.putAll(AuthenticationRequestResolver.reformatClaims(jwtClaims));
        finalParams.remove("request");
        finalParams.remove("request_uri");
        try {
            return AuthenticationRequest.parse(request.getEndpointURI(), finalParams);
        }
        catch (ParseException e) {
            throw new ResolveException("Couldn't create final OpenID authentication request: " + e.getMessage(), "Invalid request object parameter(s): " + e.getMessage(), request, e);
        }
    }
}

