/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 */
package com.sun.xml.ws.api.server;

import com.sun.istack.NotNull;
import com.sun.xml.ws.api.server.WSWebServiceContext;
import com.sun.xml.ws.server.DefaultResourceInjector;

public abstract class ResourceInjector {
    public static final ResourceInjector STANDALONE = new DefaultResourceInjector();

    public abstract void inject(@NotNull WSWebServiceContext var1, @NotNull Object var2);
}

