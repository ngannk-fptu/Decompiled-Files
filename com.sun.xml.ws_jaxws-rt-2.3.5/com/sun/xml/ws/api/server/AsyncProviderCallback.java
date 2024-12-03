/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 */
package com.sun.xml.ws.api.server;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;

public interface AsyncProviderCallback<T> {
    public void send(@Nullable T var1);

    public void sendError(@NotNull Throwable var1);
}

