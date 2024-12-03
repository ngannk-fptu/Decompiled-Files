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
import com.atlassian.upm.osgi.Package;
import com.atlassian.upm.osgi.PackageAccessor;
import com.atlassian.upm.osgi.impl.Versions;
import com.atlassian.upm.rest.representations.UpmRepresentationFactory;
import java.util.Objects;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

@Path(value="/packages/{bundleId}/{name}/{version}")
public class PackageResource {
    private final PackageAccessor packageAccessor;
    private final UpmRepresentationFactory representationFactory;
    private final PermissionEnforcer permissionEnforcer;

    public PackageResource(PackageAccessor packageAccessor, UpmRepresentationFactory representationFactory, PermissionEnforcer permissionEnforcer) {
        this.packageAccessor = Objects.requireNonNull(packageAccessor, "packageAccessor");
        this.representationFactory = Objects.requireNonNull(representationFactory, "representationFactory");
        this.permissionEnforcer = Objects.requireNonNull(permissionEnforcer, "permissionEnforcer");
    }

    @GET
    public Response get(@PathParam(value="bundleId") long bundleId, @PathParam(value="name") String name, @PathParam(value="version") String version) {
        this.permissionEnforcer.enforcePermission(Permission.GET_OSGI_STATE);
        Package pkg = this.packageAccessor.getExportedPackage(bundleId, name, Versions.fromString(version));
        if (pkg != null) {
            return Response.ok((Object)this.representationFactory.createOsgiPackageRepresentation(pkg)).type("application/vnd.atl.plugins.osgi.package+json").build();
        }
        return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
    }
}

