/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.http.client;

import org.springframework.http.client.ClientHttpRequest;

@FunctionalInterface
public interface ClientHttpRequestInitializer {
    public void initialize(ClientHttpRequest var1);
}

