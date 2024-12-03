/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.web.reactive.function.client.ClientRequest
 *  org.springframework.web.reactive.function.client.ExchangeFilterFunction
 *  org.springframework.web.reactive.function.client.ExchangeStrategies
 *  org.springframework.web.reactive.function.client.WebClient
 *  org.springframework.web.reactive.function.client.WebClient$Builder
 *  reactor.core.publisher.Mono
 *  reactor.core.scheduler.Schedulers
 */
package org.springframework.vault.client;

import java.net.URI;
import org.springframework.core.codec.ByteArrayDecoder;
import org.springframework.core.codec.ByteArrayEncoder;
import org.springframework.core.codec.StringDecoder;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.codec.CodecConfigurer;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.util.Assert;
import org.springframework.vault.client.ReactiveVaultEndpointProvider;
import org.springframework.vault.client.SimpleVaultEndpointProvider;
import org.springframework.vault.client.VaultClients;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.client.VaultEndpointProvider;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilderFactory;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public class ReactiveVaultClients {
    public static WebClient createWebClient(VaultEndpoint endpoint, ClientHttpConnector connector) {
        return ReactiveVaultClients.createWebClient(SimpleVaultEndpointProvider.of(endpoint), connector);
    }

    public static WebClient createWebClient(VaultEndpointProvider endpointProvider, ClientHttpConnector connector) {
        return ReactiveVaultClients.createWebClient(ReactiveVaultClients.wrap(endpointProvider), connector);
    }

    public static WebClient createWebClient(ReactiveVaultEndpointProvider endpointProvider, ClientHttpConnector connector) {
        return ReactiveVaultClients.createWebClientBuilder(endpointProvider, connector).build();
    }

    static WebClient.Builder createWebClientBuilder(ReactiveVaultEndpointProvider endpointProvider, ClientHttpConnector connector) {
        Assert.notNull((Object)endpointProvider, "ReactiveVaultEndpointProvider must not be null");
        Assert.notNull((Object)connector, "ClientHttpConnector must not be null");
        ExchangeStrategies strategies = ExchangeStrategies.builder().codecs(configurer -> {
            CodecConfigurer.CustomCodecs cc = configurer.customCodecs();
            cc.decoder(new ByteArrayDecoder());
            cc.decoder(new Jackson2JsonDecoder());
            cc.decoder(StringDecoder.allMimeTypes());
            cc.encoder(new ByteArrayEncoder());
            cc.encoder(new Jackson2JsonEncoder());
        }).build();
        WebClient.Builder builder = WebClient.builder().exchangeStrategies(strategies).clientConnector(connector);
        boolean simpleSource = false;
        if (endpointProvider instanceof VaultEndpointProviderAdapter && ((VaultEndpointProviderAdapter)endpointProvider).source instanceof SimpleVaultEndpointProvider) {
            simpleSource = true;
            UriBuilderFactory uriBuilderFactory = VaultClients.createUriBuilderFactory(((VaultEndpointProviderAdapter)endpointProvider).source);
            builder.uriBuilderFactory(uriBuilderFactory);
        }
        if (!simpleSource) {
            builder.filter((request, next) -> {
                URI uri = request.url();
                if (!uri.isAbsolute()) {
                    return endpointProvider.getVaultEndpoint().flatMap(endpoint -> {
                        UriComponents uriComponents = UriComponentsBuilder.fromUri(uri).scheme(endpoint.getScheme()).host(endpoint.getHost()).port(endpoint.getPort()).replacePath(endpoint.getPath()).path(VaultClients.normalizePath(endpoint.getPath(), uri.getPath())).build();
                        ClientRequest requestToSend = ClientRequest.from((ClientRequest)request).url(uriComponents.toUri()).build();
                        return next.exchange(requestToSend);
                    });
                }
                return next.exchange(request);
            });
        }
        return builder;
    }

    public static ExchangeFilterFunction namespace(String namespace) {
        Assert.hasText(namespace, "Vault Namespace must not be empty!");
        return ExchangeFilterFunction.ofRequestProcessor(request -> Mono.fromSupplier(() -> ClientRequest.from((ClientRequest)request).headers(headers -> {
            if (!headers.containsKey("X-Vault-Namespace")) {
                headers.add("X-Vault-Namespace", namespace);
            }
        }).build()));
    }

    public static ReactiveVaultEndpointProvider wrap(VaultEndpointProvider endpointProvider) {
        Assert.notNull((Object)endpointProvider, "VaultEndpointProvider must not be null");
        return new VaultEndpointProviderAdapter(endpointProvider);
    }

    private static class VaultEndpointProviderAdapter
    implements ReactiveVaultEndpointProvider {
        private final VaultEndpointProvider source;
        private final Mono<VaultEndpoint> mono;

        VaultEndpointProviderAdapter(VaultEndpointProvider provider) {
            this.source = provider;
            this.mono = Mono.fromSupplier(provider::getVaultEndpoint).subscribeOn(Schedulers.boundedElastic());
        }

        @Override
        public Mono<VaultEndpoint> getVaultEndpoint() {
            return this.mono;
        }
    }
}

