/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 *  javax.annotation.Nonnull
 */
package com.atlassian.confluence.plugins.dailysummary.components;

import com.atlassian.user.User;
import java.util.Date;
import java.util.List;
import javax.annotation.Nonnull;

public interface SummaryEmailNotificationManager {
    public static final String PLUGIN_SETTINGS_KEY = "atl.confluence.plugins.confluence-daily-summary-email:";
    public static final String DEFAULT_SCHEDULED_KEY = "atl.confluence.plugins.confluence-daily-summary-email:admin.defaultSchedule";
    public static final String DEFAULT_ENABLED_KEY = "atl.confluence.plugins.confluence-daily-summary-email:admin.defaultEnabled";

    @Nonnull
    public List<User> getUsersToReceiveNotificationAt(List<String> var1, Date var2, String var3, boolean var4);
}

