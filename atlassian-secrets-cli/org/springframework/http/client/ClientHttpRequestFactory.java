/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.http.client;

import java.io.IOException;
import java.net.URI;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;

@FunctionalInterface
public interface ClientHttpRequestFactory {
    public ClientHttpRequest createRequest(URI var1, HttpMethod var2) throws IOException;
}

