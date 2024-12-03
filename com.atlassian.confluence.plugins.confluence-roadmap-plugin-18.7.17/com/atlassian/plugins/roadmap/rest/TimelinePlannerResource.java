/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.plugins.roadmap.rest;

import com.atlassian.plugins.roadmap.BarParam;
import com.atlassian.plugins.roadmap.PageLinkParser;
import com.atlassian.plugins.roadmap.TimelinePlannerJsonBuilder;
import com.atlassian.plugins.roadmap.TimelinePlannerMacroManager;
import com.atlassian.plugins.roadmap.models.RoadmapPageLink;
import com.google.gson.JsonObject;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path(value="/")
public class TimelinePlannerResource {
    private final TimelinePlannerMacroManager timelinePlannerMacroManager;
    private final PageLinkParser linkParser;

    public TimelinePlannerResource(TimelinePlannerMacroManager timelinePlannerMacroManager, PageLinkParser pageLinkParser) {
        this.timelinePlannerMacroManager = timelinePlannerMacroManager;
        this.linkParser = pageLinkParser;
    }

    @GET
    @Consumes(value={"application/json"})
    @Produces(value={"application/json"})
    @Path(value="bar/{barId}/status")
    public Response getBarStatus(@PathParam(value="barId") String barId) {
        TimelinePlannerMacroManager.LinkStatus linkStatus = this.timelinePlannerMacroManager.checkStatus(barId);
        JsonObject json = new JsonObject();
        json.addProperty("status", String.valueOf((Object)linkStatus));
        if (linkStatus == TimelinePlannerMacroManager.LinkStatus.REDEEM) {
            json.addProperty("pageLink", TimelinePlannerJsonBuilder.toJson(this.timelinePlannerMacroManager.getBarPageLink(barId)));
            this.timelinePlannerMacroManager.removeStatus(barId);
            this.timelinePlannerMacroManager.removeBarPageLink(barId);
        }
        return Response.ok((Object)json.toString()).build();
    }

    @PUT
    @Path(value="bar/{barId}/{status}")
    public Response putBarStatus(@PathParam(value="status") String status, @PathParam(value="barId") String barId) {
        this.timelinePlannerMacroManager.put(barId, TimelinePlannerMacroManager.LinkStatus.valueOf(status));
        return Response.ok().build();
    }

    @POST
    @Path(value="bar/pagelink/reset")
    public Response resetBarPageLink(Map context) {
        if (!this.validateBarContextMap(context)) {
            return Response.status((Response.Status)Response.Status.PRECONDITION_FAILED).build();
        }
        this.timelinePlannerMacroManager.updatePagelinkToRoadmapBar(BarParam.fromMap(context), new RoadmapPageLink());
        return Response.ok().build();
    }

    @POST
    @Path(value="bar/pagelink/dolink")
    public Response linkPageToBar(Map context) {
        if (!this.validateBarContextMap(context)) {
            return Response.status((Response.Status)Response.Status.PRECONDITION_FAILED).build();
        }
        String linkedPageId = (String)context.get("linkedPageId");
        this.timelinePlannerMacroManager.updatePagelinkToRoadmapBar(BarParam.fromMap(context), Long.valueOf(linkedPageId));
        return Response.ok().build();
    }

    @POST
    @Path(value="extractPageLinks")
    @Consumes(value={"application/json"})
    @Produces(value={"application/json"})
    public Response extractPageLinks(Map linkData) {
        String currentSpaceKey = linkData.get("roadmapSpace").toString();
        Map wikiLinks = (Map)linkData.get("wikiLinks");
        LinkedHashMap<String, RoadmapPageLink> pageLinks = new LinkedHashMap<String, RoadmapPageLink>();
        for (String key : wikiLinks.keySet()) {
            pageLinks.put(key, this.linkParser.resolveConfluenceLink((String)wikiLinks.get(key), currentSpaceKey));
        }
        return Response.ok(pageLinks).build();
    }

    private boolean validateBarContextMap(Map contextMap) {
        return contextMap.containsKey("roadmapContentId") && contextMap.containsKey("version") && contextMap.containsKey("roadmapHash") && contextMap.containsKey("roadmapBarId");
    }
}

