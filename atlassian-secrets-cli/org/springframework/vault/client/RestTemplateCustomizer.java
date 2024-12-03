/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.client;

import org.springframework.web.client.RestTemplate;

@FunctionalInterface
public interface RestTemplateCustomizer {
    public void customize(RestTemplate var1);
}

