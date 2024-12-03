/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.troubleshooting.healthcheck.api.model;

import com.atlassian.troubleshooting.api.healthcheck.HealthCheckStatus;
import com.google.common.collect.ImmutableList;
import java.util.Collections;
import java.util.List;
import org.codehaus.jackson.annotate.JsonProperty;

public class HealthCheckStatusReport {
    @JsonProperty
    private final List<HealthCheckStatus> statuses;

    public HealthCheckStatusReport(List<HealthCheckStatus> statuses) {
        this.statuses = statuses != null ? ImmutableList.copyOf(statuses) : Collections.emptyList();
    }

    public List<HealthCheckStatus> getStatuses() {
        return this.statuses;
    }
}

