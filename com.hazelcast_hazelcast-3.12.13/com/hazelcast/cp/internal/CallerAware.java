/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal;

import com.hazelcast.nio.Address;

public interface CallerAware {
    public void setCaller(Address var1, long var2);
}

