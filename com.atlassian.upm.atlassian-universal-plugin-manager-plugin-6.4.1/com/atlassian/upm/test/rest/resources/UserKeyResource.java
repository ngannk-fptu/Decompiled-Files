/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.user.UserProfile
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnore
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.upm.test.rest.resources;

import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import com.atlassian.upm.UpmSys;
import com.atlassian.upm.core.rest.resources.permission.PermissionEnforcer;
import java.util.Objects;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

@Path(value="/user-key")
public class UserKeyResource {
    private final PermissionEnforcer permissionEnforcer;
    private final UserManager userManager;

    public UserKeyResource(PermissionEnforcer permissionEnforcer, UserManager userManager) {
        this.permissionEnforcer = Objects.requireNonNull(permissionEnforcer, "permissionEnforcer");
        this.userManager = Objects.requireNonNull(userManager, "userManager");
    }

    @GET
    @Produces(value={"application/vnd.atl.plugins+json"})
    public Response getUserKey() {
        if (!UpmSys.isUpmDebugModeEnabled()) {
            return Response.status((Response.Status)Response.Status.PRECONDITION_FAILED).build();
        }
        if (this.permissionEnforcer.isLoggedIn()) {
            UserProfile profile = this.userManager.getRemoteUser();
            return Response.ok((Object)new UserKeyRepresentation(profile.getUsername(), profile.getUserKey().getStringValue())).build();
        }
        return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
    }

    public static final class UserKeyRepresentation {
        @JsonProperty
        private String username;
        @JsonProperty
        private String userKey;

        @JsonCreator
        public UserKeyRepresentation(@JsonProperty(value="username") String username, @JsonProperty(value="userKey") String userKey) {
            this.username = username;
            this.userKey = userKey;
        }

        public String getUsername() {
            return this.username;
        }

        @JsonIgnore
        public UserKey getUserKey() {
            return new UserKey(this.userKey);
        }
    }
}

