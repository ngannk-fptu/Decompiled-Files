/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.fugue.Option
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.mywork.client.rest;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.fugue.Option;
import com.atlassian.mywork.service.ClientRegistrationService;
import com.atlassian.mywork.service.HostService;
import com.atlassian.mywork.service.ServiceSelector;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path(value="registration")
@Produces(value={"application/json"})
public class RegistrationResource {
    private static final Logger log = LoggerFactory.getLogger(RegistrationResource.class);
    private final ServiceSelector serviceSelector;
    private final ClientRegistrationService clientRegistrationService;
    private final HostService hostService;

    public RegistrationResource(ServiceSelector serviceSelector, ClientRegistrationService clientRegistrationService, HostService hostService) {
        this.serviceSelector = serviceSelector;
        this.clientRegistrationService = clientRegistrationService;
        this.hostService = hostService;
    }

    @GET
    @AnonymousAllowed
    public Response getRegistration(@QueryParam(value="appid") String appid) {
        log.debug("Retrieving registration for appID [{}]", (Object)appid);
        ServiceSelector.Target target = this.serviceSelector.getTarget();
        if (target == ServiceSelector.Target.REMOTE || target == ServiceSelector.Target.AUTO) {
            Option<ApplicationLink> activeHost = this.hostService.getActiveHost();
            if (activeHost.isDefined()) {
                if (((ApplicationLink)activeHost.get()).getId().get().equals(appid)) {
                    return Response.ok(this.clientRegistrationService.createRegistrations()).build();
                }
                log.debug("Active host [{}] doesn't match requested appid [{}]", activeHost.get(), (Object)appid);
            } else {
                log.debug("No active host defined by host service; cannot locate registration for appid [{}]", (Object)appid);
            }
        } else {
            log.debug("Request for registration for appid [{}] not acting as a client; target is [{}]", (Object)appid, (Object)target);
        }
        log.debug("Returning registration-not-found for appid [{}]", (Object)appid);
        return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
    }
}

