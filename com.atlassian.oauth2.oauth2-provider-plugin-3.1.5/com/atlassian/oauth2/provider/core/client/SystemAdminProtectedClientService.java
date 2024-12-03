/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth2.provider.api.client.Client
 *  com.atlassian.oauth2.provider.api.client.ClientService
 *  com.atlassian.oauth2.scopes.api.Scope
 *  com.atlassian.sal.api.permission.PermissionEnforcer
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.atlassian.oauth2.provider.core.client;

import com.atlassian.oauth2.provider.api.client.Client;
import com.atlassian.oauth2.provider.api.client.ClientService;
import com.atlassian.oauth2.scopes.api.Scope;
import com.atlassian.sal.api.permission.PermissionEnforcer;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SystemAdminProtectedClientService
implements ClientService {
    private final PermissionEnforcer permissionEnforcer;
    private final ClientService delegate;

    public SystemAdminProtectedClientService(PermissionEnforcer permissionEnforcer, ClientService delegate) {
        this.permissionEnforcer = permissionEnforcer;
        this.delegate = delegate;
    }

    @NotNull
    public Client create(@NotNull String name, Scope scope, @NotNull List<String> redirectUris) {
        this.permissionEnforcer.enforceSystemAdmin();
        return this.delegate.create(name, scope, redirectUris);
    }

    public Optional<Client> updateClient(@NotNull String id, String name, String scope, @NotNull List<String> redirectUris) {
        this.permissionEnforcer.enforceSystemAdmin();
        return this.delegate.updateClient(id, name, scope, redirectUris);
    }

    public Optional<Client> resetClientSecret(@NotNull String clientId) {
        this.permissionEnforcer.enforceSystemAdmin();
        return this.delegate.resetClientSecret(clientId);
    }

    public Optional<Client> getById(@NotNull String id) {
        this.permissionEnforcer.enforceSystemAdmin();
        return this.delegate.getById(id);
    }

    public Optional<Client> getByClientId(@NotNull String clientId) {
        this.permissionEnforcer.enforceSystemAdmin();
        return this.delegate.getByClientId(clientId);
    }

    public List<String> findRedirectUrisByClientId(@NotNull String clientId) {
        this.permissionEnforcer.enforceSystemAdmin();
        return this.delegate.findRedirectUrisByClientId(clientId);
    }

    public List<Client> list() {
        this.permissionEnforcer.enforceSystemAdmin();
        return this.delegate.list();
    }

    public Optional<Client> removeById(@NotNull String id) {
        this.permissionEnforcer.enforceSystemAdmin();
        return this.delegate.removeById(id);
    }

    public boolean isClientNameUnique(@Nullable String clientId, @NotNull String clientName) {
        this.permissionEnforcer.enforceSystemAdmin();
        return this.delegate.isClientNameUnique(clientId, clientName);
    }

    public boolean isClientSecretValid(@NotNull String clientId, @NotNull String clientSecret) {
        this.permissionEnforcer.enforceSystemAdmin();
        return this.delegate.isClientSecretValid(clientId, clientSecret);
    }
}

