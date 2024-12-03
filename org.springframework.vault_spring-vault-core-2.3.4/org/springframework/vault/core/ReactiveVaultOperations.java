/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.reactivestreams.Publisher
 *  org.springframework.lang.Nullable
 *  org.springframework.web.reactive.function.client.WebClient
 *  org.springframework.web.reactive.function.client.WebClientException
 *  reactor.core.publisher.Flux
 *  reactor.core.publisher.Mono
 */
package org.springframework.vault.core;

import java.util.function.Function;
import org.reactivestreams.Publisher;
import org.springframework.lang.Nullable;
import org.springframework.vault.VaultException;
import org.springframework.vault.support.VaultResponse;
import org.springframework.vault.support.VaultResponseSupport;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ReactiveVaultOperations {
    public Mono<VaultResponse> read(String var1);

    public <T> Mono<VaultResponseSupport<T>> read(String var1, Class<T> var2);

    public Flux<String> list(String var1);

    default public Mono<VaultResponse> write(String path) {
        return this.write(path, null);
    }

    public Mono<VaultResponse> write(String var1, @Nullable Object var2);

    public Mono<Void> delete(String var1);

    public <V, T extends Publisher<V>> T doWithVault(Function<WebClient, ? extends T> var1) throws VaultException, WebClientException;

    public <V, T extends Publisher<V>> T doWithSession(Function<WebClient, ? extends T> var1) throws VaultException, WebClientException;
}

