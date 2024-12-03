/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  com.sun.jersey.spi.resource.Singleton
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.GET
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.jfr.rest;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import com.atlassian.troubleshooting.jfr.exception.JfrException;
import com.atlassian.troubleshooting.jfr.service.JfrPropertiesService;
import com.atlassian.troubleshooting.stp.security.PermissionValidationService;
import com.sun.jersey.spi.resource.Singleton;
import java.util.Objects;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Autowired;

@Path(value="/jfr/properties")
@Produces(value={"application/json"})
@Singleton
@WebSudoRequired
public class JfrPropertiesResource {
    private final PermissionValidationService permissionValidationService;
    private final JfrPropertiesService jfrPropertiesService;

    @Autowired
    public JfrPropertiesResource(PermissionValidationService permissionValidationService, JfrPropertiesService jfrPropertiesService) {
        this.permissionValidationService = Objects.requireNonNull(permissionValidationService);
        this.jfrPropertiesService = Objects.requireNonNull(jfrPropertiesService);
    }

    @ExperimentalApi
    @GET
    public Response getProperties() {
        this.permissionValidationService.validateIsSysadmin();
        return Response.ok((Object)this.jfrPropertiesService.getProperties()).build();
    }

    @ExperimentalApi
    @PUT
    @Path(value="{propertyName}")
    public Response storeProperty(@PathParam(value="propertyName") String propertyName, String value) {
        this.permissionValidationService.validateIsSysadmin();
        try {
            this.jfrPropertiesService.setProperty(propertyName, value);
            return Response.ok().build();
        }
        catch (JfrException exc) {
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)exc.getMessage()).build();
        }
    }

    @ExperimentalApi
    @DELETE
    @Path(value="{propertyName}")
    public Response setDefaultProperty(@PathParam(value="propertyName") String propertyName) {
        return this.storeProperty(propertyName, null);
    }
}

