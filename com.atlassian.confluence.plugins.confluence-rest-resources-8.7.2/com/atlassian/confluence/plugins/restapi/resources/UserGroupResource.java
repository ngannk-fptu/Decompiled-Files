/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.people.PersonService
 *  com.atlassian.confluence.rest.api.annotation.SendsAnalytics
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.rest.common.security.jersey.SysadminOnlyResourceFilter
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  com.sun.jersey.spi.container.ResourceFilters
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 */
package com.atlassian.confluence.plugins.restapi.resources;

import com.atlassian.confluence.api.service.people.PersonService;
import com.atlassian.confluence.rest.api.annotation.SendsAnalytics;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.rest.common.security.jersey.SysadminOnlyResourceFilter;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import com.sun.jersey.spi.container.ResourceFilters;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path(value="/user/{username}/group/{groupName}")
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
@WebSudoRequired
@ResourceFilters(value={SysadminOnlyResourceFilter.class})
@SendsAnalytics
public final class UserGroupResource {
    private final PersonService personService;

    public UserGroupResource(@ComponentImport PersonService personService) {
        this.personService = personService;
    }

    @PUT
    public Response update(@PathParam(value="username") String username, @PathParam(value="groupName") String groupName) {
        this.personService.addMembership(username, groupName);
        return Response.noContent().build();
    }

    @DELETE
    public Response delete(@PathParam(value="username") String username, @PathParam(value="groupName") String groupName) {
        this.personService.removeMembership(username, groupName);
        return Response.noContent().build();
    }
}

