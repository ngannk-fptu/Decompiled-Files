/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  reactor.core.publisher.Mono
 */
package org.springframework.http.client.reactive;

import java.net.URI;
import java.util.function.Function;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.reactive.ClientHttpRequest;
import org.springframework.http.client.reactive.ClientHttpResponse;
import reactor.core.publisher.Mono;

public interface ClientHttpConnector {
    public Mono<ClientHttpResponse> connect(HttpMethod var1, URI var2, Function<? super ClientHttpRequest, Mono<Void>> var3);
}

