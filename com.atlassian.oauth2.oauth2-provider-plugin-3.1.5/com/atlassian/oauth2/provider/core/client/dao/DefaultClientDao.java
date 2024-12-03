/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.oauth2.provider.api.client.Client
 *  com.atlassian.oauth2.provider.api.client.dao.ClientDao
 *  com.atlassian.oauth2.provider.api.client.dao.ClientEntity
 *  com.atlassian.oauth2.provider.api.client.dao.RedirectUriDao
 *  com.atlassian.oauth2.scopes.api.ScopeResolver
 *  com.google.common.collect.ImmutableMap
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  net.java.ao.Query
 *  net.java.ao.RawEntity
 *  org.apache.commons.collections.CollectionUtils
 *  org.apache.commons.lang3.ArrayUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.jetbrains.annotations.NotNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.oauth2.provider.core.client.dao;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.oauth2.provider.api.client.Client;
import com.atlassian.oauth2.provider.api.client.dao.ClientDao;
import com.atlassian.oauth2.provider.api.client.dao.ClientEntity;
import com.atlassian.oauth2.provider.api.client.dao.RedirectUriDao;
import com.atlassian.oauth2.provider.core.client.dao.entity.AOClient;
import com.atlassian.oauth2.scopes.api.ScopeResolver;
import com.google.common.collect.ImmutableMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.java.ao.Query;
import net.java.ao.RawEntity;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultClientDao
implements ClientDao {
    private static final Logger logger = LoggerFactory.getLogger(DefaultClientDao.class);
    private static final String WHERE_CLIENT_ID_EQUAL_TO = "CLIENT_ID = ?";
    private static final String WHERE_CLIENT_ID_NOT_EQUAL_TO = "CLIENT_ID <> ?";
    private static final String WHERE_NAME_EQUAL_TO = "NAME = ?";
    private static final String ORDER_BY_CLIENT_ID = "CLIENT_ID ASC";
    private final ActiveObjects activeObjects;
    private final ScopeResolver scopeResolver;
    private final RedirectUriDao redirectUriDao;

    public DefaultClientDao(ActiveObjects activeObjects, ScopeResolver scopeResolver, RedirectUriDao redirectUriDao) {
        this.activeObjects = activeObjects;
        this.scopeResolver = scopeResolver;
        this.redirectUriDao = redirectUriDao;
    }

    @Nonnull
    public Client create(@Nonnull Client client) {
        return (Client)this.activeObjects.executeInTransaction(() -> {
            AOClient aoClient = this.createClient(client);
            this.redirectUriDao.create(client.getClientId(), client.getRedirects());
            return this.toEntity(aoClient, client.getRedirects());
        });
    }

    private AOClient createClient(Client client) {
        logger.debug("Creating client {}", (Object)client.getName());
        return (AOClient)this.activeObjects.create(AOClient.class, (Map)ImmutableMap.builder().put((Object)"ID", (Object)client.getId()).put((Object)"CLIENT_ID", (Object)client.getClientId()).put((Object)"CLIENT_SECRET", (Object)client.getClientSecret()).put((Object)"NAME", (Object)client.getName()).put((Object)"USER_KEY", (Object)client.getUserKey()).put((Object)"SCOPE", (Object)client.getScope().toString()).build());
    }

    public Optional<Client> updateClient(@Nonnull String id, String name, String scope, @Nonnull List<String> newRedirectUris) {
        return (Optional)this.activeObjects.executeInTransaction(() -> this.findById(id).map(aoClient -> {
            boolean updateUris;
            boolean updateScope;
            List oldRedirectUris = this.redirectUriDao.findByClientId(aoClient.getClientId());
            boolean updateName = StringUtils.isNotEmpty((CharSequence)name);
            if (updateName) {
                logger.debug("Updating client name [{}] to [{}]", (Object)aoClient.getName(), (Object)name);
                aoClient.setName(name);
            }
            if (updateScope = StringUtils.isNotEmpty((CharSequence)scope)) {
                logger.debug("Updating scope from [{}] to [{}] for client [{}]", new Object[]{aoClient.getScope(), scope, aoClient.getName()});
                aoClient.setScope(scope);
            }
            if (updateUris = CollectionUtils.isNotEmpty((Collection)newRedirectUris)) {
                this.redirectUriDao.updateRedirectUris(aoClient.getClientId(), newRedirectUris);
            }
            if (updateName || updateScope) {
                logger.debug("Saving updates for client [{}]", (Object)aoClient.getName());
                aoClient.save();
            }
            return this.toEntity((AOClient)aoClient, updateUris ? newRedirectUris : oldRedirectUris);
        }));
    }

    public Optional<Client> resetClientSecret(@Nonnull String clientId, @Nonnull String regeneratedClientSecret) {
        return (Optional)this.activeObjects.executeInTransaction(() -> this.findByClientId(clientId).map(aoClient -> {
            List redirects = this.redirectUriDao.findByClientId(clientId);
            logger.debug("Resetting client secret for client [{}]", (Object)aoClient.getName());
            aoClient.setClientSecret(regeneratedClientSecret);
            aoClient.save();
            return this.toEntity((AOClient)aoClient, redirects);
        }));
    }

    public Optional<Client> removeById(@Nonnull String id) {
        return (Optional)this.activeObjects.executeInTransaction(() -> this.findById(id).map(aoClient -> {
            List redirects = this.redirectUriDao.findByClientId(aoClient.getClientId());
            ClientEntity entityDeleted = this.toEntity((AOClient)aoClient, redirects);
            logger.debug("Removing client [{}]", (Object)aoClient.getName());
            this.activeObjects.delete(new RawEntity[]{aoClient});
            this.redirectUriDao.removeByClientId(aoClient.getClientId());
            return entityDeleted;
        }));
    }

    @Nonnull
    public Optional<Client> getById(@Nonnull String id) {
        return (Optional)this.activeObjects.executeInTransaction(() -> this.findById(id).map(aoClient -> {
            logger.debug("Found client associated with id [{}]", (Object)id);
            List redirects = this.redirectUriDao.findByClientId(aoClient.getClientId());
            return this.toEntity((AOClient)aoClient, redirects);
        }));
    }

    @Nonnull
    public Optional<Client> getByClientId(@Nonnull String clientId) {
        return (Optional)this.activeObjects.executeInTransaction(() -> this.findByClientId(clientId).map(aoClient -> {
            List redirects = this.redirectUriDao.findByClientId(aoClient.getClientId());
            return this.toEntity((AOClient)aoClient, redirects);
        }));
    }

    @Nonnull
    public Optional<String> getClientIdById(@NotNull String id) {
        return (Optional)this.activeObjects.executeInTransaction(() -> Optional.ofNullable(this.findById(id).get().getClientId()));
    }

    public boolean isClientNameUnique(@Nullable String id, @Nonnull String name) {
        return (Boolean)this.activeObjects.executeInTransaction(() -> {
            Query query = Query.select((String)"CLIENT_ID");
            Object[] params = new Object[]{name.trim()};
            if (id == null) {
                query.setWhereClause(WHERE_NAME_EQUAL_TO);
                query.setWhereParams(params);
            } else {
                query.setWhereClause("NAME = ? AND CLIENT_ID <> ?");
                query.setWhereParams(ArrayUtils.add((Object[])params, (Object)id));
            }
            return this.activeObjects.count(AOClient.class, query) == 0;
        });
    }

    private Optional<AOClient> findById(@Nonnull String id) {
        return Optional.ofNullable(this.activeObjects.get(AOClient.class, (Object)id));
    }

    private Optional<AOClient> findByClientId(@Nonnull String clientId) {
        AOClient[] entities = (AOClient[])this.activeObjects.find(AOClient.class, Query.select().where(WHERE_CLIENT_ID_EQUAL_TO, new Object[]{clientId}));
        if (entities.length > 0) {
            logger.debug("Found client associated with client id [{}]", (Object)clientId);
            return Optional.of(entities[0]);
        }
        logger.debug("Failed to find a client associated with client id [{}]", (Object)clientId);
        return Optional.empty();
    }

    @Nonnull
    public List<Client> list() {
        return (List)this.activeObjects.executeInTransaction(() -> Arrays.stream(this.activeObjects.find(AOClient.class, Query.select().order(ORDER_BY_CLIENT_ID))).map(aoClient -> {
            List redirects = this.redirectUriDao.findByClientId(aoClient.getClientId());
            return this.toEntity((AOClient)aoClient, redirects);
        }).collect(Collectors.toList()));
    }

    private ClientEntity toEntity(AOClient aoClient, List<String> redirects) {
        return ClientEntity.builder().id(aoClient.getId()).clientId(aoClient.getClientId()).clientSecret(aoClient.getClientSecret()).name(aoClient.getName()).userKey(aoClient.getUserKey()).redirects(redirects).scope(this.scopeResolver.getScope(aoClient.getScope())).build();
    }
}

