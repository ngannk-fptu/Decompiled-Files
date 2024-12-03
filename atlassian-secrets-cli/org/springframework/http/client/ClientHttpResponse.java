/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.http.client;

import java.io.Closeable;
import java.io.IOException;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpStatus;

public interface ClientHttpResponse
extends HttpInputMessage,
Closeable {
    public HttpStatus getStatusCode() throws IOException;

    public int getRawStatusCode() throws IOException;

    public String getStatusText() throws IOException;

    @Override
    public void close();
}

