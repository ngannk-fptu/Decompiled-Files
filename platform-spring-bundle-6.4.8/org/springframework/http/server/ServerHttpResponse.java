/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.http.server;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.HttpStatus;

public interface ServerHttpResponse
extends HttpOutputMessage,
Flushable,
Closeable {
    public void setStatusCode(HttpStatus var1);

    @Override
    public void flush() throws IOException;

    @Override
    public void close();
}

