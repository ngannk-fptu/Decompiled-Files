/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jersey.api.model.AbstractMethod
 *  com.sun.jersey.spi.container.ResourceFilter
 *  com.sun.jersey.spi.container.ResourceFilterFactory
 */
package com.atlassian.confluence.plugins.rest.jersey.cachecontrol;

import com.atlassian.confluence.plugins.rest.jersey.cachecontrol.PreventCachingResponseFilter;
import com.sun.jersey.api.model.AbstractMethod;
import com.sun.jersey.spi.container.ResourceFilter;
import com.sun.jersey.spi.container.ResourceFilterFactory;
import java.util.Collections;
import java.util.List;

public class PreventCachingResourceFilterFactory
implements ResourceFilterFactory {
    private static final List<ResourceFilter> FILTER = Collections.singletonList(PreventCachingResponseFilter.INSTANCE);

    public List<ResourceFilter> create(AbstractMethod abstractMethod) {
        return FILTER;
    }
}

