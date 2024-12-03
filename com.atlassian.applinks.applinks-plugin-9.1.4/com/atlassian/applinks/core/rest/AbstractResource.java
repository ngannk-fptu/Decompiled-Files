/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.spi.link.MutatingApplicationLinkService
 *  com.atlassian.plugins.rest.common.Link
 *  com.atlassian.plugins.rest.common.util.RestUrlBuilder
 *  com.atlassian.sal.api.net.Request
 *  com.atlassian.sal.api.net.RequestFactory
 *  com.atlassian.sal.api.net.Response
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.applinks.core.rest;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.core.InternalTypeAccessor;
import com.atlassian.applinks.core.rest.model.ApplicationLinkEntity;
import com.atlassian.applinks.spi.link.MutatingApplicationLinkService;
import com.atlassian.plugins.rest.common.Link;
import com.atlassian.plugins.rest.common.util.RestUrlBuilder;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.RequestFactory;
import com.atlassian.sal.api.net.Response;
import java.net.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractResource {
    protected final RestUrlBuilder restUrlBuilder;
    protected final Logger LOG = LoggerFactory.getLogger(this.getClass());
    protected final InternalTypeAccessor typeAccessor;
    protected final RequestFactory<Request<Request<?, Response>, Response>> requestFactory;
    protected final MutatingApplicationLinkService applicationLinkService;

    public AbstractResource(RestUrlBuilder restUrlBuilder, InternalTypeAccessor typeAccessor, RequestFactory<Request<Request<?, Response>, Response>> requestFactory, MutatingApplicationLinkService applicationLinkService) {
        this.restUrlBuilder = restUrlBuilder;
        this.typeAccessor = typeAccessor;
        this.requestFactory = requestFactory;
        this.applicationLinkService = applicationLinkService;
    }

    protected final <T> T getUrlFor(URI uri, Class<T> tClass) {
        return (T)this.restUrlBuilder.getUrlFor(uri, tClass);
    }

    protected ApplicationLinkEntity toApplicationLinkEntity(ApplicationLink appLink) {
        return new ApplicationLinkEntity(appLink, this.createSelfLinkFor(appLink.getId()));
    }

    protected Link createSelfLinkFor(ApplicationId appID) {
        return Link.self((URI)this.applicationLinkService.createSelfLinkFor(appID));
    }
}

