/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.client.cache;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.http.client.cache.HttpCacheEntry;

public interface HttpCacheEntrySerializer {
    public void writeTo(HttpCacheEntry var1, OutputStream var2) throws IOException;

    public HttpCacheEntry readFrom(InputStream var1) throws IOException;
}

