/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http;

public interface HttpConnectionMetrics {
    public long getRequestCount();

    public long getResponseCount();

    public long getSentBytesCount();

    public long getReceivedBytesCount();
}

