/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joda.time.DateTime
 */
package com.atlassian.confluence.extra.calendar3.util;

import com.atlassian.confluence.extra.calendar3.SupportedSystemProperties;
import org.joda.time.DateTime;

public interface BuildInformationManager
extends SupportedSystemProperties {
    public String getPluginKey();

    public String getVersion();

    public DateTime getBuildDate();

    public DateTime getLegacySubCalendarsMigrationCutoffDate();

    public boolean isNotificationsEnabled();

    public boolean isShowingWhatsNew();

    public boolean isWorkboxNotificationEnabled();
}

