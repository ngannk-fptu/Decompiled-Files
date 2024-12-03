/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.people.Group
 *  com.atlassian.confluence.api.model.people.Subject
 *  com.atlassian.confluence.api.model.people.User
 *  com.atlassian.confluence.api.model.permissions.ContentRestriction
 *  com.atlassian.confluence.api.model.permissions.OperationKey
 *  com.atlassian.confluence.api.service.exceptions.BadRequestException
 *  com.atlassian.confluence.api.service.permissions.ContentRestrictionService
 *  com.atlassian.confluence.rest.api.model.ExpansionsParser
 *  com.atlassian.confluence.rest.api.model.RestPageRequest
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.atlassian.sal.api.user.UserKey
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.DefaultValue
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Context
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  javax.ws.rs.core.UriInfo
 */
package com.atlassian.confluence.plugins.restapi.experimental.resources;

import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.people.Group;
import com.atlassian.confluence.api.model.people.Subject;
import com.atlassian.confluence.api.model.people.User;
import com.atlassian.confluence.api.model.permissions.ContentRestriction;
import com.atlassian.confluence.api.model.permissions.OperationKey;
import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.api.service.permissions.ContentRestrictionService;
import com.atlassian.confluence.plugins.restapi.annotations.LimitRequestSize;
import com.atlassian.confluence.rest.api.model.ExpansionsParser;
import com.atlassian.confluence.rest.api.model.RestPageRequest;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.sal.api.user.UserKey;
import java.util.Collection;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@AnonymousAllowed
@LimitRequestSize
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
@Path(value="/content/{id}/restriction")
public class ExperimentalContentRestrictionsResource {
    private static final String DEFAULT_FOR_OPERATION_EXPANSIONS = "restrictions.user,restrictions.group";
    private final ContentRestrictionService service;

    public ExperimentalContentRestrictionsResource(@ComponentImport ContentRestrictionService service) {
        this.service = service;
    }

    @GET
    public PageResponse<ContentRestriction> getRestrictions(@PathParam(value="id") ContentId contentId, @QueryParam(value="expand") @DefaultValue(value="restrictions.user,restrictions.group") String expand, @Context UriInfo uriInfo, @QueryParam(value="start") @DefaultValue(value="0") int start, @QueryParam(value="limit") @DefaultValue(value="100") int limit) {
        Expansion[] expansions = ExpansionsParser.parseExperimental((String)expand);
        RestPageRequest pageRequest = new RestPageRequest(uriInfo, start, limit);
        return this.service.getRestrictions(contentId, (PageRequest)pageRequest, expansions);
    }

    @PUT
    public PageResponse<ContentRestriction> updateRestrictions(@PathParam(value="id") ContentId contentId, @QueryParam(value="expand") @DefaultValue(value="restrictions.user,restrictions.group") String expand, PageResponse<ContentRestriction> contentRestrictions) {
        Expansion[] expansions = ExpansionsParser.parseExperimental((String)expand);
        return this.service.updateRestrictions(contentId, (Collection)contentRestrictions.getResults(), expansions);
    }

    @POST
    public PageResponse<ContentRestriction> addRestrictions(@PathParam(value="id") ContentId contentId, @QueryParam(value="expand") @DefaultValue(value="restrictions.user,restrictions.group") String expand, PageResponse<ContentRestriction> contentRestrictions) {
        Expansion[] expansions = ExpansionsParser.parseExperimental((String)expand);
        return this.service.addRestrictions(contentId, (Collection)contentRestrictions.getResults(), expansions);
    }

    @DELETE
    public PageResponse<ContentRestriction> deleteRestrictions(@PathParam(value="id") ContentId contentId, @QueryParam(value="expand") @DefaultValue(value="restrictions.user,restrictions.group") String expand) {
        Expansion[] expansions = ExpansionsParser.parseExperimental((String)expand);
        return this.service.deleteAllDirectRestrictions(contentId, expansions);
    }

    @GET
    @Path(value="/byOperation/{operationKey}/user")
    public Response getIndividualUserRestrictionStatus(@PathParam(value="id") ContentId contentId, @PathParam(value="operationKey") OperationKey operationKey, @QueryParam(value="key") UserKey userKey, @QueryParam(value="userName") String userName) {
        User user = this.userFromKeyOrName(userKey, userName);
        if (this.service.hasDirectRestrictionForSubject(contentId, operationKey, (Subject)user)) {
            return Response.ok().build();
        }
        return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
    }

    @GET
    @Path(value="/byOperation/{operationKey}/group/{groupName}")
    public Response getIndividualGroupRestrictionStatus(@PathParam(value="id") ContentId contentId, @PathParam(value="operationKey") OperationKey operationKey, @PathParam(value="groupName") Group group) {
        if (this.service.hasDirectRestrictionForSubject(contentId, operationKey, (Subject)group)) {
            return Response.ok().build();
        }
        return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
    }

    @DELETE
    @Path(value="/byOperation/{operationKey}/user")
    public Response deleteIndividualUserRestriction(@PathParam(value="id") ContentId contentId, @PathParam(value="operationKey") OperationKey operationKey, @QueryParam(value="key") UserKey userKey, @QueryParam(value="userName") String userName) {
        User user = this.userFromKeyOrName(userKey, userName);
        this.service.deleteDirectRestrictionForSubject(contentId, operationKey, (Subject)user);
        return Response.ok().build();
    }

    @DELETE
    @Path(value="/byOperation/{operationKey}/group/{groupName}")
    public Response deleteIndividualGroupRestriction(@PathParam(value="id") ContentId contentId, @PathParam(value="operationKey") OperationKey operationKey, @PathParam(value="groupName") Group group) {
        this.service.deleteDirectRestrictionForSubject(contentId, operationKey, (Subject)group);
        return Response.ok().build();
    }

    @PUT
    @Path(value="/byOperation/{operationKey}/user")
    public Response addIndividualUserRestriction(@PathParam(value="id") ContentId contentId, @PathParam(value="operationKey") OperationKey operationKey, @QueryParam(value="key") UserKey userKey, @QueryParam(value="userName") String userName) {
        User user = this.userFromKeyOrName(userKey, userName);
        this.service.addDirectRestrictionForSubject(contentId, operationKey, (Subject)user);
        return Response.ok().build();
    }

    @PUT
    @Path(value="/byOperation/{operationKey}/group/{groupName}")
    public Response addIndividualGroupRestriction(@PathParam(value="id") ContentId contentId, @PathParam(value="operationKey") OperationKey operationKey, @PathParam(value="groupName") Group group) {
        this.service.addDirectRestrictionForSubject(contentId, operationKey, (Subject)group);
        return Response.ok().build();
    }

    private User userFromKeyOrName(UserKey userKey, String userName) {
        if (userKey != null) {
            return User.fromUserkey((UserKey)userKey);
        }
        if (userName != null) {
            return User.fromUsername((String)userName);
        }
        throw new BadRequestException("either userName or userKey should not be <null>");
    }
}

