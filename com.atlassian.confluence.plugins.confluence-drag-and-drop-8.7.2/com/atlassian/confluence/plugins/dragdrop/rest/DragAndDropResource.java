/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.core.AtlassianCoreException
 *  com.atlassian.core.user.preferences.UserPreferences
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.user.User
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.confluence.plugins.dragdrop.rest;

import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.core.AtlassianCoreException;
import com.atlassian.core.user.preferences.UserPreferences;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.User;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path(value="/")
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
public class DragAndDropResource {
    private UserAccessor userAccessor;
    private static final String DRAG_AND_DROP_TIP_SETTING_KEY = "show-drag-and-drop-tip";

    public DragAndDropResource(@ComponentImport UserAccessor userAccessor) {
        this.userAccessor = userAccessor;
    }

    @POST
    @Path(value="tip/disable")
    @Consumes(value={"application/json"})
    public Response disableShowTip() {
        UserPreferences userPreferences = this.userAccessor.getUserPreferences((User)AuthenticatedUserThreadLocal.get());
        try {
            userPreferences.setString(DRAG_AND_DROP_TIP_SETTING_KEY, "false");
        }
        catch (AtlassianCoreException e) {
            return Response.serverError().build();
        }
        return Response.status((Response.Status)Response.Status.OK).build();
    }

    @GET
    @Path(value="tip/setting")
    @Consumes(value={"application/json"})
    @Produces(value={"application/json"})
    public Response getTipSetting() {
        UserPreferences userPreferences = this.userAccessor.getUserPreferences((User)AuthenticatedUserThreadLocal.get());
        String value = userPreferences.getString(DRAG_AND_DROP_TIP_SETTING_KEY) != null ? userPreferences.getString(DRAG_AND_DROP_TIP_SETTING_KEY) : "";
        return Response.status((Response.Status)Response.Status.OK).entity((Object)value).build();
    }
}

