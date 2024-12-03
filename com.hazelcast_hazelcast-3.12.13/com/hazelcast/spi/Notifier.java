/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi;

import com.hazelcast.spi.WaitNotifyKey;

public interface Notifier {
    public boolean shouldNotify();

    public WaitNotifyKey getNotifiedKey();
}

