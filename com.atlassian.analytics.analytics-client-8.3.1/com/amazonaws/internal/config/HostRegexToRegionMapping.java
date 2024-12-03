/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.internal.config;

import com.amazonaws.annotation.Immutable;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

@Immutable
public class HostRegexToRegionMapping {
    private final String regionName;
    private final Pattern hostNameRegexPattern;

    public HostRegexToRegionMapping(String hostNameRegex, String regionName) {
        if (hostNameRegex == null || hostNameRegex.isEmpty()) {
            throw new IllegalArgumentException("Invalid HostRegexToRegionMapping configuration: hostNameRegex must be non-empty");
        }
        try {
            this.hostNameRegexPattern = Pattern.compile(hostNameRegex);
        }
        catch (PatternSyntaxException e) {
            throw new IllegalArgumentException("Invalid HostRegexToRegionMapping configuration: hostNameRegex is not a valid regex", e);
        }
        if (regionName == null || regionName.isEmpty()) {
            throw new IllegalArgumentException("Invalid HostRegexToRegionMapping configuration: regionName must be non-empty");
        }
        this.regionName = regionName;
    }

    public String getRegionName() {
        return this.regionName;
    }

    public boolean isHostNameMatching(String hostname) {
        return this.hostNameRegexPattern.matcher(hostname).matches();
    }
}

