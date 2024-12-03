/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.websudo.WebSudoNotRequired
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 */
package com.atlassian.upm.rest.resources;

import com.atlassian.sal.api.websudo.WebSudoNotRequired;
import com.atlassian.upm.UpmHostApplicationInformation;
import com.atlassian.upm.core.permission.Permission;
import com.atlassian.upm.core.rest.resources.permission.PermissionEnforcer;
import com.atlassian.upm.pac.PacClient;
import com.atlassian.upm.rest.representations.UpmRepresentationFactory;
import java.util.Objects;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path(value="/product-version")
@WebSudoNotRequired
public class ProductVersionResource {
    private final UpmRepresentationFactory representationFactory;
    private final PermissionEnforcer permissionEnforcer;
    private final PacClient client;
    private final UpmHostApplicationInformation hostApplicationInformation;

    public ProductVersionResource(UpmRepresentationFactory factory, PacClient client, PermissionEnforcer permissionEnforcer, UpmHostApplicationInformation hostApplicationInformation) {
        this.permissionEnforcer = Objects.requireNonNull(permissionEnforcer, "permissionEnforcer");
        this.representationFactory = Objects.requireNonNull(factory, "representationFactory");
        this.client = Objects.requireNonNull(client, "client");
        this.hostApplicationInformation = Objects.requireNonNull(hostApplicationInformation, "hostApplicationInformation");
    }

    @GET
    @Produces(value={"application/vnd.atl.plugins+json"})
    public Response get() {
        this.permissionEnforcer.enforcePermission(Permission.GET_AVAILABLE_PLUGINS);
        return Response.ok((Object)this.representationFactory.createProductVersionRepresentation(this.hostApplicationInformation.isDevelopmentProductVersion(), this.client.isUnknownProductVersion().getOrElse(false))).build();
    }
}

