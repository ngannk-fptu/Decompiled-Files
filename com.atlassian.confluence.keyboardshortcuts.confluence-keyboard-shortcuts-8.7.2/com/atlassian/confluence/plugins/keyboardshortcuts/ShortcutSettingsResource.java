/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.core.AtlassianCoreException
 *  com.atlassian.core.user.preferences.UserPreferences
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.rest.common.security.AuthenticationContext
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Context
 *  javax.ws.rs.core.Response
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.plugins.keyboardshortcuts;

import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.core.AtlassianCoreException;
import com.atlassian.core.user.preferences.UserPreferences;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.rest.common.security.AuthenticationContext;
import java.security.Principal;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import org.codehaus.jackson.annotate.JsonProperty;

@Path(value="/")
public class ShortcutSettingsResource {
    private final UserAccessor userAccessor;
    @Context
    protected AuthenticationContext authContext;

    public ShortcutSettingsResource(@ComponentImport UserAccessor userAccessor) {
        this.userAccessor = userAccessor;
    }

    @GET
    @Path(value="enabled")
    @Produces(value={"application/json"})
    public Response getShortcutsEnabled() {
        UserPreferences userPreferences = this.getUserPreferences();
        Boolean shortcutsDisabled = userPreferences.getBoolean("confluence.user.keyboard.shortcuts.disabled");
        return Response.ok((Object)Boolean.valueOf(shortcutsDisabled == false).toString()).build();
    }

    @POST
    @Path(value="enabled")
    @Consumes(value={"application/json"})
    @Produces(value={"application/json"})
    public Response setShortcutsEnabled(State state) {
        UserPreferences userPreferences = this.getUserPreferences();
        try {
            userPreferences.setBoolean("confluence.user.keyboard.shortcuts.disabled", !state.enabled);
        }
        catch (AtlassianCoreException e) {
            return Response.serverError().build();
        }
        return Response.ok((Object)String.valueOf(state.enabled)).build();
    }

    private UserPreferences getUserPreferences() {
        ConfluenceUser user = this.getUser();
        if (user == null) {
            return null;
        }
        return new UserPreferences(this.userAccessor.getPropertySet(user));
    }

    private ConfluenceUser getUser() {
        Principal principal = this.authContext.getPrincipal();
        if (principal == null) {
            return null;
        }
        if (principal instanceof ConfluenceUser) {
            return (ConfluenceUser)principal;
        }
        return this.userAccessor.getUserByName(principal.getName());
    }

    public static class State {
        @JsonProperty
        private boolean enabled;
    }
}

