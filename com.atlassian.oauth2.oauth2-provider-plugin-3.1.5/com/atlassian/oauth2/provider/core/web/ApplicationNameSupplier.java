/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth2.provider.api.client.Client
 *  com.atlassian.oauth2.provider.api.client.ClientService
 *  javax.annotation.Nonnull
 */
package com.atlassian.oauth2.provider.core.web;

import com.atlassian.oauth2.provider.api.client.Client;
import com.atlassian.oauth2.provider.api.client.ClientService;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import javax.annotation.Nonnull;

class ApplicationNameSupplier
implements Supplier<Optional<String>> {
    private final AtomicReference<String> clientNameBackupInCaseOfClientRemoval = new AtomicReference();
    private final String clientId;
    private final ClientService clientService;

    ApplicationNameSupplier(String clientId, ClientService clientService) {
        this.clientId = clientId;
        this.clientService = clientService;
    }

    @Override
    public Optional<String> get() {
        return Optional.ofNullable(this.clientId).flatMap(presentClientId -> Optional.ofNullable(this.clientService.getByClientId(this.clientId).map(this::addToBackupAndGetClientName).orElseGet(this.clientNameBackupInCaseOfClientRemoval::get)));
    }

    @Nonnull
    private String addToBackupAndGetClientName(Client client) {
        this.clientNameBackupInCaseOfClientRemoval.set(client.getName());
        return client.getName();
    }
}

