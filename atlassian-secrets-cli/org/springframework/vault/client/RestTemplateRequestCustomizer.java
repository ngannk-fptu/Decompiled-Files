/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.client;

import org.springframework.http.client.ClientHttpRequest;

@FunctionalInterface
public interface RestTemplateRequestCustomizer<T extends ClientHttpRequest> {
    public void customize(T var1);
}

