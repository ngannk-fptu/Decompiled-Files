/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.nimbusds.common.contenttype.ContentType
 *  com.nimbusds.jose.JOSEException
 *  com.nimbusds.jose.JWSAlgorithm
 *  com.nimbusds.jose.JWSAlgorithm$Family
 *  com.nimbusds.jwt.SignedJWT
 *  net.jcip.annotations.Immutable
 */
package com.nimbusds.oauth2.sdk.auth;

import com.nimbusds.common.contenttype.ContentType;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.assertions.jwt.JWTAssertionFactory;
import com.nimbusds.oauth2.sdk.auth.ClientAuthenticationMethod;
import com.nimbusds.oauth2.sdk.auth.JWTAuthentication;
import com.nimbusds.oauth2.sdk.auth.JWTAuthenticationClaimsSet;
import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.id.Audience;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.util.URLUtils;
import java.net.URI;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.jcip.annotations.Immutable;

@Immutable
public final class ClientSecretJWT
extends JWTAuthentication {
    public static Set<JWSAlgorithm> supportedJWAs() {
        return Collections.unmodifiableSet(new HashSet(JWSAlgorithm.Family.HMAC_SHA));
    }

    public ClientSecretJWT(ClientID clientID, URI endpoint, JWSAlgorithm jwsAlgorithm, Secret clientSecret) throws JOSEException {
        this(JWTAssertionFactory.create(new JWTAuthenticationClaimsSet(clientID, new Audience(endpoint.toString())), jwsAlgorithm, clientSecret));
    }

    public ClientSecretJWT(SignedJWT clientAssertion) {
        super(ClientAuthenticationMethod.CLIENT_SECRET_JWT, clientAssertion);
        if (!JWSAlgorithm.Family.HMAC_SHA.contains((Object)clientAssertion.getHeader().getAlgorithm())) {
            throw new IllegalArgumentException("The client assertion JWT must be HMAC-signed (HS256, HS384 or HS512)");
        }
    }

    public static ClientSecretJWT parse(Map<String, List<String>> params) throws ParseException {
        ClientSecretJWT clientSecretJWT;
        JWTAuthentication.ensureClientAssertionType(params);
        SignedJWT clientAssertion = JWTAuthentication.parseClientAssertion(params);
        try {
            clientSecretJWT = new ClientSecretJWT(clientAssertion);
        }
        catch (IllegalArgumentException e) {
            throw new ParseException(e.getMessage(), e);
        }
        ClientID clientID = JWTAuthentication.parseClientID(params);
        if (clientID != null && !clientID.equals(clientSecretJWT.getClientID())) {
            throw new ParseException("Invalid client secret JWT authentication: The client identifier doesn't match the client assertion subject / issuer");
        }
        return clientSecretJWT;
    }

    public static ClientSecretJWT parse(String paramsString) throws ParseException {
        Map<String, List<String>> params = URLUtils.parseParameters(paramsString);
        return ClientSecretJWT.parse(params);
    }

    public static ClientSecretJWT parse(HTTPRequest httpRequest) throws ParseException {
        httpRequest.ensureMethod(HTTPRequest.Method.POST);
        httpRequest.ensureEntityContentType(ContentType.APPLICATION_URLENCODED);
        return ClientSecretJWT.parse(httpRequest.getQueryParameters());
    }
}

