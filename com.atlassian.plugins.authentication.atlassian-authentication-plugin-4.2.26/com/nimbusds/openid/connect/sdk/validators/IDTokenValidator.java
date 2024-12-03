/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.validators;

import com.nimbusds.jose.Algorithm;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.JWEDecryptionKeySelector;
import com.nimbusds.jose.proc.JWEKeySelector;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.util.ResourceRetriever;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.PlainJWT;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.proc.BadJWTException;
import com.nimbusds.jwt.proc.ClockSkewAware;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import com.nimbusds.oauth2.sdk.GeneralException;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.openid.connect.sdk.Nonce;
import com.nimbusds.openid.connect.sdk.claims.IDTokenClaimsSet;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import com.nimbusds.openid.connect.sdk.rp.OIDCClientInformation;
import com.nimbusds.openid.connect.sdk.validators.AbstractJWTValidator;
import com.nimbusds.openid.connect.sdk.validators.IDTokenClaimsVerifier;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class IDTokenValidator
extends AbstractJWTValidator
implements ClockSkewAware {
    public IDTokenValidator(Issuer expectedIssuer, ClientID clientID) {
        this(expectedIssuer, clientID, (JWSKeySelector)null, null);
    }

    public IDTokenValidator(Issuer expectedIssuer, ClientID clientID, JWSAlgorithm expectedJWSAlg, JWKSet jwkSet) {
        this(expectedIssuer, clientID, new JWSVerificationKeySelector(expectedJWSAlg, new ImmutableJWKSet(jwkSet)), null);
    }

    public IDTokenValidator(Issuer expectedIssuer, ClientID clientID, JWSAlgorithm expectedJWSAlg, URL jwkSetURI) {
        this(expectedIssuer, clientID, expectedJWSAlg, jwkSetURI, null);
    }

    public IDTokenValidator(Issuer expectedIssuer, ClientID clientID, JWSAlgorithm expectedJWSAlg, URL jwkSetURI, ResourceRetriever resourceRetriever) {
        this(expectedIssuer, clientID, new JWSVerificationKeySelector(expectedJWSAlg, new RemoteJWKSet(jwkSetURI, resourceRetriever)), null);
    }

    public IDTokenValidator(Issuer expectedIssuer, ClientID clientID, JWSAlgorithm expectedJWSAlg, Secret clientSecret) {
        this(expectedIssuer, clientID, new JWSVerificationKeySelector(expectedJWSAlg, new ImmutableSecret(clientSecret.getValueBytes())), null);
    }

    public IDTokenValidator(Issuer expectedIssuer, ClientID clientID, JWSKeySelector jwsKeySelector, JWEKeySelector jweKeySelector) {
        super(expectedIssuer, clientID, jwsKeySelector, jweKeySelector);
    }

    public IDTokenClaimsSet validate(JWT idToken, Nonce expectedNonce) throws BadJOSEException, JOSEException {
        if (idToken instanceof PlainJWT) {
            return this.validate((PlainJWT)idToken, expectedNonce);
        }
        if (idToken instanceof SignedJWT) {
            return this.validate((SignedJWT)idToken, expectedNonce);
        }
        if (idToken instanceof EncryptedJWT) {
            return this.validate((EncryptedJWT)idToken, expectedNonce);
        }
        throw new JOSEException("Unexpected JWT type: " + idToken.getClass());
    }

    private IDTokenClaimsSet validate(PlainJWT idToken, Nonce expectedNonce) throws BadJOSEException, JOSEException {
        JWTClaimsSet jwtClaimsSet;
        if (this.getJWSKeySelector() != null) {
            throw new BadJWTException("Signed ID token expected");
        }
        try {
            jwtClaimsSet = idToken.getJWTClaimsSet();
        }
        catch (java.text.ParseException e) {
            throw new BadJWTException(e.getMessage(), e);
        }
        IDTokenClaimsVerifier claimsVerifier = new IDTokenClaimsVerifier(this.getExpectedIssuer(), this.getClientID(), expectedNonce, this.getMaxClockSkew());
        claimsVerifier.verify(jwtClaimsSet, null);
        return IDTokenValidator.toIDTokenClaimsSet(jwtClaimsSet);
    }

    private IDTokenClaimsSet validate(SignedJWT idToken, Nonce expectedNonce) throws BadJOSEException, JOSEException {
        if (this.getJWSKeySelector() == null) {
            throw new BadJWTException("Verification of signed JWTs not configured");
        }
        DefaultJWTProcessor jwtProcessor = new DefaultJWTProcessor();
        jwtProcessor.setJWSKeySelector(this.getJWSKeySelector());
        jwtProcessor.setJWTClaimsSetVerifier(new IDTokenClaimsVerifier(this.getExpectedIssuer(), this.getClientID(), expectedNonce, this.getMaxClockSkew()));
        JWTClaimsSet jwtClaimsSet = jwtProcessor.process(idToken, null);
        return IDTokenValidator.toIDTokenClaimsSet(jwtClaimsSet);
    }

    private IDTokenClaimsSet validate(EncryptedJWT idToken, Nonce expectedNonce) throws BadJOSEException, JOSEException {
        if (this.getJWEKeySelector() == null) {
            throw new BadJWTException("Decryption of JWTs not configured");
        }
        if (this.getJWSKeySelector() == null) {
            throw new BadJWTException("Verification of signed JWTs not configured");
        }
        DefaultJWTProcessor jwtProcessor = new DefaultJWTProcessor();
        jwtProcessor.setJWSKeySelector(this.getJWSKeySelector());
        jwtProcessor.setJWEKeySelector(this.getJWEKeySelector());
        jwtProcessor.setJWTClaimsSetVerifier(new IDTokenClaimsVerifier(this.getExpectedIssuer(), this.getClientID(), expectedNonce, this.getMaxClockSkew()));
        JWTClaimsSet jwtClaimsSet = jwtProcessor.process(idToken, null);
        return IDTokenValidator.toIDTokenClaimsSet(jwtClaimsSet);
    }

    private static IDTokenClaimsSet toIDTokenClaimsSet(JWTClaimsSet jwtClaimsSet) throws JOSEException {
        try {
            return new IDTokenClaimsSet(jwtClaimsSet);
        }
        catch (ParseException e) {
            throw new JOSEException(e.getMessage(), e);
        }
    }

    protected static JWSKeySelector createJWSKeySelector(OIDCProviderMetadata opMetadata, OIDCClientInformation clientInfo) throws GeneralException {
        JWSAlgorithm expectedJWSAlg = clientInfo.getOIDCMetadata().getIDTokenJWSAlg();
        if (opMetadata.getIDTokenJWSAlgs() == null) {
            throw new GeneralException("Missing OpenID Provider id_token_signing_alg_values_supported parameter");
        }
        if (!opMetadata.getIDTokenJWSAlgs().contains(expectedJWSAlg)) {
            throw new GeneralException("The OpenID Provider doesn't support " + expectedJWSAlg + " ID tokens");
        }
        if (Algorithm.NONE.equals(expectedJWSAlg)) {
            return null;
        }
        if (JWSAlgorithm.Family.RSA.contains(expectedJWSAlg) || JWSAlgorithm.Family.EC.contains(expectedJWSAlg)) {
            URL jwkSetURL;
            try {
                jwkSetURL = opMetadata.getJWKSetURI().toURL();
            }
            catch (MalformedURLException e) {
                throw new GeneralException("Invalid jwk set URI: " + e.getMessage(), e);
            }
            RemoteJWKSet jwkSource = new RemoteJWKSet(jwkSetURL);
            return new JWSVerificationKeySelector(expectedJWSAlg, jwkSource);
        }
        if (JWSAlgorithm.Family.HMAC_SHA.contains(expectedJWSAlg)) {
            Secret clientSecret = clientInfo.getSecret();
            if (clientSecret == null) {
                throw new GeneralException("Missing client secret");
            }
            return new JWSVerificationKeySelector(expectedJWSAlg, new ImmutableSecret(clientSecret.getValueBytes()));
        }
        throw new GeneralException("Unsupported JWS algorithm: " + expectedJWSAlg);
    }

    protected static JWEKeySelector createJWEKeySelector(OIDCProviderMetadata opMetadata, OIDCClientInformation clientInfo, JWKSource clientJWKSource) throws GeneralException {
        JWEAlgorithm expectedJWEAlg = clientInfo.getOIDCMetadata().getIDTokenJWEAlg();
        EncryptionMethod expectedJWEEnc = clientInfo.getOIDCMetadata().getIDTokenJWEEnc();
        if (expectedJWEAlg == null) {
            return null;
        }
        if (expectedJWEEnc == null) {
            throw new GeneralException("Missing required ID token JWE encryption method for " + expectedJWEAlg);
        }
        if (opMetadata.getIDTokenJWEAlgs() == null || !opMetadata.getIDTokenJWEAlgs().contains(expectedJWEAlg)) {
            throw new GeneralException("The OpenID Provider doesn't support " + expectedJWEAlg + " ID tokens");
        }
        if (opMetadata.getIDTokenJWEEncs() == null || !opMetadata.getIDTokenJWEEncs().contains(expectedJWEEnc)) {
            throw new GeneralException("The OpenID Provider doesn't support " + expectedJWEAlg + " / " + expectedJWEEnc + " ID tokens");
        }
        return new JWEDecryptionKeySelector(expectedJWEAlg, expectedJWEEnc, clientJWKSource);
    }

    public static IDTokenValidator create(OIDCProviderMetadata opMetadata, OIDCClientInformation clientInfo, JWKSource clientJWKSource) throws GeneralException {
        JWSKeySelector jwsKeySelector = IDTokenValidator.createJWSKeySelector(opMetadata, clientInfo);
        JWEKeySelector jweKeySelector = IDTokenValidator.createJWEKeySelector(opMetadata, clientInfo, clientJWKSource);
        return new IDTokenValidator(opMetadata.getIssuer(), clientInfo.getID(), jwsKeySelector, jweKeySelector);
    }

    public static IDTokenValidator create(OIDCProviderMetadata opMetadata, OIDCClientInformation clientInfo) throws GeneralException {
        return IDTokenValidator.create(opMetadata, clientInfo, null);
    }

    public static IDTokenValidator create(Issuer opIssuer, OIDCClientInformation clientInfo) throws GeneralException, IOException {
        return IDTokenValidator.create(opIssuer, clientInfo, null, 0, 0);
    }

    public static IDTokenValidator create(Issuer opIssuer, OIDCClientInformation clientInfo, JWKSource clientJWKSource, int connectTimeout, int readTimeout) throws GeneralException, IOException {
        OIDCProviderMetadata opMetadata = OIDCProviderMetadata.resolve(opIssuer, connectTimeout, readTimeout);
        return IDTokenValidator.create(opMetadata, clientInfo, clientJWKSource);
    }
}

