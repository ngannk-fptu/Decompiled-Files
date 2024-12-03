/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.web.reactive.function.client.WebClient
 */
package org.springframework.vault.config;

import java.util.function.Consumer;
import java.util.function.Function;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.lang.Nullable;
import org.springframework.vault.client.WebClientBuilder;
import org.springframework.vault.client.WebClientFactory;
import org.springframework.web.reactive.function.client.WebClient;

class DefaultWebClientFactory
implements WebClientFactory {
    private final ClientHttpConnector connector;
    private final Function<ClientHttpConnector, WebClientBuilder> builderFunction;

    DefaultWebClientFactory(ClientHttpConnector connector, Function<ClientHttpConnector, WebClientBuilder> builderFunction) {
        this.connector = connector;
        this.builderFunction = builderFunction;
    }

    @Override
    public WebClient create(@Nullable Consumer<WebClientBuilder> customizer2) {
        WebClientBuilder builder = this.builderFunction.apply(this.connector);
        if (customizer2 != null) {
            customizer2.accept(builder);
        }
        return builder.build();
    }
}

