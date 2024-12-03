/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.DateFormatter
 *  com.atlassian.confluence.core.FormatSettingsManager
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.ConfluenceUserPreferences
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.soy.renderer.SoyServerFunction
 *  com.atlassian.user.User
 *  com.google.common.collect.ImmutableSet
 */
package com.atlassian.confluence.plugins.soy;

import com.atlassian.confluence.core.DateFormatter;
import com.atlassian.confluence.core.FormatSettingsManager;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.ConfluenceUserPreferences;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.soy.renderer.SoyServerFunction;
import com.atlassian.user.User;
import com.google.common.collect.ImmutableSet;
import java.util.Date;
import java.util.Set;

public class FormatTimeFunction
implements SoyServerFunction<String> {
    private final UserAccessor userAccessor;
    private final FormatSettingsManager formatSettingsManager;
    private final LocaleManager localeManager;

    public FormatTimeFunction(UserAccessor userAccessor, FormatSettingsManager formatSettingsManager, LocaleManager localeManager, I18NBeanFactory factory) {
        this.userAccessor = userAccessor;
        this.formatSettingsManager = formatSettingsManager;
        this.localeManager = localeManager;
    }

    public String getName() {
        return "formatTime";
    }

    public Set<Integer> validArgSizes() {
        return ImmutableSet.of((Object)1);
    }

    public String apply(Object ... args) {
        if (args.length != 1 || !(args[0] instanceof Date)) {
            throw new IllegalArgumentException();
        }
        Date date = (Date)args[0];
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        ConfluenceUserPreferences userPreferences = this.userAccessor.getConfluenceUserPreferences((User)user);
        DateFormatter dateFormatter = userPreferences.getDateFormatter(this.formatSettingsManager, this.localeManager);
        return dateFormatter.formatTime(date);
    }
}

