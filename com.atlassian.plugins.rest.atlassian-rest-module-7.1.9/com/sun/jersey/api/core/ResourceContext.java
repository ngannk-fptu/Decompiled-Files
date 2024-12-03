/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.api.core;

import com.sun.jersey.api.container.ContainerException;
import com.sun.jersey.api.core.ExtendedUriInfo;
import java.net.URI;

public interface ResourceContext {
    public ExtendedUriInfo matchUriInfo(URI var1) throws ContainerException;

    public Object matchResource(URI var1) throws ContainerException;

    public <T> T matchResource(URI var1, Class<T> var2) throws ContainerException, ClassCastException;

    public <T> T getResource(Class<T> var1) throws ContainerException;
}

