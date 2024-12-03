/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.plugins.synchrony.rest;

import org.codehaus.jackson.annotate.JsonProperty;

public class CollaborativeEditingConfigResponse {
    @JsonProperty
    private final boolean sharedDraftsEnabled;
    @JsonProperty
    private final String longRunningTaskId;
    @JsonProperty
    private final String longRunningTaskName;

    private CollaborativeEditingConfigResponse(boolean sharedDraftsEnabled, String longRunningTaskId, String longRunningTaskName) {
        this.sharedDraftsEnabled = sharedDraftsEnabled;
        this.longRunningTaskId = longRunningTaskId;
        this.longRunningTaskName = longRunningTaskName;
    }

    public static class Builder {
        private boolean sharedDraftsEnabled;
        private String longRunningTaskId;
        private String longRunningTaskName;

        public Builder setSharedDraftsEnabled(boolean sharedDraftsEnabled) {
            this.sharedDraftsEnabled = sharedDraftsEnabled;
            return this;
        }

        public Builder setLongRunningTaskId(String longRunningTaskId) {
            this.longRunningTaskId = longRunningTaskId;
            return this;
        }

        public Builder setLongRunningTaskName(String longRunningTaskName) {
            this.longRunningTaskName = longRunningTaskName;
            return this;
        }

        public CollaborativeEditingConfigResponse build() {
            return new CollaborativeEditingConfigResponse(this.sharedDraftsEnabled, this.longRunningTaskId, this.longRunningTaskName);
        }
    }
}

