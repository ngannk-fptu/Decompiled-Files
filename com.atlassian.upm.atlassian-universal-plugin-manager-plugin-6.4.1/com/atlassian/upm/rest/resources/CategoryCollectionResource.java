/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.websudo.WebSudoNotRequired
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.upm.rest.resources;

import com.atlassian.marketplace.client.MpacException;
import com.atlassian.sal.api.websudo.WebSudoNotRequired;
import com.atlassian.upm.core.permission.Permission;
import com.atlassian.upm.core.rest.resources.permission.PermissionEnforcer;
import com.atlassian.upm.pac.PacClient;
import java.util.Objects;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path(value="/categories")
@WebSudoNotRequired
public class CategoryCollectionResource {
    private final PacClient client;
    private final PermissionEnforcer permissionEnforcer;
    private static final Logger log = LoggerFactory.getLogger(CategoryCollectionResource.class);

    public CategoryCollectionResource(PacClient client, PermissionEnforcer permissionEnforcer) {
        this.permissionEnforcer = Objects.requireNonNull(permissionEnforcer, "permissionEnforcer");
        this.client = Objects.requireNonNull(client, "client");
    }

    @GET
    @Produces(value={"application/vnd.atl.plugins.categories+json"})
    public Response get() {
        this.permissionEnforcer.enforcePermission(Permission.GET_AVAILABLE_PLUGINS);
        try {
            return Response.ok(this.client.getCategories()).build();
        }
        catch (MpacException e) {
            log.warn("Failed to get categories", (Throwable)e);
            return Response.serverError().build();
        }
    }
}

