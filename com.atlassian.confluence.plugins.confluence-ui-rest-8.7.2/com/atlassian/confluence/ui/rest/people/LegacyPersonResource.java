/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.legacyapi.service.people.PersonService
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 */
package com.atlassian.confluence.ui.rest.people;

import com.atlassian.confluence.legacyapi.service.people.PersonService;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Deprecated
@Path(value="/people")
@AnonymousAllowed
@Produces(value={"application/json"})
public class LegacyPersonResource {
    private PersonService personService;

    public LegacyPersonResource(PersonService personService) {
        this.personService = personService;
    }

    @GET
    @Path(value="/anonymous")
    public Response getPerson() {
        return Response.ok((Object)this.personService.anonymous()).build();
    }

    @GET
    @Path(value="/users/{username}")
    public Response getPerson(@PathParam(value="username") String username) {
        return Response.ok((Object)this.personService.findByUsername(username)).build();
    }
}

