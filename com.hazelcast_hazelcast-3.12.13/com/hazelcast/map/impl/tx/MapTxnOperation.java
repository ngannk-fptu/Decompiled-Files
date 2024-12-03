/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.tx;

import com.hazelcast.spi.Notifier;

public interface MapTxnOperation
extends Notifier {
    public void setVersion(long var1);

    public long getVersion();

    public void setThreadId(long var1);

    public void setOwnerUuid(String var1);
}

