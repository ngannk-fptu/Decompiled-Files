/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth2.provider.api.token.TokenService
 *  com.atlassian.oauth2.provider.api.token.access.AccessToken
 *  com.atlassian.oauth2.provider.api.token.access.dao.AccessTokenDao
 *  com.atlassian.oauth2.provider.api.token.access.dao.AccessTokenEntity
 *  com.atlassian.oauth2.provider.api.token.access.exception.UserKeyNotFoundException
 *  com.atlassian.oauth2.provider.api.token.refresh.RefreshToken
 *  com.atlassian.oauth2.provider.api.token.refresh.dao.RefreshTokenDao
 *  com.atlassian.oauth2.provider.api.token.refresh.dao.RefreshTokenEntity
 *  com.atlassian.oauth2.scopes.api.Scope
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.sal.api.user.UserManager
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.oauth2.provider.core.token;

import com.atlassian.oauth2.provider.api.token.TokenService;
import com.atlassian.oauth2.provider.api.token.access.AccessToken;
import com.atlassian.oauth2.provider.api.token.access.dao.AccessTokenDao;
import com.atlassian.oauth2.provider.api.token.access.dao.AccessTokenEntity;
import com.atlassian.oauth2.provider.api.token.access.exception.UserKeyNotFoundException;
import com.atlassian.oauth2.provider.api.token.refresh.RefreshToken;
import com.atlassian.oauth2.provider.api.token.refresh.dao.RefreshTokenDao;
import com.atlassian.oauth2.provider.api.token.refresh.dao.RefreshTokenEntity;
import com.atlassian.oauth2.provider.core.credentials.ClientCredentialsGenerator;
import com.atlassian.oauth2.provider.core.event.OAuth2ProviderEventPublisher;
import com.atlassian.oauth2.provider.core.security.Hasher;
import com.atlassian.oauth2.scopes.api.Scope;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import java.time.Clock;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultTokenService
implements TokenService {
    private static final Logger logger = LoggerFactory.getLogger(DefaultTokenService.class);
    private final AccessTokenDao accessTokenDao;
    private final RefreshTokenDao refreshTokenDao;
    private final ClientCredentialsGenerator clientCredentialsGenerator;
    private final UserManager userManager;
    private final Clock clock;
    private final I18nResolver i18nResolver;
    private final Hasher hasher;
    private final OAuth2ProviderEventPublisher oAuth2ProviderEventPublisher;

    public DefaultTokenService(AccessTokenDao accessTokenDao, RefreshTokenDao refreshTokenDao, ClientCredentialsGenerator clientCredentialsGenerator, UserManager userManager, Clock clock, I18nResolver i18nResolver, Hasher hasher, OAuth2ProviderEventPublisher oAuth2ProviderEventPublisher) {
        this.accessTokenDao = accessTokenDao;
        this.refreshTokenDao = refreshTokenDao;
        this.clientCredentialsGenerator = clientCredentialsGenerator;
        this.userManager = userManager;
        this.clock = clock;
        this.i18nResolver = i18nResolver;
        this.hasher = hasher;
        this.oAuth2ProviderEventPublisher = oAuth2ProviderEventPublisher;
    }

    @Nonnull
    public AccessToken createAccessToken(String clientId, String userKey, String authorizationCode, long authorizationDate, Scope scope) {
        if (this.userManager.getUserProfile(new UserKey(userKey)) != null) {
            AccessTokenEntity accessTokenEntity = AccessTokenEntity.builder().id(this.clientCredentialsGenerator.generate(ClientCredentialsGenerator.Length.THIRTY_TWO)).clientId(clientId).userKey(userKey).authorizationCode(authorizationCode).scope(scope).authorizationDate(Long.valueOf(authorizationDate)).createdAt(Long.valueOf(this.clock.millis())).build();
            logger.debug("Creating an access token for client id [{}].", (Object)clientId);
            this.accessTokenDao.create((AccessToken)accessTokenEntity.toBuilder().id(this.hasher.hash(accessTokenEntity.getId())).build());
            return accessTokenEntity;
        }
        throw new UserKeyNotFoundException(this.i18nResolver.getText("oauth2.service.user.key.not.found"));
    }

    @Nonnull
    public RefreshToken createRefreshToken(String clientId, String userKey, long authorizationDate, String accessTokenId, Scope scope, String authorizationCode, int refreshCount) {
        if (this.userManager.getUserProfile(new UserKey(userKey)) != null) {
            RefreshTokenEntity refreshTokenEntity = RefreshTokenEntity.builder().id(this.clientCredentialsGenerator.generate(ClientCredentialsGenerator.Length.THIRTY_TWO)).clientId(clientId).userKey(userKey).accessTokenId(accessTokenId).scope(scope).authorizationCode(authorizationCode).refreshCount(Integer.valueOf(refreshCount)).authorizationDate(Long.valueOf(authorizationDate)).createdAt(Long.valueOf(this.clock.millis())).build();
            logger.debug("Creating refresh token for client id [{}].", (Object)clientId);
            this.refreshTokenDao.create((RefreshToken)refreshTokenEntity.toBuilder().id(this.hasher.hash(refreshTokenEntity.getId())).accessTokenId(this.hasher.hash(refreshTokenEntity.getAccessTokenId())).build());
            this.oAuth2ProviderEventPublisher.publishTokenCreatedEvent(clientId, (RefreshToken)refreshTokenEntity);
            return refreshTokenEntity;
        }
        throw new UserKeyNotFoundException(this.i18nResolver.getText("oauth2.service.user.key.not.found"));
    }

    public void updateAccessTokenLastAccessed(String tokenId) {
        this.accessTokenDao.updateTokenLastAccessed(this.hasher.hash(tokenId));
    }

    @Nonnull
    public Optional<AccessToken> findByAccessTokenId(String id) {
        return this.accessTokenDao.findByToken(this.hasher.hash(id));
    }

    @Nonnull
    public Optional<RefreshToken> findByRefreshTokenId(String id) {
        return this.refreshTokenDao.findByTokenId(this.hasher.hash(id));
    }

    @Nonnull
    public Optional<AccessToken> removeAccessTokenById(String tokenId) {
        return this.accessTokenDao.removeTokenById(this.hasher.hash(tokenId));
    }

    @Nonnull
    public List<RefreshToken> removeTokensByAuthorizationCode(String authorizationCode) {
        List removedRefreshTokens = this.refreshTokenDao.removeAllByAuthorizationCode(authorizationCode);
        removedRefreshTokens.forEach(token -> {
            this.accessTokenDao.removeTokenById(token.getAccessTokenId());
            this.oAuth2ProviderEventPublisher.publishTokenRevokedEvent(token.getClientId(), token.getUserKey());
        });
        return removedRefreshTokens;
    }

    @Nonnull
    public void removeByClientId(String clientId) {
        this.accessTokenDao.removeAllByClientId(clientId);
        this.refreshTokenDao.removeByClientId(clientId);
    }

    @Nonnull
    public List<AccessToken> removeByUserKey(String userKey) {
        return this.accessTokenDao.removeAllByUserKey(userKey);
    }

    public void removeExpiredAccessTokens(@Nonnull Duration expirationPeriod) {
        this.accessTokenDao.removeExpiredTokensAfter(expirationPeriod);
    }

    public void removeExpiredRefreshTokens(@Nonnull Duration expirationPeriod) {
        this.refreshTokenDao.removeExpiredTokensAfter(expirationPeriod);
    }

    @Nonnull
    public List<String> findUserKeysByClientId(String clientId) {
        return this.accessTokenDao.findUserKeysByClientId(clientId);
    }

    @Nonnull
    public List<RefreshToken> findRefreshTokensForClientId(String clientId) {
        return this.refreshTokenDao.findByClientId(clientId);
    }

    public List<AccessToken> findAccessTokensByUserKey(@Nonnull UserKey userKey) {
        return this.accessTokenDao.findByUserKey(userKey.getStringValue());
    }

    public List<RefreshToken> findRefreshTokensByUserKey(@Nonnull UserKey userKey) {
        return this.refreshTokenDao.findByUserKey(userKey.getStringValue());
    }

    public Optional<AccessToken> removeAccessTokenAssociatedWith(String refreshTokenId) {
        Optional<AccessToken> accessToken = this.refreshTokenDao.findByTokenId(this.hasher.hash(refreshTokenId)).flatMap(refreshToken -> this.accessTokenDao.findByToken(refreshToken.getAccessTokenId()));
        this.refreshTokenDao.findByTokenId(this.hasher.hash(refreshTokenId)).ifPresent(actualRefreshToken -> this.accessTokenDao.removeTokenById(actualRefreshToken.getAccessTokenId()));
        return accessToken;
    }

    public Optional<RefreshToken> removeRefreshTokenAssociatedWith(String accessTokenId) {
        return this.refreshTokenDao.removeByAccessTokenId(this.hasher.hash(accessTokenId)).map(token -> {
            this.oAuth2ProviderEventPublisher.publishTokenRevokedEvent(token.getClientId(), token.getUserKey());
            return token;
        });
    }

    public boolean isAccessTokenValid(@Nonnull String clientId, @Nonnull String accessTokenId) {
        return this.findByAccessTokenId(accessTokenId).map(token -> token.getClientId().equals(clientId)).orElse(false);
    }

    public boolean isRefreshTokenValid(@Nonnull String clientId, @Nonnull String refreshTokenId) {
        return this.findByRefreshTokenId(refreshTokenId).map(token -> token.getClientId().equals(clientId)).orElse(false);
    }

    public Optional<RefreshToken> removeRefreshToken(String refreshTokenId) {
        return this.refreshTokenDao.removeByRefreshTokenId(this.hasher.hash(refreshTokenId)).map(token -> {
            this.oAuth2ProviderEventPublisher.publishTokenRevokedEvent(token.getClientId(), token.getUserKey());
            return token;
        });
    }

    public boolean isCodeRedeemed(String authorizationCode) {
        return !this.refreshTokenDao.findByAuthorizationCode(authorizationCode).isEmpty();
    }

    public boolean removeTokensById(@Nonnull String tokenId) {
        Predicate<String> isTokenForCurrentUserOrIsCurrentUserSystemAdmin = this.isTokenForCurrentUserOrIsCurrentUserSystemAdmin();
        boolean removedTokens = this.accessTokenDao.findByToken(tokenId).filter(accessToken -> isTokenForCurrentUserOrIsCurrentUserSystemAdmin.test(accessToken.getUserKey())).map(accessToken -> {
            this.accessTokenDao.removeTokenById(tokenId);
            this.refreshTokenDao.removeByAccessTokenId(tokenId);
            this.oAuth2ProviderEventPublisher.publishTokenRevokedEvent(accessToken.getClientId(), accessToken.getUserKey());
            return true;
        }).orElse(false);
        if (!removedTokens) {
            removedTokens = this.refreshTokenDao.findByTokenId(tokenId).filter(refreshToken -> isTokenForCurrentUserOrIsCurrentUserSystemAdmin.test(refreshToken.getUserKey())).map(refreshToken -> {
                this.refreshTokenDao.removeByRefreshTokenId(tokenId);
                this.accessTokenDao.removeTokenById(refreshToken.getAccessTokenId());
                this.oAuth2ProviderEventPublisher.publishTokenRevokedEvent(refreshToken.getClientId(), refreshToken.getUserKey());
                return true;
            }).orElse(false);
        }
        return removedTokens;
    }

    private Predicate<String> isTokenForCurrentUserOrIsCurrentUserSystemAdmin() {
        UserKey currentLoggedInUserKey = this.userManager.getRemoteUserKey();
        boolean isSystemAdmin = currentLoggedInUserKey != null && this.userManager.isSystemAdmin(currentLoggedInUserKey);
        return userKey -> currentLoggedInUserKey != null && (isSystemAdmin || currentLoggedInUserKey.getStringValue().equals(userKey));
    }
}

