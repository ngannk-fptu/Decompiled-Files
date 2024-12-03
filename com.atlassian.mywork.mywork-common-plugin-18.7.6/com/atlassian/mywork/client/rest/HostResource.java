/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.security.XsrfProtectionExcluded
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.host.spi.HostApplication
 *  com.atlassian.fugue.Option
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.google.common.collect.Iterables
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.mywork.client.rest;

import com.atlassian.annotations.security.XsrfProtectionExcluded;
import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.host.spi.HostApplication;
import com.atlassian.fugue.Option;
import com.atlassian.mywork.client.ClientUtil;
import com.atlassian.mywork.rest.CacheControl;
import com.atlassian.mywork.service.HostService;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.google.common.collect.Iterables;
import java.util.Iterator;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path(value="host")
@Produces(value={"application/json"})
public class HostResource {
    private static final Logger log = LoggerFactory.getLogger(HostResource.class);
    private final HostService hostService;
    private final HostApplication hostApplication;

    public HostResource(HostService hostService, HostApplication hostApplication) {
        this.hostService = hostService;
        this.hostApplication = hostApplication;
    }

    @POST
    @Path(value="verifyAuth")
    @XsrfProtectionExcluded
    public Boolean verifyAuth() {
        Iterator iterator = this.hostService.getActiveHost().iterator();
        if (iterator.hasNext()) {
            ApplicationLink appLink = (ApplicationLink)iterator.next();
            return !ClientUtil.credentialsRequired(appLink, this.hostApplication.getId());
        }
        log.warn("User authorisation failed: Could not find an available host");
        return false;
    }

    @GET
    @Path(value="appid")
    @Produces(value={"text/plain"})
    @AnonymousAllowed
    public Response getAppId() {
        Option<ApplicationLink> hosts = this.hostService.getActiveHost();
        String appId = !Iterables.isEmpty(hosts) ? ((ApplicationLink)Iterables.getOnlyElement(hosts)).getId().get() : null;
        return Response.ok(appId).cacheControl(CacheControl.never()).build();
    }
}

