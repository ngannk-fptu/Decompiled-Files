/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.upm.osgi.rest.resources;

import com.atlassian.upm.core.permission.Permission;
import com.atlassian.upm.core.rest.resources.permission.PermissionEnforcer;
import com.atlassian.upm.osgi.Bundle;
import com.atlassian.upm.osgi.BundleAccessor;
import com.atlassian.upm.rest.representations.UpmRepresentationFactory;
import java.util.Objects;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

@Path(value="/bundles/{id}")
public class BundleResource {
    private final BundleAccessor bundleAccessor;
    private final UpmRepresentationFactory representationFactory;
    private final PermissionEnforcer permissionEnforcer;

    public BundleResource(BundleAccessor bundleAccessor, UpmRepresentationFactory representationFactory, PermissionEnforcer permissionEnforcer) {
        this.bundleAccessor = Objects.requireNonNull(bundleAccessor, "bundleAccessor");
        this.representationFactory = Objects.requireNonNull(representationFactory, "representationFactory");
        this.permissionEnforcer = Objects.requireNonNull(permissionEnforcer, "permissionEnforcer");
    }

    @GET
    public Response get(@PathParam(value="id") long id) {
        this.permissionEnforcer.enforcePermission(Permission.GET_OSGI_STATE);
        Bundle bundle = this.bundleAccessor.getBundle(id);
        if (bundle != null) {
            return Response.ok((Object)this.representationFactory.createOsgiBundleRepresentation(bundle)).type("application/vnd.atl.plugins.osgi.bundle+json").build();
        }
        return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
    }
}

