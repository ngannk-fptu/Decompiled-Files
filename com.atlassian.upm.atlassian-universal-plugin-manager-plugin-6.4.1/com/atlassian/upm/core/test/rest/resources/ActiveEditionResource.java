/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.upm.core.test.rest.resources;

import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.Sys;
import com.atlassian.upm.core.rest.resources.permission.PermissionEnforcer;
import java.util.Objects;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

@Path(value="/test/active-edition")
public class ActiveEditionResource {
    private final PermissionEnforcer permissionEnforcer;
    private static volatile ActiveEditionRepresentation edition = null;

    public ActiveEditionResource(PermissionEnforcer permissionEnforcer) {
        this.permissionEnforcer = Objects.requireNonNull(permissionEnforcer, "permissionEnforcer");
    }

    @PUT
    @Consumes(value={"application/vnd.atl.plugins+json"})
    public Response set(ActiveEditionRepresentation rep) {
        this.permissionEnforcer.enforceSystemAdmin();
        if (!Sys.isUpmDebugModeEnabled()) {
            return Response.status((Response.Status)Response.Status.PRECONDITION_FAILED).build();
        }
        edition = rep;
        return Response.ok((Object)rep).type("application/vnd.atl.plugins+json").build();
    }

    @DELETE
    public Response reset() {
        this.permissionEnforcer.enforceSystemAdmin();
        if (!Sys.isUpmDebugModeEnabled()) {
            return Response.status((Response.Status)Response.Status.PRECONDITION_FAILED).build();
        }
        edition = null;
        return Response.ok().build();
    }

    public static Option<Integer> getActiveEdition() {
        if (edition != null) {
            return Option.some(edition.edition);
        }
        return Option.none();
    }

    public static final class ActiveEditionRepresentation {
        @JsonProperty
        private Integer edition;

        @JsonCreator
        public ActiveEditionRepresentation(@JsonProperty(value="edition") Integer edition) {
            this.edition = edition;
        }

        public Integer getEdition() {
            return this.edition;
        }
    }
}

