/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.internal.user;

public interface UserIndexingManagerInternal {
    public boolean shouldProcessEvents();

    public void setProcessEventsForCurrentThread(boolean var1);
}

