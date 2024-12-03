/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.plugins.rest.common.security.jersey.AdminOnlyResourceFilter
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  com.sun.jersey.spi.container.ResourceFilters
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.GET
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.healthcheck.rest;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.plugins.rest.common.security.jersey.AdminOnlyResourceFilter;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import com.atlassian.troubleshooting.api.healthcheck.SupportHealthStatus;
import com.atlassian.troubleshooting.healthcheck.api.HealthCheckUserSettingsService;
import com.atlassian.troubleshooting.healthcheck.api.model.HealthCheckUserSettings;
import com.atlassian.troubleshooting.healthcheck.persistence.service.HealthCheckWatcherService;
import com.atlassian.troubleshooting.healthcheck.util.SupportHealthCheckUtils;
import com.sun.jersey.spi.container.ResourceFilters;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import org.springframework.beans.factory.annotation.Autowired;

@ExperimentalApi
@Produces(value={"application/json"})
@Consumes(value={"application/json"})
@ResourceFilters(value={AdminOnlyResourceFilter.class})
@Path(value="/user-setting/{username}")
public class HealthCheckUserSettingsResource {
    private final HealthCheckUserSettingsService userSettingsService;
    private final UserManager userManager;
    private final HealthCheckWatcherService watcherService;

    @Autowired
    public HealthCheckUserSettingsResource(HealthCheckUserSettingsService userSettingsService, UserManager userManager, HealthCheckWatcherService watcherService) {
        this.userSettingsService = userSettingsService;
        this.userManager = userManager;
        this.watcherService = watcherService;
    }

    @GET
    public HealthCheckUserSettings getUserSettings(@PathParam(value="username") String username) {
        UserKey userKey = this.getUserKey(username);
        return this.getUserSettings(userKey);
    }

    @GET
    @WebSudoRequired
    @Path(value="/notification-severity")
    public SupportHealthStatus.Severity getSeverityThresholdForNotifications(@PathParam(value="username") String username) {
        return this.getUserSettings(username).getSeverityThresholdForNotifications();
    }

    @PUT
    @WebSudoRequired
    @Path(value="/notification-severity")
    public SupportHealthStatus.Severity setSeverityThresholdForNotifications(@PathParam(value="username") String username, SupportHealthStatus.Severity severity) {
        UserKey userKey = this.getUserKey(username);
        this.userSettingsService.setSeverityForNotification(userKey, severity);
        return this.getUserSettings(userKey).getSeverityThresholdForNotifications();
    }

    @PUT
    @WebSudoRequired
    @Path(value="watch")
    public void watch(@PathParam(value="username") String username) {
        this.watcherService.watch(this.getUserKey(username));
    }

    @DELETE
    @WebSudoRequired
    @Path(value="watch")
    public void unwatch(@PathParam(value="username") String username) {
        this.watcherService.unwatch(this.getUserKey(username));
    }

    private HealthCheckUserSettings getUserSettings(UserKey userKey) {
        return this.userSettingsService.getUserSettings(userKey);
    }

    private UserKey getUserKey(String username) {
        return SupportHealthCheckUtils.getUserKey(this.userManager, username);
    }
}

