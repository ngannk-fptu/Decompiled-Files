/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.util.Clock
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.security.random.DefaultSecureTokenGenerator
 *  com.atlassian.security.random.SecureTokenGenerator
 *  com.atlassian.spring.container.ContainerManager
 */
package com.atlassian.confluence.user;

import com.atlassian.confluence.event.events.user.UserVerificationTokenCleanupEvent;
import com.atlassian.confluence.user.UserVerificationToken;
import com.atlassian.confluence.user.UserVerificationTokenManager;
import com.atlassian.confluence.user.UserVerificationTokenType;
import com.atlassian.confluence.user.persistence.dao.UserVerificationTokenDao;
import com.atlassian.core.util.Clock;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.security.random.DefaultSecureTokenGenerator;
import com.atlassian.security.random.SecureTokenGenerator;
import com.atlassian.spring.container.ContainerManager;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class DefaultUserVerificationTokenManager
implements UserVerificationTokenManager {
    private static final long ONE_DAY = 86400000L;
    private final SecureTokenGenerator secureTokenGenerator;
    private final UserVerificationTokenDao tokenStore;
    private final Clock clock;
    private final EventPublisher eventPublisher;

    @Deprecated
    public DefaultUserVerificationTokenManager(UserVerificationTokenDao tokenStore, Clock clock) {
        this(tokenStore, clock, DefaultSecureTokenGenerator.getInstance());
    }

    @Deprecated
    public DefaultUserVerificationTokenManager(UserVerificationTokenDao tokenStore, Clock clock, SecureTokenGenerator secureTokenGenerator) {
        this(tokenStore, clock, secureTokenGenerator, (EventPublisher)ContainerManager.getComponent((String)"eventPublisher"));
    }

    public DefaultUserVerificationTokenManager(UserVerificationTokenDao tokenStore, Clock clock, SecureTokenGenerator secureTokenGenerator, EventPublisher eventPublisher) {
        this.tokenStore = Objects.requireNonNull(tokenStore);
        this.clock = Objects.requireNonNull(clock);
        this.secureTokenGenerator = Objects.requireNonNull(secureTokenGenerator);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
    }

    @Override
    public String generateAndSaveToken(String userName, UserVerificationTokenType tokenType) {
        UserVerificationToken token = new UserVerificationToken(tokenType, userName, this.secureTokenGenerator.generateToken(), this.now());
        this.tokenStore.storeToken(token);
        return token.getTokenString();
    }

    private Date now() {
        return this.clock.getCurrentDate();
    }

    @Override
    public boolean hasToken(String userName, UserVerificationTokenType tokenType) {
        return this.tokenStore.getToken(userName, tokenType) != null;
    }

    @Override
    public boolean hasValidUserToken(String userName, UserVerificationTokenType tokenType, String token) {
        UserVerificationToken savedToken = this.tokenStore.getToken(userName, tokenType);
        return savedToken != null && savedToken.matchesToken(token) && this.isFresh(savedToken);
    }

    @Override
    public boolean hasOutdatedUserToken(String userName, UserVerificationTokenType tokenType) {
        UserVerificationToken savedToken = this.tokenStore.getToken(userName, tokenType);
        return savedToken != null && !this.isFresh(savedToken);
    }

    @Override
    public boolean isFresh(UserVerificationToken token) {
        Date earliestValidDate = new Date(this.now().getTime() - DefaultUserVerificationTokenManager.getValidityPeriod(token.getTokenType()));
        return token.wasIssuedAfter(earliestValidDate);
    }

    private static long getValidityPeriod(UserVerificationTokenType tokenType) {
        switch (tokenType) {
            case PASSWORD_RESET: {
                return 86400000L;
            }
            case USER_SIGNUP: {
                return 2592000000L;
            }
        }
        throw new IllegalArgumentException("Unknown token type " + tokenType);
    }

    @Override
    public void clearToken(String userName, UserVerificationTokenType tokenType) {
        this.tokenStore.clearToken(userName, tokenType);
    }

    @Override
    public void clearToken(String userName) {
        Arrays.stream(UserVerificationTokenType.values()).forEach(userVerificationTokenType -> this.tokenStore.clearToken(userName, (UserVerificationTokenType)((Object)userVerificationTokenType)));
    }

    @Override
    public int clearAllExpiredTokens() {
        AtomicInteger count = new AtomicInteger(0);
        Arrays.stream(UserVerificationTokenType.values()).forEach(userVerificationTokenType -> this.tokenStore.getUsernamesByTokenType((UserVerificationTokenType)((Object)userVerificationTokenType)).forEach(username -> {
            if (this.hasOutdatedUserToken((String)username, (UserVerificationTokenType)((Object)userVerificationTokenType))) {
                count.incrementAndGet();
                this.clearToken((String)username, (UserVerificationTokenType)((Object)userVerificationTokenType));
            }
        }));
        this.eventPublisher.publish((Object)new UserVerificationTokenCleanupEvent(this));
        return count.get();
    }
}

