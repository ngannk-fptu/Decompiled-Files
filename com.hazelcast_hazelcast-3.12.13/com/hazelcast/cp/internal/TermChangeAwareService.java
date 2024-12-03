/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal;

import com.hazelcast.cp.CPGroupId;

public interface TermChangeAwareService {
    public void onNewTermCommit(CPGroupId var1, long var2);
}

