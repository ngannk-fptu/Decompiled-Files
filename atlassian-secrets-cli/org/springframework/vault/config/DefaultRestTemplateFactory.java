/*
 * Decompiled with CFR 0.152.
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
    public RestTemplate create(@Nullable Consumer<RestTemplateBuilder> customizer2) {
        RestTemplateBuilder builder = this.builderFunction.apply(this.requestFactory);
        if (customizer2 != null) {
            customizer2.accept(builder);
        }
        return builder.build();
    }
}

