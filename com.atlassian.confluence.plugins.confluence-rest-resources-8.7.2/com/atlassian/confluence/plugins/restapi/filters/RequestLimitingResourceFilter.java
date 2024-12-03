/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jersey.spi.container.ContainerRequestFilter
 */
package com.atlassian.confluence.plugins.restapi.filters;

import com.atlassian.confluence.plugins.restapi.filters.AbstractRequestResourceFilter;
import com.atlassian.confluence.plugins.restapi.filters.LimitingRequestFilter;
import com.sun.jersey.spi.container.ContainerRequestFilter;

public class RequestLimitingResourceFilter
extends AbstractRequestResourceFilter {
    private final long defaultValue;

    public RequestLimitingResourceFilter(long defaultValue) {
        this.defaultValue = defaultValue;
    }

    public ContainerRequestFilter getRequestFilter() {
        return new LimitingRequestFilter(this.defaultValue);
    }
}

