/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.streams.spi.SessionManager
 *  com.google.common.base.Supplier
 */
package com.atlassian.streams.common;

import com.atlassian.streams.spi.SessionManager;
import com.google.common.base.Supplier;

public final class PassThruSessionManager
implements SessionManager {
    public <T> T withSession(Supplier<T> s) {
        return (T)s.get();
    }
}

