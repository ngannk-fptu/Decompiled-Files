/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  com.sun.jersey.spi.container.ResourceFilters
 *  com.sun.jersey.spi.resource.Singleton
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.oauth2.provider.rest.resource;

import com.atlassian.annotations.PublicApi;
import com.atlassian.oauth2.provider.rest.exception.ClientNotFoundException;
import com.atlassian.oauth2.provider.rest.exception.ValidationException;
import com.atlassian.oauth2.provider.rest.model.RestClientEntity;
import com.atlassian.oauth2.provider.rest.resource.filter.SysadminOnlyResourceFilter;
import com.atlassian.oauth2.provider.rest.service.ClientRestService;
import com.sun.jersey.spi.container.ResourceFilters;
import com.sun.jersey.spi.resource.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path(value="client")
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
@Singleton
@ResourceFilters(value={SysadminOnlyResourceFilter.class})
public class ClientResource {
    private static final Logger logger = LoggerFactory.getLogger(ClientResource.class);
    private final ClientRestService clientRestService;

    public ClientResource(ClientRestService clientRestService) {
        this.clientRestService = clientRestService;
    }

    @GET
    @PublicApi
    public Response getClients() throws ValidationException {
        logger.debug("Retrieving all clients");
        return Response.ok(this.clientRestService.list()).build();
    }

    @POST
    @PublicApi
    public Response createClient(RestClientEntity client) throws ValidationException {
        logger.debug("Creating client");
        return Response.ok((Object)this.clientRestService.create(client)).build();
    }

    @PUT
    @Path(value="/{id}")
    @PublicApi
    public Response updateClient(@PathParam(value="id") String id, RestClientEntity client) throws ValidationException, ClientNotFoundException {
        logger.debug("Updating client with id: [{}]", (Object)id);
        this.clientRestService.update(id, client);
        return Response.noContent().build();
    }

    @PUT
    @Path(value="/reset/{clientId}")
    @PublicApi
    public Response resetClientSecret(@PathParam(value="clientId") String clientId) throws ClientNotFoundException {
        logger.debug("Resetting client secret associated with client id: [{}]", (Object)clientId);
        return Response.ok((Object)this.clientRestService.resetClientSecret(clientId)).build();
    }

    @GET
    @Path(value="/{id}")
    @PublicApi
    public Response getClient(@PathParam(value="id") String id) throws ClientNotFoundException {
        logger.debug("Retrieving client associated with id: [{}]", (Object)id);
        return Response.ok((Object)this.clientRestService.get(id)).build();
    }

    @DELETE
    @Path(value="/{id}")
    @PublicApi
    public Response deleteClient(@PathParam(value="id") String id) throws ValidationException, ClientNotFoundException {
        logger.debug("Deleting client with id: [{}]", (Object)id);
        return Response.ok((Object)this.clientRestService.delete(id)).build();
    }
}

