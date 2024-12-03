/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonAutoDetect
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnore
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.plugins.mobile.dto.notification;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonAutoDetect
public class RegistrationDto {
    private String id;
    private String os;
    private String build;
    private String token;
    private String deviceId;

    @JsonCreator
    public RegistrationDto(@JsonProperty(value="os") String os, @JsonProperty(value="build") String build, @JsonProperty(value="token") String token, @JsonProperty(value="deviceId") String deviceId) {
        this(null, os, build, token, deviceId);
    }

    public RegistrationDto(String id, String os, String build, String token, String deviceId) {
        this.id = id;
        this.os = os;
        this.build = build;
        this.token = token;
        this.deviceId = deviceId;
    }

    public String getId() {
        return this.id;
    }

    public String getOs() {
        return this.os;
    }

    public String getBuild() {
        return this.build;
    }

    public String getToken() {
        return this.token;
    }

    public String getDeviceId() {
        return this.deviceId;
    }

    @JsonIgnore
    public String getAppName() {
        return String.format("confluence-%s-%s", this.os, this.build);
    }
}

