/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.RestEndpointGroup;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

public class RestApiConfig {
    private boolean enabled;
    private final Set<RestEndpointGroup> enabledGroups = Collections.synchronizedSet(EnumSet.noneOf(RestEndpointGroup.class));

    public RestApiConfig() {
        for (RestEndpointGroup eg : RestEndpointGroup.values()) {
            if (!eg.isEnabledByDefault()) continue;
            this.enabledGroups.add(eg);
        }
    }

    public RestApiConfig enableAllGroups() {
        return this.enableGroups(RestEndpointGroup.values());
    }

    public RestApiConfig enableGroups(RestEndpointGroup ... endpointGroups) {
        if (endpointGroups != null) {
            this.enabledGroups.addAll(Arrays.asList(endpointGroups));
        }
        return this;
    }

    public RestApiConfig disableAllGroups() {
        this.enabledGroups.clear();
        return this;
    }

    public RestApiConfig disableGroups(RestEndpointGroup ... endpointGroups) {
        if (endpointGroups != null) {
            this.enabledGroups.removeAll(Arrays.asList(endpointGroups));
        }
        return this;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public boolean isEnabledAndNotEmpty() {
        return this.enabled && !this.enabledGroups.isEmpty();
    }

    public RestApiConfig setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public Set<RestEndpointGroup> getEnabledGroups() {
        return new HashSet<RestEndpointGroup>(this.enabledGroups);
    }

    public boolean isGroupEnabled(RestEndpointGroup group) {
        return this.enabledGroups.contains((Object)group);
    }

    public void setEnabledGroups(Collection<RestEndpointGroup> groups) {
        this.enabledGroups.clear();
        if (groups != null) {
            this.enabledGroups.addAll(groups);
        }
    }

    public String toString() {
        return "RestApiConfig{enabled=" + this.enabled + ", enabledGroups=" + this.enabledGroups + "}";
    }
}

