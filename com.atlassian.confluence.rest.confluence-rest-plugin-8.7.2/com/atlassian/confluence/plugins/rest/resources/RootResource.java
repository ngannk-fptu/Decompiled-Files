/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 */
package com.atlassian.confluence.plugins.rest.resources;

import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path(value="")
@AnonymousAllowed
public class RootResource {
    @GET
    @Produces(value={"application/xml", "application/json"})
    public Response getAllSpaces() {
        return Response.ok().build();
    }
}

