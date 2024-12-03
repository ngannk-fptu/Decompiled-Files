/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.plugins.rest.common.security.jersey.AdminOnlyResourceFilter
 *  com.atlassian.sal.api.timezone.TimeZoneManager
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.sal.api.user.UserManager
 *  com.sun.jersey.spi.container.ResourceFilters
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.healthcheck.rest;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.plugins.rest.common.security.jersey.AdminOnlyResourceFilter;
import com.atlassian.sal.api.timezone.TimeZoneManager;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.troubleshooting.api.healthcheck.ExtendedSupportHealthCheck;
import com.atlassian.troubleshooting.api.healthcheck.HealthCheckFilter;
import com.atlassian.troubleshooting.api.healthcheck.HealthCheckStatus;
import com.atlassian.troubleshooting.api.healthcheck.SupportHealthCheckManager;
import com.atlassian.troubleshooting.api.healthcheck.exception.InvalidHealthCheckFilterException;
import com.atlassian.troubleshooting.healthcheck.api.model.HealthCheck;
import com.atlassian.troubleshooting.healthcheck.api.model.HealthCheckProcessReport;
import com.atlassian.troubleshooting.healthcheck.api.model.HealthCheckStatusReport;
import com.atlassian.troubleshooting.healthcheck.api.model.HealthChecks;
import com.atlassian.troubleshooting.healthcheck.persistence.NotificationParam;
import com.atlassian.troubleshooting.healthcheck.persistence.service.HealthCheckDisabledService;
import com.atlassian.troubleshooting.healthcheck.persistence.service.HealthStatusPropertiesPersistenceService;
import com.atlassian.troubleshooting.healthcheck.persistence.service.NotificationService;
import com.atlassian.troubleshooting.healthcheck.rest.HealthCheckPropertiesRepresentation;
import com.atlassian.troubleshooting.healthcheck.util.SupportHealthCheckUtils;
import com.sun.jersey.spi.container.ResourceFilters;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Autowired;

@Path(value="/")
@Produces(value={"application/json"})
@Consumes(value={"application/json"})
@ResourceFilters(value={AdminOnlyResourceFilter.class})
public class HealthCheckResource {
    private final HealthStatusPropertiesPersistenceService healthStatusPropertiesPersistenceService;
    private final NotificationService notificationService;
    private final SupportHealthCheckManager healthCheckManager;
    private final UserManager userManager;
    private final TimeZoneManager timeZoneManager;
    private final HealthCheckDisabledService disabledService;

    @Autowired
    public HealthCheckResource(HealthStatusPropertiesPersistenceService healthStatusPropertiesPersistenceService, NotificationService notificationService, SupportHealthCheckManager healthCheckManager, UserManager userManager, TimeZoneManager timeZoneManager, HealthCheckDisabledService disabledService) {
        this.healthStatusPropertiesPersistenceService = healthStatusPropertiesPersistenceService;
        this.notificationService = notificationService;
        this.healthCheckManager = healthCheckManager;
        this.userManager = userManager;
        this.timeZoneManager = timeZoneManager;
        this.disabledService = disabledService;
    }

    @Path(value="/check")
    @GET
    public HealthCheckStatusReport check() {
        return new HealthCheckStatusReport(this.healthCheckManager.runAllHealthChecks());
    }

    @Path(value="/check/{username}")
    @GET
    public HealthCheckStatusReport getStatusesForUser(@PathParam(value="username") String username) {
        UserKey userKey = SupportHealthCheckUtils.getUserKey(this.userManager, username);
        List<HealthCheckStatus> statuses = this.notificationService.getStatusesForUserNotifications(userKey);
        return new HealthCheckStatusReport(statuses);
    }

    @Deprecated
    @Path(value="/checkDetails")
    @GET
    public Response checkDetails(@QueryParam(value="key") Set<String> healthCheckKeys, @QueryParam(value="tag") Set<String> healthCheckTags) {
        try {
            HealthCheckFilter filter = HealthCheckFilter.builder().keys(healthCheckKeys).tags(healthCheckTags).build();
            return Response.ok((Object)new HealthCheckStatusReport(this.healthCheckManager.runHealthChecks(this.healthCheckManager.getHealthChecks(filter)))).build();
        }
        catch (InvalidHealthCheckFilterException e) {
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)e.getMessage()).build();
        }
    }

    @Path(value="/check/process")
    @POST
    public Response startHealthCheckProcess() {
        try {
            UUID processId = this.healthCheckManager.runAllHealthChecksInBackground();
            Collection<HealthCheck> representation = this.checksToCheckJsonRepresentation(this.healthCheckManager.getAllHealthChecks());
            return Response.ok((Object)HealthCheckProcessReport.builder().processId(processId).checks(new HealthChecks(representation)).build()).build();
        }
        catch (Exception e) {
            return Response.status((Response.Status)Response.Status.INTERNAL_SERVER_ERROR).entity((Object)e.getMessage()).build();
        }
    }

    @Path(value="/check/process/{processId}/results")
    @GET
    public Response getResultsFromProcess(@PathParam(value="processId") String processId) {
        if (processId != null) {
            UUID uuid;
            try {
                uuid = UUID.fromString(processId);
            }
            catch (IllegalArgumentException e) {
                return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)e.getMessage()).build();
            }
            Optional<List<HealthCheckStatus>> report = this.healthCheckManager.getHealthCheckResults(uuid);
            if (report.isPresent()) {
                return Response.ok((Object)new HealthCheckStatusReport(report.get())).build();
            }
        }
        return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
    }

    @Path(value="/setEnabled")
    @POST
    public Response setEnabled(SetEnabledParams params) {
        Optional<ExtendedSupportHealthCheck> maybeCheck;
        if (params.getHealthCheckKey() != null && (maybeCheck = this.healthCheckManager.getHealthCheck(params.getHealthCheckKey())).isPresent()) {
            ExtendedSupportHealthCheck check = maybeCheck.get();
            check.setEnabled(params.isEnabled());
            this.disabledService.setDisabledHealthCheck(check, params.isEnabled());
            return Response.ok().build();
        }
        return Response.status((Response.Status)Response.Status.NOT_FOUND).entity((Object)"Health Check Key is invalid").build();
    }

    @Path(value="/list")
    @GET
    public Response list() {
        return Response.ok((Object)new HealthChecks(this.checksToCheckJsonRepresentation(this.healthCheckManager.getAllHealthChecks()))).build();
    }

    @Path(value="/dismissNotification")
    @POST
    public Response dismissNotification(NotificationParam params) {
        UserKey userkey = SupportHealthCheckUtils.getUserKey(this.userManager, params.getUsername());
        this.notificationService.storeDismissedNotification(userkey, params.getNotificationId(), params.getIsSnoozed());
        return Response.noContent().build();
    }

    @Path(value="/dismissNotification/{username}/{notificationId}")
    @GET
    public Response isAutoDismissed(@PathParam(value="username") String username, @PathParam(value="notificationId") Integer notificationId) {
        UserKey userkey = SupportHealthCheckUtils.getUserKey(this.userManager, username);
        Boolean isAutoDismissed = this.notificationService.checkIsAutoDismissed(userkey, notificationId);
        if (isAutoDismissed.booleanValue()) {
            return Response.status((Response.Status)Response.Status.OK).build();
        }
        return Response.noContent().build();
    }

    @Path(value="/lastRun")
    @GET
    public Response getLastRun() {
        HealthCheckPropertiesRepresentation propRepresentation = this.healthStatusPropertiesPersistenceService.getLastRun();
        if (propRepresentation != null) {
            long timeStamp = Long.parseLong(propRepresentation.getPropertyValue());
            String formattedDate = SupportHealthCheckUtils.formatRelativeDate(timeStamp, this.timeZoneManager);
            return Response.ok((Object)new HealthCheckPropertiesRepresentation("Scheduler Last Run", formattedDate)).build();
        }
        return Response.noContent().build();
    }

    @ExperimentalApi
    @Path(value="/deleteDismiss/{username}")
    @GET
    public Response deleteDismiss(@PathParam(value="username") String username) {
        UserKey userkey = SupportHealthCheckUtils.getUserKey(this.userManager, username);
        this.notificationService.deleteDismissByUser(userkey);
        return Response.noContent().build();
    }

    private Collection<HealthCheck> checksToCheckJsonRepresentation(Collection<ExtendedSupportHealthCheck> healthChecks) {
        return healthChecks.stream().map(SupportHealthCheckUtils::asHealthCheckJson).collect(Collectors.toList());
    }

    public static class SetEnabledParams {
        private String healthCheckKey;
        private boolean isEnabled;

        public String getHealthCheckKey() {
            return this.healthCheckKey;
        }

        public void setHealthCheckKey(String healthCheckKey) {
            this.healthCheckKey = healthCheckKey;
        }

        public boolean isEnabled() {
            return this.isEnabled;
        }

        public void setEnabled(boolean enabled) {
            this.isEnabled = enabled;
        }
    }
}

