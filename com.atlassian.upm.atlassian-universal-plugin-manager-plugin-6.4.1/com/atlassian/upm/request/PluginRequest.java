/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserProfile
 *  org.joda.time.DateTime
 */
package com.atlassian.upm.request;

import com.atlassian.sal.api.user.UserProfile;
import com.atlassian.upm.api.util.Option;
import java.util.Objects;
import java.util.function.Function;
import org.joda.time.DateTime;

public class PluginRequest {
    private final String pluginKey;
    private final String pluginName;
    private final UserProfile user;
    private final DateTime timestamp;
    private final Option<String> message;
    private static final Function<PluginRequest, String> toPluginKey = PluginRequest::getPluginKey;

    public PluginRequest(String pluginKey, String pluginName, UserProfile user, DateTime timestamp, Option<String> message) {
        this.pluginKey = Objects.requireNonNull(pluginKey, "pluginKey");
        this.pluginName = Objects.requireNonNull(pluginName, "pluginName");
        this.user = Objects.requireNonNull(user, "user");
        this.timestamp = Objects.requireNonNull(timestamp, "timestamp");
        this.message = Objects.requireNonNull(message, "message");
    }

    public String getPluginKey() {
        return this.pluginKey;
    }

    public String getPluginName() {
        return this.pluginName;
    }

    public UserProfile getUser() {
        return this.user;
    }

    public DateTime getTimestamp() {
        return this.timestamp;
    }

    public Option<String> getMessage() {
        return this.message;
    }

    public static Function<PluginRequest, String> toPluginKey() {
        return toPluginKey;
    }
}

