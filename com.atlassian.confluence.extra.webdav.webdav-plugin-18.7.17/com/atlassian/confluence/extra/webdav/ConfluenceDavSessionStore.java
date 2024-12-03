/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.confluence.extra.webdav;

import com.atlassian.confluence.extra.webdav.ConfluenceDavSession;
import com.atlassian.confluence.extra.webdav.ConfluenceDavSessionTask;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface ConfluenceDavSessionStore {
    public void mapSession(ConfluenceDavSession var1, String var2);

    @Nullable
    public ConfluenceDavSession getSession(String var1);

    public void invalidateExpiredSessions();

    public void executeTaskOnSessions(ConfluenceDavSessionTask var1);
}

