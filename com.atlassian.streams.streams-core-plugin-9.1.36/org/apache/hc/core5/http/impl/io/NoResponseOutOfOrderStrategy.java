/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http.impl.io;

import java.io.InputStream;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.io.HttpClientConnection;
import org.apache.hc.core5.http.io.ResponseOutOfOrderStrategy;

@Contract(threading=ThreadingBehavior.IMMUTABLE)
public final class NoResponseOutOfOrderStrategy
implements ResponseOutOfOrderStrategy {
    public static final NoResponseOutOfOrderStrategy INSTANCE = new NoResponseOutOfOrderStrategy();

    @Override
    public boolean isEarlyResponseDetected(ClassicHttpRequest request, HttpClientConnection connection, InputStream inputStream, long totalBytesSent, long nextWriteSize) {
        return false;
    }
}

