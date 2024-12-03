/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.client;

import org.springframework.http.HttpHeaders;
import org.springframework.util.Assert;
import org.springframework.vault.support.VaultToken;

public abstract class VaultHttpHeaders {
    public static final String VAULT_TOKEN = "X-Vault-Token";
    public static final String VAULT_NAMESPACE = "X-Vault-Namespace";

    private VaultHttpHeaders() {
    }

    public static HttpHeaders from(VaultToken vaultToken) {
        Assert.notNull((Object)vaultToken, "VaultToken must not be null");
        HttpHeaders headers = new HttpHeaders();
        headers.add(VAULT_TOKEN, vaultToken.getToken());
        return headers;
    }
}

