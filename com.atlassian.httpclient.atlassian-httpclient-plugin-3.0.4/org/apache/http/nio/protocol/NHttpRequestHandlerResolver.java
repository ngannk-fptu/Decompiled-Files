/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.nio.protocol;

import org.apache.http.nio.protocol.NHttpRequestHandler;

@Deprecated
public interface NHttpRequestHandlerResolver {
    public NHttpRequestHandler lookup(String var1);
}

