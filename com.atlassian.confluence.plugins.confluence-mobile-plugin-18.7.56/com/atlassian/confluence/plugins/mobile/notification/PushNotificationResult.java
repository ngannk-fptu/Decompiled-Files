/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.plugins.mobile.notification;

import java.util.List;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class PushNotificationResult {
    private final List<ResultItem> invalidPushes;
    private final List<ResultItem> newPushes;

    @JsonCreator
    public PushNotificationResult(@JsonProperty(value="invalidPushes") List<ResultItem> invalidPushes, @JsonProperty(value="newPushes") List<ResultItem> newPushes) {
        this.invalidPushes = invalidPushes;
        this.newPushes = newPushes;
    }

    public List<ResultItem> getInvalidPushes() {
        return this.invalidPushes;
    }

    public List<ResultItem> getNewPushes() {
        return this.newPushes;
    }

    public static class ResultItem {
        private String registrationId;
        private String endpoint;

        @JsonCreator
        public ResultItem(@JsonProperty(value="registrationId") String registrationId, @JsonProperty(value="endpoint") String endpoint) {
            this.registrationId = registrationId;
            this.endpoint = endpoint;
        }

        public String getRegistrationId() {
            return this.registrationId;
        }

        public String getEndpoint() {
            return this.endpoint;
        }
    }
}

