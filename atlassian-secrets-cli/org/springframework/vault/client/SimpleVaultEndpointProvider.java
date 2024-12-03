/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.client;

import org.springframework.util.Assert;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.client.VaultEndpointProvider;

public class SimpleVaultEndpointProvider
implements VaultEndpointProvider {
    private final VaultEndpoint endpoint;

    private SimpleVaultEndpointProvider(VaultEndpoint endpoint) {
        this.endpoint = endpoint;
    }

    public static VaultEndpointProvider of(VaultEndpoint endpoint) {
        Assert.notNull((Object)endpoint, "VaultEndpoint must not be null");
        return new SimpleVaultEndpointProvider(endpoint);
    }

    @Override
    public VaultEndpoint getVaultEndpoint() {
        return this.endpoint;
    }
}

