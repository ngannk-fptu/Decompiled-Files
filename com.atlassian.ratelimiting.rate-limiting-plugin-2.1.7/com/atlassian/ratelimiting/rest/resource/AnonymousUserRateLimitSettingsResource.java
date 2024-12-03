/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.permission.PermissionEnforcer
 *  com.sun.jersey.spi.resource.Singleton
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.GET
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.ratelimiting.rest.resource;

import com.atlassian.ratelimiting.dmz.DmzRateLimitSettingsModificationService;
import com.atlassian.ratelimiting.rest.api.RestAnonymousUserRateLimitSettingsUpdateRequest;
import com.atlassian.ratelimiting.rest.api.RestBulkUserRateLimitSettingsUpdateRequest;
import com.atlassian.ratelimiting.rest.resource.AbstractUserRateLimitSettingsResource;
import com.atlassian.ratelimiting.user.UserService;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.permission.PermissionEnforcer;
import com.sun.jersey.spi.resource.Singleton;
import java.util.Collections;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path(value="admin/rate-limit/settings/users/anonymous")
@Consumes(value={"application/json"})
@Produces(value={"application/json;charset=UTF-8"})
@Singleton
public class AnonymousUserRateLimitSettingsResource
extends AbstractUserRateLimitSettingsResource {
    private static final Logger logger = LoggerFactory.getLogger(AnonymousUserRateLimitSettingsResource.class);

    public AnonymousUserRateLimitSettingsResource(I18nResolver i18nService, DmzRateLimitSettingsModificationService rateLimitSettingsService, UserService userService, PermissionEnforcer permissionEnforcer) {
        super(i18nService, rateLimitSettingsService, userService, permissionEnforcer);
    }

    @GET
    public Response getSettings() {
        return super.getSettings("rate_limiting_anonymous_user-67d5362d-3b2f-4531-9039-5f041bdd402a");
    }

    @PUT
    @Path(value="/token-bucket")
    public Response updateSettingsForAnonymousUser(RestAnonymousUserRateLimitSettingsUpdateRequest request) {
        return super.updateSettingsForMultipleUsers(RestBulkUserRateLimitSettingsUpdateRequest.builder().userIds(Collections.singletonList("rate_limiting_anonymous_user-67d5362d-3b2f-4531-9039-5f041bdd402a")).tokenBucketSettings(request.getTokenBucketSettings()).build());
    }

    @PUT
    @Path(value="/whitelist")
    public Response whitelistMultipleUsers() {
        return super.whitelistMultipleUsers(Collections.singleton("rate_limiting_anonymous_user-67d5362d-3b2f-4531-9039-5f041bdd402a"));
    }

    @PUT
    @Path(value="/blacklist")
    public Response blacklistMultipleUsers() {
        return super.blacklistMultipleUsers(Collections.singleton("rate_limiting_anonymous_user-67d5362d-3b2f-4531-9039-5f041bdd402a"));
    }

    @DELETE
    public Response deleteSettings() {
        return super.deleteSettings("rate_limiting_anonymous_user-67d5362d-3b2f-4531-9039-5f041bdd402a");
    }
}

