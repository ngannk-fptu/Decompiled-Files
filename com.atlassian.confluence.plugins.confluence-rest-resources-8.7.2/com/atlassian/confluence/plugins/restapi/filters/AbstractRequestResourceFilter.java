/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jersey.spi.container.ContainerResponseFilter
 *  com.sun.jersey.spi.container.ResourceFilter
 */
package com.atlassian.confluence.plugins.restapi.filters;

import com.sun.jersey.spi.container.ContainerResponseFilter;
import com.sun.jersey.spi.container.ResourceFilter;

public abstract class AbstractRequestResourceFilter
implements ResourceFilter {
    public final ContainerResponseFilter getResponseFilter() {
        return null;
    }
}

