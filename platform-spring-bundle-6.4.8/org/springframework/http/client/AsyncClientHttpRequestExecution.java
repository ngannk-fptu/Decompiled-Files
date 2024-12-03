/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.http.client;

import java.io.IOException;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.concurrent.ListenableFuture;

@Deprecated
public interface AsyncClientHttpRequestExecution {
    public ListenableFuture<ClientHttpResponse> executeAsync(HttpRequest var1, byte[] var2) throws IOException;
}

