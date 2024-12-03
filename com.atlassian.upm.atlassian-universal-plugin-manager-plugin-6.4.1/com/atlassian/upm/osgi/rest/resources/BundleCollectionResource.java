/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 */
package com.atlassian.upm.osgi.rest.resources;

import com.atlassian.upm.core.permission.Permission;
import com.atlassian.upm.core.rest.resources.permission.PermissionEnforcer;
import com.atlassian.upm.rest.representations.UpmRepresentationFactory;
import java.util.Objects;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@Path(value="/bundles")
public class BundleCollectionResource {
    private final UpmRepresentationFactory representationFactory;
    private final PermissionEnforcer permissionEnforcer;

    public BundleCollectionResource(UpmRepresentationFactory representationFactory, PermissionEnforcer permissionEnforcer) {
        this.representationFactory = Objects.requireNonNull(representationFactory, "representationFactory");
        this.permissionEnforcer = Objects.requireNonNull(permissionEnforcer, "permissionEnforcer");
    }

    @GET
    public Response get(@QueryParam(value="q") String term) {
        this.permissionEnforcer.enforcePermission(Permission.GET_OSGI_STATE);
        return Response.ok(term == null ? this.representationFactory.createOsgiBundleCollectionRepresentation() : this.representationFactory.createOsgiBundleCollectionRepresentation(term)).type("application/vnd.atl.plugins.osgi.bundles+json").build();
    }
}

