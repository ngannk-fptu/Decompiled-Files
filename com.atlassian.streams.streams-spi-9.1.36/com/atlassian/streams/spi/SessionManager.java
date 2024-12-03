/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Supplier
 */
package com.atlassian.streams.spi;

import com.google.common.base.Supplier;

public interface SessionManager {
    public <T> T withSession(Supplier<T> var1);
}

