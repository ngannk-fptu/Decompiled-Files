/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  reactor.core.publisher.Mono
 */
package org.springframework.vault.authentication;

import org.springframework.vault.authentication.VaultTokenSupplier;
import org.springframework.vault.support.VaultToken;
import reactor.core.publisher.Mono;

public interface ReactiveSessionManager
extends VaultTokenSupplier {
    default public Mono<VaultToken> getSessionToken() {
        return this.getVaultToken();
    }
}

