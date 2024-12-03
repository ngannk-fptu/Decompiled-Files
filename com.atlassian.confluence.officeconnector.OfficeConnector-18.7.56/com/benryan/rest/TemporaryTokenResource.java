/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.user.User
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.CacheControl
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 */
package com.benryan.rest;

import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.user.User;
import com.benryan.components.TemporaryAuthTokenManager;
import java.util.HashMap;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Response;

@Path(value="authtoken")
@Produces(value={"application/json"})
public class TemporaryTokenResource {
    private TemporaryAuthTokenManager authTokenManager;

    public TemporaryTokenResource(TemporaryAuthTokenManager manager) {
        this.authTokenManager = manager;
    }

    @GET
    public Response getAuthTokenForCurrentUserSession() {
        User user = AuthenticatedUserThreadLocal.getUser();
        if (user != null) {
            String token = this.authTokenManager.createToken(user);
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("token", token);
            CacheControl cacheControl = new CacheControl();
            cacheControl.setNoCache(true);
            cacheControl.setNoStore(true);
            cacheControl.setMustRevalidate(true);
            cacheControl.setProxyRevalidate(true);
            cacheControl.setMaxAge(0);
            cacheControl.setPrivate(true);
            return Response.ok(map).cacheControl(cacheControl).build();
        }
        return Response.status((Response.Status)Response.Status.FORBIDDEN).build();
    }
}

