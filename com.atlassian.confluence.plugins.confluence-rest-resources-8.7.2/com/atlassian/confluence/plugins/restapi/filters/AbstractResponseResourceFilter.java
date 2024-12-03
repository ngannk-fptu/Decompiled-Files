/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jersey.spi.container.ContainerRequestFilter
 *  com.sun.jersey.spi.container.ResourceFilter
 */
package com.atlassian.confluence.plugins.restapi.filters;

import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.spi.container.ResourceFilter;

public abstract class AbstractResponseResourceFilter
implements ResourceFilter {
    public final ContainerRequestFilter getRequestFilter() {
        return null;
    }
}

