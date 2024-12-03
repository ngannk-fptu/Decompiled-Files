/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.impl.container.filter;

import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.api.model.AbstractMethod;
import com.sun.jersey.core.spi.component.ProviderServices;
import com.sun.jersey.server.impl.container.filter.AnnotationResourceFilterFactory;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import com.sun.jersey.spi.container.ResourceFilter;
import com.sun.jersey.spi.container.ResourceFilterFactory;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

public final class FilterFactory {
    private static final Logger LOGGER = Logger.getLogger(FilterFactory.class.getName());
    private final ProviderServices providerServices;
    private final List<ContainerRequestFilter> requestFilters = new LinkedList<ContainerRequestFilter>();
    private final List<ContainerResponseFilter> responseFilters = new LinkedList<ContainerResponseFilter>();
    private final List<ResourceFilterFactory> resourceFilterFactories = new LinkedList<ResourceFilterFactory>();

    public FilterFactory(ProviderServices providerServices) {
        this.providerServices = providerServices;
    }

    public void init(ResourceConfig resourceConfig) {
        this.requestFilters.addAll(this.getFilters(ContainerRequestFilter.class, resourceConfig.getContainerRequestFilters()));
        this.requestFilters.addAll(this.providerServices.getServices(ContainerRequestFilter.class));
        this.responseFilters.addAll(this.getFilters(ContainerResponseFilter.class, resourceConfig.getContainerResponseFilters()));
        this.responseFilters.addAll(this.providerServices.getServices(ContainerResponseFilter.class));
        this.resourceFilterFactories.addAll(this.getFilters(ResourceFilterFactory.class, resourceConfig.getResourceFilterFactories()));
        this.resourceFilterFactories.addAll(this.providerServices.getServices(ResourceFilterFactory.class));
        this.resourceFilterFactories.add(new AnnotationResourceFilterFactory(this));
    }

    public List<ContainerRequestFilter> getRequestFilters() {
        return this.requestFilters;
    }

    public List<ContainerResponseFilter> getResponseFilters() {
        return this.responseFilters;
    }

    public List<ResourceFilter> getResourceFilters(AbstractMethod am) {
        LinkedList<ResourceFilter> resourceFilters = new LinkedList<ResourceFilter>();
        for (ResourceFilterFactory rff : this.resourceFilterFactories) {
            List<ResourceFilter> rfs = rff.create(am);
            if (rfs == null) continue;
            resourceFilters.addAll(rfs);
        }
        return resourceFilters;
    }

    public List<ResourceFilter> getResourceFilters(Class<? extends ResourceFilter>[] classes) {
        if (classes == null || classes.length == 0) {
            return Collections.EMPTY_LIST;
        }
        return this.providerServices.getInstances(ResourceFilter.class, classes);
    }

    public static List<ContainerRequestFilter> getRequestFilters(List<ResourceFilter> resourceFilters) {
        LinkedList<ContainerRequestFilter> filters = new LinkedList<ContainerRequestFilter>();
        for (ResourceFilter rf : resourceFilters) {
            ContainerRequestFilter crf = rf.getRequestFilter();
            if (crf == null) continue;
            filters.add(crf);
        }
        return filters;
    }

    public static List<ContainerResponseFilter> getResponseFilters(List<ResourceFilter> resourceFilters) {
        LinkedList<ContainerResponseFilter> filters = new LinkedList<ContainerResponseFilter>();
        for (ResourceFilter rf : resourceFilters) {
            ContainerResponseFilter crf = rf.getResponseFilter();
            if (crf == null) continue;
            filters.add(crf);
        }
        return filters;
    }

    private <T> List<T> getFilters(Class<T> c, List<?> l) {
        LinkedList<T> f = new LinkedList<T>();
        for (Object o : l) {
            if (o instanceof String) {
                f.addAll(this.providerServices.getInstances(c, ResourceConfig.getElements(new String[]{(String)o}, " ,;\n")));
                continue;
            }
            if (o instanceof String[]) {
                f.addAll(this.providerServices.getInstances(c, ResourceConfig.getElements((String[])o, " ,;\n")));
                continue;
            }
            if (c.isInstance(o)) {
                f.add(c.cast(o));
                continue;
            }
            if (o instanceof Class) {
                Class fc = (Class)o;
                if (c.isAssignableFrom(fc)) {
                    f.addAll(this.providerServices.getInstances(c, new Class[]{fc}));
                    continue;
                }
                LOGGER.severe("The filter, of type" + o.getClass().getName() + ", MUST be of the type Class<? extends" + c.getName() + ">. The filter is ignored.");
                continue;
            }
            LOGGER.severe("The filter, of type" + o.getClass().getName() + ", MUST be of the type String, String[], Class<? extends " + c.getName() + ">, or an instance of " + c.getName() + ". The filter is ignored.");
        }
        this.providerServices.getComponentProviderFactory().injectOnProviderInstances(f);
        return f;
    }
}

