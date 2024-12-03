/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  javax.xml.ws.WebServiceContext
 */
package com.sun.xml.ws.api.server;

import com.sun.istack.NotNull;
import com.sun.xml.ws.api.server.AsyncProviderCallback;
import javax.xml.ws.WebServiceContext;

public interface AsyncProvider<T> {
    public void invoke(@NotNull T var1, @NotNull AsyncProviderCallback<T> var2, @NotNull WebServiceContext var3);
}

