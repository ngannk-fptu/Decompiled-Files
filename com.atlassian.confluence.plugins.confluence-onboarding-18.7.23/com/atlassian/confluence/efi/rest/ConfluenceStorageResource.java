/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.user.User
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.GET
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.MediaType
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.efi.rest;

import com.atlassian.confluence.efi.rest.beans.StorageBean;
import com.atlassian.confluence.efi.store.GlobalStorageService;
import com.atlassian.confluence.efi.store.UserStorageService;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.User;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;

@Path(value="store")
@Produces(value={"application/json"})
@Consumes(value={"application/json"})
public class ConfluenceStorageResource {
    private final UserStorageService userStorageService;
    private final GlobalStorageService globalStorageService;
    private final PermissionManager permissionManager;

    public ConfluenceStorageResource(UserStorageService userStorageService, GlobalStorageService globalStorageService, @ComponentImport PermissionManager permissionManager) {
        this.userStorageService = userStorageService;
        this.permissionManager = permissionManager;
        this.globalStorageService = globalStorageService;
    }

    @GET
    public Response getStorage(@QueryParam(value="key") String key) {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        if (user == null || StringUtils.isEmpty((CharSequence)key)) {
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).build();
        }
        if (!this.permissionManager.hasPermission((User)user, Permission.VIEW, PermissionManager.TARGET_APPLICATION)) {
            return Response.status((Response.Status)Response.Status.UNAUTHORIZED).build();
        }
        String value = this.userStorageService.get(key, user);
        return Response.ok((Object)new StorageBean(key, value)).build();
    }

    @PUT
    @Produces(value={"application/json"})
    public Response setStorage(StorageBean bean) {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        if (user == null || bean == null || StringUtils.isEmpty((CharSequence)bean.getKey()) || StringUtils.isEmpty((CharSequence)bean.getValue())) {
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).build();
        }
        if (!this.permissionManager.hasPermission((User)user, Permission.VIEW, PermissionManager.TARGET_APPLICATION)) {
            return Response.status((Response.Status)Response.Status.UNAUTHORIZED).build();
        }
        this.userStorageService.set(bean.getKey(), bean.getValue(), user);
        return Response.ok((Object)bean).type(MediaType.APPLICATION_JSON_TYPE).build();
    }

    @DELETE
    public Response deleteStorage(StorageBean bean) {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        if (user == null || bean == null || StringUtils.isEmpty((CharSequence)bean.getKey())) {
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).build();
        }
        if (!this.permissionManager.hasPermission((User)user, Permission.REMOVE, PermissionManager.TARGET_APPLICATION)) {
            return Response.status((Response.Status)Response.Status.UNAUTHORIZED).build();
        }
        this.userStorageService.remove(bean.getKey(), user);
        return Response.noContent().build();
    }

    @GET
    @Path(value="global")
    public Response getGlobalStorage(@QueryParam(value="key") String key) {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        if (user == null || StringUtils.isEmpty((CharSequence)key)) {
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).build();
        }
        if (!this.permissionManager.hasPermission((User)user, Permission.VIEW, PermissionManager.TARGET_APPLICATION)) {
            return Response.status((Response.Status)Response.Status.UNAUTHORIZED).build();
        }
        return Response.ok((Object)new StorageBean(key, this.globalStorageService.get(key))).build();
    }

    @PUT
    @Path(value="global")
    public Response setGlobalStorage(StorageBean bean) {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        if (user == null || bean == null || StringUtils.isEmpty((CharSequence)bean.getKey()) || StringUtils.isEmpty((CharSequence)bean.getValue())) {
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).build();
        }
        if (!this.permissionManager.hasPermission((User)user, Permission.VIEW, PermissionManager.TARGET_APPLICATION)) {
            return Response.status((Response.Status)Response.Status.UNAUTHORIZED).build();
        }
        this.globalStorageService.set(bean.getKey(), bean.getValue());
        return Response.noContent().build();
    }

    @DELETE
    @Path(value="global")
    public Response deleteGlobalStorage(StorageBean bean) {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        if (user == null || bean == null || StringUtils.isEmpty((CharSequence)bean.getKey())) {
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).build();
        }
        if (!this.permissionManager.hasPermission((User)user, Permission.REMOVE, PermissionManager.TARGET_APPLICATION)) {
            return Response.status((Response.Status)Response.Status.UNAUTHORIZED).build();
        }
        this.globalStorageService.remove(bean.getKey());
        return Response.noContent().build();
    }
}

