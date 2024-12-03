/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap$Builder
 */
package com.atlassian.plugins.navlink.producer.capabilities;

import com.atlassian.plugins.navlink.producer.capabilities.ApplicationWithCapabilities;
import com.atlassian.plugins.navlink.producer.capabilities.RemoteApplicationWithCapabilities;
import com.google.common.collect.ImmutableMap;
import java.time.ZonedDateTime;
import java.util.Map;

public class RemoteApplicationWithCapabilitiesBuilder {
    private String applicationLinkId;
    private String selfUrl;
    private String type;
    private ZonedDateTime buildDateTime = ApplicationWithCapabilities.NULL_DATE;
    private ImmutableMap.Builder<String, String> capabilitiesBuilder = new ImmutableMap.Builder();

    public RemoteApplicationWithCapabilitiesBuilder setApplicationLinkId(String applicationLinkId) {
        this.applicationLinkId = applicationLinkId;
        return this;
    }

    public RemoteApplicationWithCapabilitiesBuilder setSelfUrl(String selfUrl) {
        this.selfUrl = selfUrl;
        return this;
    }

    public RemoteApplicationWithCapabilitiesBuilder setType(String type) {
        this.type = type;
        return this;
    }

    public RemoteApplicationWithCapabilitiesBuilder setBuildDateTime(ZonedDateTime buildDateTime) {
        this.buildDateTime = buildDateTime;
        return this;
    }

    public RemoteApplicationWithCapabilitiesBuilder addAllCapabilities(Map<String, String> capabilities) {
        this.capabilitiesBuilder.putAll(capabilities);
        return this;
    }

    public RemoteApplicationWithCapabilitiesBuilder addCapability(String capabilityName, String capabilityUrl) {
        this.capabilitiesBuilder.put((Object)capabilityName, (Object)capabilityUrl);
        return this;
    }

    public RemoteApplicationWithCapabilities build() {
        return new RemoteApplicationWithCapabilities(this.applicationLinkId, this.selfUrl, this.type, this.buildDateTime, (Map<String, String>)this.capabilitiesBuilder.build());
    }
}

