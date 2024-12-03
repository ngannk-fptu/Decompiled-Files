/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.client;

import java.net.URI;
import java.util.ArrayList;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.vault.client.SimpleVaultEndpointProvider;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.client.VaultEndpointProvider;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.DefaultUriTemplateHandler;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriBuilderFactory;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

public class VaultClients {
    public static RestTemplate createRestTemplate(VaultEndpoint endpoint, ClientHttpRequestFactory requestFactory) {
        return VaultClients.createRestTemplate(SimpleVaultEndpointProvider.of(endpoint), requestFactory);
    }

    public static RestTemplate createRestTemplate(VaultEndpointProvider endpointProvider, ClientHttpRequestFactory requestFactory) {
        RestTemplate restTemplate = VaultClients.createRestTemplate();
        restTemplate.setRequestFactory(requestFactory);
        restTemplate.setUriTemplateHandler(VaultClients.createUriBuilderFactory(endpointProvider));
        return restTemplate;
    }

    public static RestTemplate createRestTemplate() {
        ArrayList messageConverters = new ArrayList(3);
        messageConverters.add(new ByteArrayHttpMessageConverter());
        messageConverters.add(new StringHttpMessageConverter());
        messageConverters.add(new MappingJackson2HttpMessageConverter());
        RestTemplate restTemplate = new RestTemplate(messageConverters);
        restTemplate.getInterceptors().add((request, body, execution) -> execution.execute(request, body));
        return restTemplate;
    }

    public static ClientHttpRequestInterceptor createNamespaceInterceptor(String namespace) {
        Assert.hasText(namespace, "Vault Namespace must not be empty!");
        return (request, body, execution) -> {
            HttpHeaders headers = request.getHeaders();
            if (!headers.containsKey("X-Vault-Namespace")) {
                headers.add("X-Vault-Namespace", namespace);
            }
            return execution.execute(request, body);
        };
    }

    public static UriBuilderFactory createUriBuilderFactory(VaultEndpointProvider endpointProvider) {
        return new PrefixAwareUriBuilderFactory(endpointProvider);
    }

    private static String toBaseUri(VaultEndpoint endpoint) {
        return String.format("%s://%s:%s/%s", endpoint.getScheme(), endpoint.getHost(), endpoint.getPort(), endpoint.getPath());
    }

    static String prepareUriTemplate(@Nullable String baseUrl, String uriTemplate) {
        if (uriTemplate.startsWith("http:") || uriTemplate.startsWith("https:")) {
            return uriTemplate;
        }
        if (baseUrl != null) {
            return VaultClients.normalizePath(baseUrl, uriTemplate);
        }
        try {
            URI uri = URI.create(uriTemplate);
            if (uri.getHost() != null) {
                return uriTemplate;
            }
        }
        catch (IllegalArgumentException illegalArgumentException) {
            // empty catch block
        }
        if (!uriTemplate.startsWith("/")) {
            return "/" + uriTemplate;
        }
        return uriTemplate;
    }

    static String normalizePath(String prefix, String path) {
        if (path.startsWith("/") && prefix.endsWith("/")) {
            return path.substring(1);
        }
        if (!path.startsWith("/") && !prefix.endsWith("/")) {
            return "/" + path;
        }
        return path;
    }

    public static class PrefixAwareUriBuilderFactory
    extends DefaultUriBuilderFactory {
        private final VaultEndpointProvider endpointProvider;

        public PrefixAwareUriBuilderFactory(VaultEndpointProvider endpointProvider) {
            this.endpointProvider = endpointProvider;
        }

        @Override
        public UriBuilder uriString(String uriTemplate) {
            if (uriTemplate.startsWith("http:") || uriTemplate.startsWith("https:")) {
                return UriComponentsBuilder.fromUriString(uriTemplate);
            }
            VaultEndpoint endpoint = this.endpointProvider.getVaultEndpoint();
            String baseUri = VaultClients.toBaseUri(endpoint);
            UriComponents uriComponents = UriComponentsBuilder.fromUriString(VaultClients.prepareUriTemplate(baseUri, uriTemplate)).build();
            return UriComponentsBuilder.fromUriString(baseUri).uriComponents(uriComponents);
        }
    }

    public static class PrefixAwareUriTemplateHandler
    extends DefaultUriTemplateHandler {
        @Nullable
        private final VaultEndpointProvider endpointProvider;

        public PrefixAwareUriTemplateHandler() {
            this.endpointProvider = null;
        }

        public PrefixAwareUriTemplateHandler(VaultEndpointProvider endpointProvider) {
            this.endpointProvider = endpointProvider;
        }

        @Override
        protected URI expandInternal(String uriTemplate, Map<String, ?> uriVariables) {
            return super.expandInternal(VaultClients.prepareUriTemplate(this.getBaseUrl(), uriTemplate), uriVariables);
        }

        @Override
        protected URI expandInternal(String uriTemplate, Object ... uriVariables) {
            return super.expandInternal(VaultClients.prepareUriTemplate(this.getBaseUrl(), uriTemplate), uriVariables);
        }

        @Override
        public String getBaseUrl() {
            if (this.endpointProvider != null) {
                VaultEndpoint endpoint = this.endpointProvider.getVaultEndpoint();
                return VaultClients.toBaseUri(endpoint);
            }
            return super.getBaseUrl();
        }
    }
}

