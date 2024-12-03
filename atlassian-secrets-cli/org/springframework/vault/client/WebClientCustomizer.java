/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.web.reactive.function.client.WebClient$Builder
 */
package org.springframework.vault.client;

import org.springframework.web.reactive.function.client.WebClient;

@FunctionalInterface
public interface WebClientCustomizer {
    public void customize(WebClient.Builder var1);
}

