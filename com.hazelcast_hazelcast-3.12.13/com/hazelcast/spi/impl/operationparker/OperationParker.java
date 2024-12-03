/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.operationparker;

import com.hazelcast.spi.BlockingOperation;
import com.hazelcast.spi.Notifier;

public interface OperationParker {
    public static final String SERVICE_NAME = "hz:impl:operationParker";

    public void park(BlockingOperation var1);

    public void unpark(Notifier var1);

    public void cancelParkedOperations(String var1, Object var2, Throwable var3);
}

