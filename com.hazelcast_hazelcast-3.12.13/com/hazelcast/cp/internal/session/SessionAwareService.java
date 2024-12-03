/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.session;

import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.internal.session.SessionAccessor;
import java.util.Collection;

public interface SessionAwareService {
    public void setSessionAccessor(SessionAccessor var1);

    public void onSessionClose(CPGroupId var1, long var2);

    public Collection<Long> getAttachedSessions(CPGroupId var1);
}

