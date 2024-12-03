/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.impl.container.filter;

import com.sun.jersey.api.model.AbstractMethod;
import com.sun.jersey.server.impl.container.filter.FilterFactory;
import com.sun.jersey.spi.container.ResourceFilter;
import com.sun.jersey.spi.container.ResourceFilterFactory;
import com.sun.jersey.spi.container.ResourceFilters;
import java.util.List;

public final class AnnotationResourceFilterFactory
implements ResourceFilterFactory {
    private FilterFactory ff;

    public AnnotationResourceFilterFactory(FilterFactory ff) {
        this.ff = ff;
    }

    @Override
    public List<ResourceFilter> create(AbstractMethod am) {
        ResourceFilters rfs = am.getAnnotation(ResourceFilters.class);
        if (rfs == null) {
            rfs = am.getResource().getAnnotation(ResourceFilters.class);
        }
        if (rfs == null) {
            return null;
        }
        return this.getResourceFilters(rfs.value());
    }

    private List<ResourceFilter> getResourceFilters(Class<? extends ResourceFilter>[] classes) {
        if (classes == null || classes.length == 0) {
            return null;
        }
        return this.ff.getResourceFilters(classes);
    }
}

