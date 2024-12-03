/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi;

import com.hazelcast.spi.WaitNotifyKey;

public interface BlockingOperation {
    public WaitNotifyKey getWaitKey();

    public boolean shouldWait();

    public long getWaitTimeout();

    public void onWaitExpire();
}

