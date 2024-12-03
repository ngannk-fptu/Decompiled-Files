/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.internal.config;

import com.amazonaws.internal.config.Builder;
import com.amazonaws.internal.config.HostRegexToRegionMapping;

public class HostRegexToRegionMappingJsonHelper
implements Builder<HostRegexToRegionMapping> {
    private String hostNameRegex;
    private String regionName;

    public String getHostNameRegex() {
        return this.hostNameRegex;
    }

    public void setHostNameRegex(String hostNameRegex) {
        this.hostNameRegex = hostNameRegex;
    }

    public String getRegionName() {
        return this.regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    @Override
    public HostRegexToRegionMapping build() {
        return new HostRegexToRegionMapping(this.hostNameRegex, this.regionName);
    }
}

