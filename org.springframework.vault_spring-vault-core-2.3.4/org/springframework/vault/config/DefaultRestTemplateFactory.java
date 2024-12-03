/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.http.client.ClientHttpRequestFactory
 *  org.springframework.lang.Nullable
 *  org.springframework.web.client.RestTemplate
 */
package org.springframework.vault.config;

import java.util.function.Consumer;
import java.util.function.Function;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.lang.Nullable;
import org.springframework.vault.client.RestTemplateBuilder;
import org.springframework.vault.client.RestTemplateFactory;
import org.springframework.web.client.RestTemplate;

class DefaultRestTemplateFactory
implements RestTemplateFactory {
    private final ClientHttpRequestFactory requestFactory;
    private final Function<ClientHttpRequestFactory, RestTemplateBuilder> builderFunction;

    DefaultRestTemplateFactory(ClientHttpRequestFactory requestFactory, Function<ClientHttpRequestFactory, RestTemplateBuilder> builderFunction) {
        this.requestFactory = requestFactory;
        this.builderFunction = builderFunction;
    }

    @Override
    public RestTemplate create(@Nullable Consumer<RestTemplateBuilder> customizer) {
        RestTemplateBuilder builder = this.builderFunction.apply(this.requestFactory);
        if (customizer != null) {
            customizer.accept(builder);
        }
        return builder.build();
    }
}

