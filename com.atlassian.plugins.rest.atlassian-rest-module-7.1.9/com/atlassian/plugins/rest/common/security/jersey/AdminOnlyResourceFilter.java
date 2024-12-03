/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.sal.api.user.UserManager
 */
package com.atlassian.plugins.rest.common.security.jersey;

import com.atlassian.plugins.rest.common.security.AuthenticationRequiredException;
import com.atlassian.plugins.rest.common.security.AuthorisationException;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import com.sun.jersey.spi.container.ResourceFilter;
import java.util.Objects;
import javax.ws.rs.ext.Provider;

@Deprecated
@Provider
public class AdminOnlyResourceFilter
implements ResourceFilter,
ContainerRequestFilter {
    private final UserManager userManager;

    public AdminOnlyResourceFilter(UserManager userManager) {
        this.userManager = Objects.requireNonNull(userManager);
    }

    @Override
    public ContainerRequestFilter getRequestFilter() {
        return this;
    }

    @Override
    public ContainerResponseFilter getResponseFilter() {
        return null;
    }

    @Override
    public ContainerRequest filter(ContainerRequest containerRequest) {
        UserKey userKey = this.userManager.getRemoteUserKey();
        if (userKey == null) {
            throw new AuthenticationRequiredException();
        }
        if (!this.userManager.isAdmin(userKey)) {
            throw new AuthorisationException("Client must be authenticated as an administrator to access this resource.");
        }
        return containerRequest;
    }
}

