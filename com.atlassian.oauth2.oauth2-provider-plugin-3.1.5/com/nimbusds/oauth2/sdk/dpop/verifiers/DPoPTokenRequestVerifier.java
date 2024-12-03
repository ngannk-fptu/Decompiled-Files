/*
 * Decompiled with CFR 0.152.
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
import com.nimbusds.oauth2.sdk.util.singleuse.SingleUseChecker;
import java.net.URI;
import java.util.Map;
import java.util.Set;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class DPoPTokenRequestVerifier
extends DPoPCommonVerifier {
    private final URI endpointURI;

    public DPoPTokenRequestVerifier(Set<JWSAlgorithm> acceptedJWSAlgs, URI endpointURI, long maxClockSkewSeconds, SingleUseChecker<Map.Entry<DPoPIssuer, JWTID>> singleUseChecker) {
        super(acceptedJWSAlgs, maxClockSkewSeconds, singleUseChecker);
        if (endpointURI == null) {
            throw new IllegalArgumentException("The token endpoint URI must not be null");
        }
        this.endpointURI = endpointURI;
    }

    public JWKThumbprintConfirmation verify(DPoPIssuer issuer, SignedJWT proof) throws InvalidDPoPProofException, JOSEException {
        try {
            super.verify("POST", this.endpointURI, issuer, proof, null, null);
        }
        catch (AccessTokenValidationException e) {
            throw new RuntimeException("Unexpected exception", e);
        }
        return new JWKThumbprintConfirmation(proof.getHeader().getJWK().computeThumbprint());
    }
}

