/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  reactor.core.publisher.Mono
 */
package org.springframework.vault.client;

import org.springframework.vault.client.VaultEndpoint;
import reactor.core.publisher.Mono;

@FunctionalInterface
public interface ReactiveVaultEndpointProvider {
    public Mono<VaultEndpoint> getVaultEndpoint();
}

