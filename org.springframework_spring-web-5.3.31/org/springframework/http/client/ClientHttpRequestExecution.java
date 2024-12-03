/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.http.client;

import java.io.IOException;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpResponse;

@FunctionalInterface
public interface ClientHttpRequestExecution {
    public ClientHttpResponse execute(HttpRequest var1, byte[] var2) throws IOException;
}

