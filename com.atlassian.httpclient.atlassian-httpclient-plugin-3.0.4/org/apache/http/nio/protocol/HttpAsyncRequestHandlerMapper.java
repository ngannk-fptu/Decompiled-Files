/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.nio.protocol;

import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncRequestHandler;

public interface HttpAsyncRequestHandlerMapper {
    public HttpAsyncRequestHandler<?> lookup(HttpRequest var1);
}

