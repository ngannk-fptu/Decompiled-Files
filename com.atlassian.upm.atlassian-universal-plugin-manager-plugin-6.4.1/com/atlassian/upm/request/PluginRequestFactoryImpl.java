/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.sal.api.user.UserManager
 *  org.joda.time.DateTime
 */
package com.atlassian.upm.request;

import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.request.PluginRequest;
import com.atlassian.upm.request.PluginRequestFactory;
import java.util.Objects;
import org.joda.time.DateTime;

public class PluginRequestFactoryImpl
implements PluginRequestFactory {
    private UserManager userManager;

    public PluginRequestFactoryImpl(UserManager userManager) {
        this.userManager = Objects.requireNonNull(userManager, "userManager");
    }

    @Override
    public PluginRequest getPluginRequest(UserKey userKey, String pluginKey, String pluginName, DateTime timestamp, Option<String> message) {
        return new PluginRequest(pluginKey, pluginName, this.userManager.getUserProfile(userKey), timestamp, message);
    }
}

