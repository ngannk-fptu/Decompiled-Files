/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.authentication.impl.web;

import com.atlassian.plugins.authentication.impl.web.SessionData;

public interface SessionDataCache {
    public void put(String var1, SessionData var2);

    public SessionData get(String var1);

    public void remove(String var1);
}

