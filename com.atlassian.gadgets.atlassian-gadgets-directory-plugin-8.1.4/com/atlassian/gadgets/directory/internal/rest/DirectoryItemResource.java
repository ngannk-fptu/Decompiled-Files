/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.gadgets.GadgetRequestContext
 *  com.atlassian.gadgets.GadgetRequestContextFactory
 *  com.atlassian.gadgets.directory.Directory
 *  com.atlassian.gadgets.directory.Directory$EntryScope
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.rest.common.security.AnonymousSiteAccess
 *  com.atlassian.sal.api.user.UserManager
 *  javax.servlet.http.HttpServletRequest
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Context
 *  javax.ws.rs.core.Response
 */
package com.atlassian.gadgets.directory.internal.rest;

import com.atlassian.gadgets.GadgetRequestContext;
import com.atlassian.gadgets.GadgetRequestContextFactory;
import com.atlassian.gadgets.directory.Directory;
import com.atlassian.gadgets.directory.internal.jaxb.DashboardItemRepresentationFactory;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.rest.common.security.AnonymousSiteAccess;
import com.atlassian.sal.api.user.UserManager;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

@Path(value="/directoryitems")
@AnonymousSiteAccess
public class DirectoryItemResource {
    private final Directory directory;
    private final GadgetRequestContextFactory gadgetReqCtxFactory;
    private final DashboardItemRepresentationFactory dashboardItemRepresentationFactory;
    private final UserManager userManager;

    public DirectoryItemResource(Directory directory, @ComponentImport GadgetRequestContextFactory gadgetReqCtxFactory, @ComponentImport UserManager userManager, DashboardItemRepresentationFactory dashboardItemRepresentationFactory) {
        this.directory = directory;
        this.gadgetReqCtxFactory = gadgetReqCtxFactory;
        this.dashboardItemRepresentationFactory = dashboardItemRepresentationFactory;
        this.userManager = userManager;
    }

    @GET
    @Path(value="/")
    @Produces(value={"application/xml", "application/json"})
    public Response getAllDashboardItems(@Context HttpServletRequest request) {
        return this.getDashboardItems(request, Directory.EntryScope.ALL);
    }

    @GET
    @Path(value="/local")
    @Produces(value={"application/xml", "application/json"})
    public Response getLocalDashboardItems(@Context HttpServletRequest request) {
        return this.getDashboardItems(request, Directory.EntryScope.LOCAL);
    }

    @GET
    @Path(value="/external")
    @Produces(value={"application/xml", "application/json"})
    public Response getExternalDashboardItems(@Context HttpServletRequest request) {
        return this.getDashboardItems(request, Directory.EntryScope.EXTERNAL);
    }

    private Response getDashboardItems(HttpServletRequest request, Directory.EntryScope entryScope) {
        GadgetRequestContext gadgetReqCtx = this.gadgetReqCtxFactory.get(request);
        Iterable entries = this.directory.getEntries(gadgetReqCtx, entryScope);
        Iterable items = StreamSupport.stream(entries.spliterator(), false).map(this.dashboardItemRepresentationFactory::createDashboardItemRepresentation).collect(Collectors.toList());
        return Response.ok((Object)items).build();
    }
}

