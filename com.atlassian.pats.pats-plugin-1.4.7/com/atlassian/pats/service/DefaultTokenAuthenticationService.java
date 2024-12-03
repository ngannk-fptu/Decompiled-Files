/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.permission.AuthorisationException
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.util.Assert
 */
package com.atlassian.pats.service;

import com.atlassian.pats.api.TokenAuthenticationService;
import com.atlassian.pats.api.TokenValidator;
import com.atlassian.pats.db.TokenDTO;
import com.atlassian.pats.db.TokenRepository;
import com.atlassian.pats.service.TokenUtils;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.permission.AuthorisationException;
import java.io.Serializable;
import java.time.Clock;
import java.time.ZonedDateTime;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

public class DefaultTokenAuthenticationService
implements TokenAuthenticationService {
    private static final Logger logger = LoggerFactory.getLogger(DefaultTokenAuthenticationService.class);
    private final TokenValidator tokenValidator;
    private final TokenRepository tokenRepository;
    private final Clock utcClock;
    private final I18nResolver i18nResolver;

    public DefaultTokenAuthenticationService(TokenValidator tokenValidator, TokenRepository tokenRepository, Clock utcClock, I18nResolver i18nResolver) {
        this.tokenValidator = tokenValidator;
        this.tokenRepository = tokenRepository;
        this.utcClock = utcClock;
        this.i18nResolver = i18nResolver;
    }

    @Override
    @Nonnull
    public TokenDTO authenticate(@Nonnull String rawToken) {
        Assert.hasText((String)rawToken, () -> this.i18nResolver.getText("personal.access.tokens.filter.authentication.token.cannot.be.empty"));
        TokenUtils.ExtractedTokenInfo extractTokenInfo = TokenUtils.extractTokenInfo(rawToken);
        String tokenId = extractTokenInfo.getTokenId();
        logger.trace("Got tokenId: [{}] from token", (Object)tokenId);
        TokenDTO token = this.tokenRepository.getByTokenIdAndExpiringAtIsAfter(tokenId, ZonedDateTime.now(this.utcClock)).orElseThrow(() -> new AuthorisationException(this.i18nResolver.getText("personal.access.tokens.filter.authentication.failed.for.token", new Serializable[]{tokenId})));
        if (this.tokenValidator.doTokensMatch(rawToken, token.getHashedToken())) {
            logger.trace("Authentication successful - returning token: [{}]", (Object)token);
            return token;
        }
        throw new AuthorisationException(this.i18nResolver.getText("personal.access.tokens.filter.authentication.failed.for.user", new Serializable[]{token.getUserKey(), tokenId}));
    }
}

