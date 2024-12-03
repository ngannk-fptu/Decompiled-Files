/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.oauth2.client.api.ClientConfiguration
 *  com.atlassian.oauth2.client.api.storage.config.ClientConfigurationEntity
 *  com.atlassian.oauth2.client.api.storage.config.ProviderType
 *  com.atlassian.oauth2.client.api.storage.token.exception.ConfigurationNotFoundException
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  net.java.ao.Query
 *  net.java.ao.RawEntity
 *  org.apache.commons.lang3.ArrayUtils
 *  org.springframework.util.StringUtils
 */
package com.atlassian.oauth2.client.storage.config.dao;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.oauth2.client.api.ClientConfiguration;
import com.atlassian.oauth2.client.api.storage.config.ClientConfigurationEntity;
import com.atlassian.oauth2.client.api.storage.config.ProviderType;
import com.atlassian.oauth2.client.api.storage.token.exception.ConfigurationNotFoundException;
import com.atlassian.oauth2.client.storage.AbstractStore;
import com.atlassian.oauth2.client.storage.config.dao.ClientConfigStore;
import com.atlassian.oauth2.client.storage.config.dao.entity.AOClientConfig;
import com.atlassian.oauth2.client.util.ClientHttpsValidator;
import com.atlassian.oauth2.common.IdGenerator;
import com.google.common.collect.ImmutableMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.java.ao.Query;
import net.java.ao.RawEntity;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.util.StringUtils;

public class ClientConfigStoreImpl
extends AbstractStore
implements ClientConfigStore {
    private static final String DELIMITER = " ";
    private final IdGenerator idGenerator;
    private final ClientHttpsValidator clientHttpsValidator;

    public ClientConfigStoreImpl(ActiveObjects activeObjects, IdGenerator idGenerator, ClientHttpsValidator clientHttpsValidator) {
        super(activeObjects);
        this.idGenerator = idGenerator;
        this.clientHttpsValidator = clientHttpsValidator;
    }

    @Override
    public void delete(@Nonnull String id) throws ConfigurationNotFoundException {
        this.executeInTransaction(() -> {
            AOClientConfig client = this.findByIdOrFail(id);
            this.activeObjects.delete(new RawEntity[]{client});
            return null;
        }, ConfigurationNotFoundException.class);
    }

    @Override
    @Nonnull
    public ClientConfigurationEntity create(@Nonnull ClientConfigurationEntity clientConfigurationEntity) {
        return (ClientConfigurationEntity)this.activeObjects.executeInTransaction(() -> {
            this.assertValid(clientConfigurationEntity);
            String newId = this.idGenerator.generate();
            ImmutableMap.Builder configProperties = ImmutableMap.builder().put((Object)"ID", (Object)newId).put((Object)"NAME", (Object)clientConfigurationEntity.getName()).put((Object)"TYPE", (Object)clientConfigurationEntity.getProviderType().key).put((Object)"CLIENT_ID", (Object)clientConfigurationEntity.getClientId()).put((Object)"CLIENT_SECRET", (Object)clientConfigurationEntity.getClientSecret()).put((Object)"AUTHORIZATION_ENDPOINT", (Object)clientConfigurationEntity.getAuthorizationEndpoint()).put((Object)"TOKEN_ENDPOINT", (Object)clientConfigurationEntity.getTokenEndpoint()).put((Object)"SCOPES", (Object)this.listToDelimited(clientConfigurationEntity.getScopes()));
            Optional.ofNullable(clientConfigurationEntity.getDescription()).ifPresent(description -> configProperties.put((Object)"DESCRIPTION", description));
            this.activeObjects.create(AOClientConfig.class, (Map)configProperties.build());
            return ClientConfigurationEntity.builder((ClientConfigurationEntity)clientConfigurationEntity).id(newId).build();
        });
    }

    @Override
    @Nonnull
    public ClientConfigurationEntity update(ClientConfigurationEntity clientConfigurationEntity) throws ConfigurationNotFoundException {
        return this.executeInTransaction(() -> {
            this.assertValid(clientConfigurationEntity);
            AOClientConfig clientConfig = this.findByIdOrFail(clientConfigurationEntity.getId());
            this.updateClientConfiguration(clientConfig, clientConfigurationEntity);
            return clientConfigurationEntity;
        }, ConfigurationNotFoundException.class);
    }

    private void updateClientConfiguration(AOClientConfig clientConfig, ClientConfigurationEntity clientConfigurationEntity) {
        clientConfig.setName(clientConfigurationEntity.getName());
        clientConfig.setDescription(clientConfigurationEntity.getDescription());
        clientConfig.setType(clientConfigurationEntity.getProviderType().key);
        clientConfig.setClientId(clientConfigurationEntity.getClientId());
        clientConfig.setClientSecret(clientConfigurationEntity.getClientSecret());
        clientConfig.setAuthorizationEndpoint(clientConfigurationEntity.getAuthorizationEndpoint());
        clientConfig.setTokenEndpoint(clientConfigurationEntity.getTokenEndpoint());
        clientConfig.setScopes(this.listToDelimited(clientConfigurationEntity.getScopes()));
        clientConfig.save();
    }

    private void assertValid(ClientConfigurationEntity config) {
        this.clientHttpsValidator.assertSecure((ClientConfiguration)config);
    }

    @Override
    public boolean isNameUnique(@Nullable String id, @Nonnull String name) {
        return (Boolean)this.activeObjects.executeInTransaction(() -> this.isUnique(id, "NAME = ?", name));
    }

    private boolean isUnique(String id, String where, String ... params) {
        Query query = Query.select((String)"ID");
        if (id == null) {
            query.setWhereClause(where);
            query.setWhereParams((Object[])params);
        } else {
            query.setWhereClause("(" + where + ") AND " + "ID" + "<> ?");
            query.setWhereParams(ArrayUtils.add((Object[])params, (Object)id));
        }
        return this.activeObjects.count(AOClientConfig.class, query) == 0;
    }

    private String listToDelimited(List<String> scopes) {
        return StringUtils.collectionToDelimitedString(scopes, (String)DELIMITER);
    }

    @Override
    @Nullable
    public ClientConfigurationEntity getById(@Nonnull String id) {
        return ((Optional)this.activeObjects.executeInTransaction(() -> this.findById(id))).map(this::toEntity).orElse(null);
    }

    @Override
    @Nonnull
    public ClientConfigurationEntity getByIdOrFail(@Nonnull String id) throws ConfigurationNotFoundException {
        return this.executeInTransaction(() -> this.toEntity(this.findByIdOrFail(id)), ConfigurationNotFoundException.class);
    }

    private AOClientConfig findByIdOrFail(@Nonnull String id) throws ConfigurationNotFoundException {
        return this.findById(id).orElseThrow(() -> new ConfigurationNotFoundException("Configuration {" + id + "} does not exist"));
    }

    private Optional<AOClientConfig> findById(@Nonnull String id) {
        return Optional.ofNullable(this.activeObjects.get(AOClientConfig.class, (Object)id));
    }

    @Override
    @Nonnull
    public List<ClientConfigurationEntity> list() {
        return (List)this.activeObjects.executeInTransaction(() -> Arrays.stream(this.activeObjects.find(AOClientConfig.class, Query.select().order("NAME ASC"))).map(this::toEntity).collect(Collectors.toList()));
    }

    @Override
    public Optional<ClientConfigurationEntity> getByName(String configName) {
        return (Optional)this.activeObjects.executeInTransaction(() -> Arrays.stream(this.activeObjects.find(AOClientConfig.class, Query.select().where("NAME = ?", new Object[]{configName}))).findFirst().map(this::toEntity));
    }

    private ClientConfigurationEntity toEntity(AOClientConfig aoClientConfig) {
        return ClientConfigurationEntity.builder().id(aoClientConfig.getId()).name(aoClientConfig.getName()).description(aoClientConfig.getDescription()).providerType(ProviderType.getOrThrow((String)aoClientConfig.getType())).clientId(aoClientConfig.getClientId()).clientSecret(aoClientConfig.getClientSecret()).authorizationEndpoint(aoClientConfig.getAuthorizationEndpoint()).tokenEndpoint(aoClientConfig.getTokenEndpoint()).scopes(this.delimitedToList(aoClientConfig.getScopes())).build();
    }

    private List<String> delimitedToList(String scopes) {
        return Arrays.asList(StringUtils.delimitedListToStringArray((String)scopes, (String)DELIMITER));
    }
}

