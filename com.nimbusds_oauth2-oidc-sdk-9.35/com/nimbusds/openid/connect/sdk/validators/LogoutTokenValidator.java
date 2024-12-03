/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.nimbusds.jose.JOSEException
 *  com.nimbusds.jose.JOSEObjectType
 *  com.nimbusds.jose.JWSAlgorithm
 *  com.nimbusds.jose.jwk.JWKSet
 *  com.nimbusds.jose.jwk.source.ImmutableJWKSet
 *  com.nimbusds.jose.jwk.source.ImmutableSecret
 *  com.nimbusds.jose.jwk.source.JWKSource
 *  com.nimbusds.jose.jwk.source.RemoteJWKSet
 *  com.nimbusds.jose.proc.BadJOSEException
 *  com.nimbusds.jose.proc.JOSEObjectTypeVerifier
 *  com.nimbusds.jose.proc.JWEKeySelector
 *  com.nimbusds.jose.proc.JWSKeySelector
 *  com.nimbusds.jose.proc.JWSVerificationKeySelector
 *  com.nimbusds.jose.proc.SecurityContext
 *  com.nimbusds.jose.util.ResourceRetriever
 *  com.nimbusds.jwt.EncryptedJWT
 *  com.nimbusds.jwt.JWT
 *  com.nimbusds.jwt.JWTClaimsSet
 *  com.nimbusds.jwt.PlainJWT
 *  com.nimbusds.jwt.SignedJWT
 *  com.nimbusds.jwt.proc.BadJWTException
 *  com.nimbusds.jwt.proc.DefaultJWTProcessor
 *  com.nimbusds.jwt.proc.JWTClaimsSetVerifier
 *  net.jcip.annotations.ThreadSafe
 */
package com.nimbusds.openid.connect.sdk.validators;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.JOSEObjectTypeVerifier;
import com.nimbusds.jose.proc.JWEKeySelector;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jose.util.ResourceRetriever;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.PlainJWT;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.proc.BadJWTException;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import com.nimbusds.jwt.proc.JWTClaimsSetVerifier;
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
    public static final JOSEObjectType TYPE = new JOSEObjectType("logout+jwt");
    private final boolean requireTypedTokens;

    public LogoutTokenValidator(Issuer expectedIssuer, ClientID clientID, JWSAlgorithm expectedJWSAlg, JWKSet jwkSet) {
        this(expectedIssuer, clientID, (JWSKeySelector<?>)new JWSVerificationKeySelector(expectedJWSAlg, (JWKSource)new ImmutableJWKSet(jwkSet)), (JWEKeySelector<?>)null);
    }

    public LogoutTokenValidator(Issuer expectedIssuer, ClientID clientID, JWSAlgorithm expectedJWSAlg, URL jwkSetURI) {
        this(expectedIssuer, clientID, expectedJWSAlg, jwkSetURI, null);
    }

    public LogoutTokenValidator(Issuer expectedIssuer, ClientID clientID, JWSAlgorithm expectedJWSAlg, URL jwkSetURI, ResourceRetriever resourceRetriever) {
        this(expectedIssuer, clientID, (JWSKeySelector<?>)new JWSVerificationKeySelector(expectedJWSAlg, (JWKSource)new RemoteJWKSet(jwkSetURI, resourceRetriever)), (JWEKeySelector<?>)null);
    }

    public LogoutTokenValidator(Issuer expectedIssuer, ClientID clientID, JWSAlgorithm expectedJWSAlg, Secret clientSecret) {
        this(expectedIssuer, clientID, (JWSKeySelector<?>)new JWSVerificationKeySelector(expectedJWSAlg, (JWKSource)new ImmutableSecret(clientSecret.getValueBytes())), (JWEKeySelector<?>)null);
    }

    @Deprecated
    public LogoutTokenValidator(Issuer expectedIssuer, ClientID clientID, JWSKeySelector<?> jwsKeySelector, JWEKeySelector<?> jweKeySelector) {
        this(expectedIssuer, clientID, false, jwsKeySelector, jweKeySelector);
    }

    public LogoutTokenValidator(Issuer expectedIssuer, ClientID clientID, boolean requireTypedToken, JWSKeySelector<?> jwsKeySelector, JWEKeySelector<?> jweKeySelector) {
        super(TYPE, expectedIssuer, clientID, jwsKeySelector, jweKeySelector);
        this.requireTypedTokens = requireTypedToken;
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
        jwtProcessor.setJWSTypeVerifier((JOSEObjectTypeVerifier)new TypeVerifier(this.requireTypedTokens));
        jwtProcessor.setJWSKeySelector(this.getJWSKeySelector());
        jwtProcessor.setJWTClaimsSetVerifier((JWTClaimsSetVerifier)new LogoutTokenClaimsVerifier(this.getExpectedIssuer(), this.getClientID()));
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
        jwtProcessor.setJWETypeVerifier((JOSEObjectTypeVerifier)new TypeVerifier(this.requireTypedTokens));
        jwtProcessor.setJWSKeySelector(this.getJWSKeySelector());
        jwtProcessor.setJWEKeySelector(this.getJWEKeySelector());
        jwtProcessor.setJWTClaimsSetVerifier((JWTClaimsSetVerifier)new LogoutTokenClaimsVerifier(this.getExpectedIssuer(), this.getClientID()));
        JWTClaimsSet jwtClaimsSet = jwtProcessor.process(logoutToken, null);
        return LogoutTokenValidator.toLogoutTokenClaimsSet(jwtClaimsSet);
    }

    private static LogoutTokenClaimsSet toLogoutTokenClaimsSet(JWTClaimsSet jwtClaimsSet) throws JOSEException {
        try {
            return new LogoutTokenClaimsSet(jwtClaimsSet);
        }
        catch (ParseException e) {
            throw new JOSEException(e.getMessage(), (Throwable)e);
        }
    }

    public static LogoutTokenValidator create(OIDCProviderMetadata opMetadata, OIDCClientInformation clientInfo, JWKSource<?> clientJWKSource) throws GeneralException {
        JWSKeySelector jwsKeySelector = IDTokenValidator.createJWSKeySelector(opMetadata, clientInfo);
        JWEKeySelector jweKeySelector = IDTokenValidator.createJWEKeySelector(opMetadata, clientInfo, clientJWKSource);
        return new LogoutTokenValidator(opMetadata.getIssuer(), clientInfo.getID(), (JWSKeySelector<?>)jwsKeySelector, (JWEKeySelector<?>)jweKeySelector);
    }

    private static class TypeVerifier
    implements JOSEObjectTypeVerifier {
        private final boolean requireTypedTokens;

        public TypeVerifier(boolean requireTypedTokens) {
            this.requireTypedTokens = requireTypedTokens;
        }

        public void verify(JOSEObjectType type, SecurityContext context) throws BadJOSEException {
            if (this.requireTypedTokens) {
                if (!TYPE.equals((Object)type)) {
                    throw new BadJOSEException("Invalid / missing logout token typ (type) header, must be " + TYPE);
                }
            } else if (type != null && !TYPE.equals((Object)type)) {
                throw new BadJOSEException("If set the logout token typ (type) header must be " + TYPE);
            }
        }
    }
}

