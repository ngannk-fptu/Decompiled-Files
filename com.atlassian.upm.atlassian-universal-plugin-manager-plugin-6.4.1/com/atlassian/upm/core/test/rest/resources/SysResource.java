/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
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

import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.Sys;
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

@Path(value="/sys")
public class SysResource {
    private final PermissionEnforcer permissionEnforcer;
    private static Option<SysUpdateValueRepresentation> isDevMode = Option.none();

    public SysResource(PermissionEnforcer permissionEnforcer) {
        this.permissionEnforcer = Objects.requireNonNull(permissionEnforcer, "permissionEnforcer");
    }

    private Response getBooleanResponse(Option<SysUpdateValueRepresentation> rep) {
        Iterator<Object> iterator = this.checkPermission().iterator();
        if (iterator.hasNext()) {
            Response resp = iterator.next();
            return resp;
        }
        iterator = rep.iterator();
        if (iterator.hasNext()) {
            SysUpdateValueRepresentation value = (SysUpdateValueRepresentation)iterator.next();
            return Response.ok((Object)value).build();
        }
        return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
    }

    private Option<Response> checkPermission() {
        this.permissionEnforcer.enforceSystemAdmin();
        if (!Sys.isUpmDebugModeEnabled()) {
            return Option.some(Response.status((Response.Status)Response.Status.PRECONDITION_FAILED).build());
        }
        return Option.none();
    }

    private static Option<Boolean> isValueEnabled(Option<SysUpdateValueRepresentation> rep) {
        return rep.map(SysUpdateValueRepresentation::getValue);
    }

    @GET
    @Produces(value={"application/vnd.atl.plugins+json"})
    @Path(value="dev-mode")
    public Response getDevMode() {
        return this.getBooleanResponse(isDevMode);
    }

    @PUT
    @Consumes(value={"application/vnd.atl.plugins+json"})
    @Path(value="dev-mode")
    public Response setIsDevMode(SysUpdateValueRepresentation rep) throws Exception {
        Iterator<Response> iterator = this.checkPermission().iterator();
        if (iterator.hasNext()) {
            Response resp = iterator.next();
            return resp;
        }
        isDevMode = Option.some(rep);
        return Response.ok((Object)rep).type("application/vnd.atl.plugins+json").build();
    }

    @DELETE
    @Path(value="dev-mode")
    public Response resetIsDevMode() {
        Iterator<Response> iterator = this.checkPermission().iterator();
        if (iterator.hasNext()) {
            Response resp = iterator.next();
            return resp;
        }
        isDevMode = Option.none();
        return Response.ok().build();
    }

    public static Option<Boolean> getIsDevMode() {
        return SysResource.isValueEnabled(isDevMode);
    }

    public static final class SysUpdateValueRepresentation {
        @JsonProperty
        private Boolean value;

        @JsonCreator
        public SysUpdateValueRepresentation(@JsonProperty(value="value") Boolean value) {
            this.value = value;
        }

        public Boolean getValue() {
            return this.value;
        }
    }
}

