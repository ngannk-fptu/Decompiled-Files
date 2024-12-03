/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.http.client;

import java.io.IOException;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.AsyncClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.concurrent.ListenableFuture;

@Deprecated
public interface AsyncClientHttpRequestInterceptor {
    public ListenableFuture<ClientHttpResponse> intercept(HttpRequest var1, byte[] var2, AsyncClientHttpRequestExecution var3) throws IOException;
}

