/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.http.HttpHeaders
 *  org.springframework.http.HttpMethod
 *  org.springframework.http.client.AbstractClientHttpRequestFactoryWrapper
 *  org.springframework.http.client.ClientHttpRequest
 *  org.springframework.http.client.ClientHttpRequestFactory
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.web.client.ResponseErrorHandler
 *  org.springframework.web.client.RestTemplate
 */
package org.springframework.vault.client;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.AbstractClientHttpRequestFactoryWrapper;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.vault.client.ClientHttpRequestFactoryFactory;
import org.springframework.vault.client.RestTemplateCustomizer;
import org.springframework.vault.client.RestTemplateRequestCustomizer;
import org.springframework.vault.client.SimpleVaultEndpointProvider;
import org.springframework.vault.client.VaultClients;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.client.VaultEndpointProvider;
import org.springframework.vault.support.ClientOptions;
import org.springframework.vault.support.SslConfiguration;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

public class RestTemplateBuilder {
    @Nullable
    private VaultEndpointProvider endpointProvider;
    private Supplier<ClientHttpRequestFactory> requestFactory = () -> ClientHttpRequestFactoryFactory.create(new ClientOptions(), SslConfiguration.unconfigured());
    @Nullable
    private ResponseErrorHandler errorHandler;
    private final Map<String, String> defaultHeaders = new LinkedHashMap<String, String>();
    private final List<RestTemplateCustomizer> customizers = new ArrayList<RestTemplateCustomizer>();
    private final Set<RestTemplateRequestCustomizer<ClientHttpRequest>> requestCustomizers = new LinkedHashSet<RestTemplateRequestCustomizer<ClientHttpRequest>>();

    private RestTemplateBuilder() {
    }

    public static RestTemplateBuilder builder() {
        return new RestTemplateBuilder();
    }

    public RestTemplateBuilder endpoint(VaultEndpoint endpoint) {
        return this.endpointProvider(SimpleVaultEndpointProvider.of(endpoint));
    }

    public RestTemplateBuilder endpointProvider(VaultEndpointProvider provider) {
        Assert.notNull((Object)provider, (String)"VaultEndpointProvider must not be null");
        this.endpointProvider = provider;
        return this;
    }

    public RestTemplateBuilder requestFactory(ClientHttpRequestFactory requestFactory) {
        Assert.notNull((Object)requestFactory, (String)"ClientHttpRequestFactory must not be null");
        return this.requestFactory(() -> requestFactory);
    }

    public RestTemplateBuilder requestFactory(Supplier<ClientHttpRequestFactory> requestFactory) {
        Assert.notNull(requestFactory, (String)"Supplier of ClientHttpRequestFactory must not be null");
        this.requestFactory = requestFactory;
        return this;
    }

    public RestTemplateBuilder errorHandler(ResponseErrorHandler errorHandler) {
        Assert.notNull((Object)errorHandler, (String)"ErrorHandler must not be null");
        this.errorHandler = errorHandler;
        return this;
    }

    public RestTemplateBuilder defaultHeader(String name, String value) {
        Assert.hasText((String)name, (String)"Header name must not be null or empty");
        this.defaultHeaders.put(name, value);
        return this;
    }

    public RestTemplateBuilder customizers(RestTemplateCustomizer ... customizer) {
        this.customizers.addAll(Arrays.asList(customizer));
        return this;
    }

    public RestTemplateBuilder requestCustomizers(RestTemplateRequestCustomizer<?> ... requestCustomizers) {
        Assert.notNull(requestCustomizers, (String)"RequestCustomizers must not be null");
        this.requestCustomizers.addAll(Arrays.asList(requestCustomizers));
        return this;
    }

    public RestTemplate build() {
        Assert.state((this.endpointProvider != null ? 1 : 0) != 0, (String)"VaultEndpointProvider must not be null");
        RestTemplate restTemplate = this.createTemplate();
        if (this.errorHandler != null) {
            restTemplate.setErrorHandler(this.errorHandler);
        }
        this.customizers.forEach(customizer -> customizer.customize(restTemplate));
        return restTemplate;
    }

    protected RestTemplate createTemplate() {
        ClientHttpRequestFactory requestFactory = this.requestFactory.get();
        LinkedHashMap<String, String> defaultHeaders = new LinkedHashMap<String, String>(this.defaultHeaders);
        LinkedHashSet<RestTemplateRequestCustomizer<ClientHttpRequest>> requestCustomizers = new LinkedHashSet<RestTemplateRequestCustomizer<ClientHttpRequest>>(this.requestCustomizers);
        RestTemplate restTemplate = VaultClients.createRestTemplate(this.endpointProvider, (ClientHttpRequestFactory)new RestTemplateBuilderClientHttpRequestFactoryWrapper(requestFactory, requestCustomizers));
        restTemplate.getInterceptors().add((httpRequest, bytes, clientHttpRequestExecution) -> {
            HttpHeaders headers = httpRequest.getHeaders();
            defaultHeaders.forEach((key, value) -> {
                if (!headers.containsKey(key)) {
                    headers.add(key, value);
                }
            });
            return clientHttpRequestExecution.execute(httpRequest, bytes);
        });
        return restTemplate;
    }

    static class RestTemplateBuilderClientHttpRequestFactoryWrapper
    extends AbstractClientHttpRequestFactoryWrapper {
        private final Set<RestTemplateRequestCustomizer<ClientHttpRequest>> requestCustomizers;

        RestTemplateBuilderClientHttpRequestFactoryWrapper(ClientHttpRequestFactory requestFactory, Set<RestTemplateRequestCustomizer<ClientHttpRequest>> requestCustomizers) {
            super(requestFactory);
            this.requestCustomizers = requestCustomizers;
        }

        protected ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod, ClientHttpRequestFactory requestFactory) throws IOException {
            ClientHttpRequest request = requestFactory.createRequest(uri, httpMethod);
            this.requestCustomizers.forEach(it -> it.customize(request));
            return request;
        }
    }
}

