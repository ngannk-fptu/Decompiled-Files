/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.client;

import java.io.IOException;
import java.net.URI;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;

public interface ResponseErrorHandler {
    public boolean hasError(ClientHttpResponse var1) throws IOException;

    public void handleError(ClientHttpResponse var1) throws IOException;

    default public void handleError(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
        this.handleError(response);
    }
}

