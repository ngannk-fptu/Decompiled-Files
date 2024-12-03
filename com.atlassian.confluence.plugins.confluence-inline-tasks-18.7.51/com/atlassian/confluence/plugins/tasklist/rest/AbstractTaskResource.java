/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.security.SpacePermissionManager
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.plugins.rest.common.security.AuthenticationContext
 *  com.atlassian.plugins.rest.common.security.AuthenticationRequiredException
 *  javax.ws.rs.core.Context
 *  javax.ws.rs.core.UriInfo
 */
package com.atlassian.confluence.plugins.tasklist.rest;

import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.plugins.rest.common.security.AuthenticationContext;
import com.atlassian.plugins.rest.common.security.AuthenticationRequiredException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

public abstract class AbstractTaskResource {
    protected final UserAccessor userAccessor;
    @Context
    protected AuthenticationContext authContext;
    @Context
    private UriInfo uriInfo;
    private SpacePermissionManager spacePermissionManager;

    AbstractTaskResource() {
        this.userAccessor = null;
    }

    public AbstractTaskResource(UserAccessor userAccessor, SpacePermissionManager spm) {
        this.userAccessor = userAccessor;
        this.spacePermissionManager = spm;
    }

    protected void createRequestContext() {
        if (!this.authContext.isAuthenticated() && !this.spacePermissionManager.hasPermission("USECONFLUENCE", null, null)) {
            throw new AuthenticationRequiredException();
        }
    }

    public void setAuthContext(AuthenticationContext authenticationContext) {
        this.authContext = authenticationContext;
    }
}

