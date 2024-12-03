/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.web.client.RestTemplate
 */
package org.springframework.vault.client;

import java.util.function.Consumer;
import org.springframework.vault.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

@FunctionalInterface
public interface RestTemplateFactory {
    default public RestTemplate create() {
        return this.create(builder -> {});
    }

    public RestTemplate create(Consumer<RestTemplateBuilder> var1);
}

