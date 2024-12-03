/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.nimbusds.jose.JWSAlgorithm
 *  com.nimbusds.jose.JWSHeader$Builder
 *  com.nimbusds.jose.JWSSigner
 *  com.nimbusds.jose.crypto.RSASSASigner
 *  com.nimbusds.jose.util.Base64
 *  com.nimbusds.jose.util.Base64URL
 *  com.nimbusds.jwt.JWTClaimsSet
 *  com.nimbusds.jwt.JWTClaimsSet$Builder
 *  com.nimbusds.jwt.SignedJWT
 */
package com.microsoft.aad.msal4j;

import com.microsoft.aad.msal4j.ClientAssertion;
import com.microsoft.aad.msal4j.ClientCertificate;
import com.microsoft.aad.msal4j.MsalClientException;
import com.microsoft.aad.msal4j.StringHelper;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.util.Base64;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.UUID;

final class JwtHelper {
    JwtHelper() {
    }

    static ClientAssertion buildJwt(String clientId, ClientCertificate credential, String jwtAudience, boolean sendX5c) throws MsalClientException {
        SignedJWT jwt;
        if (StringHelper.isBlank(clientId)) {
            throw new IllegalArgumentException("clientId is null or empty");
        }
        if (credential == null) {
            throw new IllegalArgumentException("credential is null");
        }
        long time = System.currentTimeMillis();
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder().audience(Collections.singletonList(jwtAudience)).issuer(clientId).jwtID(UUID.randomUUID().toString()).notBeforeTime(new Date(time)).expirationTime(new Date(time + 600000L)).subject(clientId).build();
        try {
            JWSHeader.Builder builder = new JWSHeader.Builder(JWSAlgorithm.RS256);
            if (sendX5c) {
                ArrayList<Base64> certs = new ArrayList<Base64>();
                for (String cert : credential.getEncodedPublicKeyCertificateChain()) {
                    certs.add(new Base64(cert));
                }
                builder.x509CertChain(certs);
            }
            builder.x509CertThumbprint(new Base64URL(credential.publicCertificateHash()));
            jwt = new SignedJWT(builder.build(), claimsSet);
            RSASSASigner signer = new RSASSASigner(credential.privateKey());
            jwt.sign((JWSSigner)signer);
        }
        catch (Exception e) {
            throw new MsalClientException(e);
        }
        return new ClientAssertion(jwt.serialize());
    }
}

