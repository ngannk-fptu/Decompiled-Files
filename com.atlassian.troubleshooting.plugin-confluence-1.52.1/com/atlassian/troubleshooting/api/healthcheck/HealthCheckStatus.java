/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.troubleshooting.api.healthcheck;

import com.atlassian.troubleshooting.api.healthcheck.SupportHealthStatus;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;
import org.codehaus.jackson.annotate.JsonProperty;

public class HealthCheckStatus {
    @JsonProperty
    private final int id;
    @JsonProperty
    private final boolean isSoftLaunch;
    @JsonProperty
    private final boolean isEnabled;
    @JsonProperty
    private final String completeKey;
    @JsonProperty
    private final String name;
    @JsonProperty
    private final String description;
    @JsonProperty
    private final boolean isHealthy;
    @JsonProperty
    private final String failureReason;
    @JsonProperty
    private final String application;
    @JsonProperty
    private final String nodeId;
    @JsonProperty
    private final long time;
    @JsonProperty
    private final SupportHealthStatus.Severity severity;
    @JsonProperty
    private final String documentation;
    @JsonProperty
    private final String tag;
    @JsonProperty
    private final Set<Link> additionalLinks;

    private HealthCheckStatus() {
        this(HealthCheckStatus.builder());
    }

    private HealthCheckStatus(Builder builder) {
        this.id = builder.id;
        this.isSoftLaunch = builder.isSoftLaunch;
        this.isEnabled = builder.isEnabled;
        this.completeKey = builder.completeKey;
        this.name = builder.name;
        this.description = builder.description;
        this.isHealthy = builder.isHealthy;
        this.failureReason = builder.failureReason;
        this.application = builder.application;
        this.nodeId = builder.nodeId;
        this.time = builder.time != 0L ? builder.time : new Date().getTime();
        this.severity = builder.severity;
        this.documentation = builder.documentation;
        this.tag = builder.tag;
        this.additionalLinks = builder.additionalLinks;
    }

    public static Builder builder() {
        return new Builder();
    }

    public int getId() {
        return this.id;
    }

    public boolean isSoftLaunch() {
        return this.isSoftLaunch;
    }

    public boolean isEnabled() {
        return this.isEnabled;
    }

    public String getCompleteKey() {
        return this.completeKey;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public boolean isHealthy() {
        return this.isHealthy;
    }

    public String getFailureReason() {
        return this.failureReason;
    }

    public String getApplication() {
        return this.application;
    }

    public String getNodeId() {
        return this.nodeId;
    }

    public long getTime() {
        return this.time;
    }

    public SupportHealthStatus.Severity getSeverity() {
        return this.severity;
    }

    public String getDocumentation() {
        return this.documentation;
    }

    public String getTag() {
        return this.tag;
    }

    public Set<Link> getAdditionalLinks() {
        return this.additionalLinks;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Name: ");
        sb.append(this.getName());
        sb.append("\n");
        sb.append("NodeId: ");
        sb.append(this.getNodeId());
        sb.append("\n");
        sb.append("Is healthy: ");
        sb.append(this.isHealthy());
        sb.append("\n");
        sb.append("Failure reason: ");
        sb.append(this.getFailureReason());
        sb.append("\n");
        sb.append("Severity: ");
        sb.append((Object)this.getSeverity());
        sb.append("Additional links: ");
        sb.append(this.getAdditionalLinks());
        sb.append("\n");
        return sb.toString();
    }

    public static class Link {
        @JsonProperty
        private final String displayName;
        @JsonProperty
        private final String url;

        public Link(String displayName, String url) {
            this.displayName = displayName;
            this.url = url;
        }

        public String getDisplayName() {
            return this.displayName;
        }

        public String getUrl() {
            return this.url;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Display name: ");
            sb.append(this.getDisplayName());
            sb.append("\n");
            sb.append("Url: ");
            sb.append(this.getUrl());
            sb.append("\n");
            return sb.toString();
        }
    }

    public static class Builder {
        private int id;
        private String completeKey;
        private String name;
        private String description;
        private boolean isHealthy = false;
        private String failureReason;
        private String application;
        private String nodeId;
        private long time;
        private SupportHealthStatus.Severity severity = SupportHealthStatus.Severity.UNDEFINED;
        private String documentation;
        private String tag;
        private boolean isSoftLaunch;
        private Set<Link> additionalLinks = new LinkedHashSet<Link>();
        private boolean isEnabled = true;

        private Builder() {
        }

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder isSoftLaunch(boolean softLaunch) {
            this.isSoftLaunch = softLaunch;
            return this;
        }

        public Builder isEnabled(boolean isEnabled) {
            this.isEnabled = isEnabled;
            return this;
        }

        public Builder completeKey(String completeKey) {
            this.completeKey = completeKey;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder isHealthy(boolean isHealthy) {
            this.isHealthy = isHealthy;
            return this;
        }

        public Builder failureReason(String failureReason) {
            this.failureReason = failureReason;
            return this;
        }

        public Builder application(String application) {
            this.application = application;
            return this;
        }

        public Builder nodeId(String nodeId) {
            this.nodeId = nodeId;
            return this;
        }

        public Builder time(long time) {
            this.time = time;
            return this;
        }

        public Builder severity(SupportHealthStatus.Severity severity) {
            this.severity = severity;
            return this;
        }

        public Builder documentation(String documentation) {
            this.documentation = documentation;
            return this;
        }

        public Builder tag(String tag) {
            this.tag = tag;
            return this;
        }

        public Builder additionalLinks(Iterable<Link> links) {
            links.forEach(this.additionalLinks::add);
            return this;
        }

        public HealthCheckStatus build() {
            return new HealthCheckStatus(this);
        }
    }
}

