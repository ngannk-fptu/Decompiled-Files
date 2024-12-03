/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  javax.servlet.http.HttpServletRequest
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Context
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.plugins.custom_apps.rest;

import com.atlassian.plugins.custom_apps.api.CustomApp;
import com.atlassian.plugins.custom_apps.api.CustomAppNotFoundException;
import com.atlassian.plugins.custom_apps.api.CustomAppService;
import com.atlassian.plugins.custom_apps.api.CustomAppsValidationException;
import com.atlassian.plugins.custom_apps.rest.data.CustomAppData;
import com.atlassian.plugins.custom_apps.rest.data.MoveBean;
import com.atlassian.plugins.navlink.util.CacheControlFactory;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlRootElement;

@Produces(value={"application/json"})
@Consumes(value={"application/json"})
@Path(value="/customapps")
@WebSudoRequired
public class CustomAppsRestResource {
    private final CustomAppService customAppService;
    private final UserManager userManager;

    public CustomAppsRestResource(CustomAppService customAppService, UserManager userManager) {
        this.customAppService = customAppService;
        this.userManager = userManager;
    }

    @Path(value="list")
    @GET
    public Response list(@Context HttpServletRequest request) {
        try {
            this.checkAdminPermission(request);
            List links = this.customAppService.getLocalCustomAppsAndRemoteLinks().stream().map(this.converter()).collect(Collectors.toList());
            return Response.ok(links).cacheControl(CacheControlFactory.withNoCache()).build();
        }
        catch (PermissionDeniedException e) {
            return this.handleNoPermission();
        }
    }

    private Function<CustomApp, CustomAppData> converter() {
        return c -> new CustomAppData(c.getId(), c.getDisplayName(), c.getUrl(), c.getSourceApplicationType(), c.getHide(), c.getEditable(), c.getAllowedGroups(), c.getSourceApplicationUrl(), c.getSourceApplicationName(), c.isSelf());
    }

    @Path(value="{id}")
    @GET
    public Response get(@PathParam(value="id") String id, @Context HttpServletRequest request) {
        try {
            this.checkAdminPermission(request);
            return Response.ok((Object)this.converter().apply(this.customAppService.get(id))).build();
        }
        catch (CustomAppNotFoundException e) {
            return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
        }
        catch (PermissionDeniedException e) {
            return this.handleNoPermission();
        }
    }

    @Path(value="groups")
    @GET
    public Response get(@QueryParam(value="q") String q, @QueryParam(value="page_limit") int pageLimit, @QueryParam(value="page") int page, @Context HttpServletRequest request) {
        try {
            this.checkAdminPermission(request);
            Iterable groups = this.userManager.findGroupNamesByPrefix(q, (page - 1) * pageLimit, pageLimit + 1);
            Groups groupResponse = new Groups();
            groupResponse.names = new ArrayList<String>();
            Iterator i = groups.iterator();
            for (int index = 0; index < pageLimit && i.hasNext(); ++index) {
                groupResponse.names.add((String)i.next());
            }
            groupResponse.more = i.hasNext();
            return Response.ok((Object)groupResponse).cacheControl(CacheControlFactory.withNoCache()).build();
        }
        catch (PermissionDeniedException e) {
            return this.handleNoPermission();
        }
    }

    private Response handleNoPermission() {
        return Response.status((Response.Status)Response.Status.UNAUTHORIZED).build();
    }

    private void checkAdminPermission(HttpServletRequest request) throws PermissionDeniedException {
        if (!this.userManager.isAdmin(this.userManager.getRemoteUsername(request))) {
            throw new PermissionDeniedException();
        }
    }

    @Path(value="{id}")
    @DELETE
    public Response delete(@PathParam(value="id") String id, @Context HttpServletRequest request) {
        try {
            this.checkAdminPermission(request);
            this.customAppService.delete(id);
        }
        catch (CustomAppNotFoundException e) {
            return Response.status((Response.Status)Response.Status.NOT_FOUND).entity((Object)Collections.EMPTY_MAP).build();
        }
        catch (PermissionDeniedException e) {
            return this.handleNoPermission();
        }
        return Response.ok().cacheControl(CacheControlFactory.withNoCache()).build();
    }

    @POST
    public Response create(CustomAppData data, @Context HttpServletRequest request) {
        try {
            this.checkAdminPermission(request);
            return Response.ok((Object)this.converter().apply(this.customAppService.create(data.displayName, data.url, null, data.hide == null ? false : data.hide, data.allowedGroups))).cacheControl(CacheControlFactory.withNoCache()).build();
        }
        catch (CustomAppsValidationException e) {
            return this.validationErrorResponse(e);
        }
        catch (PermissionDeniedException e) {
            return this.handleNoPermission();
        }
    }

    private Response validationErrorResponse(CustomAppsValidationException e) {
        return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)("{\"errors\": {\"" + e.getField() + "\": \"" + e.getValidationError() + "\"}}")).build();
    }

    @Path(value="{id}")
    @PUT
    public Response update(@PathParam(value="id") String id, CustomAppData data, @Context HttpServletRequest request) {
        try {
            this.checkAdminPermission(request);
            CustomApp c = this.customAppService.get(id);
            return Response.ok((Object)this.converter().apply(this.customAppService.update(id, data.displayName == null ? c.getDisplayName() : data.displayName, data.url == null ? c.getUrl() : data.url, data.hide == null ? c.getHide() : data.hide.booleanValue(), data.allowedGroups == null ? c.getAllowedGroups() : data.allowedGroups))).build();
        }
        catch (CustomAppNotFoundException e) {
            return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
        }
        catch (CustomAppsValidationException e) {
            return this.validationErrorResponse(e);
        }
        catch (PermissionDeniedException e) {
            return this.handleNoPermission();
        }
    }

    @POST
    @Path(value="{id}/move")
    public Response movePosition(@PathParam(value="id") Integer id, @Context HttpServletRequest request, MoveBean bean) {
        try {
            this.checkAdminPermission(request);
            if (bean.after != null) {
                int idToMoveAfter = this.extractIdFromLink(bean.after.getPath());
                this.customAppService.moveAfter(id, idToMoveAfter);
            } else {
                switch (bean.position) {
                    case Earlier: 
                    case Later: 
                    case Last: {
                        throw new IllegalArgumentException("Unexpected position '" + (Object)((Object)bean.position) + "'");
                    }
                    case First: {
                        this.customAppService.moveToStart(id);
                    }
                }
            }
            return Response.ok().cacheControl(CacheControlFactory.withNoCache()).build();
        }
        catch (PermissionDeniedException e) {
            return this.handleNoPermission();
        }
        catch (CustomAppNotFoundException e) {
            return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
        }
    }

    private int extractIdFromLink(String path) {
        String idString = path.substring(path.lastIndexOf(47) + 1);
        try {
            return Integer.parseInt(idString);
        }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException("Failed to parse id from path '" + path + "'");
        }
    }

    private class PermissionDeniedException
    extends Exception {
        private PermissionDeniedException() {
        }
    }

    @XmlRootElement
    private static class Groups {
        public List<String> names;
        public boolean more;

        private Groups(List<String> names, boolean more) {
            this.names = names;
            this.more = more;
        }

        private Groups() {
        }
    }
}

