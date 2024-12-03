/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.rest.common.security.CorsAllowed
 *  com.google.common.base.Function
 *  com.google.common.collect.Collections2
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.healthcheck.core.rest;

import com.atlassian.healthcheck.core.ExtendedHealthCheck;
import com.atlassian.healthcheck.core.HealthCheckModuleDescriptorNotFoundException;
import com.atlassian.healthcheck.core.HealthStatus;
import com.atlassian.healthcheck.core.HealthStatusExtended;
import com.atlassian.healthcheck.core.impl.HealthCheckManager;
import com.atlassian.healthcheck.core.impl.Pair;
import com.atlassian.healthcheck.core.rest.HealthCheckRepresentation;
import com.atlassian.healthcheck.core.rest.HealthCheckRepresentations;
import com.atlassian.healthcheck.core.rest.HealthCheckStatusesRepresentation;
import com.atlassian.healthcheck.core.rest.HealthStatusRepresentation;
import com.atlassian.healthcheck.core.security.PermissionValidationService;
import com.atlassian.plugins.rest.common.security.CorsAllowed;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path(value="/")
@Produces(value={"application/xml", "application/json"})
public class HealthCheckResource {
    private static final Logger log = LoggerFactory.getLogger(HealthCheckResource.class);
    private static final Response KEYS_TAGS_BAD_REQUEST_RESPONSE = Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)"Providing both key/s and tag/s is not valid. Please provide one or the other.").build();
    private final HealthCheckManager healthCheckManager;
    private final PermissionValidationService permissionValidationService;

    public HealthCheckResource(HealthCheckManager healthCheckManager, PermissionValidationService permissionValidationService) {
        this.healthCheckManager = Objects.requireNonNull(healthCheckManager);
        this.permissionValidationService = Objects.requireNonNull(permissionValidationService);
    }

    @Path(value="/check")
    @GET
    public Response check(@QueryParam(value="tag") Set<String> tags) {
        this.permissionValidationService.validateIsAdmin();
        Collection<Pair<ExtendedHealthCheck, HealthStatus>> statuses = tags != null && !tags.isEmpty() ? this.healthCheckManager.performChecksWithTags(tags) : this.healthCheckManager.performChecks();
        ArrayList<HealthStatusRepresentation> failures = new ArrayList<HealthStatusRepresentation>();
        for (Pair<ExtendedHealthCheck, HealthStatus> status : statuses) {
            if (status.getRight().isHealthy()) continue;
            failures.add(this.statusToStatusRepresentation(status));
        }
        if (failures.isEmpty()) {
            return Response.ok().build();
        }
        HealthCheckStatusesRepresentation healthCheck = new HealthCheckStatusesRepresentation(failures);
        return Response.status((Response.Status)Response.Status.SERVICE_UNAVAILABLE).entity((Object)healthCheck).build();
    }

    @Path(value="/checkDetails")
    @GET
    @CorsAllowed
    public Response checkDetails(@QueryParam(value="key") Set<String> healthCheckKeys, @QueryParam(value="tag") Set<String> tags) {
        Collection<Pair<ExtendedHealthCheck, HealthStatus>> statuses;
        this.permissionValidationService.validateIsAdmin();
        if (this.isKeysAndTagsDefined(healthCheckKeys, tags)) {
            return KEYS_TAGS_BAD_REQUEST_RESPONSE;
        }
        if (healthCheckKeys != null && !healthCheckKeys.isEmpty()) {
            try {
                statuses = this.healthCheckManager.performChecksWithKeys(healthCheckKeys);
            }
            catch (HealthCheckModuleDescriptorNotFoundException e) {
                return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)String.format("Health check module descriptor with key '%s' was not found.", e.getUnfoundKey())).build();
            }
        } else {
            statuses = tags != null && !tags.isEmpty() ? this.healthCheckManager.performChecksWithTags(tags) : this.healthCheckManager.performChecks();
        }
        ArrayList<HealthStatusRepresentation> statusRepresentations = new ArrayList<HealthStatusRepresentation>(statuses.size());
        boolean isHealthy = true;
        for (Pair<ExtendedHealthCheck, HealthStatus> status : statuses) {
            isHealthy &= status.getRight().isHealthy();
            statusRepresentations.add(this.statusToStatusRepresentation(status));
        }
        HealthCheckStatusesRepresentation healthCheck = new HealthCheckStatusesRepresentation(statusRepresentations);
        if (isHealthy) {
            return Response.ok((Object)healthCheck).build();
        }
        return Response.status((Response.Status)Response.Status.SERVICE_UNAVAILABLE).entity((Object)healthCheck).build();
    }

    @Path(value="/list")
    @GET
    public Response list(@QueryParam(value="tag") Set<String> tags) {
        this.permissionValidationService.validateIsAdmin();
        Collection<ExtendedHealthCheck> healthChecks = tags != null && !tags.isEmpty() ? this.healthCheckManager.getHealthChecksWithTags(tags) : this.healthCheckManager.getHealthChecks();
        return Response.ok((Object)new HealthCheckRepresentations(this.checksToCheckRepresentations(healthChecks))).build();
    }

    private HealthStatusRepresentation statusToStatusRepresentation(Pair<ExtendedHealthCheck, HealthStatus> entry) {
        ExtendedHealthCheck healthCheck = entry.getLeft();
        HealthStatus status = entry.getRight();
        if (status instanceof HealthStatusExtended) {
            return new HealthStatusRepresentation(healthCheck.getName(), healthCheck.getDescription(), status.isHealthy(), status.failureReason(), status.getApplication().name(), status.getTime(), ((HealthStatusExtended)status).getSeverity(), ((HealthStatusExtended)status).getDocumentation());
        }
        return new HealthStatusRepresentation(healthCheck.getName(), healthCheck.getDescription(), status.isHealthy(), status.failureReason(), status.getApplication().name(), status.getTime());
    }

    private Collection<HealthCheckRepresentation> checksToCheckRepresentations(Collection<ExtendedHealthCheck> healthChecks) {
        return Collections2.transform(healthChecks, (Function)new Function<ExtendedHealthCheck, HealthCheckRepresentation>(){

            public HealthCheckRepresentation apply(ExtendedHealthCheck healthCheck) {
                return new HealthCheckRepresentation(healthCheck.getName(), healthCheck.getDescription(), healthCheck.getKey(), healthCheck.getTag(), healthCheck.getTimeOut());
            }
        });
    }

    private boolean isKeysAndTagsDefined(Set<String> keys, Set<String> tags) {
        return keys != null && !keys.isEmpty() && tags != null && !tags.isEmpty();
    }
}

