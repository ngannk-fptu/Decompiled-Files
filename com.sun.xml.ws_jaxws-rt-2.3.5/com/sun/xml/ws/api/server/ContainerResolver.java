/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 */
package com.sun.xml.ws.api.server;

import com.sun.istack.NotNull;
import com.sun.xml.ws.api.server.Container;
import com.sun.xml.ws.api.server.ThreadLocalContainerResolver;

public abstract class ContainerResolver {
    private static final ThreadLocalContainerResolver DEFAULT = new ThreadLocalContainerResolver();
    private static volatile ContainerResolver theResolver = DEFAULT;

    public static void setInstance(ContainerResolver resolver) {
        if (resolver == null) {
            resolver = DEFAULT;
        }
        theResolver = resolver;
    }

    @NotNull
    public static ContainerResolver getInstance() {
        return theResolver;
    }

    public static ThreadLocalContainerResolver getDefault() {
        return DEFAULT;
    }

    @NotNull
    public abstract Container getContainer();
}

