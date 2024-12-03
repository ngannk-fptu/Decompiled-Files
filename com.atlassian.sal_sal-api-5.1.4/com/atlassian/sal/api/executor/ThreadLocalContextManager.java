/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.sal.api.executor;

public interface ThreadLocalContextManager<C> {
    public C getThreadLocalContext();

    public void setThreadLocalContext(C var1);

    public void clearThreadLocalContext();
}

