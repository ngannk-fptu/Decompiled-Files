/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.plugins.mobile.notification;

import org.codehaus.jackson.annotate.JsonProperty;

public class PushNotificationContent {
    @JsonProperty
    private String appName;
    @JsonProperty
    private String notificationId;
    @JsonProperty
    private String registrationId;
    @JsonProperty
    private String endpoint;
    @JsonProperty
    private String token;

    public PushNotificationContent(String appName, String notificationId, String registrationId, String endpoint, String token) {
        this.appName = appName;
        this.notificationId = notificationId;
        this.registrationId = registrationId;
        this.endpoint = endpoint;
        this.token = token;
    }

    public String getAppName() {
        return this.appName;
    }

    public String getNotificationId() {
        return this.notificationId;
    }

    public String getRegistrationId() {
        return this.registrationId;
    }

    public String getEndpoint() {
        return this.endpoint;
    }

    public String getToken() {
        return this.token;
    }
}

