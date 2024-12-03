/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.oauth2.provider.api.authorization.Authorization
 *  com.atlassian.oauth2.provider.api.authorization.AuthorizationFlowResult
 *  com.atlassian.oauth2.provider.api.authorization.AuthorizationService
 *  com.atlassian.oauth2.provider.api.authorization.TokenResponseErrorDescription
 *  com.atlassian.oauth2.provider.api.authorization.dao.AuthorizationDao
 *  com.atlassian.oauth2.provider.api.authorization.dao.AuthorizationEntity
 *  com.atlassian.oauth2.provider.api.authorization.dao.AuthorizationEntity$AuthorizationEntityBuilder
 *  com.atlassian.oauth2.provider.api.event.authorization.ClientAuthorizationEvent
 *  com.atlassian.oauth2.provider.api.pkce.CodeChallengeMethod
 *  com.atlassian.oauth2.provider.api.pkce.PkceService
 *  com.atlassian.oauth2.scopes.api.Scope
 *  com.atlassian.sal.api.user.UserManager
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.oauth2.provider.core.authorization;

import com.atlassian.event.api.EventPublisher;
import com.atlassian.oauth2.common.IdGenerator;
import com.atlassian.oauth2.provider.api.authorization.Authorization;
import com.atlassian.oauth2.provider.api.authorization.AuthorizationFlowResult;
import com.atlassian.oauth2.provider.api.authorization.AuthorizationService;
import com.atlassian.oauth2.provider.api.authorization.TokenResponseErrorDescription;
import com.atlassian.oauth2.provider.api.authorization.dao.AuthorizationDao;
import com.atlassian.oauth2.provider.api.authorization.dao.AuthorizationEntity;
import com.atlassian.oauth2.provider.api.event.authorization.ClientAuthorizationEvent;
import com.atlassian.oauth2.provider.api.pkce.CodeChallengeMethod;
import com.atlassian.oauth2.provider.api.pkce.PkceService;
import com.atlassian.oauth2.provider.core.authorization.exception.AuthorizationCodeFlowException;
import com.atlassian.oauth2.provider.core.properties.SystemProperty;
import com.atlassian.oauth2.scopes.api.Scope;
import com.atlassian.sal.api.user.UserManager;
import java.time.Clock;
import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultAuthorizationService
implements AuthorizationService {
    private static final Logger logger = LoggerFactory.getLogger(DefaultAuthorizationService.class);
    private static final Integer MAX_RETRIES = 5;
    private final IdGenerator codeGenerator;
    private final UserManager userManager;
    private final AuthorizationDao authorizationDao;
    private final PkceService pkceService;
    private final Clock clock;
    private final EventPublisher eventPublisher;

    public DefaultAuthorizationService(IdGenerator codeGenerator, UserManager userManager, AuthorizationDao authorizationDao, Clock clock, PkceService pkceService, EventPublisher eventPublisher) {
        this.codeGenerator = codeGenerator;
        this.userManager = userManager;
        this.authorizationDao = authorizationDao;
        this.clock = clock;
        this.pkceService = pkceService;
        this.eventPublisher = eventPublisher;
    }

    @Nonnull
    public String startAuthorizationFlow(@Nonnull String clientId, @Nonnull String redirectUri, @Nonnull Scope scope, CodeChallengeMethod codeChallengeMethod, String codeChallenge) {
        return this.attemptSaveWithRetry(0, AuthorizationEntity.builder().clientId(clientId).redirectUri(redirectUri).userKey(this.userManager.getRemoteUserKey().getStringValue()).createdAt(Long.valueOf(this.clock.millis())).scope(scope).codeChallengeMethod(codeChallengeMethod).codeChallenge(codeChallenge));
    }

    private String attemptSaveWithRetry(Integer failureCount, AuthorizationEntity.AuthorizationEntityBuilder authorizationBuilder) {
        if (failureCount >= MAX_RETRIES) {
            throw new AuthorizationCodeFlowException();
        }
        String code = this.codeGenerator.generate();
        AuthorizationEntity authorization = authorizationBuilder.authorizationCode(code).build();
        try {
            this.authorizationDao.save((Authorization)authorization);
        }
        catch (Exception e) {
            logger.debug("Failed to create authorization. Retry [" + failureCount + "] of [" + MAX_RETRIES + "]", (Throwable)e);
            this.attemptSaveWithRetry(failureCount + 1, authorizationBuilder);
        }
        this.eventPublisher.publish((Object)new ClientAuthorizationEvent((Authorization)authorization));
        return code;
    }

    public AuthorizationFlowResult completeAuthorizationFlow(@Nonnull String clientId, @Nonnull String redirectUri, @Nonnull String code) {
        return this.authorizationDao.removeByCode(code).filter(this::isNotExpired).map(authorization -> {
            if (this.isNotEquals(clientId, authorization.getClientId())) {
                return AuthorizationFlowResult.failed((TokenResponseErrorDescription)TokenResponseErrorDescription.INVALID_CLIENT_ID);
            }
            if (this.isNotEquals(redirectUri, authorization.getRedirectUri())) {
                return AuthorizationFlowResult.failed((TokenResponseErrorDescription)TokenResponseErrorDescription.INVALID_REDIRECT_URI);
            }
            return AuthorizationFlowResult.success((Authorization)authorization);
        }).orElse(AuthorizationFlowResult.failed((TokenResponseErrorDescription)TokenResponseErrorDescription.INVALID_CODE));
    }

    public Optional<Authorization> getAuthorization(@Nonnull String authorizationCode) {
        return this.authorizationDao.findByCode(authorizationCode);
    }

    public void removeExpiredAuthorizations(@Nonnull Duration expiration) {
        this.authorizationDao.removeExpiredAuthorizationsAfter(expiration);
    }

    public boolean isPkceEnabledForAuthorization(@Nonnull String authorizationCode) {
        return this.authorizationDao.findByCode(authorizationCode).map(authorization -> authorization.getCodeChallengeMethod() != null).orElse(false);
    }

    public boolean isPkceCodeVerifierValidAgainstAuthorization(@Nonnull String codeVerifier, @Nonnull String authorizationCode) {
        return this.authorizationDao.findByCode(authorizationCode).map(authorization -> this.pkceService.isExpectedCodeChallengeGenerated(authorization.getCodeChallenge(), authorization.getCodeChallengeMethod(), codeVerifier)).orElse(false);
    }

    private boolean isNotExpired(Authorization authorization) {
        long expiryTime = this.clock.millis() + SystemProperty.MAX_AUTHORIZATION_CODE_LIFETIME.getValue().toMillis();
        return authorization.getCreatedAt() < expiryTime;
    }

    private <T> boolean isNotEquals(T obj1, T obj2) {
        return !Objects.equals(obj1, obj2);
    }
}

