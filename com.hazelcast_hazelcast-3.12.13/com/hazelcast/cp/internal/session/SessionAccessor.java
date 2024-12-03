/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.session;

import com.hazelcast.cp.CPGroupId;

public interface SessionAccessor {
    public boolean isActive(CPGroupId var1, long var2);

    public void heartbeat(CPGroupId var1, long var2);
}

