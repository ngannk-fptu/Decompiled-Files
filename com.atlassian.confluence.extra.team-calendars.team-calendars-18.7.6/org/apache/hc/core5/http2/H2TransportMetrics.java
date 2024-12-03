/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http2;

import org.apache.hc.core5.http.io.HttpTransportMetrics;

public interface H2TransportMetrics
extends HttpTransportMetrics {
    public long getFramesTransferred();
}

