/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.nimbusds.jose.JOSEException
 *  com.nimbusds.jose.JWSAlgorithm
 *  com.nimbusds.jose.JWSAlgorithm$Family
 *  com.nimbusds.jose.JWSHeader
 *  com.nimbusds.jose.JWSHeader$Builder
 *  com.nimbusds.jose.JWSSigner
 *  com.nimbusds.jose.crypto.ECDSASigner
 *  com.nimbusds.jose.crypto.MACSigner
 *  com.nimbusds.jose.crypto.RSASSASigner
 *  com.nimbusds.jwt.SignedJWT
 */
package com.nimbusds.oauth2.sdk.assertions.jwt;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.oauth2.sdk.assertions.jwt.JWTAssertionDetails;
import com.nimbusds.oauth2.sdk.auth.Secret;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class JWTAssertionFactory {
    public static Set<JWSAlgorithm> supportedJWAs() {
        HashSet supported = new HashSet();
        supported.addAll(JWSAlgorithm.Family.HMAC_SHA);
        supported.addAll(JWSAlgorithm.Family.RSA);
        supported.addAll(JWSAlgorithm.Family.EC);
        return Collections.unmodifiableSet(supported);
    }

    public static SignedJWT create(JWTAssertionDetails details, JWSAlgorithm jwsAlgorithm, Secret secret) throws JOSEException {
        SignedJWT signedJWT = new SignedJWT(new JWSHeader(jwsAlgorithm), details.toJWTClaimsSet());
        signedJWT.sign((JWSSigner)new MACSigner(secret.getValueBytes()));
        return signedJWT;
    }

    public static SignedJWT create(JWTAssertionDetails details, JWSAlgorithm jwsAlgorithm, RSAPrivateKey rsaPrivateKey, String keyID, Provider jcaProvider) throws JOSEException {
        SignedJWT signedJWT = new SignedJWT(new JWSHeader.Builder(jwsAlgorithm).keyID(keyID).build(), details.toJWTClaimsSet());
        RSASSASigner signer = new RSASSASigner((PrivateKey)rsaPrivateKey);
        if (jcaProvider != null) {
            signer.getJCAContext().setProvider(jcaProvider);
        }
        signedJWT.sign((JWSSigner)signer);
        return signedJWT;
    }

    public static SignedJWT create(JWTAssertionDetails details, JWSAlgorithm jwsAlgorithm, ECPrivateKey ecPrivateKey, String keyID, Provider jcaProvider) throws JOSEException {
        SignedJWT signedJWT = new SignedJWT(new JWSHeader.Builder(jwsAlgorithm).keyID(keyID).build(), details.toJWTClaimsSet());
        ECDSASigner signer = new ECDSASigner(ecPrivateKey);
        if (jcaProvider != null) {
            signer.getJCAContext().setProvider(jcaProvider);
        }
        signedJWT.sign((JWSSigner)signer);
        return signedJWT;
    }

    private JWTAssertionFactory() {
    }
}

