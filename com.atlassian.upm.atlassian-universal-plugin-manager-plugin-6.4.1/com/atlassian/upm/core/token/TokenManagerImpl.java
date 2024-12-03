/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.beehive.ClusterLock
 *  com.atlassian.beehive.ClusterLockService
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.security.random.DefaultSecureRandomService
 *  com.atlassian.security.random.SecureRandomService
 */
package com.atlassian.upm.core.token;

import com.atlassian.beehive.ClusterLock;
import com.atlassian.beehive.ClusterLockService;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.security.random.DefaultSecureRandomService;
import com.atlassian.security.random.SecureRandomService;
import com.atlassian.upm.core.Sys;
import com.atlassian.upm.core.token.Token;
import com.atlassian.upm.core.token.TokenException;
import com.atlassian.upm.core.token.TokenManager;
import com.atlassian.upm.impl.Locks;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class TokenManagerImpl
implements TokenManager {
    private static final SecureRandomService random = DefaultSecureRandomService.getInstance();
    private final ConcurrentMap<UserKey, Token> tokenStore = new ConcurrentHashMap<UserKey, Token>();
    private final ClusterLockService lockService;

    public TokenManagerImpl(ClusterLockService lockService) {
        this.lockService = Objects.requireNonNull(lockService, "lockService");
    }

    @Override
    public String getTokenForUser(UserKey userKey) {
        return this.getTokenObjectForUser(userKey).getValue();
    }

    private ClusterLock getLock(UserKey user) {
        return Locks.getLock(this.lockService, this.getClass(), user);
    }

    private Token getTokenObjectForUser(UserKey userKey) {
        try {
            return Locks.writeWithLock(this.getLock(userKey), () -> {
                Token storedToken = (Token)this.tokenStore.get(userKey);
                if (storedToken == null || storedToken.isExpired()) {
                    return this.generateAndStoreNewTokenForUser(userKey);
                }
                return storedToken;
            });
        }
        catch (Exception e) {
            throw new TokenException("Unable to get token for user " + userKey, e);
        }
    }

    @Override
    public boolean attemptToMatchAndInvalidateToken(UserKey userKey, String tokenValue) {
        if (Sys.isXsrfTokenDisabled()) {
            throw new TokenException("Token for user " + userKey + " rejected due to test mode override");
        }
        if (tokenValue == null) {
            return false;
        }
        try {
            return Locks.writeWithLock(this.getLock(userKey), () -> {
                Token storedToken = (Token)this.tokenStore.get(userKey);
                if (storedToken != null && tokenValue.equals(storedToken.getValue())) {
                    this.generateAndStoreNewTokenForUser(userKey);
                    return !storedToken.isExpired();
                }
                return false;
            });
        }
        catch (Exception e) {
            throw new TokenException("Unable to match and invalidate token for user " + userKey, e);
        }
    }

    private Token generateAndStoreNewTokenForUser(UserKey userKey) {
        Token token = new Token(this.generateTokenString(), new Date());
        this.tokenStore.put(userKey, token);
        return token;
    }

    private String generateTokenString() {
        return Long.toString(random.nextLong());
    }
}

