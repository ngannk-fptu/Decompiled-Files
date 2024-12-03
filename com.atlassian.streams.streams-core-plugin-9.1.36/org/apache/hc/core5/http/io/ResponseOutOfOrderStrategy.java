/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http.io;

import java.io.IOException;
import java.io.InputStream;
import org.apache.hc.core5.annotation.Internal;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.io.HttpClientConnection;

@Internal
public interface ResponseOutOfOrderStrategy {
    public boolean isEarlyResponseDetected(ClassicHttpRequest var1, HttpClientConnection var2, InputStream var3, long var4, long var6) throws IOException;
}

