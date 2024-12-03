/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.permission.PermissionEnforcer
 *  com.atlassian.sal.api.user.UserProfile
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.ratelimiting.rest.resource;

import com.atlassian.ratelimiting.dmz.DmzRateLimitSettingsModificationService;
import com.atlassian.ratelimiting.dmz.TokenBucketSettings;
import com.atlassian.ratelimiting.dmz.UserRateLimitSettings;
import com.atlassian.ratelimiting.dmz.UserRateLimitSettingsSearchResult;
import com.atlassian.ratelimiting.rest.api.RestBulkUserRateLimitSettingsUpdateRequest;
import com.atlassian.ratelimiting.rest.api.RestUserRateLimitSettings;
import com.atlassian.ratelimiting.rest.utils.RestUtils;
import com.atlassian.ratelimiting.user.UserService;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.permission.PermissionEnforcer;
import com.atlassian.sal.api.user.UserProfile;
import java.util.Set;
import java.util.stream.Collectors;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractUserRateLimitSettingsResource {
    private static final Logger logger = LoggerFactory.getLogger(AbstractUserRateLimitSettingsResource.class);
    private final I18nResolver i18nResolver;
    protected final DmzRateLimitSettingsModificationService rateLimitSettingsService;
    protected final UserService userService;
    protected final PermissionEnforcer permissionEnforcer;

    protected AbstractUserRateLimitSettingsResource(I18nResolver i18nService, DmzRateLimitSettingsModificationService rateLimitSettingsService, UserService userService, PermissionEnforcer permissionEnforcer) {
        this.i18nResolver = i18nService;
        this.rateLimitSettingsService = rateLimitSettingsService;
        this.userService = userService;
        this.permissionEnforcer = permissionEnforcer;
    }

    protected Response getSettings(String userId) {
        this.permissionEnforcer.enforceSystemAdmin();
        logger.debug("Getting Rate limiting settings for user: [{}]", (Object)userId);
        UserProfile userProfile = RestUtils.validateUserProfile(userId, this.userService);
        return this.rateLimitSettingsService.getUserSettings(userProfile.getUserKey()).map(us -> Response.ok((Object)new RestUserRateLimitSettings((UserRateLimitSettings)us, userProfile)).build()).orElseGet(() -> Response.status((Response.Status)Response.Status.NOT_FOUND).build());
    }

    private RestUserRateLimitSettings mapSearchResult(UserRateLimitSettingsSearchResult searchResult) {
        return new RestUserRateLimitSettings(searchResult.getUserRateLimitSettings(), searchResult.getUserProfile());
    }

    protected Response updateSettingsForMultipleUsers(RestBulkUserRateLimitSettingsUpdateRequest request) {
        this.permissionEnforcer.enforceSystemAdmin();
        logger.debug("Attempting to update settings: [{}] for user(s): [{}]", (Object)request.getTokenBucketSettings(), request.getUserIds());
        Set<UserProfile> userProfiles = RestUtils.validateUserProfiles(request.getUserIds(), this.userService);
        TokenBucketSettings tokenBucketSettings = RestUtils.validateRestTokenBucketSettings(request.getTokenBucketSettings(), this.i18nResolver);
        Set userSettings = userProfiles.stream().map(userProfile -> {
            UserRateLimitSettings updated = this.rateLimitSettingsService.updateUserSettings(userProfile.getUserKey(), tokenBucketSettings);
            return new RestUserRateLimitSettings(updated, (UserProfile)userProfile);
        }).collect(Collectors.toSet());
        return Response.ok(userSettings).build();
    }

    protected Response whitelistMultipleUsers(Set<String> userIds) {
        this.permissionEnforcer.enforceSystemAdmin();
        logger.debug("Attempting to whitelist user(s): [{}]", userIds);
        Set<UserProfile> userProfiles = RestUtils.validateUserProfiles(userIds, this.userService);
        Set userSettings = userProfiles.stream().map(userProfile -> {
            UserRateLimitSettings updated = this.rateLimitSettingsService.whitelistUser(userProfile.getUserKey());
            return new RestUserRateLimitSettings(updated, (UserProfile)userProfile);
        }).collect(Collectors.toSet());
        return Response.ok(userSettings).build();
    }

    protected Response blacklistMultipleUsers(Set<String> userIds) {
        this.permissionEnforcer.enforceSystemAdmin();
        logger.debug("Attempting to blacklistUsers user(s): [{}]", userIds);
        Set<UserProfile> userProfiles = RestUtils.validateUserProfiles(userIds, this.userService);
        Set userSettings = userProfiles.stream().map(userProfile -> {
            UserRateLimitSettings updated = this.rateLimitSettingsService.blacklistUser(userProfile.getUserKey());
            return new RestUserRateLimitSettings(updated, (UserProfile)userProfile);
        }).collect(Collectors.toSet());
        return Response.ok(userSettings).build();
    }

    protected Response deleteSettings(String userId) {
        this.permissionEnforcer.enforceSystemAdmin();
        logger.debug("Attempting to delete settings for user: [{}]", (Object)userId);
        UserProfile userProfile = RestUtils.validateUserProfile(userId, this.userService);
        return this.rateLimitSettingsService.delete(userProfile.getUserKey()) ? Response.noContent().build() : Response.status((Response.Status)Response.Status.NOT_FOUND).build();
    }
}

