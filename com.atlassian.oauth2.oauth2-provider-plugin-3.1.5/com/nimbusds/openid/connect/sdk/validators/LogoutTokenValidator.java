/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.validators;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.BadJOSEException;
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
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import com.nimbusds.oauth2.sdk.GeneralException;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.openid.connect.sdk.claims.LogoutTokenClaimsSet;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import com.nimbusds.openid.connect.sdk.rp.OIDCClientInformation;
import com.nimbusds.openid.connect.sdk.validators.AbstractJWTValidator;
import com.nimbusds.openid.connect.sdk.validators.IDTokenValidator;
import com.nimbusds.openid.connect.sdk.validators.LogoutTokenClaimsVerifier;
import java.net.URL;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class LogoutTokenValidator
extends AbstractJWTValidator {
    public LogoutTokenValidator(Issuer expectedIssuer, ClientID clientID, JWSAlgorithm expectedJWSAlg, JWKSet jwkSet) {
        this(expectedIssuer, clientID, new JWSVerificationKeySelector(expectedJWSAlg, new ImmutableJWKSet(jwkSet)), null);
    }

    public LogoutTokenValidator(Issuer expectedIssuer, ClientID clientID, JWSAlgorithm expectedJWSAlg, URL jwkSetURI) {
        this(expectedIssuer, clientID, expectedJWSAlg, jwkSetURI, null);
    }

    public LogoutTokenValidator(Issuer expectedIssuer, ClientID clientID, JWSAlgorithm expectedJWSAlg, URL jwkSetURI, ResourceRetriever resourceRetriever) {
        this(expectedIssuer, clientID, new JWSVerificationKeySelector(expectedJWSAlg, new RemoteJWKSet(jwkSetURI, resourceRetriever)), null);
    }

    public LogoutTokenValidator(Issuer expectedIssuer, ClientID clientID, JWSAlgorithm expectedJWSAlg, Secret clientSecret) {
        this(expectedIssuer, clientID, new JWSVerificationKeySelector(expectedJWSAlg, new ImmutableSecret(clientSecret.getValueBytes())), null);
    }

    public LogoutTokenValidator(Issuer expectedIssuer, ClientID clientID, JWSKeySelector jwsKeySelector, JWEKeySelector jweKeySelector) {
        super(expectedIssuer, clientID, jwsKeySelector, jweKeySelector);
    }

    public LogoutTokenClaimsSet validate(JWT logoutToken) throws BadJOSEException, JOSEException {
        if (logoutToken instanceof PlainJWT) {
            throw new BadJWTException("Unsecured (plain) logout tokens are illegal");
        }
        if (logoutToken instanceof SignedJWT) {
            return this.validate((SignedJWT)logoutToken);
        }
        if (logoutToken instanceof EncryptedJWT) {
            return this.validate((EncryptedJWT)logoutToken);
        }
        throw new JOSEException("Unexpected JWT type: " + logoutToken.getClass());
    }

    private LogoutTokenClaimsSet validate(SignedJWT logoutToken) throws BadJOSEException, JOSEException {
        if (this.getJWSKeySelector() == null) {
            throw new BadJWTException("Verification of signed JWTs not configured");
        }
        DefaultJWTProcessor jwtProcessor = new DefaultJWTProcessor();
        jwtProcessor.setJWSKeySelector(this.getJWSKeySelector());
        jwtProcessor.setJWTClaimsSetVerifier(new LogoutTokenClaimsVerifier(this.getExpectedIssuer(), this.getClientID()));
        JWTClaimsSet jwtClaimsSet = jwtProcessor.process(logoutToken, null);
        return LogoutTokenValidator.toLogoutTokenClaimsSet(jwtClaimsSet);
    }

    private LogoutTokenClaimsSet validate(EncryptedJWT logoutToken) throws BadJOSEException, JOSEException {
        if (this.getJWEKeySelector() == null) {
            throw new BadJWTException("Decryption of JWTs not configured");
        }
        if (this.getJWSKeySelector() == null) {
            throw new BadJWTException("Verification of signed JWTs not configured");
        }
        DefaultJWTProcessor jwtProcessor = new DefaultJWTProcessor();
        jwtProcessor.setJWSKeySelector(this.getJWSKeySelector());
        jwtProcessor.setJWEKeySelector(this.getJWEKeySelector());
        jwtProcessor.setJWTClaimsSetVerifier(new LogoutTokenClaimsVerifier(this.getExpectedIssuer(), this.getClientID()));
        JWTClaimsSet jwtClaimsSet = jwtProcessor.process(logoutToken, null);
        return LogoutTokenValidator.toLogoutTokenClaimsSet(jwtClaimsSet);
    }

    private static LogoutTokenClaimsSet toLogoutTokenClaimsSet(JWTClaimsSet jwtClaimsSet) throws JOSEException {
        try {
            return new LogoutTokenClaimsSet(jwtClaimsSet);
        }
        catch (ParseException e) {
            throw new JOSEException(e.getMessage(), e);
        }
    }

    public static LogoutTokenValidator create(OIDCProviderMetadata opMetadata, OIDCClientInformation clientInfo, JWKSource clientJWKSource) throws GeneralException {
        JWSKeySelector jwsKeySelector = IDTokenValidator.createJWSKeySelector(opMetadata, clientInfo);
        JWEKeySelector jweKeySelector = IDTokenValidator.createJWEKeySelector(opMetadata, clientInfo, clientJWKSource);
        return new LogoutTokenValidator(opMetadata.getIssuer(), clientInfo.getID(), jwsKeySelector, jweKeySelector);
    }
}

