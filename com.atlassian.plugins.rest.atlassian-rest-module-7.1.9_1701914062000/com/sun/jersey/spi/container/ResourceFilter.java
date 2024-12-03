/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.spi.container;

import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.spi.container.ContainerResponseFilter;

public interface ResourceFilter {
    public ContainerRequestFilter getRequestFilter();

    public ContainerResponseFilter getResponseFilter();
}

