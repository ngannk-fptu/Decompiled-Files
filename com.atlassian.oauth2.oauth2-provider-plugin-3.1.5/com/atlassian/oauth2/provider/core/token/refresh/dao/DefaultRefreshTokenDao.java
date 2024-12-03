/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.oauth2.provider.api.token.refresh.RefreshToken
 *  com.atlassian.oauth2.provider.api.token.refresh.dao.RefreshTokenDao
 *  com.atlassian.oauth2.provider.api.token.refresh.dao.RefreshTokenEntity
 *  com.atlassian.oauth2.scopes.api.ScopeResolver
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.oauth2.provider.core.token.refresh.dao;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.oauth2.provider.api.token.refresh.RefreshToken;
import com.atlassian.oauth2.provider.api.token.refresh.dao.RefreshTokenDao;
import com.atlassian.oauth2.provider.api.token.refresh.dao.RefreshTokenEntity;
import com.atlassian.oauth2.provider.core.dao.OAuth2ProviderDao;
import com.atlassian.oauth2.provider.core.token.refresh.dao.entity.AORefreshToken;
import com.atlassian.oauth2.scopes.api.ScopeResolver;
import com.google.common.collect.ImmutableMap;
import java.time.Clock;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultRefreshTokenDao
extends OAuth2ProviderDao
implements RefreshTokenDao {
    private static final Logger logger = LoggerFactory.getLogger(DefaultRefreshTokenDao.class);
    private static final String WHERE_TOKEN_ID_EQUAL_TO = "ID = ?";
    private static final String WHERE_CLIENT_ID_EQUAL_TO = "CLIENT_ID = ?";
    private static final String WHERE_ACCESS_TOKEN_ID_EQUAL_TO = "ACCESS_TOKEN_ID = ?";
    private static final String WHERE_USER_KEY_EQUAL_TO = "USER_KEY = ?";
    private static final String WHERE_CODE_EQUAL_TO = "AUTHORIZATION_CODE = ?";
    private final Clock clock;
    private final ScopeResolver scopeResolver;

    public DefaultRefreshTokenDao(ActiveObjects activeObjects, Clock clock, ScopeResolver scopeResolver) {
        super(activeObjects);
        this.clock = clock;
        this.scopeResolver = scopeResolver;
    }

    @Nonnull
    public RefreshToken create(RefreshToken refreshTokenEntity) {
        logger.debug("Creating an refresh token with id [{}].", (Object)refreshTokenEntity.getId());
        return (RefreshToken)this.activeObjects.executeInTransaction(() -> {
            ((AORefreshToken)this.activeObjects.create(AORefreshToken.class, this.toEntityAttributes(refreshTokenEntity))).save();
            return refreshTokenEntity;
        });
    }

    private Map<String, Object> toEntityAttributes(RefreshToken refreshTokenEntity) {
        ImmutableMap.Builder attributes = ImmutableMap.builder().put((Object)"ID", (Object)refreshTokenEntity.getId()).put((Object)"CLIENT_ID", (Object)refreshTokenEntity.getClientId()).put((Object)"USER_KEY", (Object)refreshTokenEntity.getUserKey()).put((Object)"ACCESS_TOKEN_ID", (Object)refreshTokenEntity.getAccessTokenId()).put((Object)"SCOPE", (Object)refreshTokenEntity.getScope().toString()).put((Object)"AUTHORIZATION_CODE", (Object)refreshTokenEntity.getAuthorizationCode()).put((Object)"AUTHORIZATION_DATE", (Object)refreshTokenEntity.getAuthorizationDate()).put((Object)"CREATED_AT", (Object)refreshTokenEntity.getCreatedAt());
        if (refreshTokenEntity.getRefreshCount() != null) {
            attributes.put((Object)"REFRESH_COUNT", (Object)refreshTokenEntity.getRefreshCount());
        }
        return attributes.build();
    }

    @Nonnull
    public Optional<RefreshToken> findByTokenId(String tokenId) {
        return (Optional)this.activeObjects.executeInTransaction(() -> this.findEntity(tokenId).map(this::toEntity));
    }

    private Optional<AORefreshToken> findEntity(String tokenId) {
        AORefreshToken[] entities = (AORefreshToken[])this.activeObjects.find(AORefreshToken.class, WHERE_TOKEN_ID_EQUAL_TO, new Object[]{tokenId});
        if (entities.length > 0) {
            logger.debug("Found refresh token associated with token id [{}].", (Object)tokenId);
            return Optional.of(entities[0]);
        }
        logger.debug("Failed to find a refresh token associated with token id [{}].", (Object)tokenId);
        return Optional.empty();
    }

    @Nonnull
    public List<RefreshToken> findByClientId(String clientId) {
        return (List)this.activeObjects.executeInTransaction(() -> this.toEntities(this.findEntityByClientId(clientId)));
    }

    @Nonnull
    public List<RefreshToken> findByUserKey(String userKey) {
        return (List)this.activeObjects.executeInTransaction(() -> this.toEntities((AORefreshToken[])this.activeObjects.find(AORefreshToken.class, WHERE_USER_KEY_EQUAL_TO, new Object[]{userKey})));
    }

    @Nonnull
    public List<RefreshToken> findByAuthorizationCode(String authorizationCode) {
        return (List)this.activeObjects.executeInTransaction(() -> this.toEntities(this.findEntitiesByAuthorizationCode(authorizationCode)));
    }

    public void removeExpiredTokensAfter(Duration expiration) {
        logger.debug("Removing expired refresh tokens after [{}].", (Object)expiration);
        this.activeObjects.executeInTransaction(() -> this.activeObjects.deleteWithSQL(AORefreshToken.class, this.whereColumnIsGreaterThanValue("CREATED_AT"), new Object[]{this.clock.millis(), expiration.toMillis()}));
    }

    @Nonnull
    public Optional<RefreshToken> removeByRefreshTokenId(String refreshTokenId) {
        return (Optional)this.activeObjects.executeInTransaction(() -> {
            logger.debug("Removing refresh token with id [{}].", (Object)refreshTokenId);
            AORefreshToken[] tokenToRemove = (AORefreshToken[])this.activeObjects.find(AORefreshToken.class, WHERE_TOKEN_ID_EQUAL_TO, new Object[]{refreshTokenId});
            if (tokenToRemove.length == 1) {
                this.activeObjects.deleteWithSQL(AORefreshToken.class, WHERE_TOKEN_ID_EQUAL_TO, new Object[]{refreshTokenId});
                logger.debug("Successfully removed refresh token with id [{}].", (Object)refreshTokenId);
                return Optional.of(this.toEntity(tokenToRemove[0]));
            }
            logger.debug("Unable to find a refresh token with id [{}]. No tokens have been removed.", (Object)refreshTokenId);
            return Optional.empty();
        });
    }

    public Optional<RefreshToken> removeByAccessTokenId(String accessTokenId) {
        return (Optional)this.activeObjects.executeInTransaction(() -> {
            logger.debug("Removing refresh token associated with access token id [{}].", (Object)accessTokenId);
            AORefreshToken[] aoRefreshTokens = (AORefreshToken[])this.activeObjects.find(AORefreshToken.class, WHERE_ACCESS_TOKEN_ID_EQUAL_TO, new Object[]{accessTokenId});
            if (aoRefreshTokens.length == 1) {
                this.activeObjects.deleteWithSQL(AORefreshToken.class, WHERE_ACCESS_TOKEN_ID_EQUAL_TO, new Object[]{accessTokenId});
                logger.debug("Successfully removed refresh token associated with access token id [{}].", (Object)accessTokenId);
                return Optional.of(this.toEntity(aoRefreshTokens[0]));
            }
            logger.debug("Unable to find a refresh token associated with access token id [{}]. No tokens have been removed.", (Object)accessTokenId);
            return Optional.empty();
        });
    }

    @Nonnull
    public List<RefreshToken> removeByClientId(String clientId) {
        return (List)this.activeObjects.executeInTransaction(() -> {
            logger.debug("Removing refresh tokens associated with client id [{}].", (Object)clientId);
            AORefreshToken[] removedTokens = this.findEntityByClientId(clientId);
            if (removedTokens.length > 0) {
                this.activeObjects.deleteWithSQL(AORefreshToken.class, WHERE_CLIENT_ID_EQUAL_TO, new Object[]{clientId});
                logger.debug("Successfully removed refresh tokens associated with client id [{}].", (Object)clientId);
            }
            return this.toEntities(removedTokens);
        });
    }

    public List<RefreshToken> removeAllByAuthorizationCode(String authorizationCode) {
        return (List)this.activeObjects.executeInTransaction(() -> {
            logger.debug("Removing refresh tokens associated with authorization code [{}].", (Object)authorizationCode);
            AORefreshToken[] tokensToRemove = this.findEntitiesByAuthorizationCode(authorizationCode);
            if (tokensToRemove.length > 0) {
                this.activeObjects.deleteWithSQL(AORefreshToken.class, WHERE_CODE_EQUAL_TO, new Object[]{authorizationCode});
                logger.debug("Successfully removed refresh tokens associated with authorization code [{}].", (Object)authorizationCode);
            }
            return this.toEntities(tokensToRemove);
        });
    }

    private AORefreshToken[] findEntityByClientId(String clientId) {
        return (AORefreshToken[])this.activeObjects.find(AORefreshToken.class, WHERE_CLIENT_ID_EQUAL_TO, new Object[]{clientId});
    }

    private List<RefreshToken> toEntities(AORefreshToken[] refreshTokens) {
        return Arrays.stream(refreshTokens).map(this::toEntity).collect(Collectors.toList());
    }

    private AORefreshToken[] findEntitiesByAuthorizationCode(String authorizationCode) {
        return (AORefreshToken[])this.activeObjects.find(AORefreshToken.class, WHERE_CODE_EQUAL_TO, new Object[]{authorizationCode});
    }

    private RefreshToken toEntity(AORefreshToken aoRefreshToken) {
        return RefreshTokenEntity.builder().id(aoRefreshToken.getId()).clientId(aoRefreshToken.getClientId()).userKey(aoRefreshToken.getUserKey()).accessTokenId(aoRefreshToken.getAccessTokenId()).scope(this.scopeResolver.getScope(aoRefreshToken.getScope())).authorizationCode(aoRefreshToken.getAuthorizationCode()).authorizationDate(aoRefreshToken.getAuthorizationDate()).createdAt(aoRefreshToken.getCreatedAt()).refreshCount(aoRefreshToken.getRefreshCount()).build();
    }
}

