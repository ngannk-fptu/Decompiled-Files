/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.http.io.HttpTransportMetrics
 */
package org.apache.hc.core5.http2;

import org.apache.hc.core5.http.io.HttpTransportMetrics;

public interface H2TransportMetrics
extends HttpTransportMetrics {
    public long getFramesTransferred();
}

