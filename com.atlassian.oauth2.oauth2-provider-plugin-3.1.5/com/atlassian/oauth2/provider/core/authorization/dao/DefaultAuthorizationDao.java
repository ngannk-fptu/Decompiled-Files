/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.oauth2.provider.api.authorization.Authorization
 *  com.atlassian.oauth2.provider.api.authorization.dao.AuthorizationDao
 *  com.atlassian.oauth2.provider.api.authorization.dao.AuthorizationEntity
 *  com.atlassian.oauth2.provider.api.pkce.CodeChallengeMethod
 *  com.atlassian.oauth2.scopes.api.ScopeResolver
 *  net.java.ao.Query
 *  net.java.ao.RawEntity
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.oauth2.provider.core.authorization.dao;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.oauth2.provider.api.authorization.Authorization;
import com.atlassian.oauth2.provider.api.authorization.dao.AuthorizationDao;
import com.atlassian.oauth2.provider.api.authorization.dao.AuthorizationEntity;
import com.atlassian.oauth2.provider.api.pkce.CodeChallengeMethod;
import com.atlassian.oauth2.provider.core.authorization.dao.entity.AOAuthorization;
import com.atlassian.oauth2.provider.core.dao.OAuth2ProviderDao;
import com.atlassian.oauth2.scopes.api.ScopeResolver;
import java.time.Clock;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;
import net.java.ao.Query;
import net.java.ao.RawEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultAuthorizationDao
extends OAuth2ProviderDao
implements AuthorizationDao {
    private static final Logger logger = LoggerFactory.getLogger(DefaultAuthorizationDao.class);
    private static final String WHERE_AUTHORIZATION_CODE_EQUAL_TO = "AUTHORIZATION_CODE = ?";
    private final Clock clock;
    private final ScopeResolver scopeResolver;

    public DefaultAuthorizationDao(ActiveObjects activeObjects, Clock clock, ScopeResolver scopeResolver) {
        super(activeObjects);
        this.clock = clock;
        this.scopeResolver = scopeResolver;
    }

    public Authorization save(Authorization authorization) {
        logger.debug("Saving authorization for client id {}", (Object)authorization.getClientId());
        HashMap<String, Object> properties = new HashMap<String, Object>();
        properties.put("AUTHORIZATION_CODE", authorization.getAuthorizationCode());
        properties.put("CLIENT_ID", authorization.getClientId());
        properties.put("REDIRECT_URI", authorization.getRedirectUri());
        properties.put("USER_KEY", authorization.getUserKey());
        properties.put("CREATED_AT", authorization.getCreatedAt());
        properties.put("SCOPE", authorization.getScope().toString());
        if (authorization.getCodeChallengeMethod() != null) {
            properties.put("CODE_CHALLENGE_METHOD", authorization.getCodeChallengeMethod().toString());
        }
        if (authorization.getCodeChallenge() != null) {
            properties.put("CODE_CHALLENGE", authorization.getCodeChallenge());
        }
        return this.toEntity((AOAuthorization)this.activeObjects.create(AOAuthorization.class, properties));
    }

    public Optional<Authorization> removeByCode(String authorizationCode) {
        logger.debug("Removing authorization associated with authorization code [{}].", (Object)authorizationCode);
        return Arrays.stream(this.activeObjects.find(AOAuthorization.class, Query.select().where(WHERE_AUTHORIZATION_CODE_EQUAL_TO, new Object[]{authorizationCode}))).map(authorization -> {
            this.activeObjects.delete(new RawEntity[]{authorization});
            return this.toEntity((AOAuthorization)authorization);
        }).findFirst();
    }

    public Optional<Authorization> findByCode(String authorizationCode) {
        logger.debug("Finding authorization associated with authorization code [{}].", (Object)authorizationCode);
        AOAuthorization[] entities = (AOAuthorization[])this.activeObjects.find(AOAuthorization.class, Query.select().where(WHERE_AUTHORIZATION_CODE_EQUAL_TO, new Object[]{authorizationCode}));
        if (entities.length > 0) {
            logger.debug("Found authorization associated with authorizationCode [{}].", (Object)authorizationCode);
            return Optional.of(this.toEntity(entities[0]));
        }
        logger.debug("Failed to find authorization associated with authorizationCode [{}].", (Object)authorizationCode);
        return Optional.empty();
    }

    public void removeExpiredAuthorizationsAfter(Duration expiration) {
        logger.debug("Removing expired tokens after [{}]", (Object)expiration);
        this.activeObjects.executeInTransaction(() -> this.activeObjects.deleteWithSQL(AOAuthorization.class, this.whereColumnIsGreaterThanValue("CREATED_AT"), new Object[]{this.clock.millis(), expiration.toMillis()}));
    }

    private Authorization toEntity(AOAuthorization aoAuthorization) {
        return AuthorizationEntity.builder().authorizationCode(aoAuthorization.getAuthorizationCode()).clientId(aoAuthorization.getClientId()).userKey(aoAuthorization.getUserKey()).scope(this.scopeResolver.getScope(aoAuthorization.getScope())).redirectUri(aoAuthorization.getRedirectUri()).createdAt(aoAuthorization.getCreatedAt()).codeChallengeMethod(CodeChallengeMethod.fromString((String)aoAuthorization.getCodeChallengeMethod())).codeChallenge(aoAuthorization.getCodeChallenge()).build();
    }
}

