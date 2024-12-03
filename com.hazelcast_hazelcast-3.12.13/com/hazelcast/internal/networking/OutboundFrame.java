/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.networking;

public interface OutboundFrame {
    public boolean isUrgent();

    public int getFrameLength();
}

