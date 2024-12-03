/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.http.HttpEntity
 *  org.springframework.http.HttpMethod
 *  org.springframework.http.ResponseEntity
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.web.client.HttpStatusCodeException
 */
package org.springframework.vault.core;

import java.util.Collections;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.vault.client.VaultResponses;
import org.springframework.vault.core.VaultOperations;
import org.springframework.vault.core.VaultTokenOperations;
import org.springframework.vault.support.VaultResponseSupport;
import org.springframework.vault.support.VaultToken;
import org.springframework.vault.support.VaultTokenRequest;
import org.springframework.vault.support.VaultTokenResponse;
import org.springframework.web.client.HttpStatusCodeException;

public class VaultTokenTemplate
implements VaultTokenOperations {
    private final VaultOperations vaultOperations;

    public VaultTokenTemplate(VaultOperations vaultOperations) {
        Assert.notNull((Object)vaultOperations, (String)"VaultOperations must not be null");
        this.vaultOperations = vaultOperations;
    }

    @Override
    public VaultTokenResponse create() {
        return this.create(VaultTokenRequest.builder().build());
    }

    @Override
    public VaultTokenResponse create(VaultTokenRequest request) {
        Assert.notNull((Object)request, (String)"VaultTokenRequest must not be null");
        return this.writeAndReturn("auth/token/create", request, VaultTokenResponse.class);
    }

    @Override
    public VaultTokenResponse createOrphan() {
        return this.createOrphan(VaultTokenRequest.builder().build());
    }

    @Override
    public VaultTokenResponse createOrphan(VaultTokenRequest request) {
        Assert.notNull((Object)request, (String)"VaultTokenRequest must not be null");
        return this.writeAndReturn("auth/token/create-orphan", request, VaultTokenResponse.class);
    }

    @Override
    public VaultTokenResponse renew(VaultToken vaultToken) {
        Assert.notNull((Object)vaultToken, (String)"VaultToken must not be null");
        return this.writeAndReturn("auth/token/renew", vaultToken, VaultTokenResponse.class);
    }

    @Override
    public void revoke(VaultToken vaultToken) {
        Assert.notNull((Object)vaultToken, (String)"VaultToken must not be null");
        this.writeToken("auth/token/revoke", vaultToken, VaultTokenResponse.class);
    }

    @Override
    public void revokeOrphan(VaultToken vaultToken) {
        Assert.notNull((Object)vaultToken, (String)"VaultToken must not be null");
        this.writeToken("auth/token/revoke-orphan", vaultToken, VaultTokenResponse.class);
    }

    private <T extends VaultResponseSupport<?>> T writeAndReturn(String path, @Nullable Object body, Class<T> responseType) {
        Assert.hasText((String)path, (String)"Path must not be empty");
        VaultResponseSupport response = this.vaultOperations.doWithSession(restOperations -> {
            try {
                ResponseEntity exchange = restOperations.exchange(path, HttpMethod.POST, body == null ? HttpEntity.EMPTY : new HttpEntity(body), responseType, new Object[0]);
                return (VaultResponseSupport)exchange.getBody();
            }
            catch (HttpStatusCodeException e) {
                throw VaultResponses.buildException(e, path);
            }
        });
        Assert.state((response != null ? 1 : 0) != 0, (String)"Response must not be null");
        return (T)response;
    }

    @Nullable
    private void writeToken(String path, VaultToken token, Class<?> responseType) {
        Assert.hasText((String)path, (String)"Path must not be empty");
        this.vaultOperations.doWithSession(restOperations -> {
            try {
                restOperations.exchange(path, HttpMethod.POST, new HttpEntity(Collections.singletonMap("token", token.getToken())), responseType, new Object[0]);
                return null;
            }
            catch (HttpStatusCodeException e) {
                throw VaultResponses.buildException(e, path);
            }
        });
    }
}

