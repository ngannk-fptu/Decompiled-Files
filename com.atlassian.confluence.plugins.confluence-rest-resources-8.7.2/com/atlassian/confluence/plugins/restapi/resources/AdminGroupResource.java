/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.people.Group
 *  com.atlassian.confluence.api.service.people.GroupService
 *  com.atlassian.confluence.rest.api.annotation.SendsAnalytics
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.rest.common.security.jersey.SysadminOnlyResourceFilter
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  com.sun.jersey.spi.container.ResourceFilters
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 */
package com.atlassian.confluence.plugins.restapi.resources;

import com.atlassian.confluence.api.model.people.Group;
import com.atlassian.confluence.api.service.people.GroupService;
import com.atlassian.confluence.plugins.restapi.annotations.LimitRequestSize;
import com.atlassian.confluence.rest.api.annotation.SendsAnalytics;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.rest.common.security.jersey.SysadminOnlyResourceFilter;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import com.sun.jersey.spi.container.ResourceFilters;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path(value="/admin/group")
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
@LimitRequestSize(value=65536L)
@WebSudoRequired
@ResourceFilters(value={SysadminOnlyResourceFilter.class})
@SendsAnalytics
public final class AdminGroupResource {
    private final GroupService groupService;

    public AdminGroupResource(@ComponentImport GroupService groupService) {
        this.groupService = groupService;
    }

    @POST
    public Response create(Group model) throws URISyntaxException, UnsupportedEncodingException {
        Group group = this.groupService.createGroup(model.getName());
        String encodedName = URLEncoder.encode(group.getName(), "UTF-8");
        URI location = new URI(encodedName);
        return Response.created((URI)location).entity((Object)group).build();
    }

    @DELETE
    @Path(value="/{groupName}")
    public Response delete(@PathParam(value="groupName") String groupName) {
        this.groupService.deleteGroup(groupName);
        return Response.noContent().build();
    }
}

