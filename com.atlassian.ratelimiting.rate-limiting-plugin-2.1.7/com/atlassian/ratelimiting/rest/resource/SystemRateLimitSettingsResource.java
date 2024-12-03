/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.permission.PermissionEnforcer
 *  com.sun.jersey.spi.resource.Singleton
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.GET
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.ratelimiting.rest.resource;

import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.ratelimiting.dmz.DmzRateLimitSettingsModificationService;
import com.atlassian.ratelimiting.dmz.RateLimitingMode;
import com.atlassian.ratelimiting.dmz.SystemJobControlSettings;
import com.atlassian.ratelimiting.dmz.SystemRateLimitingSettings;
import com.atlassian.ratelimiting.dmz.TokenBucketSettings;
import com.atlassian.ratelimiting.rest.api.RestJobControlSettings;
import com.atlassian.ratelimiting.rest.api.RestRateLimitSettings;
import com.atlassian.ratelimiting.rest.api.RestTokenBucketSettings;
import com.atlassian.ratelimiting.rest.utils.RestUtils;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.permission.PermissionEnforcer;
import com.sun.jersey.spi.resource.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@AnonymousAllowed
@Consumes(value={"application/json"})
@Path(value="admin/rate-limit/settings")
@Produces(value={"application/json;charset=UTF-8"})
@Singleton
public class SystemRateLimitSettingsResource {
    private static final Logger logger = LoggerFactory.getLogger(SystemRateLimitSettingsResource.class);
    private final DmzRateLimitSettingsModificationService rateLimitSettingsService;
    private final I18nResolver i18nResolver;
    private final PermissionEnforcer permissionEnforcer;

    public SystemRateLimitSettingsResource(DmzRateLimitSettingsModificationService rateLimitSettingsService, I18nResolver i18nResolver, PermissionEnforcer permissionEnforcer) {
        this.rateLimitSettingsService = rateLimitSettingsService;
        this.i18nResolver = i18nResolver;
        this.permissionEnforcer = permissionEnforcer;
    }

    @GET
    public RestRateLimitSettings getGlobalSettings() {
        this.permissionEnforcer.enforceSystemAdmin();
        RestRateLimitSettings result = RestRateLimitSettings.valueOf(this.rateLimitSettingsService.getRateLimitingMode(), this.rateLimitSettingsService.getSystemDefaultSettings());
        logger.debug("Returning default Rate limiting settings: [{}]", (Object)result);
        return result;
    }

    @PUT
    public Response updateGlobalSettings(RestRateLimitSettings restRateLimitSettings) {
        this.permissionEnforcer.enforceSystemAdmin();
        logger.debug("Updating default Rate limiting settings: [{}]", (Object)restRateLimitSettings);
        TokenBucketSettings tokenBucketSettings = RestUtils.validateRestTokenBucketSettings(new RestTokenBucketSettings(restRateLimitSettings.getDefaultCapacity(), restRateLimitSettings.getDefaultFillRate(), restRateLimitSettings.getDefaultIntervalFrequency(), restRateLimitSettings.getDefaultIntervalTimeUnit()), this.i18nResolver);
        SystemRateLimitingSettings systemRateLimitingSettings = new SystemRateLimitingSettings.Builder().mode(RateLimitingMode.valueOf(restRateLimitSettings.getMode().name())).bucketSettings(tokenBucketSettings).build();
        this.rateLimitSettingsService.updateSystemDefaultSettings(systemRateLimitingSettings);
        return Response.ok().entity((Object)restRateLimitSettings).build();
    }

    @PUT
    @Path(value="/jobs")
    public Response updateJobControlSettings(RestJobControlSettings jobControlSettings) {
        this.permissionEnforcer.enforceSystemAdmin();
        logger.debug("Updating Rate limiting job control settings: [{}]", (Object)jobControlSettings);
        SystemJobControlSettings systemRateLimitingSettings = RestUtils.validateRestJobControlSettings(jobControlSettings, this.i18nResolver);
        SystemJobControlSettings updatedSystemSettings = this.rateLimitSettingsService.updateJobControlSettings(systemRateLimitingSettings);
        return Response.ok().entity((Object)new RestJobControlSettings(updatedSystemSettings)).build();
    }
}

