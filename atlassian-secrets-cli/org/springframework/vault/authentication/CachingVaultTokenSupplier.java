/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  reactor.core.publisher.Mono
 */
package org.springframework.vault.authentication;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import org.springframework.vault.VaultException;
import org.springframework.vault.authentication.ReactiveSessionManager;
import org.springframework.vault.authentication.VaultTokenSupplier;
import org.springframework.vault.support.VaultToken;
import reactor.core.publisher.Mono;

public class CachingVaultTokenSupplier
implements VaultTokenSupplier,
ReactiveSessionManager {
    private static final Mono<VaultToken> EMPTY = Mono.empty();
    private final VaultTokenSupplier clientAuthentication;
    private final AtomicReference<Mono<VaultToken>> tokenRef = new AtomicReference<Mono<VaultToken>>(EMPTY);

    private CachingVaultTokenSupplier(VaultTokenSupplier clientAuthentication) {
        this.clientAuthentication = clientAuthentication;
    }

    public static CachingVaultTokenSupplier of(VaultTokenSupplier delegate) {
        return new CachingVaultTokenSupplier(delegate);
    }

    @Override
    public Mono<VaultToken> getVaultToken() throws VaultException {
        if (Objects.equals(this.tokenRef.get(), EMPTY)) {
            this.tokenRef.compareAndSet(EMPTY, (Mono<VaultToken>)this.clientAuthentication.getVaultToken().cache());
        }
        return this.tokenRef.get();
    }
}

