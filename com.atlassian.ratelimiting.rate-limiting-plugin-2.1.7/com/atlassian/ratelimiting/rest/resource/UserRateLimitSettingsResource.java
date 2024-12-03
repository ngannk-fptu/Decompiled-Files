/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.permission.PermissionEnforcer
 *  com.sun.jersey.spi.resource.Singleton
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.DefaultValue
 *  javax.ws.rs.GET
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.ratelimiting.rest.resource;

import com.atlassian.ratelimiting.dmz.DmzRateLimitSettingsModificationService;
import com.atlassian.ratelimiting.dmz.UserRateLimitSettingsSearchRequest;
import com.atlassian.ratelimiting.dmz.UserRateLimitSettingsSearchResult;
import com.atlassian.ratelimiting.page.Page;
import com.atlassian.ratelimiting.page.PageRequest;
import com.atlassian.ratelimiting.rest.api.RestBulkUserRateLimitSettingsUpdateRequest;
import com.atlassian.ratelimiting.rest.api.RestExemptionsLimitInfo;
import com.atlassian.ratelimiting.rest.api.RestPage;
import com.atlassian.ratelimiting.rest.api.RestUserRateLimitSettings;
import com.atlassian.ratelimiting.rest.resource.AbstractUserRateLimitSettingsResource;
import com.atlassian.ratelimiting.rest.utils.RestUtils;
import com.atlassian.ratelimiting.user.UserService;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.permission.PermissionEnforcer;
import com.sun.jersey.spi.resource.Singleton;
import java.util.List;
import java.util.Set;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path(value="admin/rate-limit/settings/users")
@Consumes(value={"application/json"})
@Produces(value={"application/json;charset=UTF-8"})
@Singleton
public class UserRateLimitSettingsResource
extends AbstractUserRateLimitSettingsResource {
    private static final Logger logger = LoggerFactory.getLogger(UserRateLimitSettingsResource.class);

    public UserRateLimitSettingsResource(I18nResolver i18nService, DmzRateLimitSettingsModificationService rateLimitSettingsService, UserService userService, PermissionEnforcer permissionEnforcer) {
        super(i18nService, rateLimitSettingsService, userService, permissionEnforcer);
    }

    @Override
    @GET
    @Path(value="/{userId}")
    public Response getSettings(@PathParam(value="userId") String userId) {
        return super.getSettings(userId);
    }

    @GET
    public Response getAllSettings(@QueryParam(value="filter") List<String> filter, @DefaultValue(value="0") @QueryParam(value="page") int pageNumber, @DefaultValue(value="20") @QueryParam(value="size") int pageSize) {
        this.permissionEnforcer.enforceSystemAdmin();
        List<String> userKeyFilter = RestUtils.lookupUserKeysForUsernames(filter, this.userService);
        logger.debug("Getting All Rate limiting settings with filter: [{}] and paging info: [{},{}]", new Object[]{filter, pageNumber, pageSize});
        Page<UserRateLimitSettingsSearchResult> page = this.rateLimitSettingsService.searchUserSettings(new UserRateLimitSettingsSearchRequest(userKeyFilter), new PageRequest(pageNumber, pageSize));
        return Response.ok(new RestPage<RestUserRateLimitSettings>(page.map(this::mapSearchResult))).build();
    }

    private RestUserRateLimitSettings mapSearchResult(UserRateLimitSettingsSearchResult searchResult) {
        return new RestUserRateLimitSettings(searchResult.getUserRateLimitSettings(), searchResult.getUserProfile());
    }

    @Override
    @PUT
    @Path(value="/token-bucket")
    public Response updateSettingsForMultipleUsers(RestBulkUserRateLimitSettingsUpdateRequest request) {
        return super.updateSettingsForMultipleUsers(request);
    }

    @Override
    @PUT
    @Path(value="/whitelist")
    public Response whitelistMultipleUsers(Set<String> userIds) {
        return super.whitelistMultipleUsers(userIds);
    }

    @Override
    @PUT
    @Path(value="/blacklist")
    public Response blacklistMultipleUsers(Set<String> userIds) {
        return super.blacklistMultipleUsers(userIds);
    }

    @Override
    @DELETE
    @Path(value="/{userId}")
    public Response deleteSettings(@PathParam(value="userId") String userId) {
        return super.deleteSettings(userId);
    }

    @GET
    @Path(value="/exemptionsLimit")
    public Response getExemptionsLimitStatus() {
        this.permissionEnforcer.enforceSystemAdmin();
        RestExemptionsLimitInfo exemptionsInfo = RestExemptionsLimitInfo.builder().maxAllowed(this.rateLimitSettingsService.getExemptionsLimit()).current(this.rateLimitSettingsService.getExemptionsCount()).maxReached(this.rateLimitSettingsService.getExemptionsCount() >= this.rateLimitSettingsService.getExemptionsLimit()).build();
        return Response.ok((Object)exemptionsInfo).build();
    }
}

