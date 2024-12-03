/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.rest.dto.UserDto
 *  com.atlassian.confluence.plugins.rest.dto.UserDtoFactory
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.WebApplicationException
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.confluence.plugins.mobile.rest;

import com.atlassian.confluence.plugins.mobile.AnonymousUserSupport;
import com.atlassian.confluence.plugins.mobile.rest.ProfileResourceInterface;
import com.atlassian.confluence.plugins.rest.dto.UserDto;
import com.atlassian.confluence.plugins.rest.dto.UserDtoFactory;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

@Path(value="/profile")
public class ProfileResource
implements ProfileResourceInterface {
    private final UserDtoFactory userDtoFactory;
    private final UserAccessor userAccessor;
    private final AnonymousUserSupport anonymousUserSupport;

    public ProfileResource(UserDtoFactory userDtoFactory, UserAccessor userAccessor, AnonymousUserSupport anonymousUserSupport) {
        this.userDtoFactory = userDtoFactory;
        this.userAccessor = userAccessor;
        this.anonymousUserSupport = anonymousUserSupport;
    }

    @Override
    @GET
    @Path(value="/{username}")
    @AnonymousAllowed
    @Produces(value={"application/json"})
    public UserDto getProfile(@PathParam(value="username") String username) {
        ConfluenceUser user = this.userAccessor.getUserByName(username);
        if (user == null || !this.anonymousUserSupport.isProfileViewPermitted()) {
            this.throwNotFoundResponse();
        }
        return this.userDtoFactory.getUserDto(user);
    }

    private void throwNotFoundResponse() {
        if (AuthenticatedUserThreadLocal.isAnonymousUser()) {
            throw new WebApplicationException(Response.status((Response.Status)Response.Status.UNAUTHORIZED).build());
        }
        throw new WebApplicationException(Response.status((Response.Status)Response.Status.NOT_FOUND).build());
    }
}

