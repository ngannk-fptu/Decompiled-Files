/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.troubleshooting.healthcheck.api.model;

import com.atlassian.troubleshooting.healthcheck.api.model.HealthCheck;
import java.util.Collection;
import org.codehaus.jackson.annotate.JsonProperty;

public class HealthChecks {
    @JsonProperty
    private final Collection<HealthCheck> healthCheckRepresentations;

    public HealthChecks(Collection<HealthCheck> healthCheckRepresentations) {
        this.healthCheckRepresentations = healthCheckRepresentations;
    }

    public Collection<HealthCheck> getHealthCheckRepresentations() {
        return this.healthCheckRepresentations;
    }
}

