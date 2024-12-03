/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.jetbrains.annotations.Nullable
 */
package com.atlassian.migration.agent.dto.assessment;

import com.atlassian.migration.agent.dto.assessment.DeviceDto;
import com.atlassian.migration.agent.dto.assessment.NetworkDto;
import lombok.Generated;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.jetbrains.annotations.Nullable;

public class BrowserMetricsDto {
    @JsonProperty(value="browserName")
    @Nullable
    private String browserName;
    @JsonProperty(value="browserVersion")
    @Nullable
    private String browserVersion;
    @JsonProperty(value="platform")
    @Nullable
    private String platform;
    @JsonProperty(value="device")
    @Nullable
    private DeviceDto device;
    @JsonProperty(value="network")
    @Nullable
    private NetworkDto network;
    @JsonProperty(value="protocol")
    @Nullable
    private String protocol;

    @JsonCreator
    public BrowserMetricsDto(@JsonProperty(value="browserName") @Nullable String browserName, @JsonProperty(value="browserVersion") @Nullable String browserVersion, @JsonProperty(value="platform") @Nullable String platform, @JsonProperty(value="device") @Nullable DeviceDto device, @JsonProperty(value="network") @Nullable NetworkDto network, @JsonProperty(value="protocol") @Nullable String protocol) {
        this.browserName = browserName;
        this.browserVersion = browserVersion;
        this.platform = platform;
        this.device = device;
        this.network = network;
        this.protocol = protocol;
    }

    @Nullable
    @Generated
    public String getBrowserName() {
        return this.browserName;
    }

    @Nullable
    @Generated
    public String getBrowserVersion() {
        return this.browserVersion;
    }

    @Nullable
    @Generated
    public String getPlatform() {
        return this.platform;
    }

    @Nullable
    @Generated
    public DeviceDto getDevice() {
        return this.device;
    }

    @Nullable
    @Generated
    public NetworkDto getNetwork() {
        return this.network;
    }

    @Nullable
    @Generated
    public String getProtocol() {
        return this.protocol;
    }

    @Generated
    public void setBrowserName(@Nullable String browserName) {
        this.browserName = browserName;
    }

    @Generated
    public void setBrowserVersion(@Nullable String browserVersion) {
        this.browserVersion = browserVersion;
    }

    @Generated
    public void setPlatform(@Nullable String platform) {
        this.platform = platform;
    }

    @Generated
    public void setDevice(@Nullable DeviceDto device) {
        this.device = device;
    }

    @Generated
    public void setNetwork(@Nullable NetworkDto network) {
        this.network = network;
    }

    @Generated
    public void setProtocol(@Nullable String protocol) {
        this.protocol = protocol;
    }
}

