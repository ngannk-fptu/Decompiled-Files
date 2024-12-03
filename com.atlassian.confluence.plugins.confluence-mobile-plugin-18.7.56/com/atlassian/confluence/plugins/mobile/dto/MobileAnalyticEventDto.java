/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.plugins.mobile.dto;

import java.util.Map;
import org.codehaus.jackson.annotate.JsonProperty;

public final class MobileAnalyticEventDto {
    @JsonProperty
    private String name;
    @JsonProperty
    private long clientTime;
    @JsonProperty
    private String appVersion;
    @JsonProperty
    private String os;
    @JsonProperty
    private String osVersion;
    @JsonProperty
    private String deviceModel;
    @JsonProperty
    private Map<String, Object> properties;

    public MobileAnalyticEventDto() {
    }

    public MobileAnalyticEventDto(String name, long clientTime, String appVersion, String os, String osVersion, String deviceModel, Map<String, Object> properties) {
        this.name = name;
        this.clientTime = clientTime;
        this.appVersion = appVersion;
        this.os = os;
        this.osVersion = osVersion;
        this.deviceModel = deviceModel;
        this.properties = properties;
    }

    public String getName() {
        return this.name;
    }

    public long getClientTime() {
        return this.clientTime;
    }

    public String getAppVersion() {
        return this.appVersion;
    }

    public String getOs() {
        return this.os;
    }

    public String getOsVersion() {
        return this.osVersion;
    }

    public String getDeviceModel() {
        return this.deviceModel;
    }

    public Map<String, Object> getProperties() {
        return this.properties;
    }
}

