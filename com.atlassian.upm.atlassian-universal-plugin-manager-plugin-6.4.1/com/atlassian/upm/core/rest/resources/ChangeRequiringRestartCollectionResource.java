/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  com.google.common.collect.Iterables
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 */
package com.atlassian.upm.core.rest.resources;

import com.atlassian.upm.core.Change;
import com.atlassian.upm.core.PluginRestartRequiredService;
import com.atlassian.upm.core.rest.representations.BasePluginRepresentationFactory;
import com.atlassian.upm.core.rest.resources.permission.PermissionEnforcer;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import java.util.Objects;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path(value="/requires-restart")
public class ChangeRequiringRestartCollectionResource {
    private final BasePluginRepresentationFactory factory;
    private final PermissionEnforcer permissionEnforcer;
    private final PluginRestartRequiredService restartRequiredService;

    public ChangeRequiringRestartCollectionResource(PluginRestartRequiredService restartRequiredService, BasePluginRepresentationFactory factory, PermissionEnforcer permissionEnforcer) {
        this.restartRequiredService = Objects.requireNonNull(restartRequiredService, "restartRequiredService");
        this.permissionEnforcer = Objects.requireNonNull(permissionEnforcer, "permissionEnforcer");
        this.factory = Objects.requireNonNull(factory, "factory");
    }

    @GET
    @Produces(value={"application/vnd.atl.plugins.changes.requiring.restart+json"})
    public Response get() {
        Iterable filteredChanges = Iterables.filter(this.restartRequiredService.getRestartRequiredChanges(), (Predicate)new Predicate<Change>(){

            public boolean apply(Change change) {
                return ChangeRequiringRestartCollectionResource.this.permissionEnforcer.hasPermission(change.getRequiredPermission());
            }
        });
        return Response.ok((Object)this.factory.createChangesRequiringRestartRepresentation(filteredChanges)).build();
    }
}

