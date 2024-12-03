/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk.dpop.verifiers;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.KeySourceException;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.oauth2.sdk.dpop.verifiers.DPoPProofContext;
import com.nimbusds.oauth2.sdk.util.CollectionUtils;
import java.security.Key;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

class DPoPKeySelector
implements JWSKeySelector<DPoPProofContext> {
    private final Set<JWSAlgorithm> acceptedJWSAlgs;

    DPoPKeySelector(Set<JWSAlgorithm> acceptedJWSAlgs) {
        if (CollectionUtils.isEmpty(acceptedJWSAlgs)) {
            throw new IllegalArgumentException();
        }
        this.acceptedJWSAlgs = acceptedJWSAlgs;
    }

    @Override
    public List<Key> selectJWSKeys(JWSHeader header, DPoPProofContext context) throws KeySourceException {
        JWSAlgorithm alg = header.getAlgorithm();
        if (!this.acceptedJWSAlgs.contains(alg)) {
            throw new KeySourceException("JWS header algorithm not accepted: " + alg);
        }
        JWK jwk = header.getJWK();
        if (jwk == null) {
            throw new KeySourceException("Missing JWS jwk header parameter");
        }
        LinkedList<Key> candidates = new LinkedList<Key>();
        if (JWSAlgorithm.Family.RSA.contains(alg) && jwk instanceof RSAKey) {
            try {
                candidates.add(((RSAKey)jwk).toRSAPublicKey());
            }
            catch (JOSEException e) {
                throw new KeySourceException("Invalid RSA JWK: " + e.getMessage(), e);
            }
        } else if (JWSAlgorithm.Family.EC.contains(alg) && jwk instanceof ECKey) {
            try {
                candidates.add(((ECKey)jwk).toECPublicKey());
            }
            catch (JOSEException e) {
                throw new KeySourceException("Invalid EC JWK: " + e.getMessage(), e);
            }
        } else {
            throw new KeySourceException("JWS header alg / jwk mismatch: alg=" + alg + " jwk.kty=" + jwk.getKeyType());
        }
        return candidates;
    }
}

