/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.core;

import java.util.Collections;
import java.util.Map;
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
        Assert.notNull((Object)vaultOperations, "VaultOperations must not be null");
        this.vaultOperations = vaultOperations;
    }

    @Override
    public VaultTokenResponse create() {
        return this.create(VaultTokenRequest.builder().build());
    }

    @Override
    public VaultTokenResponse create(VaultTokenRequest request) {
        Assert.notNull((Object)request, "VaultTokenRequest must not be null");
        return this.writeAndReturn("auth/token/create", request, VaultTokenResponse.class);
    }

    @Override
    public VaultTokenResponse createOrphan() {
        return this.createOrphan(VaultTokenRequest.builder().build());
    }

    @Override
    public VaultTokenResponse createOrphan(VaultTokenRequest request) {
        Assert.notNull((Object)request, "VaultTokenRequest must not be null");
        return this.writeAndReturn("auth/token/create-orphan", request, VaultTokenResponse.class);
    }

    @Override
    public VaultTokenResponse renew(VaultToken vaultToken) {
        Assert.notNull((Object)vaultToken, "VaultToken must not be null");
        return this.writeAndReturn("auth/token/renew", vaultToken, VaultTokenResponse.class);
    }

    @Override
    public void revoke(VaultToken vaultToken) {
        Assert.notNull((Object)vaultToken, "VaultToken must not be null");
        this.writeToken("auth/token/revoke", vaultToken, VaultTokenResponse.class);
    }

    @Override
    public void revokeOrphan(VaultToken vaultToken) {
        Assert.notNull((Object)vaultToken, "VaultToken must not be null");
        this.writeToken("auth/token/revoke-orphan", vaultToken, VaultTokenResponse.class);
    }

    private <T extends VaultResponseSupport<?>> T writeAndReturn(String path, @Nullable Object body, Class<T> responseType) {
        Assert.hasText(path, "Path must not be empty");
        VaultResponseSupport response = this.vaultOperations.doWithSession(restOperations -> {
            try {
                ResponseEntity exchange2 = restOperations.exchange(path, HttpMethod.POST, body == null ? HttpEntity.EMPTY : new HttpEntity<Object>(body), responseType, new Object[0]);
                return (VaultResponseSupport)exchange2.getBody();
            }
            catch (HttpStatusCodeException e) {
                throw VaultResponses.buildException(e, path);
            }
        });
        Assert.state(response != null, "Response must not be null");
        return (T)response;
    }

    @Nullable
    private void writeToken(String path, VaultToken token, Class<?> responseType) {
        Assert.hasText(path, "Path must not be empty");
        this.vaultOperations.doWithSession(restOperations -> {
            try {
                restOperations.exchange(path, HttpMethod.POST, new HttpEntity<Map<String, String>>(Collections.singletonMap("token", token.getToken())), responseType, new Object[0]);
                return null;
            }
            catch (HttpStatusCodeException e) {
                throw VaultResponses.buildException(e, path);
            }
        });
    }
}

