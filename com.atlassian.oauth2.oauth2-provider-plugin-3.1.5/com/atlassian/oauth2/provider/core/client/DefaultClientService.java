/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth2.provider.api.client.Client
 *  com.atlassian.oauth2.provider.api.client.ClientService
 *  com.atlassian.oauth2.provider.api.client.dao.ClientDao
 *  com.atlassian.oauth2.provider.api.client.dao.ClientEntity
 *  com.atlassian.oauth2.provider.api.client.dao.RedirectUriDao
 *  com.atlassian.oauth2.provider.api.token.TokenService
 *  com.atlassian.oauth2.provider.api.token.access.exception.UserKeyNotFoundException
 *  com.atlassian.oauth2.scopes.api.Scope
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.sal.api.user.UserManager
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.jetbrains.annotations.NotNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.oauth2.provider.core.client;

import com.atlassian.oauth2.common.IdGenerator;
import com.atlassian.oauth2.provider.api.client.Client;
import com.atlassian.oauth2.provider.api.client.ClientService;
import com.atlassian.oauth2.provider.api.client.dao.ClientDao;
import com.atlassian.oauth2.provider.api.client.dao.ClientEntity;
import com.atlassian.oauth2.provider.api.client.dao.RedirectUriDao;
import com.atlassian.oauth2.provider.api.token.TokenService;
import com.atlassian.oauth2.provider.api.token.access.exception.UserKeyNotFoundException;
import com.atlassian.oauth2.provider.core.credentials.ClientCredentialsGenerator;
import com.atlassian.oauth2.provider.core.event.OAuth2ProviderEventPublisher;
import com.atlassian.oauth2.provider.core.properties.SystemProperty;
import com.atlassian.oauth2.scopes.api.Scope;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultClientService
implements ClientService {
    private static final Logger logger = LoggerFactory.getLogger(DefaultClientService.class);
    private final ClientDao clientDao;
    private final ClientCredentialsGenerator clientCredentialsGenerator;
    private final TokenService tokenService;
    private final UserManager userManager;
    private final I18nResolver i18nResolver;
    private final IdGenerator idGenerator;
    private final OAuth2ProviderEventPublisher oAuth2ProviderEventPublisher;
    private final RedirectUriDao redirectUriDao;

    public DefaultClientService(ClientDao clientDao, ClientCredentialsGenerator clientCredentialsGenerator, TokenService tokenService, UserManager userManager, I18nResolver i18nResolver, IdGenerator idGenerator, OAuth2ProviderEventPublisher oAuth2ProviderEventPublisher, RedirectUriDao redirectUriDao) {
        this.clientDao = clientDao;
        this.clientCredentialsGenerator = clientCredentialsGenerator;
        this.tokenService = tokenService;
        this.userManager = userManager;
        this.i18nResolver = i18nResolver;
        this.idGenerator = idGenerator;
        this.oAuth2ProviderEventPublisher = oAuth2ProviderEventPublisher;
        this.redirectUriDao = redirectUriDao;
    }

    @Nonnull
    public Client create(@Nonnull String name, @Nonnull Scope scope, @Nonnull List<String> redirectUris) {
        if (redirectUris.isEmpty()) {
            throw new IllegalStateException("Must contain at least one redirect uri");
        }
        Optional<UserKey> userKey = Optional.ofNullable(this.userManager.getRemoteUserKey());
        if (userKey.isPresent()) {
            Client newClient = this.clientDao.create((Client)ClientEntity.builder().id(this.idGenerator.generate()).clientId(this.clientCredentialsGenerator.generate(ClientCredentialsGenerator.Length.THIRTY_TWO)).clientSecret(this.generateClientSecret()).scope(scope).name(name).userKey(userKey.get().getStringValue()).redirects(redirectUris).build());
            this.oAuth2ProviderEventPublisher.publishClientConfigurationCreatedEvent(newClient);
            return newClient;
        }
        throw new UserKeyNotFoundException(this.i18nResolver.getText("oauth2.service.user.key.not.found"));
    }

    public Optional<Client> updateClient(@Nonnull String id, String name, String scope, @Nonnull List<String> redirectUris) {
        return this.clientDao.getById(id).flatMap(oldClient -> this.clientDao.updateClient(id, name, scope, redirectUris).map(newClient -> {
            this.oAuth2ProviderEventPublisher.publishClientConfigurationUpdatedEvent((Client)oldClient, (Client)newClient);
            return newClient;
        }));
    }

    public Optional<Client> resetClientSecret(@Nonnull String clientId) {
        return this.clientDao.resetClientSecret(clientId, this.generateClientSecret()).map(client -> {
            this.oAuth2ProviderEventPublisher.publishClientSecretRefreshedEvent((Client)client);
            return client;
        });
    }

    public Optional<Client> getById(@Nonnull String id) {
        return this.clientDao.getById(id);
    }

    public Optional<Client> getByClientId(@NotNull String clientId) {
        return this.clientDao.getByClientId(clientId);
    }

    public List<String> findRedirectUrisByClientId(@Nonnull String clientId) {
        return this.redirectUriDao.findByClientId(clientId);
    }

    public Optional<Client> removeById(@Nonnull String id) {
        Optional client = this.clientDao.removeById(id);
        client.ifPresent(it -> {
            this.oAuth2ProviderEventPublisher.publishClientConfigurationDeletedEvent((Client)it);
            this.tokenService.removeByClientId(it.getClientId());
        });
        return client;
    }

    public List<Client> list() {
        return this.clientDao.list();
    }

    public boolean isClientNameUnique(@Nullable String clientId, @Nonnull String clientName) {
        return this.clientDao.isClientNameUnique(clientId, clientName);
    }

    public boolean isClientSecretValid(@Nonnull String clientId, @Nonnull String clientSecret) {
        if (!SystemProperty.VALIDATE_CLIENT_SECRET.getValue().booleanValue()) {
            return true;
        }
        return this.getByClientId(clientId).map(client -> client.getClientSecret().equals(clientSecret)).orElse(false);
    }

    private String generateClientSecret() {
        return this.clientCredentialsGenerator.generate(ClientCredentialsGenerator.Length.SIXTY_FOUR);
    }
}

