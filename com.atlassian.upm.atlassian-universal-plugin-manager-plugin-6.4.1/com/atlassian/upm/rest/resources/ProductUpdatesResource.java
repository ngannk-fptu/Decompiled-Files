/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Context
 *  javax.ws.rs.core.Response
 */
package com.atlassian.upm.rest.resources;

import com.atlassian.marketplace.client.MpacException;
import com.atlassian.marketplace.client.model.ApplicationVersion;
import com.atlassian.upm.core.permission.Permission;
import com.atlassian.upm.core.rest.resources.RequestContext;
import com.atlassian.upm.core.rest.resources.permission.PermissionEnforcer;
import com.atlassian.upm.pac.PacClient;
import com.atlassian.upm.rest.representations.UpmRepresentationFactory;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

@Path(value="/product-updates")
public class ProductUpdatesResource {
    private final UpmRepresentationFactory representationFactory;
    private final PacClient client;
    private final PermissionEnforcer permissionEnforcer;

    public ProductUpdatesResource(UpmRepresentationFactory factory, PacClient client, PermissionEnforcer permissionEnforcer) {
        this.permissionEnforcer = Objects.requireNonNull(permissionEnforcer, "permissionEnforcer");
        this.representationFactory = Objects.requireNonNull(factory, "representationFactory");
        this.client = Objects.requireNonNull(client, "client");
    }

    @GET
    @Produces(value={"application/vnd.atl.plugins.product.updates+json"})
    public Response get(@Context HttpServletRequest request) {
        Collection<ApplicationVersion> productVersions;
        this.permissionEnforcer.enforcePermission(Permission.GET_PRODUCT_UPDATE_COMPATIBILITY);
        boolean pacUnreachable = !this.client.isPacReachable();
        try {
            productVersions = this.client.getProductUpdates();
        }
        catch (MpacException e) {
            productVersions = Collections.emptyList();
            pacUnreachable = true;
        }
        return Response.ok((Object)this.representationFactory.createProductUpdatesRepresentation(productVersions, new RequestContext(request).pacUnreachable(pacUnreachable))).build();
    }
}

