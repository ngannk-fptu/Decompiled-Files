/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.http.client;

import java.io.IOException;
import java.net.URI;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.AsyncClientHttpRequest;

@Deprecated
public interface AsyncClientHttpRequestFactory {
    public AsyncClientHttpRequest createAsyncRequest(URI var1, HttpMethod var2) throws IOException;
}

