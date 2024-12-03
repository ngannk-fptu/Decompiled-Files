/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.security.XsrfProtectionExcluded
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.atlassian.sal.api.websudo.WebSudoNotRequired
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.upm.rest.resources;

import com.atlassian.annotations.security.XsrfProtectionExcluded;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.sal.api.websudo.WebSudoNotRequired;
import com.atlassian.upm.core.SelfUpdateController;
import com.atlassian.upm.core.rest.resources.permission.PermissionEnforcer;
import java.util.Objects;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

@WebSudoNotRequired
@Path(value="/self-update-completed")
public class SelfUpdateCompletionResource {
    private final PermissionEnforcer permissionEnforcer;
    private final SelfUpdateController selfUpdateController;

    public SelfUpdateCompletionResource(PermissionEnforcer permissionEnforcer, SelfUpdateController selfUpdateController) {
        this.permissionEnforcer = Objects.requireNonNull(permissionEnforcer, "permissionEnforcer");
        this.selfUpdateController = Objects.requireNonNull(selfUpdateController, "selfUpdateController");
    }

    @GET
    @Produces(value={"application/vnd.atl.plugins+json"})
    public Response get() {
        this.permissionEnforcer.enforceSystemAdmin();
        return Response.ok((Object)new SelfUpdateCompletionStateRepresentation(this.selfUpdateController.isCleanupNeeded())).build();
    }

    @AnonymousAllowed
    @POST
    @XsrfProtectionExcluded
    public Response post() {
        if (this.selfUpdateController.cleanupAfterSelfUpdate()) {
            return Response.ok().build();
        }
        return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
    }

    public static final class SelfUpdateCompletionStateRepresentation {
        @JsonProperty
        private Boolean cleanupNeeded;

        @JsonCreator
        public SelfUpdateCompletionStateRepresentation(@JsonProperty(value="cleanupNeeded") Boolean cleanupNeeded) {
            this.cleanupNeeded = cleanupNeeded;
        }

        public Boolean isCleanupNeeded() {
            return this.cleanupNeeded;
        }
    }
}

