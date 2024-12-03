/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.web.reactive.function.client.WebClient
 */
package org.springframework.vault.client;

import java.util.function.Consumer;
import org.springframework.vault.client.WebClientBuilder;
import org.springframework.web.reactive.function.client.WebClient;

@FunctionalInterface
public interface WebClientFactory {
    default public WebClient create() {
        return this.create(builder -> {});
    }

    public WebClient create(Consumer<WebClientBuilder> var1);
}

