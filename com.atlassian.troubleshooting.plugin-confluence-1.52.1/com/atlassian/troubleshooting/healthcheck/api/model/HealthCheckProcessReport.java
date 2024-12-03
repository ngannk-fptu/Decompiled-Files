/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.troubleshooting.healthcheck.api.model;

import com.atlassian.troubleshooting.healthcheck.api.model.HealthCheck;
import com.atlassian.troubleshooting.healthcheck.api.model.HealthChecks;
import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class HealthCheckProcessReport {
    @JsonProperty
    private final UUID processId;
    @JsonProperty
    private final Collection<HealthCheck> healthCheckRepresentations;

    @JsonCreator
    private HealthCheckProcessReport() {
        this(HealthCheckProcessReport.builder());
    }

    private HealthCheckProcessReport(Builder builder) {
        this.processId = builder.processId != null ? builder.processId : null;
        this.healthCheckRepresentations = builder.healthCheckRepresentations != null && !builder.healthCheckRepresentations.getHealthCheckRepresentations().isEmpty() ? ImmutableList.copyOf(builder.healthCheckRepresentations.getHealthCheckRepresentations()) : Collections.emptyList();
    }

    public static Builder builder() {
        return new Builder();
    }

    public Collection<HealthCheck> getChecks() {
        return this.healthCheckRepresentations;
    }

    public UUID getProcessId() {
        return this.processId;
    }

    public static class Builder {
        private UUID processId;
        private HealthChecks healthCheckRepresentations;

        private Builder() {
        }

        public Builder processId(UUID processId) {
            this.processId = processId;
            return this;
        }

        public Builder checks(HealthChecks healthCheckRepresentations) {
            this.healthCheckRepresentations = healthCheckRepresentations;
            return this;
        }

        public HealthCheckProcessReport build() {
            return new HealthCheckProcessReport(this);
        }
    }
}

