/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi;

import com.hazelcast.nio.Address;

public interface CanCancelOperations {
    public boolean cancelOperation(Address var1, long var2);
}

