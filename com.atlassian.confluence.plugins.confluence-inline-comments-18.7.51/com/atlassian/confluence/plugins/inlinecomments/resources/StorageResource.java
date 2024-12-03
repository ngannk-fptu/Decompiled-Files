/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.json.jsonorg.JSONObject
 *  com.atlassian.plugins.rest.common.security.RequiresXsrfCheck
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.GET
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.confluence.plugins.inlinecomments.resources;

import com.atlassian.confluence.plugins.inlinecomments.service.UserStorageService;
import com.atlassian.json.jsonorg.JSONObject;
import com.atlassian.plugins.rest.common.security.RequiresXsrfCheck;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path(value="storage")
public class StorageResource {
    private UserStorageService userStorageService;

    public StorageResource(UserStorageService userStorageService) {
        this.userStorageService = userStorageService;
    }

    @PUT
    @Path(value="/{key}")
    @Consumes(value={"application/json"})
    @Produces(value={"application/json"})
    @RequiresXsrfCheck
    public Response storeKeyForCurrentUser(@PathParam(value="key") String key) {
        this.userStorageService.storeKeyForCurrentUser(key);
        return Response.ok((Object)Response.Status.OK).build();
    }

    @GET
    @Path(value="/{key}")
    public String isKeyStoredForCurrentUser(@PathParam(value="key") String key) {
        JSONObject result = new JSONObject();
        return result.put("isExist", this.userStorageService.isKeyStoredForCurrentUser(key)).toString();
    }
}

