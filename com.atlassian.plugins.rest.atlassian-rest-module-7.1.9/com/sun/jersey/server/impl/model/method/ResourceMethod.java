/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.impl.model.method;

import com.sun.jersey.api.model.AbstractResourceMethod;
import com.sun.jersey.api.uri.UriTemplate;
import com.sun.jersey.core.header.MediaTypes;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import com.sun.jersey.spi.dispatch.RequestDispatcher;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.ws.rs.core.MediaType;

public abstract class ResourceMethod {
    public static final Comparator<ResourceMethod> COMPARATOR = new Comparator<ResourceMethod>(){

        @Override
        public int compare(ResourceMethod o1, ResourceMethod o2) {
            int i = MediaTypes.MEDIA_TYPE_LIST_COMPARATOR.compare(o1.consumeMime, o2.consumeMime);
            if (i == 0) {
                i = MediaTypes.MEDIA_TYPE_LIST_COMPARATOR.compare(o1.produceMime, o2.produceMime);
            }
            return i;
        }
    };
    private final String httpMethod;
    private final UriTemplate template;
    private final List<? extends MediaType> consumeMime;
    private final List<? extends MediaType> produceMime;
    private final boolean isProducesDeclared;
    private final RequestDispatcher dispatcher;
    private final List<ContainerRequestFilter> requestFilters;
    private final List<ContainerResponseFilter> responseFilters;

    public ResourceMethod(String httpMethod, UriTemplate template, List<? extends MediaType> consumeMime, List<? extends MediaType> produceMime, boolean isProducesDeclared, RequestDispatcher dispatcher) {
        this(httpMethod, template, consumeMime, produceMime, isProducesDeclared, dispatcher, Collections.EMPTY_LIST, Collections.EMPTY_LIST);
    }

    public ResourceMethod(String httpMethod, UriTemplate template, List<? extends MediaType> consumeMime, List<? extends MediaType> produceMime, boolean isProducesDeclared, RequestDispatcher dispatcher, List<ContainerRequestFilter> requestFilters, List<ContainerResponseFilter> responseFilters) {
        this.httpMethod = httpMethod;
        this.template = template;
        this.consumeMime = consumeMime;
        this.produceMime = produceMime;
        this.isProducesDeclared = isProducesDeclared;
        this.dispatcher = dispatcher;
        this.requestFilters = requestFilters;
        this.responseFilters = responseFilters;
    }

    public final String getHttpMethod() {
        return this.httpMethod;
    }

    public final UriTemplate getTemplate() {
        return this.template;
    }

    public final List<? extends MediaType> getConsumes() {
        return this.consumeMime;
    }

    public final List<? extends MediaType> getProduces() {
        return this.produceMime;
    }

    public final boolean isProducesDeclared() {
        return this.isProducesDeclared;
    }

    public final RequestDispatcher getDispatcher() {
        return this.dispatcher;
    }

    public final List<ContainerRequestFilter> getRequestFilters() {
        return this.requestFilters;
    }

    public final List<ContainerResponseFilter> getResponseFilters() {
        return this.responseFilters;
    }

    public final boolean consumes(MediaType contentType) {
        for (MediaType mediaType : this.consumeMime) {
            if (mediaType.getType().equals("*")) {
                return true;
            }
            if (!contentType.isCompatible(mediaType)) continue;
            return true;
        }
        return false;
    }

    public final boolean consumesWild() {
        for (MediaType mediaType : this.consumeMime) {
            if (!mediaType.getType().equals("*")) continue;
            return true;
        }
        return false;
    }

    public final boolean mediaEquals(ResourceMethod that) {
        boolean v = this.consumeMime.equals(that.consumeMime);
        if (!v) {
            return false;
        }
        return this.produceMime.equals(that.produceMime);
    }

    public AbstractResourceMethod getAbstractResourceMethod() {
        return null;
    }
}

