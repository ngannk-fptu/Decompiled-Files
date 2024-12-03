/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.nimbusds.jose.JOSEException
 *  com.nimbusds.jose.JOSEObjectType
 *  com.nimbusds.jose.JWSAlgorithm
 *  com.nimbusds.jose.JWSAlgorithm$Family
 *  com.nimbusds.jose.KeySourceException
 *  com.nimbusds.jose.proc.BadJOSEException
 *  com.nimbusds.jose.proc.DefaultJOSEObjectTypeVerifier
 *  com.nimbusds.jose.proc.JOSEObjectTypeVerifier
 *  com.nimbusds.jose.proc.JWSKeySelector
 *  com.nimbusds.jose.proc.SecurityContext
 *  com.nimbusds.jose.util.Base64URL
 *  com.nimbusds.jwt.SignedJWT
 *  com.nimbusds.jwt.proc.DefaultJWTProcessor
 *  com.nimbusds.jwt.proc.JWTClaimsSetVerifier
 *  net.jcip.annotations.ThreadSafe
 */
package com.nimbusds.oauth2.sdk.dpop.verifiers;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.KeySourceException;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.DefaultJOSEObjectTypeVerifier;
import com.nimbusds.jose.proc.JOSEObjectTypeVerifier;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import com.nimbusds.jwt.proc.JWTClaimsSetVerifier;
import com.nimbusds.oauth2.sdk.dpop.DPoPProofFactory;
import com.nimbusds.oauth2.sdk.dpop.DPoPUtils;
import com.nimbusds.oauth2.sdk.dpop.JWKThumbprintConfirmation;
import com.nimbusds.oauth2.sdk.dpop.verifiers.AccessTokenValidationException;
import com.nimbusds.oauth2.sdk.dpop.verifiers.DPoPIssuer;
import com.nimbusds.oauth2.sdk.dpop.verifiers.DPoPKeySelector;
import com.nimbusds.oauth2.sdk.dpop.verifiers.DPoPProofClaimsSetVerifier;
import com.nimbusds.oauth2.sdk.dpop.verifiers.DPoPProofContext;
import com.nimbusds.oauth2.sdk.dpop.verifiers.InvalidDPoPProofException;
import com.nimbusds.oauth2.sdk.id.JWTID;
import com.nimbusds.oauth2.sdk.token.DPoPAccessToken;
import com.nimbusds.oauth2.sdk.util.StringUtils;
import com.nimbusds.oauth2.sdk.util.URIUtils;
import com.nimbusds.oauth2.sdk.util.singleuse.SingleUseChecker;
import java.net.URI;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
class DPoPCommonVerifier {
    public static final Set<JWSAlgorithm> SUPPORTED_JWS_ALGORITHMS;
    private final Set<JWSAlgorithm> acceptedJWSAlgs;
    private final long maxClockSkewSeconds;
    private final SingleUseChecker<Map.Entry<DPoPIssuer, JWTID>> singleUseChecker;

    DPoPCommonVerifier(Set<JWSAlgorithm> acceptedJWSAlgs, long maxClockSkewSeconds, SingleUseChecker<Map.Entry<DPoPIssuer, JWTID>> singleUseChecker) {
        if (!SUPPORTED_JWS_ALGORITHMS.containsAll(acceptedJWSAlgs)) {
            throw new IllegalArgumentException("Unsupported JWS algorithms: " + acceptedJWSAlgs.retainAll(SUPPORTED_JWS_ALGORITHMS));
        }
        this.acceptedJWSAlgs = acceptedJWSAlgs;
        this.maxClockSkewSeconds = maxClockSkewSeconds;
        this.singleUseChecker = singleUseChecker;
    }

    void verify(String method, URI uri, DPoPIssuer issuer, SignedJWT proof, DPoPAccessToken accessToken, JWKThumbprintConfirmation cnf) throws InvalidDPoPProofException, AccessTokenValidationException, JOSEException {
        if (StringUtils.isBlank(method)) {
            throw new IllegalArgumentException("The HTTP request method must not be null or blank");
        }
        if (uri == null) {
            throw new IllegalArgumentException("The HTTP URI must not be null");
        }
        DefaultJWTProcessor proc = new DefaultJWTProcessor();
        proc.setJWSTypeVerifier((JOSEObjectTypeVerifier)new DefaultJOSEObjectTypeVerifier(new JOSEObjectType[]{DPoPProofFactory.TYPE}));
        proc.setJWSKeySelector((JWSKeySelector)new DPoPKeySelector(this.acceptedJWSAlgs));
        proc.setJWTClaimsSetVerifier((JWTClaimsSetVerifier)new DPoPProofClaimsSetVerifier(method, URIUtils.getBaseURI(uri), this.maxClockSkewSeconds, accessToken != null, this.singleUseChecker));
        DPoPProofContext context = new DPoPProofContext(issuer);
        try {
            proc.process(proof, (SecurityContext)context);
        }
        catch (KeySourceException | BadJOSEException e) {
            throw new InvalidDPoPProofException("Invalid DPoP proof: " + e.getMessage(), e);
        }
        if (accessToken != null) {
            Base64URL accessTokenHash = DPoPUtils.computeSHA256(accessToken);
            if (!context.getAccessTokenHash().equals((Object)accessTokenHash)) {
                throw new AccessTokenValidationException("The access token hash doesn't match the JWT ath claim");
            }
            if (!proof.getHeader().getJWK().computeThumbprint().equals((Object)cnf.getValue())) {
                throw new AccessTokenValidationException("The DPoP proof JWK doesn't match the JWK SHA-256 thumbprint confirmation");
            }
        }
    }

    static {
        HashSet supported = new HashSet();
        supported.addAll(JWSAlgorithm.Family.EC);
        supported.addAll(JWSAlgorithm.Family.RSA);
        SUPPORTED_JWS_ALGORITHMS = Collections.unmodifiableSet(supported);
    }
}

