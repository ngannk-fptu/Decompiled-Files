/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.troubleshooting.healthcheck.api.model;

import org.codehaus.jackson.annotate.JsonProperty;

public class HealthCheck {
    @JsonProperty
    private final boolean isSoftLaunch;
    @JsonProperty
    private final boolean isEnabled;
    @JsonProperty
    private final String name;
    @JsonProperty
    private final String description;
    @JsonProperty
    private final String completeKey;
    @JsonProperty
    private final int timeout;
    @JsonProperty
    private final String tag;

    private HealthCheck() {
        this(HealthCheck.builder());
    }

    public HealthCheck(Builder builder) {
        this.isSoftLaunch = builder.isSoftLaunch;
        this.isEnabled = builder.isEnabled;
        this.name = builder.name;
        this.description = builder.description;
        this.completeKey = builder.completeKey;
        this.timeout = builder.timeout;
        this.tag = builder.tag;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getCompleteKey() {
        return this.completeKey;
    }

    public String getDescription() {
        return this.description;
    }

    public String getName() {
        return this.name;
    }

    public int getTimeout() {
        return this.timeout;
    }

    public String getTag() {
        return this.tag;
    }

    public String toString() {
        return String.format("name: %s\ndescription: %s\ncompleteKey: %s\ntimeout: %s\ntag: %s\n", this.getName(), this.getDescription(), this.getCompleteKey(), this.getTimeout(), this.getTag());
    }

    public static class Builder {
        private boolean isSoftLaunch;
        private boolean isEnabled;
        private String name;
        private String description;
        private String completeKey;
        private int timeout;
        private String tag;

        private Builder() {
        }

        public Builder isSoftLaunch(boolean isSoftLaunch) {
            this.isSoftLaunch = isSoftLaunch;
            return this;
        }

        public Builder isEnabled(boolean isEnabled) {
            this.isEnabled = isEnabled;
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

        public Builder completeKey(String completeKey) {
            this.completeKey = completeKey;
            return this;
        }

        public Builder timeout(int timeout) {
            this.timeout = timeout;
            return this;
        }

        public Builder tag(String tag) {
            this.tag = tag;
            return this;
        }

        public HealthCheck build() {
            return new HealthCheck(this);
        }
    }
}

