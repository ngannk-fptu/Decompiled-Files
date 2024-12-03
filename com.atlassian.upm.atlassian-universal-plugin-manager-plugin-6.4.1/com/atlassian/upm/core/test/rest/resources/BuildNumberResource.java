/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.GET
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.upm.core.test.rest.resources;

import com.atlassian.event.api.EventPublisher;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.Sys;
import com.atlassian.upm.core.pac.PlatformBuildNumberChangeEvent;
import com.atlassian.upm.core.permission.Permission;
import com.atlassian.upm.core.rest.resources.permission.PermissionEnforcer;
import java.util.Iterator;
import java.util.Objects;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

@Path(value="/build-number")
public class BuildNumberResource {
    private final EventPublisher eventPublisher;
    private final PermissionEnforcer permissionEnforcer;
    private static Option<BuildNumberRepresentation> buildNumber = Option.none();

    public BuildNumberResource(EventPublisher eventPublisher, PermissionEnforcer permissionEnforcer) {
        this.eventPublisher = Objects.requireNonNull(eventPublisher, "eventPublisher");
        this.permissionEnforcer = Objects.requireNonNull(permissionEnforcer, "permissionEnforcer");
    }

    @GET
    @Produces(value={"application/vnd.atl.plugins.build.number+json"})
    public Response getBuildNumberResource() {
        this.permissionEnforcer.enforcePermission(Permission.GET_AUDIT_LOG);
        if (!Sys.isUpmDebugModeEnabled()) {
            return Response.status((Response.Status)Response.Status.PRECONDITION_FAILED).build();
        }
        return this.response();
    }

    @PUT
    @Consumes(value={"application/vnd.atl.plugins.build.number+json"})
    public Response setBuildNumber(BuildNumberRepresentation buildNumberRepresentation) {
        this.permissionEnforcer.enforceSystemAdmin();
        if (!Sys.isUpmDebugModeEnabled()) {
            return Response.status((Response.Status)Response.Status.PRECONDITION_FAILED).build();
        }
        BuildNumberResource.setBuildNumberOverride(Option.some(buildNumberRepresentation));
        this.eventPublisher.publish((Object)new PlatformBuildNumberChangeEvent());
        return this.response();
    }

    @DELETE
    public Response clearBuildNumber() {
        this.permissionEnforcer.enforceSystemAdmin();
        if (!Sys.isUpmDebugModeEnabled()) {
            return Response.status((Response.Status)Response.Status.PRECONDITION_FAILED).build();
        }
        BuildNumberResource.setBuildNumberOverride(Option.none(BuildNumberRepresentation.class));
        this.eventPublisher.publish((Object)new PlatformBuildNumberChangeEvent());
        return this.response();
    }

    private Response response() {
        return Response.ok((Object)buildNumber.getOrElse((BuildNumberRepresentation)null)).type("application/vnd.atl.plugins.build.number+json").build();
    }

    public static void setBuildNumberOverride(Option<BuildNumberRepresentation> rep) {
        buildNumber = rep;
    }

    public static Option<String> getBuildNumber() {
        Iterator<BuildNumberRepresentation> iterator = buildNumber.iterator();
        if (iterator.hasNext()) {
            BuildNumberRepresentation rep = iterator.next();
            return Option.option(rep.getBuildNumber());
        }
        return Option.none();
    }

    public static Option<Boolean> isDevelopment() {
        Iterator<BuildNumberRepresentation> iterator = buildNumber.iterator();
        if (iterator.hasNext()) {
            BuildNumberRepresentation rep = iterator.next();
            return Option.option(rep.isDevelopment());
        }
        return Option.none();
    }

    public static final class BuildNumberRepresentation {
        @JsonProperty
        private String buildNumber;
        @JsonProperty
        private Boolean development;

        @JsonCreator
        public BuildNumberRepresentation(@JsonProperty(value="build-number") String buildNumber, @JsonProperty(value="development") Boolean development) {
            this.buildNumber = buildNumber;
            this.development = development;
        }

        public String getBuildNumber() {
            return this.buildNumber;
        }

        public boolean isDevelopment() {
            return this.development;
        }
    }
}

