/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.codec.ByteArrayDecoder
 *  org.springframework.core.codec.ByteArrayEncoder
 *  org.springframework.core.codec.Decoder
 *  org.springframework.core.codec.Encoder
 *  org.springframework.core.codec.StringDecoder
 *  org.springframework.http.client.reactive.ClientHttpConnector
 *  org.springframework.http.codec.CodecConfigurer$CustomCodecs
 *  org.springframework.http.codec.json.Jackson2JsonDecoder
 *  org.springframework.http.codec.json.Jackson2JsonEncoder
 *  org.springframework.util.Assert
 *  org.springframework.web.reactive.function.client.ClientRequest
 *  org.springframework.web.reactive.function.client.ExchangeFilterFunction
 *  org.springframework.web.reactive.function.client.ExchangeStrategies
 *  org.springframework.web.reactive.function.client.WebClient
 *  org.springframework.web.reactive.function.client.WebClient$Builder
 *  org.springframework.web.util.UriBuilderFactory
 *  org.springframework.web.util.UriComponents
 *  org.springframework.web.util.UriComponentsBuilder
 *  reactor.core.publisher.Mono
 *  reactor.core.scheduler.Schedulers
 */
package org.springframework.vault.client;

import java.net.URI;
import org.springframework.core.codec.ByteArrayDecoder;
import org.springframework.core.codec.ByteArrayEncoder;
import org.springframework.core.codec.Decoder;
import org.springframework.core.codec.Encoder;
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
        Assert.notNull((Object)endpointProvider, (String)"ReactiveVaultEndpointProvider must not be null");
        Assert.notNull((Object)connector, (String)"ClientHttpConnector must not be null");
        ExchangeStrategies strategies = ExchangeStrategies.builder().codecs(configurer -> {
            CodecConfigurer.CustomCodecs cc = configurer.customCodecs();
            cc.decoder((Decoder)new ByteArrayDecoder());
            cc.decoder((Decoder)new Jackson2JsonDecoder());
            cc.decoder((Decoder)StringDecoder.allMimeTypes());
            cc.encoder((Encoder)new ByteArrayEncoder());
            cc.encoder((Encoder)new Jackson2JsonEncoder());
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
                        UriComponents uriComponents = UriComponentsBuilder.fromUri((URI)uri).scheme(endpoint.getScheme()).host(endpoint.getHost()).port(endpoint.getPort()).replacePath(endpoint.getPath()).path(VaultClients.normalizePath(endpoint.getPath(), uri.getPath())).build();
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
        Assert.hasText((String)namespace, (String)"Vault Namespace must not be empty!");
        return ExchangeFilterFunction.ofRequestProcessor(request -> Mono.fromSupplier(() -> ClientRequest.from((ClientRequest)request).headers(headers -> {
            if (!headers.containsKey((Object)"X-Vault-Namespace")) {
                headers.add("X-Vault-Namespace", namespace);
            }
        }).build()));
    }

    public static ReactiveVaultEndpointProvider wrap(VaultEndpointProvider endpointProvider) {
        Assert.notNull((Object)endpointProvider, (String)"VaultEndpointProvider must not be null");
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

