/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.DateFormatter
 *  com.atlassian.confluence.core.FormatSettingsManager
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.extra.dynamictasklist2;

import com.atlassian.confluence.core.DateFormatter;
import com.atlassian.confluence.core.FormatSettingsManager;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.user.User;
import java.util.Date;

public class DateRenderer {
    private final UserAccessor userAccessor;
    private final FormatSettingsManager formatSettingsManager;
    private final LocaleManager localeManager;

    public DateRenderer(UserAccessor userAccessor, FormatSettingsManager formatSettingsManager, LocaleManager localeManager) {
        this.userAccessor = userAccessor;
        this.formatSettingsManager = formatSettingsManager;
        this.localeManager = localeManager;
    }

    public String render(long time, boolean showTime) {
        if (time == 0L) {
            return "N/A";
        }
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        DateFormatter formatter = this.userAccessor.getConfluenceUserPreferences((User)user).getDateFormatter(this.formatSettingsManager, this.localeManager);
        if (showTime) {
            return formatter.formatDateTime(new Date(time));
        }
        return formatter.format(new Date(time));
    }
}

