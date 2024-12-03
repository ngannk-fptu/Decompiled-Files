/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.notifications.api.medium.ServerConfiguration
 *  com.atlassian.plugin.notifications.spi.UserRole
 */
package com.atlassian.confluence.notifications.impl.spi;

import com.atlassian.plugin.notifications.api.medium.ServerConfiguration;
import com.atlassian.plugin.notifications.spi.UserRole;

public interface StaticServerPreferenceKeyProvider {
    public String getPreferenceKey(ServerConfiguration var1, UserRole var2);
}

