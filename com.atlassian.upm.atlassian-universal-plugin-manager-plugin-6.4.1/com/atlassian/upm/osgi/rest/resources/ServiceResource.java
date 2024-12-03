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
import com.atlassian.upm.osgi.Service;
import com.atlassian.upm.osgi.ServiceAccessor;
import com.atlassian.upm.rest.representations.UpmRepresentationFactory;
import java.util.Objects;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

@Path(value="/services/{id}")
public class ServiceResource {
    private final ServiceAccessor serviceAccessor;
    private final UpmRepresentationFactory representationFactory;
    private final PermissionEnforcer permissionEnforcer;

    public ServiceResource(ServiceAccessor serviceAccessor, UpmRepresentationFactory representationFactory, PermissionEnforcer permissionEnforcer) {
        this.serviceAccessor = Objects.requireNonNull(serviceAccessor, "serviceAccessor");
        this.representationFactory = Objects.requireNonNull(representationFactory, "representationFactory");
        this.permissionEnforcer = Objects.requireNonNull(permissionEnforcer, "permissionEnforcer");
    }

    @GET
    public Response get(@PathParam(value="id") long id) {
        this.permissionEnforcer.enforcePermission(Permission.GET_OSGI_STATE);
        Service service = this.serviceAccessor.getService(id);
        if (service != null) {
            return Response.ok((Object)this.representationFactory.createOsgiServiceRepresentation(service)).type("application/vnd.atl.plugins.osgi.service+json").build();
        }
        return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
    }
}

