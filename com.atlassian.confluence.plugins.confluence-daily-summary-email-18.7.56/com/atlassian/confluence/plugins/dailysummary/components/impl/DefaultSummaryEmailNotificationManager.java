/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.security.login.LoginInfo
 *  com.atlassian.confluence.security.login.LoginManager
 *  com.atlassian.confluence.user.ConfluenceUserPreferences
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.core.user.preferences.UserPreferences
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.user.User
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.dailysummary.components.impl;

import com.atlassian.confluence.plugins.dailysummary.components.SummaryEmailNotificationManager;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.security.login.LoginInfo;
import com.atlassian.confluence.security.login.LoginManager;
import com.atlassian.confluence.user.ConfluenceUserPreferences;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.core.user.preferences.UserPreferences;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.User;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DefaultSummaryEmailNotificationManager
implements SummaryEmailNotificationManager {
    private final UserAccessor userAccessor;
    private final PermissionManager permissionManager;
    private final LoginManager loginManager;
    private static final int DEFAULT_HOUR = Integer.getInteger("daily.summary.send.hour", 13);
    private static final int DEFAULT_DAY_OF_WEEK = Integer.getInteger("daily.summary.send.dayofweek", 5);
    private static final Logger log = LoggerFactory.getLogger(DefaultSummaryEmailNotificationManager.class);
    private static final int FIRST_DAY_OF_WEEKEND = Integer.getInteger("daily.summary.weekend.one", 7);
    private static final int SECOND_DAY_OF_WEEKEND = Integer.getInteger("daily.summary.weekend.two", 1);

    public DefaultSummaryEmailNotificationManager(@ComponentImport UserAccessor userAccessor, @ComponentImport PermissionManager permissionManager, @ComponentImport LoginManager loginManager) {
        this.userAccessor = userAccessor;
        this.permissionManager = permissionManager;
        this.loginManager = loginManager;
    }

    @Override
    public List<User> getUsersToReceiveNotificationAt(List<String> userNames, Date date, String defaultSchedule, boolean defaultEnabled) {
        if (date == null) {
            throw new IllegalArgumentException("Notification date is null");
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return userNames.stream().map(arg_0 -> ((UserAccessor)this.userAccessor).getUserByName(arg_0)).filter(this.getUserFilter(cal, defaultSchedule, defaultEnabled)).collect(Collectors.toList());
    }

    protected Predicate<User> getUserFilter(Calendar cal, String defaultSchedule, boolean defaultEnabled) {
        return user -> {
            boolean sendMail;
            String schedule;
            if (user == null || StringUtils.isBlank((CharSequence)user.getEmail())) {
                return false;
            }
            ConfluenceUserPreferences confluenceUserPreferences = this.userAccessor.getConfluenceUserPreferences(user);
            UserPreferences userPreferences = confluenceUserPreferences.getWrappedPreferences();
            TimeZone tz = confluenceUserPreferences.getTimeZone().getWrappedTimeZone();
            if (tz != null) {
                cal.setTimeZone(tz);
            }
            if ((schedule = userPreferences.getString("confluence.prefs.daily.summary.schedule")) == null) {
                schedule = defaultSchedule;
            }
            if (cal.get(11) != DEFAULT_HOUR && !schedule.equals("hourly")) {
                log.debug("Not sending email to {}, hour of day is {} and needs to be {} in timezone : {}", new Object[]{user.getName(), cal.get(11), DEFAULT_HOUR, cal.getTimeZone()});
                return false;
            }
            if (userPreferences.getBoolean("confluence.prefs.daily.summary.receive.updates.set")) {
                if (!userPreferences.getBoolean("confluence.prefs.daily.summary.receive.updates")) {
                    log.debug("User {} is not subscribed to updates {} or not set and default is not enabled {}", new Object[]{user, userPreferences.getBoolean("confluence.prefs.daily.summary.receive.updates"), defaultEnabled});
                    return false;
                }
            } else {
                if (!defaultEnabled) {
                    return false;
                }
                if (!this.shouldSendToUserByDefault((User)user)) {
                    return false;
                }
            }
            boolean bl = sendMail = schedule.equals("daily") && cal.get(7) != FIRST_DAY_OF_WEEKEND && cal.get(7) != SECOND_DAY_OF_WEEKEND || schedule.equals("weekly") && cal.get(7) == DEFAULT_DAY_OF_WEEK || schedule.equals("hourly");
            if (log.isDebugEnabled()) {
                log.debug("Evaluating summary email condition for {} sending is: {}, hour : {}, enabled {}, schedule {}, dayofweek {}", new Object[]{user, sendMail, cal.get(11), userPreferences.getBoolean("confluence.prefs.daily.summary.receive.updates"), schedule, cal.get(7)});
            }
            return sendMail && this.permissionManager.hasPermission(user, Permission.VIEW, PermissionManager.TARGET_APPLICATION);
        };
    }

    private boolean shouldSendToUserByDefault(User user) {
        LoginInfo loginInfo = this.loginManager.getLoginInfo(user);
        if (loginInfo == null || loginInfo.getLastSuccessfulLoginDate() == null) {
            log.debug("User {} from external management has no successful previous login, not sending daily summary", (Object)user.getName());
            return false;
        }
        return true;
    }
}

