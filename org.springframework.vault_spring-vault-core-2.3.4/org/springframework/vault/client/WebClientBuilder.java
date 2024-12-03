/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.http.client.reactive.ClientHttpConnector
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.web.reactive.function.client.ClientRequest
 *  org.springframework.web.reactive.function.client.ExchangeFilterFunction
 *  org.springframework.web.reactive.function.client.WebClient
 *  org.springframework.web.reactive.function.client.WebClient$Builder
 */
package org.springframework.vault.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.vault.client.ClientHttpConnectorFactory;
import org.springframework.vault.client.ReactiveVaultClients;
import org.springframework.vault.client.ReactiveVaultEndpointProvider;
import org.springframework.vault.client.SimpleVaultEndpointProvider;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.client.VaultEndpointProvider;
import org.springframework.vault.client.WebClientCustomizer;
import org.springframework.vault.support.ClientOptions;
import org.springframework.vault.support.SslConfiguration;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

public class WebClientBuilder {
    @Nullable
    private ReactiveVaultEndpointProvider endpointProvider;
    private Supplier<ClientHttpConnector> httpConnector = () -> ClientHttpConnectorFactory.create(new ClientOptions(), SslConfiguration.unconfigured());
    private final Map<String, String> defaultHeaders = new LinkedHashMap<String, String>();
    private final List<WebClientCustomizer> customizers = new ArrayList<WebClientCustomizer>();
    private final Set<ExchangeFilterFunction> filterFunctions = new LinkedHashSet<ExchangeFilterFunction>();

    private WebClientBuilder() {
    }

    public static WebClientBuilder builder() {
        return new WebClientBuilder();
    }

    public WebClientBuilder endpoint(VaultEndpoint endpoint) {
        return this.endpointProvider(SimpleVaultEndpointProvider.of(endpoint));
    }

    public WebClientBuilder endpointProvider(VaultEndpointProvider provider) {
        return this.endpointProvider(ReactiveVaultClients.wrap(provider));
    }

    public WebClientBuilder endpointProvider(ReactiveVaultEndpointProvider provider) {
        Assert.notNull((Object)provider, (String)"ReactiveVaultEndpointProvider must not be null");
        this.endpointProvider = provider;
        return this;
    }

    public WebClientBuilder httpConnector(ClientHttpConnector httpConnector) {
        Assert.notNull((Object)httpConnector, (String)"ClientHttpConnector must not be null");
        return this.requestFactory(() -> httpConnector);
    }

    public WebClientBuilder httpConnectorFactory(Supplier<ClientHttpConnector> httpConnector) {
        Assert.notNull(httpConnector, (String)"Supplier of ClientHttpConnector must not be null");
        this.httpConnector = httpConnector;
        return this;
    }

    @Deprecated
    public WebClientBuilder requestFactory(Supplier<ClientHttpConnector> httpConnector) {
        return this.httpConnectorFactory(httpConnector);
    }

    public WebClientBuilder defaultHeader(String name, String value) {
        Assert.hasText((String)name, (String)"Header name must not be null or empty");
        this.defaultHeaders.put(name, value);
        return this;
    }

    public WebClientBuilder customizers(WebClientCustomizer ... customizer) {
        this.customizers.addAll(Arrays.asList(customizer));
        return this;
    }

    public WebClientBuilder filter(ExchangeFilterFunction ... filterFunctions) {
        Assert.notNull((Object)filterFunctions, (String)"ExchangeFilterFunctions must not be null");
        this.filterFunctions.addAll(Arrays.asList(filterFunctions));
        return this;
    }

    public WebClient build() {
        WebClient.Builder builder = this.createWebClientBuilder();
        if (!this.defaultHeaders.isEmpty()) {
            Map<String, String> defaultHeaders = this.defaultHeaders;
            builder.filter((request, next) -> next.exchange(ClientRequest.from((ClientRequest)request).headers(headers -> defaultHeaders.forEach((key, value) -> {
                if (!headers.containsKey(key)) {
                    headers.add(key, value);
                }
            })).build()));
        }
        builder.filters(exchangeFilterFunctions -> exchangeFilterFunctions.addAll(this.filterFunctions));
        this.customizers.forEach(customizer -> customizer.customize(builder));
        return builder.build();
    }

    protected WebClient.Builder createWebClientBuilder() {
        Assert.state((this.endpointProvider != null ? 1 : 0) != 0, (String)"VaultEndpointProvider must not be null");
        ClientHttpConnector connector = this.httpConnector.get();
        return ReactiveVaultClients.createWebClientBuilder(this.endpointProvider, connector);
    }
}

