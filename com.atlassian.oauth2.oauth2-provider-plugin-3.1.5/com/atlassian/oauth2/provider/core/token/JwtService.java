/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth2.provider.api.settings.ProviderSettingsService
 *  com.atlassian.oauth2.provider.api.token.exception.access.UnrecognisedTokenException
 *  com.atlassian.sal.api.message.I18nResolver
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.oauth2.provider.core.token;

import com.atlassian.oauth2.provider.api.settings.ProviderSettingsService;
import com.atlassian.oauth2.provider.api.token.exception.access.UnrecognisedTokenException;
import com.atlassian.sal.api.message.I18nResolver;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.text.ParseException;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JwtService {
    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);
    private final ProviderSettingsService providerSettingsService;
    private final I18nResolver i18nResolver;

    public JwtService(ProviderSettingsService providerSettingsService, I18nResolver i18nResolver) {
        this.providerSettingsService = providerSettingsService;
        this.i18nResolver = i18nResolver;
    }

    public String createToken(String tokenId) {
        logger.debug("Creating token for id [{}].", (Object)tokenId);
        MACSigner signer = new MACSigner(this.providerSettingsService.getJwtSecret());
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder().claim("id", tokenId).build();
        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
        signedJWT.sign(signer);
        return signedJWT.serialize();
    }

    @Nonnull
    public String extractTokenId(String jwtToken) throws UnrecognisedTokenException {
        try {
            SignedJWT signedJWT = SignedJWT.parse(jwtToken);
            if (!this.verifySignature(signedJWT)) {
                logger.warn("Unable to verify JWT token signature.");
                return "";
            }
            return signedJWT.getJWTClaimsSet().getStringClaim("id");
        }
        catch (JOSEException | IllegalArgumentException | ParseException e) {
            logger.trace("Failed to parse token", (Throwable)e);
            throw new UnrecognisedTokenException(this.i18nResolver.getText("oauth2.authentication.token.not.recognized"));
        }
    }

    private boolean verifySignature(SignedJWT signedJWT) throws JOSEException {
        MACVerifier verifier = new MACVerifier(this.providerSettingsService.getJwtSecret());
        return signedJWT.verify(verifier);
    }
}

