/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.reactivestreams.Publisher
 *  org.springframework.core.ParameterizedTypeReference
 *  org.springframework.http.HttpMethod
 *  org.springframework.http.HttpStatus
 *  org.springframework.http.client.reactive.ClientHttpConnector
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.web.client.HttpStatusCodeException
 *  org.springframework.web.reactive.function.BodyExtractors
 *  org.springframework.web.reactive.function.client.ClientRequest
 *  org.springframework.web.reactive.function.client.ClientResponse
 *  org.springframework.web.reactive.function.client.ExchangeFilterFunction
 *  org.springframework.web.reactive.function.client.WebClient
 *  org.springframework.web.reactive.function.client.WebClient$RequestBodySpec
 *  org.springframework.web.reactive.function.client.WebClientException
 *  reactor.core.publisher.Flux
 *  reactor.core.publisher.Mono
 */
package org.springframework.vault.core;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.reactivestreams.Publisher;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.vault.VaultException;
import org.springframework.vault.authentication.VaultTokenSupplier;
import org.springframework.vault.client.SimpleVaultEndpointProvider;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.client.VaultEndpointProvider;
import org.springframework.vault.client.VaultResponses;
import org.springframework.vault.client.WebClientBuilder;
import org.springframework.vault.core.ReactiveVaultOperations;
import org.springframework.vault.support.VaultResponse;
import org.springframework.vault.support.VaultResponseSupport;
import org.springframework.vault.support.VaultToken;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ReactiveVaultTemplate
implements ReactiveVaultOperations {
    private final WebClient statelessClient;
    private final WebClient sessionClient;
    private final VaultTokenSupplier vaultTokenSupplier;

    public ReactiveVaultTemplate(VaultEndpoint vaultEndpoint, ClientHttpConnector connector) {
        this(SimpleVaultEndpointProvider.of(vaultEndpoint), connector);
    }

    public ReactiveVaultTemplate(VaultEndpoint vaultEndpoint, ClientHttpConnector connector, VaultTokenSupplier vaultTokenSupplier) {
        this(SimpleVaultEndpointProvider.of(vaultEndpoint), connector, vaultTokenSupplier);
    }

    public ReactiveVaultTemplate(VaultEndpointProvider endpointProvider, ClientHttpConnector connector) {
        Assert.notNull((Object)endpointProvider, (String)"VaultEndpointProvider must not be null");
        Assert.notNull((Object)connector, (String)"ClientHttpConnector must not be null");
        WebClient webClient = this.doCreateWebClient(endpointProvider, connector);
        this.vaultTokenSupplier = NoTokenSupplier.INSTANCE;
        this.statelessClient = webClient;
        this.sessionClient = webClient;
    }

    public ReactiveVaultTemplate(VaultEndpointProvider endpointProvider, ClientHttpConnector connector, VaultTokenSupplier vaultTokenSupplier) {
        Assert.notNull((Object)endpointProvider, (String)"VaultEndpointProvider must not be null");
        Assert.notNull((Object)connector, (String)"ClientHttpConnector must not be null");
        Assert.notNull((Object)vaultTokenSupplier, (String)"VaultTokenSupplier must not be null");
        this.vaultTokenSupplier = vaultTokenSupplier;
        this.statelessClient = this.doCreateWebClient(endpointProvider, connector);
        this.sessionClient = this.doCreateSessionWebClient(endpointProvider, connector);
    }

    public ReactiveVaultTemplate(WebClientBuilder webClientBuilder) {
        Assert.notNull((Object)webClientBuilder, (String)"WebClientBuilder must not be null");
        WebClient webClient = webClientBuilder.build();
        this.vaultTokenSupplier = NoTokenSupplier.INSTANCE;
        this.statelessClient = webClient;
        this.sessionClient = webClient;
    }

    public ReactiveVaultTemplate(WebClientBuilder webClientBuilder, VaultTokenSupplier vaultTokenSupplier) {
        Assert.notNull((Object)webClientBuilder, (String)"WebClientBuilder must not be null");
        Assert.notNull((Object)vaultTokenSupplier, (String)"VaultTokenSupplier must not be null");
        this.vaultTokenSupplier = vaultTokenSupplier;
        this.statelessClient = webClientBuilder.build();
        this.sessionClient = webClientBuilder.build().mutate().filter(this.getSessionFilter()).build();
    }

    protected WebClient doCreateWebClient(VaultEndpointProvider endpointProvider, ClientHttpConnector connector) {
        Assert.notNull((Object)endpointProvider, (String)"VaultEndpointProvider must not be null");
        Assert.notNull((Object)connector, (String)"ClientHttpConnector must not be null");
        return WebClientBuilder.builder().httpConnector(connector).endpointProvider(endpointProvider).build();
    }

    protected WebClient doCreateSessionWebClient(VaultEndpointProvider endpointProvider, ClientHttpConnector connector) {
        Assert.notNull((Object)endpointProvider, (String)"VaultEndpointProvider must not be null");
        Assert.notNull((Object)connector, (String)"ClientHttpConnector must not be null");
        ExchangeFilterFunction filter = this.getSessionFilter();
        return WebClientBuilder.builder().httpConnector(connector).endpointProvider(endpointProvider).filter(filter).build();
    }

    private ExchangeFilterFunction getSessionFilter() {
        return ExchangeFilterFunction.ofRequestProcessor(request -> this.vaultTokenSupplier.getVaultToken().map(token -> ClientRequest.from((ClientRequest)request).headers(headers -> headers.set("X-Vault-Token", token.getToken())).build()));
    }

    @Override
    public Mono<VaultResponse> read(String path) {
        Assert.hasText((String)path, (String)"Path must not be empty");
        return this.doRead(path, VaultResponse.class);
    }

    @Override
    public <T> Mono<VaultResponseSupport<T>> read(String path, Class<T> responseType) {
        return this.doWithSession(webClient -> {
            ParameterizedTypeReference ref = VaultResponses.getTypeReference(responseType);
            return webClient.get().uri(path, new Object[0]).exchangeToMono(ReactiveVaultTemplate.mapResponse(ref, path, HttpMethod.GET));
        });
    }

    @Override
    public Flux<String> list(String path) {
        Assert.hasText((String)path, (String)"Path must not be empty");
        Mono<VaultListResponse> read = this.doRead(String.format("%s?list=true", path.endsWith("/") ? path : path + "/"), VaultListResponse.class);
        return read.filter(response -> response.getData() != null && ((Map)response.getData()).containsKey("keys")).flatMapIterable(response -> (List)((Map)response.getRequiredData()).get("keys"));
    }

    @Override
    public Mono<VaultResponse> write(String path, @Nullable Object body) {
        Assert.hasText((String)path, (String)"Path must not be empty");
        return this.doWithSession(webClient -> {
            WebClient.RequestBodySpec uri = (WebClient.RequestBodySpec)webClient.post().uri(path, new Object[0]);
            if (body != null) {
                return uri.bodyValue(body).exchangeToMono(ReactiveVaultTemplate.mapResponse(VaultResponse.class, path, HttpMethod.POST));
            }
            return uri.exchangeToMono(ReactiveVaultTemplate.mapResponse(VaultResponse.class, path, HttpMethod.POST));
        });
    }

    @Override
    public Mono<Void> delete(String path) {
        Assert.hasText((String)path, (String)"Path must not be empty");
        return this.doWithSession(webClient -> webClient.delete().uri(path, new Object[0]).exchangeToMono(ReactiveVaultTemplate.mapResponse(String.class, path, HttpMethod.DELETE))).then();
    }

    @Override
    public <V, T extends Publisher<V>> T doWithVault(Function<WebClient, ? extends T> clientCallback) throws VaultException, WebClientException {
        Assert.notNull(clientCallback, (String)"Client callback must not be null");
        try {
            return (T)((Publisher)clientCallback.apply(this.statelessClient));
        }
        catch (HttpStatusCodeException e) {
            throw VaultResponses.buildException(e);
        }
    }

    @Override
    public <V, T extends Publisher<V>> T doWithSession(Function<WebClient, ? extends T> sessionCallback) throws VaultException, WebClientException {
        Assert.notNull(sessionCallback, (String)"Session callback must not be null");
        try {
            return (T)((Publisher)sessionCallback.apply(this.sessionClient));
        }
        catch (HttpStatusCodeException e) {
            throw VaultResponses.buildException(e);
        }
    }

    private <T> Mono<T> doRead(String path, Class<T> responseType) {
        return this.doWithSession(client -> client.get().uri(path, new Object[0]).exchangeToMono(ReactiveVaultTemplate.mapResponse(responseType, path, HttpMethod.GET)));
    }

    private static <T> Function<ClientResponse, Mono<T>> mapResponse(Class<T> bodyType, String path, HttpMethod method) {
        return response -> ReactiveVaultTemplate.isSuccess(response) ? response.bodyToMono(bodyType) : ReactiveVaultTemplate.mapOtherwise(response, path, method);
    }

    private static <T> Function<ClientResponse, Mono<T>> mapResponse(ParameterizedTypeReference<T> typeReference, String path, HttpMethod method) {
        return response -> ReactiveVaultTemplate.isSuccess(response) ? (Mono)response.body(BodyExtractors.toMono((ParameterizedTypeReference)typeReference)) : ReactiveVaultTemplate.mapOtherwise(response, path, method);
    }

    private static boolean isSuccess(ClientResponse response) {
        return response.statusCode().is2xxSuccessful();
    }

    private static <T> Mono<T> mapOtherwise(ClientResponse response, String path, HttpMethod method) {
        if (response.statusCode() == HttpStatus.NOT_FOUND && method == HttpMethod.GET) {
            return response.releaseBody().then(Mono.empty());
        }
        return response.bodyToMono(String.class).flatMap(body -> {
            String error = VaultResponses.getError(body);
            return Mono.error((Throwable)((Object)VaultResponses.buildException(response.statusCode(), path, error)));
        });
    }

    private static enum NoTokenSupplier implements VaultTokenSupplier
    {
        INSTANCE;


        @Override
        public Mono<VaultToken> getVaultToken() {
            return Mono.error((Throwable)new UnsupportedOperationException("Token retrieval disabled"));
        }
    }

    private static class VaultListResponse
    extends VaultResponseSupport<Map<String, Object>> {
        private VaultListResponse() {
        }
    }
}

