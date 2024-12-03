/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.plugin.notifications.api.notification;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class NotificationStatusRepresentation {
    @JsonProperty
    private final boolean enabled;
    @JsonProperty
    private final String jvmArg;

    @JsonCreator
    public NotificationStatusRepresentation(@JsonProperty(value="enabled") boolean enabled, @JsonProperty(value="jvmArg") String jvmArg) {
        this.enabled = enabled;
        this.jvmArg = jvmArg;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public String getJvmArg() {
        return this.jvmArg;
    }
}

