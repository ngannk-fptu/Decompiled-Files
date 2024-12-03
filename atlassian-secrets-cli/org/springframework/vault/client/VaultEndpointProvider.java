/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.client;

import org.springframework.vault.client.VaultEndpoint;

@FunctionalInterface
public interface VaultEndpointProvider {
    public VaultEndpoint getVaultEndpoint();
}

