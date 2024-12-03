/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http2.impl;

import java.util.concurrent.atomic.AtomicLong;
import org.apache.hc.core5.http.impl.BasicHttpTransportMetrics;
import org.apache.hc.core5.http2.H2TransportMetrics;

public class BasicH2TransportMetrics
extends BasicHttpTransportMetrics
implements H2TransportMetrics {
    private final AtomicLong framesTransferred = new AtomicLong(0L);

    @Override
    public long getFramesTransferred() {
        return this.framesTransferred.get();
    }

    public void incrementFramesTransferred() {
        this.framesTransferred.incrementAndGet();
    }
}

