/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  reactor.core.publisher.Mono
 */
package org.springframework.vault.authentication;

import org.springframework.vault.support.VaultToken;
import reactor.core.publisher.Mono;

@FunctionalInterface
public interface VaultTokenSupplier {
    public Mono<VaultToken> getVaultToken();
}

