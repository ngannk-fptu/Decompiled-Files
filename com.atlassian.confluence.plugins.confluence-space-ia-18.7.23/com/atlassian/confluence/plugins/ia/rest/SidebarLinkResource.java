/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.rpc.NotPermittedException
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.ia.rest;

import com.atlassian.confluence.plugins.ia.SidebarLinkCategory;
import com.atlassian.confluence.plugins.ia.rest.SidebarLinkBean;
import com.atlassian.confluence.plugins.ia.service.SidebarLinkService;
import com.atlassian.confluence.rpc.NotPermittedException;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;

@Path(value="link")
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
public class SidebarLinkResource {
    private final SidebarLinkService sidebarLinkService;

    public SidebarLinkResource(SidebarLinkService sidebarLinkService) {
        this.sidebarLinkService = sidebarLinkService;
    }

    @AnonymousAllowed
    @GET
    @Path(value="main")
    public Response getMainLinks(@QueryParam(value="spaceKey") String spaceKey, @QueryParam(value="includeHidden") boolean includeHidden) {
        return Response.ok(this.sidebarLinkService.getLinksForSpace(SidebarLinkCategory.MAIN, spaceKey, includeHidden)).build();
    }

    @AnonymousAllowed
    @GET
    @Path(value="quick")
    public Response getQuickLinks(@QueryParam(value="spaceKey") String spaceKey) {
        return Response.ok(this.sidebarLinkService.getLinksForSpace(SidebarLinkCategory.QUICK, spaceKey, false)).build();
    }

    @AnonymousAllowed
    @GET
    @Path(value="advanced")
    public Response getAdvancedLinks(@QueryParam(value="spaceKey") String spaceKey) {
        return Response.ok(this.sidebarLinkService.getLinksForSpace(SidebarLinkCategory.ADVANCED, spaceKey, false)).build();
    }

    @POST
    @Path(value="{id}/move")
    public Response moveLink(@PathParam(value="id") String id, Map data) {
        String[] split;
        String after = (String)data.get("after");
        if (after != null && (split = after.split("link/")).length == 2) {
            after = split[1];
        }
        try {
            this.sidebarLinkService.move(Integer.parseInt(id), after == null ? null : Integer.valueOf(Integer.parseInt(after)));
            return Response.ok().build();
        }
        catch (NotPermittedException e) {
            return Response.status((Response.Status)Response.Status.FORBIDDEN).entity((Object)e.getMessage()).build();
        }
        catch (NumberFormatException e) {
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)"Couldn't parse sidebar link ID").build();
        }
    }

    @POST
    public Response createLink(Map<String, String> linkData) {
        try {
            SidebarLinkBean link;
            if (linkData.containsKey("resourceType")) {
                String resourceId;
                link = this.sidebarLinkService.create(linkData.get("spaceKey"), linkData.get("resourceType"), (resourceId = linkData.get("resourceId")) == null ? null : Long.valueOf(Long.parseLong(resourceId)), linkData.get("customTitle"), linkData.get("url"));
            } else {
                String pageId = linkData.get("pageId");
                if (StringUtils.isBlank((CharSequence)pageId)) {
                    pageId = linkData.get("resourceId");
                }
                link = this.sidebarLinkService.create(linkData.get("spaceKey"), pageId == null ? null : Long.valueOf(Long.parseLong(pageId)), linkData.get("customTitle"), linkData.get("url"));
            }
            return Response.ok((Object)link).build();
        }
        catch (NotPermittedException e) {
            return Response.status((Response.Status)Response.Status.FORBIDDEN).entity((Object)e.getMessage()).build();
        }
        catch (NumberFormatException e) {
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)"Couldn't parse page ID").build();
        }
        catch (Exception e) {
            return Response.status((Response.Status)Response.Status.INTERNAL_SERVER_ERROR).entity((Object)e.getMessage()).build();
        }
    }

    @DELETE
    @Path(value="{id}")
    public Response delete(@PathParam(value="id") String id) {
        try {
            this.sidebarLinkService.delete(Integer.parseInt(id));
            return Response.ok().build();
        }
        catch (NotPermittedException e) {
            return Response.status((Response.Status)Response.Status.FORBIDDEN).entity((Object)e.getMessage()).build();
        }
        catch (NumberFormatException e) {
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)"Couldn't parse sidebar link ID").build();
        }
    }

    @POST
    @Path(value="{id}/hide")
    public Response hide(@PathParam(value="id") String id) {
        try {
            this.sidebarLinkService.hide(Integer.parseInt(id));
            return Response.ok().build();
        }
        catch (NotPermittedException e) {
            return Response.status((Response.Status)Response.Status.FORBIDDEN).entity((Object)e.getMessage()).build();
        }
        catch (NumberFormatException e) {
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)"Couldn't parse sidebar link ID").build();
        }
    }

    @POST
    @Path(value="{id}/show")
    public Response show(@PathParam(value="id") String id) {
        try {
            this.sidebarLinkService.show(Integer.parseInt(id));
            return Response.ok().build();
        }
        catch (NotPermittedException e) {
            return Response.status((Response.Status)Response.Status.FORBIDDEN).entity((Object)e.getMessage()).build();
        }
        catch (NumberFormatException e) {
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)"Couldn't parse sidebar link ID").build();
        }
    }
}

