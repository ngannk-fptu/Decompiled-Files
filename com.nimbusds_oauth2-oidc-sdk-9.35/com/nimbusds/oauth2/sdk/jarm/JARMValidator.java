/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.nimbusds.jose.Algorithm
 *  com.nimbusds.jose.EncryptionMethod
 *  com.nimbusds.jose.JOSEException
 *  com.nimbusds.jose.JWEAlgorithm
 *  com.nimbusds.jose.JWSAlgorithm
 *  com.nimbusds.jose.JWSAlgorithm$Family
 *  com.nimbusds.jose.jwk.JWKSet
 *  com.nimbusds.jose.jwk.source.ImmutableJWKSet
 *  com.nimbusds.jose.jwk.source.ImmutableSecret
 *  com.nimbusds.jose.jwk.source.JWKSource
 *  com.nimbusds.jose.jwk.source.RemoteJWKSet
 *  com.nimbusds.jose.proc.BadJOSEException
 *  com.nimbusds.jose.proc.JWEDecryptionKeySelector
 *  com.nimbusds.jose.proc.JWEKeySelector
 *  com.nimbusds.jose.proc.JWSKeySelector
 *  com.nimbusds.jose.proc.JWSVerificationKeySelector
 *  com.nimbusds.jose.util.ResourceRetriever
 *  com.nimbusds.jwt.EncryptedJWT
 *  com.nimbusds.jwt.JWT
 *  com.nimbusds.jwt.JWTClaimsSet
 *  com.nimbusds.jwt.JWTParser
 *  com.nimbusds.jwt.PlainJWT
 *  com.nimbusds.jwt.SignedJWT
 *  com.nimbusds.jwt.proc.BadJWTException
 *  com.nimbusds.jwt.proc.ClockSkewAware
 *  com.nimbusds.jwt.proc.DefaultJWTProcessor
 *  com.nimbusds.jwt.proc.JWTClaimsSetVerifier
 *  net.jcip.annotations.ThreadSafe
 */
package com.nimbusds.oauth2.sdk.jarm;

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
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.jwt.PlainJWT;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.proc.BadJWTException;
import com.nimbusds.jwt.proc.ClockSkewAware;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import com.nimbusds.jwt.proc.JWTClaimsSetVerifier;
import com.nimbusds.oauth2.sdk.GeneralException;
import com.nimbusds.oauth2.sdk.as.AuthorizationServerMetadata;
import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.oauth2.sdk.client.ClientInformation;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.oauth2.sdk.jarm.JARMClaimsVerifier;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import com.nimbusds.openid.connect.sdk.validators.AbstractJWTValidator;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class JARMValidator
extends AbstractJWTValidator
implements ClockSkewAware {
    public JARMValidator(Issuer expectedIssuer, ClientID clientID, JWSAlgorithm expectedJWSAlg, JWKSet jwkSet) {
        this(expectedIssuer, clientID, (JWSKeySelector)new JWSVerificationKeySelector(expectedJWSAlg, (JWKSource)new ImmutableJWKSet(jwkSet)), null);
    }

    public JARMValidator(Issuer expectedIssuer, ClientID clientID, JWSAlgorithm expectedJWSAlg, URL jwkSetURI) {
        this(expectedIssuer, clientID, expectedJWSAlg, jwkSetURI, null);
    }

    public JARMValidator(Issuer expectedIssuer, ClientID clientID, JWSAlgorithm expectedJWSAlg, URL jwkSetURI, ResourceRetriever resourceRetriever) {
        this(expectedIssuer, clientID, (JWSKeySelector)new JWSVerificationKeySelector(expectedJWSAlg, (JWKSource)new RemoteJWKSet(jwkSetURI, resourceRetriever)), null);
    }

    public JARMValidator(Issuer expectedIssuer, ClientID clientID, JWSAlgorithm expectedJWSAlg, Secret clientSecret) {
        this(expectedIssuer, clientID, (JWSKeySelector)new JWSVerificationKeySelector(expectedJWSAlg, (JWKSource)new ImmutableSecret(clientSecret.getValueBytes())), null);
    }

    public JARMValidator(Issuer expectedIssuer, ClientID clientID, JWSKeySelector jwsKeySelector, JWEKeySelector jweKeySelector) {
        super(expectedIssuer, clientID, jwsKeySelector, jweKeySelector);
    }

    public JWTClaimsSet validate(String jwtResponseString) throws BadJOSEException, JOSEException {
        try {
            return this.validate(JWTParser.parse((String)jwtResponseString));
        }
        catch (ParseException e) {
            throw new BadJOSEException("Invalid JWT: " + e.getMessage(), (Throwable)e);
        }
    }

    public JWTClaimsSet validate(JWT jwtResponse) throws BadJOSEException, JOSEException {
        if (jwtResponse instanceof SignedJWT) {
            return this.validate((SignedJWT)jwtResponse);
        }
        if (jwtResponse instanceof EncryptedJWT) {
            return this.validate((EncryptedJWT)jwtResponse);
        }
        if (jwtResponse instanceof PlainJWT) {
            throw new BadJWTException("The JWT must not be plain (unsecured)");
        }
        throw new BadJOSEException("Unexpected JWT type: " + jwtResponse.getClass());
    }

    private JWTClaimsSet validate(SignedJWT jwtResponse) throws BadJOSEException, JOSEException {
        if (this.getJWSKeySelector() == null) {
            throw new BadJWTException("Verification of signed JWTs not configured");
        }
        DefaultJWTProcessor jwtProcessor = new DefaultJWTProcessor();
        jwtProcessor.setJWSKeySelector(this.getJWSKeySelector());
        jwtProcessor.setJWTClaimsSetVerifier((JWTClaimsSetVerifier)new JARMClaimsVerifier(this.getExpectedIssuer(), this.getClientID(), this.getMaxClockSkew()));
        return jwtProcessor.process(jwtResponse, null);
    }

    private JWTClaimsSet validate(EncryptedJWT jwtResponse) throws BadJOSEException, JOSEException {
        if (this.getJWEKeySelector() == null) {
            throw new BadJWTException("Decryption of JWTs not configured");
        }
        if (this.getJWSKeySelector() == null) {
            throw new BadJWTException("Verification of signed JWTs not configured");
        }
        DefaultJWTProcessor jwtProcessor = new DefaultJWTProcessor();
        jwtProcessor.setJWSKeySelector(this.getJWSKeySelector());
        jwtProcessor.setJWEKeySelector(this.getJWEKeySelector());
        jwtProcessor.setJWTClaimsSetVerifier((JWTClaimsSetVerifier)new JARMClaimsVerifier(this.getExpectedIssuer(), this.getClientID(), this.getMaxClockSkew()));
        return jwtProcessor.process(jwtResponse, null);
    }

    protected static JWSKeySelector createJWSKeySelector(AuthorizationServerMetadata asMetadata, ClientInformation clientInfo) throws GeneralException {
        JWSAlgorithm expectedJWSAlg = clientInfo.getMetadata().getAuthorizationJWSAlg();
        if (asMetadata.getAuthorizationJWSAlgs() == null) {
            throw new GeneralException("Missing Authorization Server authorization_signing_alg_values_supported parameter");
        }
        if (!asMetadata.getAuthorizationJWSAlgs().contains(expectedJWSAlg)) {
            throw new GeneralException("The Authorization Server doesn't support " + expectedJWSAlg + " authorization responses");
        }
        if (Algorithm.NONE.equals((Object)expectedJWSAlg)) {
            return null;
        }
        if (JWSAlgorithm.Family.RSA.contains((Object)expectedJWSAlg) || JWSAlgorithm.Family.EC.contains((Object)expectedJWSAlg)) {
            URL jwkSetURL;
            try {
                jwkSetURL = asMetadata.getJWKSetURI().toURL();
            }
            catch (MalformedURLException e) {
                throw new GeneralException("Invalid jwk set URI: " + e.getMessage(), e);
            }
            RemoteJWKSet jwkSource = new RemoteJWKSet(jwkSetURL);
            return new JWSVerificationKeySelector(expectedJWSAlg, (JWKSource)jwkSource);
        }
        if (JWSAlgorithm.Family.HMAC_SHA.contains((Object)expectedJWSAlg)) {
            Secret clientSecret = clientInfo.getSecret();
            if (clientSecret == null) {
                throw new GeneralException("Missing client secret");
            }
            return new JWSVerificationKeySelector(expectedJWSAlg, (JWKSource)new ImmutableSecret(clientSecret.getValueBytes()));
        }
        throw new GeneralException("Unsupported JWS algorithm: " + expectedJWSAlg);
    }

    protected static JWEKeySelector createJWEKeySelector(AuthorizationServerMetadata asMetadata, ClientInformation clientInfo, JWKSource clientJWKSource) throws GeneralException {
        JWEAlgorithm expectedJWEAlg = clientInfo.getMetadata().getAuthorizationJWEAlg();
        EncryptionMethod expectedJWEEnc = clientInfo.getMetadata().getAuthorizationJWEEnc();
        if (expectedJWEAlg == null) {
            return null;
        }
        if (expectedJWEEnc == null) {
            throw new GeneralException("Missing required authorization response JWE encryption method for " + expectedJWEAlg);
        }
        if (asMetadata.getAuthorizationJWEAlgs() == null || !asMetadata.getAuthorizationJWEAlgs().contains(expectedJWEAlg)) {
            throw new GeneralException("The Authorization Server doesn't support " + expectedJWEAlg + " authorization responses");
        }
        if (asMetadata.getAuthorizationJWEEncs() == null || !asMetadata.getAuthorizationJWEEncs().contains(expectedJWEEnc)) {
            throw new GeneralException("The Authorization Server doesn't support " + expectedJWEAlg + " / " + expectedJWEEnc + " authorization responses");
        }
        return new JWEDecryptionKeySelector(expectedJWEAlg, expectedJWEEnc, clientJWKSource);
    }

    public static JARMValidator create(AuthorizationServerMetadata asMetadata, ClientInformation clientInfo, JWKSource clientJWKSource) throws GeneralException {
        JWSKeySelector jwsKeySelector = JARMValidator.createJWSKeySelector(asMetadata, clientInfo);
        JWEKeySelector jweKeySelector = JARMValidator.createJWEKeySelector(asMetadata, clientInfo, clientJWKSource);
        return new JARMValidator(asMetadata.getIssuer(), clientInfo.getID(), jwsKeySelector, jweKeySelector);
    }

    public static JARMValidator create(AuthorizationServerMetadata asMetadata, ClientInformation clientInfo) throws GeneralException {
        return JARMValidator.create(asMetadata, clientInfo, null);
    }

    public static JARMValidator create(Issuer issuer, ClientInformation clientInfo) throws GeneralException, IOException {
        return JARMValidator.create(issuer, clientInfo, null, 0, 0);
    }

    public static JARMValidator create(Issuer issuer, ClientInformation clientInfo, JWKSource clientJWKSource, int connectTimeout, int readTimeout) throws GeneralException, IOException {
        AuthorizationServerMetadata asMetadata;
        try {
            asMetadata = OIDCProviderMetadata.resolve(issuer, connectTimeout, readTimeout);
        }
        catch (Exception e) {
            asMetadata = AuthorizationServerMetadata.resolve(issuer, connectTimeout, readTimeout);
        }
        return JARMValidator.create(asMetadata, clientInfo, clientJWKSource);
    }
}

