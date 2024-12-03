/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.client.cache;

import java.io.IOException;

public class HttpCacheEntrySerializationException
extends IOException {
    private static final long serialVersionUID = 9219188365878433519L;

    public HttpCacheEntrySerializationException(String message) {
        super(message);
    }

    public HttpCacheEntrySerializationException(String message, Throwable cause) {
        super(message);
        this.initCause(cause);
    }
}

