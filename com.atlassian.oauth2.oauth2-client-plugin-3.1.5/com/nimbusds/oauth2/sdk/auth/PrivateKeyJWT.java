/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk.auth;

import com.nimbusds.common.contenttype.ContentType;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.assertions.jwt.JWTAssertionDetails;
import com.nimbusds.oauth2.sdk.assertions.jwt.JWTAssertionFactory;
import com.nimbusds.oauth2.sdk.auth.ClientAuthenticationMethod;
import com.nimbusds.oauth2.sdk.auth.JWTAuthentication;
import com.nimbusds.oauth2.sdk.auth.JWTAuthenticationClaimsSet;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.id.Audience;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.util.URLUtils;
import java.net.URI;
import java.security.Provider;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.jcip.annotations.Immutable;

@Immutable
public final class PrivateKeyJWT
extends JWTAuthentication {
    public static Set<JWSAlgorithm> supportedJWAs() {
        HashSet<JWSAlgorithm> supported = new HashSet<JWSAlgorithm>();
        supported.addAll(JWSAlgorithm.Family.RSA);
        supported.addAll(JWSAlgorithm.Family.EC);
        return Collections.unmodifiableSet(supported);
    }

    public PrivateKeyJWT(ClientID clientID, URI endpoint, JWSAlgorithm jwsAlgorithm, RSAPrivateKey rsaPrivateKey, String keyID, Provider jcaProvider) throws JOSEException {
        this(new JWTAuthenticationClaimsSet(clientID, new Audience(endpoint.toString())), jwsAlgorithm, rsaPrivateKey, keyID, jcaProvider);
    }

    public PrivateKeyJWT(JWTAuthenticationClaimsSet jwtAuthClaimsSet, JWSAlgorithm jwsAlgorithm, RSAPrivateKey rsaPrivateKey, String keyID, Provider jcaProvider) throws JOSEException {
        this(JWTAssertionFactory.create((JWTAssertionDetails)jwtAuthClaimsSet, jwsAlgorithm, rsaPrivateKey, keyID, jcaProvider));
    }

    public PrivateKeyJWT(ClientID clientID, URI endpoint, JWSAlgorithm jwsAlgorithm, ECPrivateKey ecPrivateKey, String keyID, Provider jcaProvider) throws JOSEException {
        this(new JWTAuthenticationClaimsSet(clientID, new Audience(endpoint.toString())), jwsAlgorithm, ecPrivateKey, keyID, jcaProvider);
    }

    public PrivateKeyJWT(JWTAuthenticationClaimsSet jwtAuthClaimsSet, JWSAlgorithm jwsAlgorithm, ECPrivateKey ecPrivateKey, String keyID, Provider jcaProvider) throws JOSEException {
        this(JWTAssertionFactory.create((JWTAssertionDetails)jwtAuthClaimsSet, jwsAlgorithm, ecPrivateKey, keyID, jcaProvider));
    }

    public PrivateKeyJWT(SignedJWT clientAssertion) {
        super(ClientAuthenticationMethod.PRIVATE_KEY_JWT, clientAssertion);
        JWSAlgorithm alg = clientAssertion.getHeader().getAlgorithm();
        if (!JWSAlgorithm.Family.RSA.contains(alg) && !JWSAlgorithm.Family.EC.contains(alg)) {
            throw new IllegalArgumentException("The client assertion JWT must be RSA or ECDSA-signed (RS256, RS384, RS512, PS256, PS384, PS512, ES256, ES384 or ES512)");
        }
    }

    public static PrivateKeyJWT parse(Map<String, List<String>> params) throws ParseException {
        PrivateKeyJWT privateKeyJWT;
        JWTAuthentication.ensureClientAssertionType(params);
        SignedJWT clientAssertion = JWTAuthentication.parseClientAssertion(params);
        try {
            privateKeyJWT = new PrivateKeyJWT(clientAssertion);
        }
        catch (IllegalArgumentException e) {
            throw new ParseException(e.getMessage(), e);
        }
        ClientID clientID = JWTAuthentication.parseClientID(params);
        if (clientID != null && !clientID.equals(privateKeyJWT.getClientID())) {
            throw new ParseException("Invalid private key JWT authentication: The client identifier doesn't match the client assertion subject / issuer");
        }
        return privateKeyJWT;
    }

    public static PrivateKeyJWT parse(String paramsString) throws ParseException {
        Map<String, List<String>> params = URLUtils.parseParameters(paramsString);
        return PrivateKeyJWT.parse(params);
    }

    public static PrivateKeyJWT parse(HTTPRequest httpRequest) throws ParseException {
        httpRequest.ensureMethod(HTTPRequest.Method.POST);
        httpRequest.ensureEntityContentType(ContentType.APPLICATION_URLENCODED);
        return PrivateKeyJWT.parse(httpRequest.getQueryParameters());
    }
}

