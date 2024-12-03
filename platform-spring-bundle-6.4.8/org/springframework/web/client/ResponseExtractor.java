/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.client;

import java.io.IOException;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.Nullable;

@FunctionalInterface
public interface ResponseExtractor<T> {
    @Nullable
    public T extractData(ClientHttpResponse var1) throws IOException;
}

