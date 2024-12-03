/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.nimbusds.jose.JOSEException
 *  com.nimbusds.jose.JWSAlgorithm
 *  com.nimbusds.jose.JWSHeader
 *  com.nimbusds.jose.JWSHeader$Builder
 *  com.nimbusds.jose.JWSObject$State
 *  com.nimbusds.jose.JWSSigner
 *  com.nimbusds.jose.JWSVerifier
 *  com.nimbusds.jose.crypto.factories.DefaultJWSSignerFactory
 *  com.nimbusds.jose.crypto.factories.DefaultJWSVerifierFactory
 *  com.nimbusds.jose.jwk.AsymmetricJWK
 *  com.nimbusds.jose.jwk.Curve
 *  com.nimbusds.jose.jwk.ECKey
 *  com.nimbusds.jose.jwk.JWK
 *  com.nimbusds.jose.jwk.JWKMatcher
 *  com.nimbusds.jose.jwk.JWKSelector
 *  com.nimbusds.jose.jwk.JWKSet
 *  com.nimbusds.jose.jwk.KeyType
 *  com.nimbusds.jose.jwk.OctetKeyPair
 *  com.nimbusds.jose.proc.BadJOSEException
 *  com.nimbusds.jose.util.Base64URL
 *  com.nimbusds.jwt.JWTClaimsSet
 *  com.nimbusds.jwt.SignedJWT
 *  net.jcip.annotations.Immutable
 */
package com.nimbusds.openid.connect.sdk.federation.entities;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.factories.DefaultJWSSignerFactory;
import com.nimbusds.jose.crypto.factories.DefaultJWSVerifierFactory;
import com.nimbusds.jose.jwk.AsymmetricJWK;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKMatcher;
import com.nimbusds.jose.jwk.JWKSelector;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyType;
import com.nimbusds.jose.jwk.OctetKeyPair;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.util.CollectionUtils;
import com.nimbusds.openid.connect.sdk.federation.entities.EntityID;
import com.nimbusds.openid.connect.sdk.federation.entities.EntityStatementClaimsSet;
import com.nimbusds.openid.connect.sdk.federation.entities.EntityStatementClaimsVerifier;
import java.security.Key;
import java.security.PublicKey;
import java.util.List;
import net.jcip.annotations.Immutable;

@Immutable
public final class EntityStatement {
    private final SignedJWT statementJWT;
    private final EntityStatementClaimsSet statementClaimsSet;

    private EntityStatement(SignedJWT statementJWT, EntityStatementClaimsSet statementClaimsSet) {
        if (statementJWT == null) {
            throw new IllegalArgumentException("The entity statement must not be null");
        }
        if (JWSObject.State.UNSIGNED.equals((Object)statementJWT.getState())) {
            throw new IllegalArgumentException("The statement is not signed");
        }
        this.statementJWT = statementJWT;
        if (statementClaimsSet == null) {
            throw new IllegalArgumentException("The entity statement claims set must not be null");
        }
        this.statementClaimsSet = statementClaimsSet;
    }

    public EntityID getEntityID() {
        return this.getClaimsSet().getSubjectEntityID();
    }

    public SignedJWT getSignedStatement() {
        return this.statementJWT;
    }

    public EntityStatementClaimsSet getClaimsSet() {
        return this.statementClaimsSet;
    }

    public boolean isTrustAnchor() {
        return this.getClaimsSet().isSelfStatement() && CollectionUtils.isEmpty(this.getClaimsSet().getAuthorityHints());
    }

    public Base64URL verifySignatureOfSelfStatement() throws BadJOSEException, JOSEException {
        if (!this.getClaimsSet().isSelfStatement()) {
            throw new BadJOSEException("Entity statement not self-issued");
        }
        return this.verifySignature(this.getClaimsSet().getJWKSet());
    }

    public Base64URL verifySignature(JWKSet jwkSet) throws BadJOSEException, JOSEException {
        List jwkMatches = new JWKSelector(JWKMatcher.forJWSHeader((JWSHeader)this.statementJWT.getHeader())).select(jwkSet);
        if (jwkMatches.isEmpty()) {
            throw new BadJOSEException("Entity statement rejected: Another JOSE algorithm expected, or no matching key(s) found");
        }
        DefaultJWSVerifierFactory verifierFactory = new DefaultJWSVerifierFactory();
        JWK signingJWK = null;
        for (JWK candidateJWK : jwkMatches) {
            if (!(candidateJWK instanceof AsymmetricJWK)) continue;
            PublicKey publicKey = ((AsymmetricJWK)candidateJWK).toPublicKey();
            JWSVerifier jwsVerifier = verifierFactory.createJWSVerifier(this.statementJWT.getHeader(), (Key)publicKey);
            if (!this.statementJWT.verify(jwsVerifier)) continue;
            signingJWK = candidateJWK;
        }
        if (signingJWK == null) {
            throw new BadJOSEException("Entity statement rejected: Invalid signature");
        }
        try {
            new EntityStatementClaimsVerifier(null).verify(this.statementJWT.getJWTClaimsSet(), null);
        }
        catch (java.text.ParseException e) {
            throw new BadJOSEException(e.getMessage(), (Throwable)e);
        }
        return signingJWK.computeThumbprint();
    }

    public static EntityStatement sign(EntityStatementClaimsSet claimsSet, JWK signingJWK) throws JOSEException {
        return EntityStatement.sign(claimsSet, signingJWK, EntityStatement.resolveSigningAlgorithm(signingJWK));
    }

    public static EntityStatement sign(EntityStatementClaimsSet claimsSet, JWK signingJWK, JWSAlgorithm jwsAlg) throws JOSEException {
        SignedJWT signedJWT;
        if (claimsSet.isSelfStatement() && !claimsSet.getJWKSet().containsJWK(signingJWK)) {
            throw new JOSEException("Signing JWK not found in JWK set of self-statement");
        }
        JWSSigner jwsSigner = new DefaultJWSSignerFactory().createJWSSigner(signingJWK, jwsAlg);
        JWSHeader jwsHeader = new JWSHeader.Builder(jwsAlg).keyID(signingJWK.getKeyID()).build();
        try {
            signedJWT = new SignedJWT(jwsHeader, claimsSet.toJWTClaimsSet());
        }
        catch (ParseException e) {
            throw new JOSEException(e.getMessage(), (Throwable)e);
        }
        signedJWT.sign(jwsSigner);
        return new EntityStatement(signedJWT, claimsSet);
    }

    private static JWSAlgorithm resolveSigningAlgorithm(JWK jwk) throws JOSEException {
        KeyType jwkType = jwk.getKeyType();
        if (KeyType.RSA.equals((Object)jwkType)) {
            if (jwk.getAlgorithm() != null) {
                return new JWSAlgorithm(jwk.getAlgorithm().getName());
            }
            return JWSAlgorithm.RS256;
        }
        if (KeyType.EC.equals((Object)jwkType)) {
            ECKey ecJWK = jwk.toECKey();
            if (jwk.getAlgorithm() != null) {
                return new JWSAlgorithm(ecJWK.getAlgorithm().getName());
            }
            if (Curve.P_256.equals((Object)ecJWK.getCurve())) {
                return JWSAlgorithm.ES256;
            }
            if (Curve.P_384.equals((Object)ecJWK.getCurve())) {
                return JWSAlgorithm.ES384;
            }
            if (Curve.P_521.equals((Object)ecJWK.getCurve())) {
                return JWSAlgorithm.ES512;
            }
            throw new JOSEException("Unsupported ECDSA curve: " + ecJWK.getCurve());
        }
        if (KeyType.OKP.equals((Object)jwkType)) {
            OctetKeyPair okp = jwk.toOctetKeyPair();
            if (Curve.Ed25519.equals((Object)okp.getCurve())) {
                return JWSAlgorithm.EdDSA;
            }
            throw new JOSEException("Unsupported EdDSA curve: " + okp.getCurve());
        }
        throw new JOSEException("Unsupported JWK type: " + jwkType);
    }

    public static EntityStatement parse(SignedJWT signedStmt) throws ParseException {
        JWTClaimsSet jwtClaimsSet;
        if (JWSObject.State.UNSIGNED.equals((Object)signedStmt.getState())) {
            throw new ParseException("The statement is not signed");
        }
        try {
            jwtClaimsSet = signedStmt.getJWTClaimsSet();
        }
        catch (java.text.ParseException e) {
            throw new ParseException(e.getMessage(), e);
        }
        EntityStatementClaimsSet claimsSet = new EntityStatementClaimsSet(jwtClaimsSet);
        return new EntityStatement(signedStmt, claimsSet);
    }

    public static EntityStatement parse(String signedStmtString) throws ParseException {
        try {
            return EntityStatement.parse(SignedJWT.parse((String)signedStmtString));
        }
        catch (java.text.ParseException e) {
            throw new ParseException("Invalid entity statement: " + e.getMessage(), e);
        }
    }
}

