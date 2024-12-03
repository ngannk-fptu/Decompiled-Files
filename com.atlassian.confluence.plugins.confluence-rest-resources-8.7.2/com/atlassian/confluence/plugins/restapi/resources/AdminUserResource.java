/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.longtasks.LongTaskSubmission
 *  com.atlassian.confluence.api.model.people.Credentials
 *  com.atlassian.confluence.api.model.people.Person
 *  com.atlassian.confluence.api.model.people.UserDetailsForCreation
 *  com.atlassian.confluence.api.model.people.UserKey
 *  com.atlassian.confluence.api.service.people.PersonService
 *  com.atlassian.confluence.rest.api.annotation.SendsAnalytics
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.rest.common.security.jersey.AdminOnlyResourceFilter
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  com.sun.jersey.spi.container.ResourceFilters
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.POST
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 */
package com.atlassian.confluence.plugins.restapi.resources;

import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.longtasks.LongTaskSubmission;
import com.atlassian.confluence.api.model.people.Credentials;
import com.atlassian.confluence.api.model.people.Person;
import com.atlassian.confluence.api.model.people.UserDetailsForCreation;
import com.atlassian.confluence.api.model.people.UserKey;
import com.atlassian.confluence.api.service.people.PersonService;
import com.atlassian.confluence.plugins.restapi.resources.LongTaskResource;
import com.atlassian.confluence.rest.api.annotation.SendsAnalytics;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.rest.common.security.jersey.AdminOnlyResourceFilter;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import com.sun.jersey.spi.container.ResourceFilters;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path(value="/admin/user")
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
@WebSudoRequired
@ResourceFilters(value={AdminOnlyResourceFilter.class})
@SendsAnalytics
public final class AdminUserResource {
    private final PersonService personService;

    public AdminUserResource(@ComponentImport PersonService personService) {
        this.personService = personService;
    }

    @POST
    public Response createUser(UserDetailsForCreation userDetailsForCreation) throws URISyntaxException, UnsupportedEncodingException {
        UserKey userKey = this.personService.create(userDetailsForCreation);
        String encodedName = URLEncoder.encode(userDetailsForCreation.getUserName(), "UTF-8");
        URI location = new URI(encodedName);
        return Response.created((URI)location).entity((Object)userKey).build();
    }

    @PUT
    @Path(value="/{username}/disable")
    public Response disable(@PathParam(value="username") String username) {
        this.personService.disable(username);
        return Response.noContent().build();
    }

    @PUT
    @Path(value="/{username}/enable")
    public Response enable(@PathParam(value="username") String username) {
        this.personService.enable(username);
        return Response.noContent().build();
    }

    @DELETE
    @Path(value="/{username}")
    public Response delete(@PathParam(value="username") String username) {
        Person person = (Person)this.personService.find(new Expansion[0]).withUsername(username).fetchOrNull();
        LongTaskSubmission task = this.personService.delete(person);
        return LongTaskResource.submissionResponse(task);
    }

    @POST
    @Path(value="/{username}/password")
    public Response changePassword(@PathParam(value="username") String username, Credentials credentials) {
        this.personService.changeUserPassword(username, credentials.getPassword());
        return Response.noContent().build();
    }
}

