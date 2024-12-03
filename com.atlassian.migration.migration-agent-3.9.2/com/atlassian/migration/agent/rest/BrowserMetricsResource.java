/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  javax.servlet.http.HttpServletRequest
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Context
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.jetbrains.annotations.NotNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.rest;

import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.migration.agent.dto.assessment.BrowserMetricsDto;
import com.atlassian.migration.agent.rest.BrowserMetricsCheckResponse;
import com.atlassian.migration.agent.service.guardrails.BrowserMetricsService;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path(value="browser-metrics")
@Consumes(value={"application/json"})
public final class BrowserMetricsResource {
    private final BrowserMetricsService browserMetricsService;
    private final Logger log = LoggerFactory.getLogger(BrowserMetricsResource.class);

    public BrowserMetricsResource(BrowserMetricsService browserMetricsService) {
        this.browserMetricsService = browserMetricsService;
    }

    @GET
    @Path(value="/enabled")
    @Produces(value={"application/json"})
    @NotNull
    public BrowserMetricsCheckResponse isBrowserMetricsEnabled() {
        ConfluenceUser loggedInUser = AuthenticatedUserThreadLocal.get();
        return new BrowserMetricsCheckResponse(this.browserMetricsService.shouldCollectBrowserMetrics(loggedInUser));
    }

    @POST
    @NotNull
    public Response addBrowserMetrics(@NotNull BrowserMetricsDto browserMetricsDto, @Context HttpServletRequest request) {
        ConfluenceUser loggedInUser = AuthenticatedUserThreadLocal.get();
        browserMetricsDto.setProtocol(request.getProtocol());
        this.browserMetricsService.recordBrowserMetrics(loggedInUser, browserMetricsDto);
        this.log.info("Browser-metrics saved successfully.");
        return Response.status((Response.Status)Response.Status.CREATED).build();
    }
}

