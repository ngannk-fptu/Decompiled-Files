/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi;

import com.hazelcast.spi.Operation;

public interface OperationResponseHandler<O extends Operation> {
    public void sendResponse(O var1, Object var2);
}

