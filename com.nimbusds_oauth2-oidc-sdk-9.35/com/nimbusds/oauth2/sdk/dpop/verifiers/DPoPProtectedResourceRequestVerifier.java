/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.nimbusds.jose.JOSEException
 *  com.nimbusds.jose.JWSAlgorithm
 *  com.nimbusds.jwt.SignedJWT
 *  net.jcip.annotations.ThreadSafe
 */
package com.nimbusds.oauth2.sdk.dpop.verifiers;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.oauth2.sdk.dpop.JWKThumbprintConfirmation;
import com.nimbusds.oauth2.sdk.dpop.verifiers.AccessTokenValidationException;
import com.nimbusds.oauth2.sdk.dpop.verifiers.DPoPCommonVerifier;
import com.nimbusds.oauth2.sdk.dpop.verifiers.DPoPIssuer;
import com.nimbusds.oauth2.sdk.dpop.verifiers.InvalidDPoPProofException;
import com.nimbusds.oauth2.sdk.id.JWTID;
import com.nimbusds.oauth2.sdk.token.DPoPAccessToken;
import com.nimbusds.oauth2.sdk.util.singleuse.SingleUseChecker;
import java.net.URI;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class DPoPProtectedResourceRequestVerifier
extends DPoPCommonVerifier {
    public DPoPProtectedResourceRequestVerifier(Set<JWSAlgorithm> acceptedJWSAlgs, long maxClockSkewSeconds, SingleUseChecker<Map.Entry<DPoPIssuer, JWTID>> singleUseChecker) {
        super(acceptedJWSAlgs, maxClockSkewSeconds, singleUseChecker);
    }

    @Override
    public void verify(String method, URI uri, DPoPIssuer issuer, SignedJWT proof, DPoPAccessToken accessToken, JWKThumbprintConfirmation cnf) throws InvalidDPoPProofException, AccessTokenValidationException, JOSEException {
        if (proof == null) {
            throw new InvalidDPoPProofException("Missing required DPoP proof");
        }
        Objects.requireNonNull(accessToken, "The access token must not be null");
        Objects.requireNonNull(cnf, "The DPoP JWK thumbprint confirmation must not be null");
        super.verify(method, uri, issuer, proof, accessToken, cnf);
    }
}

