/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.session;

import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.cp.session.CPSession;
import java.util.Collection;

public interface CPSessionManagementService {
    public ICompletableFuture<Collection<CPSession>> getAllSessions(String var1);

    public ICompletableFuture<Boolean> forceCloseSession(String var1, long var2);
}

