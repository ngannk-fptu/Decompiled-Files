/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.nio.protocol;

import org.apache.http.nio.protocol.HttpAsyncRequestHandler;

@Deprecated
public interface HttpAsyncRequestHandlerResolver {
    public HttpAsyncRequestHandler<?> lookup(String var1);
}

