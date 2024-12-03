/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.core.Response
 */
package com.atlassian.upm.osgi.rest.resources;

import com.atlassian.upm.core.permission.Permission;
import com.atlassian.upm.core.rest.resources.permission.PermissionEnforcer;
import com.atlassian.upm.rest.representations.UpmRepresentationFactory;
import java.util.Objects;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path(value="/packages")
public class PackageCollectionResource {
    private final UpmRepresentationFactory representationFactory;
    private final PermissionEnforcer permissionEnforcer;

    public PackageCollectionResource(UpmRepresentationFactory representationFactory, PermissionEnforcer permissionEnforcer) {
        this.representationFactory = Objects.requireNonNull(representationFactory, "representationFactory");
        this.permissionEnforcer = Objects.requireNonNull(permissionEnforcer, "permissionEnforcer");
    }

    @GET
    public Response get() {
        this.permissionEnforcer.enforcePermission(Permission.GET_OSGI_STATE);
        return Response.ok(this.representationFactory.createOsgiPackageCollectionRepresentation()).type("application/vnd.atl.plugins.osgi.packages+json").build();
    }
}

