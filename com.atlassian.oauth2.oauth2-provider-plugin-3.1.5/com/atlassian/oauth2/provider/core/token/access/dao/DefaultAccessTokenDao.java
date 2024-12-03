/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.oauth2.provider.api.token.access.AccessToken
 *  com.atlassian.oauth2.provider.api.token.access.dao.AccessTokenDao
 *  com.atlassian.oauth2.provider.api.token.access.dao.AccessTokenEntity
 *  com.atlassian.oauth2.scopes.api.ScopeResolver
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  javax.annotation.Nonnull
 *  net.java.ao.Query
 *  net.java.ao.RawEntity
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.oauth2.provider.core.token.access.dao;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.oauth2.provider.api.token.access.AccessToken;
import com.atlassian.oauth2.provider.api.token.access.dao.AccessTokenDao;
import com.atlassian.oauth2.provider.api.token.access.dao.AccessTokenEntity;
import com.atlassian.oauth2.provider.core.dao.OAuth2ProviderDao;
import com.atlassian.oauth2.provider.core.token.access.dao.entity.AOAccessToken;
import com.atlassian.oauth2.scopes.api.ScopeResolver;
import com.google.common.collect.ImmutableMap;
import java.time.Clock;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import net.java.ao.Query;
import net.java.ao.RawEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultAccessTokenDao
extends OAuth2ProviderDao
implements AccessTokenDao {
    private static final Logger logger = LoggerFactory.getLogger(DefaultAccessTokenDao.class);
    private static final String WHERE_TOKEN_ID_EQUAL_TO = "ID = ?";
    private static final String WHERE_CLIENT_ID_EQUAL_TO = "CLIENT_ID = ?";
    private static final String WHERE_USER_KEY_EQUAL_TO = "USER_KEY = ?";
    private final Clock clock;
    private final ScopeResolver scopeResolver;

    public DefaultAccessTokenDao(ActiveObjects activeObjects, Clock clock, ScopeResolver scopeResolver) {
        super(activeObjects);
        this.clock = clock;
        this.scopeResolver = scopeResolver;
    }

    @Nonnull
    public AccessToken create(AccessToken accessTokenEntity) {
        return (AccessToken)this.activeObjects.executeInTransaction(() -> {
            logger.debug("Creating an access token with id [{}].", (Object)accessTokenEntity.getId());
            ((AOAccessToken)this.activeObjects.create(AOAccessToken.class, this.toEntityAttributes(accessTokenEntity))).save();
            return accessTokenEntity;
        });
    }

    private Map<String, Object> toEntityAttributes(AccessToken accessTokenEntity) {
        ImmutableMap.Builder attributes = ImmutableMap.builder().put((Object)"ID", (Object)accessTokenEntity.getId()).put((Object)"CLIENT_ID", (Object)accessTokenEntity.getClientId()).put((Object)"USER_KEY", (Object)accessTokenEntity.getUserKey()).put((Object)"AUTHORIZATION_CODE", (Object)accessTokenEntity.getAuthorizationCode()).put((Object)"SCOPE", (Object)accessTokenEntity.getScope().toString()).put((Object)"AUTHORIZATION_DATE", (Object)accessTokenEntity.getAuthorizationDate()).put((Object)"CREATED_AT", (Object)accessTokenEntity.getCreatedAt());
        if (accessTokenEntity.getLastAccessed() != null) {
            attributes.put((Object)"LAST_ACCESSED", (Object)accessTokenEntity.getLastAccessed());
        }
        return attributes.build();
    }

    public void updateTokenLastAccessed(String tokenId) {
        this.activeObjects.executeInTransaction(() -> {
            logger.debug("Updating access token last accessed time for token id [{}].", (Object)tokenId);
            this.findEntity(tokenId).ifPresent(aoEntity -> {
                aoEntity.setLastAccessed(this.clock.millis());
                aoEntity.save();
            });
            return null;
        });
    }

    public void removeExpiredTokensAfter(Duration expiration) {
        this.activeObjects.executeInTransaction(() -> {
            logger.debug("Removing expired access tokens after [{}].", (Object)expiration);
            this.activeObjects.deleteWithSQL(AOAccessToken.class, this.whereColumnIsGreaterThanValue("CREATED_AT"), new Object[]{this.clock.millis(), expiration.toMillis()});
            return null;
        });
    }

    @Nonnull
    public List<AccessToken> findByClientId(String clientId) {
        return (List)this.activeObjects.executeInTransaction(() -> this.toEntities(this.findAOEntitiesByClientId(clientId)));
    }

    @Nonnull
    public Optional<AccessToken> findByToken(String tokenId) {
        return (Optional)this.activeObjects.executeInTransaction(() -> this.findEntity(tokenId).map(this::toEntity));
    }

    private Optional<AOAccessToken> findEntity(String tokenId) {
        AOAccessToken[] entities = (AOAccessToken[])this.activeObjects.find(AOAccessToken.class, Query.select().where(WHERE_TOKEN_ID_EQUAL_TO, new Object[]{tokenId}));
        if (entities.length > 0) {
            logger.debug("Found access token associated with token id [{}].", (Object)tokenId);
            return Optional.of(entities[0]);
        }
        logger.debug("Failed to find access token associated with token id [{}].", (Object)tokenId);
        return Optional.empty();
    }

    @Nonnull
    public List<String> findUserKeysByClientId(String clientId) {
        return (List)this.activeObjects.executeInTransaction(() -> Arrays.stream(this.activeObjects.find(AOAccessToken.class, WHERE_CLIENT_ID_EQUAL_TO, new Object[]{clientId})).map(AOAccessToken::getUserKey).collect(Collectors.toList()));
    }

    @Nonnull
    public List<AccessToken> findByUserKey(String userKey) {
        return (List)this.activeObjects.executeInTransaction(() -> this.toEntities((AOAccessToken[])this.activeObjects.find(AOAccessToken.class, WHERE_USER_KEY_EQUAL_TO, new Object[]{userKey})));
    }

    @Nonnull
    public Optional<AccessToken> removeTokenById(String tokenId) {
        return (Optional)this.activeObjects.executeInTransaction(() -> {
            logger.debug("Removing access token with id [{}].", (Object)tokenId);
            AOAccessToken[] tokenToRemove = (AOAccessToken[])this.activeObjects.find(AOAccessToken.class, WHERE_TOKEN_ID_EQUAL_TO, new Object[]{tokenId});
            if (tokenToRemove.length == 1) {
                this.activeObjects.delete(new RawEntity[]{tokenToRemove[0]});
                logger.debug("Successfully removed access token with id [{}].", (Object)tokenId);
                return Optional.of(this.toEntity(tokenToRemove[0]));
            }
            logger.debug("Unable to find an access token with id [{}]. No tokens have been removed.", (Object)tokenId);
            return Optional.empty();
        });
    }

    @Nonnull
    public List<AccessToken> removeAllByClientId(String clientId) {
        return (List)this.activeObjects.executeInTransaction(() -> {
            logger.debug("Removing all tokens associated with client id [{}].", (Object)clientId);
            AOAccessToken[] tokensToRemove = this.findAOEntitiesByClientId(clientId);
            if (tokensToRemove.length > 0) {
                this.activeObjects.deleteWithSQL(AOAccessToken.class, WHERE_CLIENT_ID_EQUAL_TO, new Object[]{clientId});
                logger.debug("Successfully removed access tokens associated with client id [{}].", (Object)clientId);
            }
            return this.toEntities(tokensToRemove);
        });
    }

    @Nonnull
    public List<AccessToken> removeAllByUserKey(String userKey) {
        return (List)this.activeObjects.executeInTransaction(() -> {
            logger.debug("Removing all tokens associated with user key [{}].", (Object)userKey);
            AOAccessToken[] tokensToRemove = (AOAccessToken[])this.activeObjects.find(AOAccessToken.class, WHERE_USER_KEY_EQUAL_TO, new Object[]{userKey});
            if (tokensToRemove.length > 0) {
                this.activeObjects.deleteWithSQL(AOAccessToken.class, WHERE_USER_KEY_EQUAL_TO, new Object[]{userKey});
                logger.debug("Succesfully removed tokens associated with user key [{}].", (Object)userKey);
            }
            return this.toEntities(tokensToRemove);
        });
    }

    private AOAccessToken[] findAOEntitiesByClientId(String clientId) {
        return (AOAccessToken[])this.activeObjects.find(AOAccessToken.class, Query.select().where(WHERE_CLIENT_ID_EQUAL_TO, new Object[]{clientId}));
    }

    private List<AccessToken> toEntities(AOAccessToken[] entities) {
        if (entities.length > 0) {
            return Arrays.stream(entities).map(this::toEntity).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private AccessToken toEntity(AOAccessToken aoAccessToken) {
        return AccessTokenEntity.builder().id(aoAccessToken.getId()).clientId(aoAccessToken.getClientId()).userKey(aoAccessToken.getUserKey()).authorizationCode(aoAccessToken.getAuthorizationCode()).scope(this.scopeResolver.getScope(aoAccessToken.getScope())).authorizationDate(aoAccessToken.getAuthorizationDate()).createdAt(aoAccessToken.getCreatedAt()).lastAccessed(aoAccessToken.getLastAccessed()).build();
    }
}

