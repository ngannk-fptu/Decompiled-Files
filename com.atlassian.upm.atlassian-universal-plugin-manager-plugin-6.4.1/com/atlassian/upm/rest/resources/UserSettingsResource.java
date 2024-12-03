/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.websudo.WebSudoNotRequired
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.GET
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.upm.rest.resources;

import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.websudo.WebSudoNotRequired;
import com.atlassian.upm.UserSettings;
import com.atlassian.upm.UserSettingsStore;
import com.atlassian.upm.analytics.event.UserSettingsChangedEvent;
import com.atlassian.upm.core.analytics.AnalyticsLogger;
import com.atlassian.upm.core.permission.Permission;
import com.atlassian.upm.core.rest.resources.permission.PermissionEnforcer;
import com.atlassian.upm.rest.representations.UpmRepresentationFactory;
import com.atlassian.upm.rest.representations.UserSettingsRepresentation;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path(value="/user-settings")
@WebSudoNotRequired
public class UserSettingsResource {
    private final UpmRepresentationFactory factory;
    private final PermissionEnforcer permissionEnforcer;
    private final UserManager userManager;
    private final UserSettingsStore userSettingsStore;
    private final AnalyticsLogger analytics;

    public UserSettingsResource(UpmRepresentationFactory factory, PermissionEnforcer permissionEnforcer, UserManager userManager, UserSettingsStore userSettingsStore, AnalyticsLogger analytics) {
        this.permissionEnforcer = Objects.requireNonNull(permissionEnforcer, "permissionEnforcer");
        this.factory = Objects.requireNonNull(factory, "factory");
        this.userManager = Objects.requireNonNull(userManager, "userManager");
        this.userSettingsStore = Objects.requireNonNull(userSettingsStore, "userSettingsStore");
        this.analytics = Objects.requireNonNull(analytics, "analytics");
    }

    @GET
    @Produces(value={"application/vnd.atl.plugins+json"})
    public Response getNonSysadminSettings() {
        this.permissionEnforcer.enforcePermission(Permission.GET_USER_SETTINGS);
        return Response.ok((Object)this.factory.createUserSettingsRepresentation(this.userSettingsStore.getBoolean(this.userManager.getRemoteUserKey(), UserSettings.DISABLE_EMAIL))).build();
    }

    @PUT
    @Consumes(value={"application/vnd.atl.plugins+json"})
    public Response putNonSysadminSettings(UserSettingsRepresentation userSettingsRep) {
        this.permissionEnforcer.enforcePermission(Permission.MANAGE_USER_SETTINGS);
        return this.putSettingsInternal(Collections.singletonMap(UserSettings.DISABLE_EMAIL, userSettingsRep.isEmailDisabled()));
    }

    @Path(value="/{key}")
    @GET
    @Produces(value={"application/vnd.atl.plugins+json"})
    public Response getAnySetting(@PathParam(value="key") String key) {
        Iterator<UserSettings> iterator = UserSettings.withKey(key).iterator();
        if (iterator.hasNext()) {
            UserSettings setting = iterator.next();
            if (!setting.isAllowedForSysadmin()) {
                this.permissionEnforcer.enforcePermission(Permission.GET_USER_SETTINGS);
            }
            boolean value = this.userSettingsStore.getBoolean(this.userManager.getRemoteUserKey(), setting);
            return Response.ok((Object)String.valueOf(value)).build();
        }
        return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
    }

    @Path(value="/{key}")
    @PUT
    @Consumes(value={"application/vnd.atl.plugins+json"})
    public Response putAnySetting(@PathParam(value="key") String key, String strValue) {
        Iterator<UserSettings> iterator = UserSettings.withKey(key).iterator();
        if (iterator.hasNext()) {
            UserSettings setting = iterator.next();
            boolean value = Boolean.parseBoolean(strValue);
            if (!setting.isAllowedForSysadmin()) {
                this.permissionEnforcer.enforcePermission(Permission.MANAGE_USER_SETTINGS);
            }
            return this.putSettingsInternal(Collections.singletonMap(setting, value));
        }
        return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
    }

    private Response putSettingsInternal(Map<UserSettings, Boolean> values) {
        UserKey userKey = this.userManager.getRemoteUserKey();
        HashMap<UserSettings, String> changedValues = new HashMap<UserSettings, String>();
        for (Map.Entry<UserSettings, Boolean> entry : values.entrySet()) {
            UserSettings setting = entry.getKey();
            boolean newValue = entry.getValue();
            boolean previousValue = this.userSettingsStore.getBoolean(userKey, setting);
            if (previousValue == newValue) continue;
            this.userSettingsStore.setBoolean(userKey, setting, newValue);
            changedValues.put(setting, String.valueOf(newValue));
        }
        Map<UserSettings, String> cv = Collections.unmodifiableMap(changedValues);
        if (!cv.isEmpty()) {
            this.analytics.log(new UserSettingsChangedEvent(cv));
        }
        return Response.ok().build();
    }
}

