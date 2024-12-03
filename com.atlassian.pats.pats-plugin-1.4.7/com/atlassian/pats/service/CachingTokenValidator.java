/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  javax.validation.constraints.NotNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.pats.service;

import com.atlassian.cache.Cache;
import com.atlassian.pats.api.TokenAuthenticationService;
import com.atlassian.pats.api.TokenValidator;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CachingTokenValidator
implements TokenValidator {
    private static final Logger logger = LoggerFactory.getLogger(CachingTokenValidator.class);
    private final Cache<Long, TokenAuthenticationService.AuthenticationResult> authenticationCache;
    private final TokenValidator delegate;
    private final TokenValidator.ChecksumGenerator checksumGenerator;

    public CachingTokenValidator(Cache<Long, TokenAuthenticationService.AuthenticationResult> authenticationCache, TokenValidator delegate) {
        this(authenticationCache, delegate, new TokenValidator.DefaultChecksumGenerator());
    }

    public CachingTokenValidator(Cache<Long, TokenAuthenticationService.AuthenticationResult> authenticationCache, TokenValidator delegate, TokenValidator.ChecksumGenerator checksumGenerator) {
        this.authenticationCache = authenticationCache;
        this.delegate = delegate;
        this.checksumGenerator = checksumGenerator;
    }

    @Override
    public boolean doTokensMatch(@NotNull String token, @NotNull String hashedToken) {
        logger.trace("Verifying user token with hashed token: [{}]", (Object)hashedToken);
        Long checksum = this.checksumGenerator.getKey(token, hashedToken);
        TokenAuthenticationService.AuthenticationResult cachedAuthResult = this.retrieveAndCache(checksum, token, hashedToken);
        return this.isChecksumCollision(hashedToken, cachedAuthResult) ? this.replaceCachedItem(token, hashedToken, checksum).isAuthenticated() : cachedAuthResult.isAuthenticated();
    }

    private boolean isChecksumCollision(String hashedToken, TokenAuthenticationService.AuthenticationResult cachedAuthResult) {
        return !cachedAuthResult.getHashedToken().equals(hashedToken);
    }

    private TokenAuthenticationService.AuthenticationResult replaceCachedItem(String token, String hashedToken, Long checksum) {
        logger.warn("Caught checksum collision - re-caching: [{}]", (Object)checksum);
        this.authenticationCache.remove((Object)checksum);
        return this.retrieveAndCache(checksum, token, hashedToken);
    }

    private TokenAuthenticationService.AuthenticationResult retrieveAndCache(Long checksum, String token, String hashedToken) {
        return (TokenAuthenticationService.AuthenticationResult)this.authenticationCache.get((Object)checksum, () -> new TokenAuthenticationService.AuthenticationResult(hashedToken, this.delegate.doTokensMatch(token, hashedToken)));
    }
}

