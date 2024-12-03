/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserProfile
 */
package com.atlassian.plugin.notifications.api.macros;

import com.atlassian.plugin.notifications.api.medium.ServerConfiguration;
import com.atlassian.sal.api.user.UserProfile;

public interface UserProfileMappingResolver {
    public String resolveMapping(UserProfile var1, ServerConfiguration var2);
}

