/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.oauth2.provider.api.client.Client
 *  com.atlassian.oauth2.provider.api.client.ClientService
 *  com.atlassian.oauth2.scopes.api.ScopeResolver
 *  com.atlassian.sal.api.message.I18nResolver
 *  javax.annotation.Nonnull
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.oauth2.provider.rest.service;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.oauth2.provider.api.client.Client;
import com.atlassian.oauth2.provider.api.client.ClientService;
import com.atlassian.oauth2.provider.rest.exception.ClientNotFoundException;
import com.atlassian.oauth2.provider.rest.exception.ValidationException;
import com.atlassian.oauth2.provider.rest.model.RestClientEntity;
import com.atlassian.oauth2.provider.rest.validation.RestClientValidator;
import com.atlassian.oauth2.scopes.api.ScopeResolver;
import com.atlassian.sal.api.message.I18nResolver;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientRestService {
    private static final Logger logger = LoggerFactory.getLogger(ClientRestService.class);
    private final RestClientValidator restClientValidator;
    private final ClientService clientService;
    private final ScopeResolver scopeResolver;
    private final I18nResolver i18nResolver;

    public ClientRestService(RestClientValidator restClientValidator, ClientService clientService, ScopeResolver scopeResolver, I18nResolver i18nResolver) {
        this.restClientValidator = restClientValidator;
        this.clientService = clientService;
        this.scopeResolver = scopeResolver;
        this.i18nResolver = i18nResolver;
    }

    public RestClientEntity create(@Nonnull RestClientEntity client) throws ValidationException {
        RestClientEntity restClientEntity = this.restClientValidator.validateCreate(this.trim(client));
        logger.debug("Creating client {}", (Object)restClientEntity.getName());
        Client newClientEntity = this.clientService.create(restClientEntity.getName(), this.scopeResolver.getScope(restClientEntity.getScope()), restClientEntity.getRedirects());
        return ClientRestService.clientEntityToRestClient(newClientEntity);
    }

    public List<RestClientEntity> list() {
        return this.clientService.list().stream().map(ClientRestService::clientEntityToRestClient).collect(Collectors.toList());
    }

    public RestClientEntity get(@Nonnull String id) throws ClientNotFoundException {
        logger.debug("Retrieving client associated with id {}", (Object)id);
        Client client = (Client)this.clientService.getById(id).orElseThrow(this.clientNotFoundException());
        return ClientRestService.clientEntityToRestClient(client);
    }

    public RestClientEntity delete(@Nonnull String id) throws ClientNotFoundException {
        logger.debug("Removing client associated with id {}", (Object)id);
        Client client = (Client)this.clientService.removeById(id).orElseThrow(this.clientNotFoundException());
        return ClientRestService.clientEntityToRestClient(client);
    }

    public void update(@Nonnull String id, @Nonnull RestClientEntity clientUpdate) throws ClientNotFoundException, ValidationException {
        Client oldClient = (Client)this.clientService.getById(id).orElseThrow(this.clientNotFoundException());
        RestClientEntity restClientEntity = this.restClientValidator.validateUpdate(oldClient, this.trim(clientUpdate));
        logger.debug("Updating client {}", (Object)oldClient.getName());
        this.clientService.updateClient(id, restClientEntity.getName(), restClientEntity.getScope(), restClientEntity.getRedirects());
    }

    public RestClientEntity resetClientSecret(@Nonnull String clientId) throws ClientNotFoundException {
        logger.debug("Resetting client secret associated with client id {}", (Object)clientId);
        Client client = (Client)this.clientService.resetClientSecret(clientId).orElseThrow(() -> new ClientNotFoundException(this.i18nResolver.getText("oauth2.rest.error.client.does.not.exist")));
        return ClientRestService.clientEntityToRestClient(client);
    }

    private RestClientEntity trim(RestClientEntity client) {
        return RestClientEntity.builder().clientId(StringUtils.trim((String)client.getClientId())).clientSecret(StringUtils.trim((String)client.getClientSecret())).redirects(this.getTrimmedRedirects(client.getRedirects())).name(StringUtils.trim((String)client.getName())).scope(StringUtils.trim((String)client.getScope())).build();
    }

    private List<String> getTrimmedRedirects(List<String> redirects) {
        if (redirects != null) {
            return redirects.stream().map(StringUtils::trim).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private Supplier<ClientNotFoundException> clientNotFoundException() {
        return () -> new ClientNotFoundException(this.i18nResolver.getText("oauth2.rest.error.client.does.not.exist"));
    }

    @VisibleForTesting
    public static RestClientEntity clientEntityToRestClient(Client clientEntity) {
        logger.debug("Mapping client entity to rest client for client [{}]", (Object)clientEntity.getName());
        return RestClientEntity.builder().id(clientEntity.getId()).clientId(clientEntity.getClientId()).clientSecret(clientEntity.getClientSecret()).name(clientEntity.getName()).redirects(clientEntity.getRedirects()).userKey(clientEntity.getUserKey()).scope(clientEntity.getScope().toString()).build();
    }
}

