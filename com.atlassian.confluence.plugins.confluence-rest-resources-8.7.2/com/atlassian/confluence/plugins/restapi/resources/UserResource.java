/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.people.Group
 *  com.atlassian.confluence.api.model.people.PasswordChangeDetails
 *  com.atlassian.confluence.api.model.people.Person
 *  com.atlassian.confluence.api.model.people.User
 *  com.atlassian.confluence.api.model.validation.ServiceExceptionSupplier
 *  com.atlassian.confluence.api.service.exceptions.BadRequestException
 *  com.atlassian.confluence.api.service.exceptions.NotFoundException
 *  com.atlassian.confluence.api.service.people.GroupService
 *  com.atlassian.confluence.api.service.people.PersonService
 *  com.atlassian.confluence.api.service.people.PersonService$PersonFinder
 *  com.atlassian.confluence.rest.api.annotation.SendsAnalytics
 *  com.atlassian.confluence.rest.api.model.ExpansionsParser
 *  com.atlassian.confluence.rest.api.model.RestList
 *  com.atlassian.confluence.rest.api.model.RestPageRequest
 *  com.atlassian.graphql.annotations.GraphQLName
 *  com.atlassian.graphql.annotations.GraphQLProvider
 *  com.atlassian.graphql.annotations.expansions.GraphQLExpansionParam
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.atlassian.sal.api.user.UserKey
 *  com.google.common.base.Strings
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DefaultValue
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Context
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.UriInfo
 */
package com.atlassian.confluence.plugins.restapi.resources;

import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.people.Group;
import com.atlassian.confluence.api.model.people.PasswordChangeDetails;
import com.atlassian.confluence.api.model.people.Person;
import com.atlassian.confluence.api.model.people.User;
import com.atlassian.confluence.api.model.validation.ServiceExceptionSupplier;
import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.api.service.exceptions.NotFoundException;
import com.atlassian.confluence.api.service.people.GroupService;
import com.atlassian.confluence.api.service.people.PersonService;
import com.atlassian.confluence.plugins.restapi.annotations.LimitRequestSize;
import com.atlassian.confluence.rest.api.annotation.SendsAnalytics;
import com.atlassian.confluence.rest.api.model.ExpansionsParser;
import com.atlassian.confluence.rest.api.model.RestList;
import com.atlassian.confluence.rest.api.model.RestPageRequest;
import com.atlassian.graphql.annotations.GraphQLName;
import com.atlassian.graphql.annotations.GraphQLProvider;
import com.atlassian.graphql.annotations.expansions.GraphQLExpansionParam;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.sal.api.user.UserKey;
import com.google.common.base.Strings;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@AnonymousAllowed
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
@Path(value="/user")
@LimitRequestSize(value=0x500000L)
@GraphQLProvider
@SendsAnalytics
public class UserResource {
    private final PersonService personService;
    private final GroupService groupService;

    public UserResource(@ComponentImport PersonService personService, @ComponentImport GroupService groupService) {
        this.personService = personService;
        this.groupService = groupService;
    }

    @GraphQLName(value="user")
    public Person getUserGraph(@GraphQLName(value="current") boolean current, @GraphQLName(value="key") String key, @GraphQLName(value="username") String username, @GraphQLExpansionParam String expand) {
        if (current) {
            return this.getCurrent(expand);
        }
        return this.getUser(key, username, expand);
    }

    @GET
    public Person getUser(@QueryParam(value="key") String key, @QueryParam(value="username") String username, @QueryParam(value="expand") String expand) {
        UserKey userkey;
        UserKey userKey = userkey = !Strings.isNullOrEmpty((String)key) ? new UserKey(key) : null;
        if (userkey == null && Strings.isNullOrEmpty((String)username) || userkey != null && !Strings.isNullOrEmpty((String)username)) {
            throw new BadRequestException("Only one query param of key or username is required");
        }
        PersonService.PersonFinder personFinder = this.personService.find(ExpansionsParser.parse((String)expand));
        if (userkey != null) {
            personFinder.withUserKey(userkey);
        } else {
            personFinder.withUsername(username);
        }
        return (Person)personFinder.fetch().orElseThrow(ServiceExceptionSupplier.notFound((String)("No user found with key : " + key)));
    }

    @GET
    @Path(value="/list")
    public PageResponse<Person> getUsers(@QueryParam(value="expand") @DefaultValue(value="") String expand, @QueryParam(value="start") @DefaultValue(value="0") int start, @QueryParam(value="limit") @DefaultValue(value="100") int limit, @Context UriInfo uriInfo) {
        Expansion[] expansions = ExpansionsParser.parse((String)expand);
        RestPageRequest restPageRequest = new RestPageRequest(uriInfo, start, limit);
        PageResponse users = this.personService.find(expansions).fetchMany((PageRequest)restPageRequest);
        return RestList.newRestList((PageResponse)users).pageRequest((PageRequest)restPageRequest).build();
    }

    @GET
    @Path(value="/current")
    public Person getCurrent(@GraphQLExpansionParam @QueryParam(value="expand") String expand) {
        return this.personService.getCurrentUser(ExpansionsParser.parse((String)expand));
    }

    @GET
    @Path(value="/anonymous")
    public Person getAnonymous() {
        return (Person)this.personService.find(new Expansion[0]).withUserKey(null).fetchOrNull();
    }

    @GET
    @Path(value="/memberof")
    public PageResponse<Group> getGroups(@QueryParam(value="key") String userKey, @QueryParam(value="username") String username, @QueryParam(value="expand") String expand, @QueryParam(value="start") int start, @QueryParam(value="limit") @DefaultValue(value="200") int limit, @Context UriInfo uriInfo) {
        Expansion[] expansions = ExpansionsParser.parse((String)expand);
        RestPageRequest restPageRequest = new RestPageRequest(uriInfo, start, limit);
        Person person = this.getUser(userKey, username, "");
        if (person instanceof User) {
            return RestList.newRestList((PageResponse)this.groupService.find(expansions).withMember((User)person).fetchMany((PageRequest)restPageRequest)).pageRequest((PageRequest)restPageRequest).build();
        }
        throw new NotFoundException(String.format("User cannot belong to a group : %s", person));
    }

    @POST
    @Path(value="/current/password")
    public Response changePassword(PasswordChangeDetails passwordChangeDetails) {
        this.personService.changeMyPassword(passwordChangeDetails);
        return Response.noContent().build();
    }
}

