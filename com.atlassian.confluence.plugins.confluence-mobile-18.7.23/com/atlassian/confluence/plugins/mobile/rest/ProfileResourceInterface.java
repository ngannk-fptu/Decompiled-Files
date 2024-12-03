/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.rest.dto.UserDto
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 */
package com.atlassian.confluence.plugins.mobile.rest;

import com.atlassian.confluence.plugins.rest.dto.UserDto;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

public interface ProfileResourceInterface {
    @GET
    @Path(value="/{username}")
    @AnonymousAllowed
    @Produces(value={"application/json"})
    public UserDto getProfile(@PathParam(value="username") String var1);
}

