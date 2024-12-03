/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.people.Group
 *  com.atlassian.confluence.api.model.people.Person
 *  com.atlassian.confluence.api.model.validation.ServiceExceptionSupplier
 *  com.atlassian.confluence.api.service.people.GroupService
 *  com.atlassian.confluence.api.service.people.PersonService
 *  com.atlassian.confluence.rest.api.model.ExpansionsParser
 *  com.atlassian.confluence.rest.api.model.RestList
 *  com.atlassian.confluence.rest.api.model.RestPageRequest
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DefaultValue
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Context
 *  javax.ws.rs.core.UriInfo
 */
package com.atlassian.confluence.plugins.restapi.experimental.resources;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.people.Group;
import com.atlassian.confluence.api.model.people.Person;
import com.atlassian.confluence.api.model.validation.ServiceExceptionSupplier;
import com.atlassian.confluence.api.service.people.GroupService;
import com.atlassian.confluence.api.service.people.PersonService;
import com.atlassian.confluence.plugins.restapi.annotations.LimitRequestSize;
import com.atlassian.confluence.rest.api.model.ExpansionsParser;
import com.atlassian.confluence.rest.api.model.RestList;
import com.atlassian.confluence.rest.api.model.RestPageRequest;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

@ExperimentalApi
@AnonymousAllowed
@LimitRequestSize(value=0x500000L)
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
@Path(value="/group")
public class ExperimentalGroupResource {
    private final GroupService groupService;
    private final PersonService personService;

    public ExperimentalGroupResource(@ComponentImport GroupService groupService, @ComponentImport PersonService personService) {
        this.groupService = groupService;
        this.personService = personService;
    }

    @GET
    public PageResponse<Group> getGroups(@QueryParam(value="expand") @DefaultValue(value="") String expand, @QueryParam(value="start") int start, @QueryParam(value="limit") @DefaultValue(value="200") int limit, @Context UriInfo uriInfo) {
        Expansion[] expansions = ExpansionsParser.parse((String)expand);
        RestPageRequest pageRequest = new RestPageRequest(uriInfo, start, limit);
        return RestList.newRestList((PageResponse)this.groupService.find(expansions).fetchMany((PageRequest)pageRequest)).pageRequest((PageRequest)pageRequest).build();
    }

    @GET
    @Path(value="{groupName}")
    public Group getGroup(@PathParam(value="groupName") String groupName, @QueryParam(value="expand") String expand) {
        Expansion[] expansions = ExpansionsParser.parse((String)expand);
        return (Group)this.groupService.find(expansions).withName(groupName).fetch().orElseThrow(ServiceExceptionSupplier.notFound((String)("No group with name : " + groupName)));
    }

    @GET
    @Path(value="{groupName}/member")
    public PageResponse<Person> getMembers(@PathParam(value="groupName") Group group, @QueryParam(value="expand") String expand, @QueryParam(value="start") int start, @QueryParam(value="limit") @DefaultValue(value="200") int limit, @Context UriInfo uriInfo) {
        RestPageRequest request = new RestPageRequest(uriInfo, start, limit);
        Expansion[] expansions = ExpansionsParser.parse((String)expand);
        PageResponse results = this.personService.find(expansions).withMembershipOf(group).fetchMany((PageRequest)request);
        return RestList.newRestList((PageResponse)results).pageRequest((PageRequest)request).build();
    }
}

