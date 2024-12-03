/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.accessmode.ReadOnlyAccessBlocked
 *  com.atlassian.confluence.plugins.previews.api.CompanionActionBean
 *  com.atlassian.confluence.plugins.previews.api.CompanionActionEvent
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.sal.api.user.UserManager
 *  com.sun.jersey.api.NotFoundException
 *  javax.servlet.ServletRequest
 *  javax.servlet.http.HttpServletRequest
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Context
 *  javax.ws.rs.core.Response
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.confluence.plugins.previews.rest;

import com.atlassian.confluence.api.service.accessmode.ReadOnlyAccessBlocked;
import com.atlassian.confluence.plugins.previews.api.CompanionActionBean;
import com.atlassian.confluence.plugins.previews.api.CompanionActionEvent;
import com.atlassian.confluence.plugins.previews.jwt.JwtLogHitService;
import com.atlassian.confluence.plugins.previews.model.AttachmentUsageStatusModel;
import com.atlassian.confluence.plugins.previews.model.CompanionAttachmentModel;
import com.atlassian.confluence.plugins.previews.model.CompanionLinkModel;
import com.atlassian.confluence.plugins.previews.model.TempLinksModel;
import com.atlassian.confluence.plugins.previews.service.TempLinksService;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.sun.jersey.api.NotFoundException;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.Optional;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@Path(value="/templinksresource")
public class TempLinksResource {
    private final Logger LOGGER = LoggerFactory.getLogger(TempLinksResource.class);
    @Context
    private HttpServletRequest request;
    private final TempLinksService tempLinksService;
    private final JwtLogHitService jwtLogHitService;
    private final UserManager userManager;
    private final EventPublisher eventPublisher;

    @Autowired
    public TempLinksResource(TempLinksService tempLinksService, JwtLogHitService jwtLogHitService, @ComponentImport UserManager userManager, @ComponentImport EventPublisher eventPublisher) {
        this.tempLinksService = tempLinksService;
        this.jwtLogHitService = jwtLogHitService;
        this.userManager = userManager;
        this.eventPublisher = eventPublisher;
    }

    @GET
    @Path(value="/attachmenturl")
    @Produces(value={"application/json"})
    @ReadOnlyAccessBlocked
    public TempLinksModel getMessage(@QueryParam(value="attachmentId") long attachmentId) {
        Optional<TempLinksModel> model = this.tempLinksService.resolveLinks(attachmentId);
        return model.orElseThrow(NotFoundException::new);
    }

    @GET
    @Path(value="/companion/attachment")
    @Produces(value={"application/json"})
    public CompanionAttachmentModel getAttachmentMeta(@QueryParam(value="attachmentId") long attachmentId) {
        Optional<CompanionAttachmentModel> model = this.tempLinksService.resolveCompanionAttachmentMeta(attachmentId);
        this.jwtLogHitService.logHit((ServletRequest)this.request);
        return model.orElseThrow(NotFoundException::new);
    }

    @GET
    @Path(value="/companion/attachment-usage-status")
    @Produces(value={"application/json"})
    public AttachmentUsageStatusModel getAttachmentUsageStatus(@QueryParam(value="jwtId") String jwtId) {
        UserKey userKey = Objects.requireNonNull(this.userManager.getRemoteUserKey());
        try {
            Optional<Boolean> retVal = this.jwtLogHitService.isInCache(userKey.getStringValue(), jwtId);
            return new AttachmentUsageStatusModel(retVal.orElse(Boolean.FALSE));
        }
        catch (RuntimeException ex) {
            this.LOGGER.error("TempLinksResource.getAttachmentUsageStatus: error checking cache", (Throwable)ex);
            return new AttachmentUsageStatusModel(Boolean.FALSE);
        }
    }

    @GET
    @Path(value="/companion/link")
    @Produces(value={"application/json"})
    @ReadOnlyAccessBlocked
    public CompanionLinkModel getCompanionLink(@QueryParam(value="attachmentId") long attachmentId) throws URISyntaxException {
        Optional<CompanionLinkModel> model = this.tempLinksService.resolveCompanionLink(attachmentId);
        return model.orElseThrow(NotFoundException::new);
    }

    @POST
    @Path(value="/companion/{attachmentId}/action")
    @Consumes(value={"application/json"})
    @Produces(value={"application/json"})
    public Response discardCallback(@PathParam(value="attachmentId") long attachmentId, CompanionActionBean companionActionBean) {
        this.eventPublisher.publish((Object)new CompanionActionEvent((Object)attachmentId, AuthenticatedUserThreadLocal.get(), attachmentId, companionActionBean));
        String action = companionActionBean != null ? companionActionBean.getAction().name() : "unknown";
        this.LOGGER.debug("Published CompanionActionEvent event with attachment id {" + attachmentId + "} and action {" + action + "}");
        return Response.ok().build();
    }
}

