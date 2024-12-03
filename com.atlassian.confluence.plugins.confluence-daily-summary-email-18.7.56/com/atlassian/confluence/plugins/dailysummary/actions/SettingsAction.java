/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ConfluenceActionSupport
 *  com.atlassian.confluence.core.ConfluenceSystemProperties
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.util.GeneralUtil
 *  com.atlassian.core.user.preferences.UserPreferences
 *  com.atlassian.user.User
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.dailysummary.actions;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.core.ConfluenceSystemProperties;
import com.atlassian.confluence.plugins.dailysummary.components.SingleUseUnsubscribeTokenManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.core.user.preferences.UserPreferences;
import com.atlassian.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SettingsAction
extends ConfluenceActionSupport {
    static final String SWITCH_SCHEDULE = "switch-schedule";
    static final String SUBSCRIBE_TO_SUMMARY = "subscribe-to-recommended";
    private static final Logger log = LoggerFactory.getLogger(SettingsAction.class);
    private static final String USER_PREFERENCE_SCHEDULE = "confluence.prefs.daily.summary.schedule";
    private String setting;
    private String value;
    private boolean undo;
    private Object previousValue;
    private String token;
    private String username;
    private SingleUseUnsubscribeTokenManager unsubscribeTokenManager;

    public boolean isPermitted() {
        if (this.getAuthenticatedUser() == null) {
            return this.getUserPreferenceKey() != null && SUBSCRIBE_TO_SUMMARY.equals(this.getSetting()) && this.username != null;
        }
        return super.isPermitted();
    }

    public void validate() {
        if (this.getUserPreferenceKey() != null) {
            this.addActionError("Could not set value");
        }
        super.validate();
    }

    public String execute() throws Exception {
        ConfluenceUser authenticatedUser = AuthenticatedUserThreadLocal.get();
        if (this.username != null && this.anonActionAllowedForSetting() && (authenticatedUser == null || !this.username.equals(authenticatedUser.getName()))) {
            ConfluenceUser user = this.userAccessor.getUserByName(this.username);
            if (user != null) {
                if (this.unsubscribeTokenManager.isValidToken((User)user, this.token)) {
                    return this.executeAnonAction();
                }
                this.addActionError(this.getText("daily.summary.setting.invalid.token"));
            }
            return "error";
        }
        if (authenticatedUser != null) {
            return this.executeAuthAction((User)authenticatedUser);
        }
        return "error";
    }

    private boolean anonActionAllowedForSetting() {
        return this.setting != null && this.setting.equals(SUBSCRIBE_TO_SUMMARY);
    }

    String executeAnonAction() throws Exception {
        if (!this.anonActionAllowedForSetting()) {
            this.addActionError(this.getText("daily.summary.setting.invalid.parameter", new Object[]{"setting"}));
            return "error";
        }
        if (this.username != null) {
            ConfluenceUser user = this.userAccessor.getUserByName(this.username);
            if (user != null) {
                UserPreferences prefs = this.userAccessor.getUserPreferences((User)user);
                prefs.setBoolean("confluence.prefs.daily.summary.receive.updates.set", true);
                prefs.setBoolean("confluence.prefs.daily.summary.receive.updates", false);
                log.info("{} changed subscription to recommended updates email using token {}", (Object)this.username, (Object)this.token);
                return "anon-changedsubscription";
            }
            log.error("User not found for username {}", (Object)this.username);
        } else {
            log.warn("Username parameter was null, cannot adjust user settings for anonymous user");
        }
        this.addActionError(this.getText("daily.summary.setting.invalid.parameter", new Object[]{"username"}));
        return "error";
    }

    String executeAuthAction(User user) throws Exception {
        String retVal;
        UserPreferences prefs = this.userAccessor.getUserPreferences(user);
        Object value = this.getValueForKey();
        String key = this.getUserPreferenceKey();
        if (SUBSCRIBE_TO_SUMMARY.equals(this.setting)) {
            prefs.setBoolean("confluence.prefs.daily.summary.receive.updates.set", true);
            retVal = "changedsubscription";
            this.previousValue = value instanceof Boolean ? Boolean.valueOf((Boolean)value == false) : Boolean.valueOf(true);
        } else if (SWITCH_SCHEDULE.equals(this.setting)) {
            this.previousValue = prefs.getString(key);
            if (this.previousValue == null) {
                this.previousValue = "weekly";
            }
            retVal = "changedschedule";
        } else {
            log.warn("Invalid key for setting {}:{}, no changes made ", (Object)this.setting, (Object)this.undo);
            this.addActionError(this.getText("daily.summary.setting.invalid.parameter", new Object[]{"setting"}));
            return "error";
        }
        if (this.undo) {
            retVal = "undone";
        }
        if (this.setting == null || key == null) {
            log.warn("Invalid setting {}:{}, no changes made ", (Object)this.setting, (Object)key);
            this.addActionError("daily.summary.setting.invalid.parameter", new Object[]{"setting"});
            return "error";
        }
        if (value instanceof Boolean) {
            prefs.setBoolean(key, ((Boolean)value).booleanValue());
        } else if (value instanceof Long) {
            prefs.setLong(key, ((Long)value).longValue());
        } else if (value instanceof String) {
            prefs.setString(key, (String)value);
        }
        return retVal;
    }

    public String getSetting() {
        return this.setting;
    }

    public void setSetting(String setting) {
        this.setting = setting;
    }

    public Object getPreviousValue() {
        return this.previousValue;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setUndo(boolean undo) {
        this.undo = undo;
    }

    private String getUserPreferenceKey() {
        if (this.setting == null) {
            return null;
        }
        if (this.setting.equals(SWITCH_SCHEDULE)) {
            return USER_PREFERENCE_SCHEDULE;
        }
        if (this.setting.equals(SUBSCRIBE_TO_SUMMARY)) {
            return "confluence.prefs.daily.summary.receive.updates";
        }
        return null;
    }

    private Object getValueForKey() {
        if (this.setting.equals(SWITCH_SCHEDULE)) {
            SCHEDULE schedule = SCHEDULE.valueOf(this.value);
            if (!schedule.equals((Object)SCHEDULE.hourly) || ConfluenceSystemProperties.isDevMode()) {
                return SCHEDULE.valueOf(this.value).toString();
            }
        } else if (this.setting.equals(SUBSCRIBE_TO_SUMMARY)) {
            return Boolean.parseBoolean(this.value);
        }
        throw new IllegalStateException("Could not parse value " + GeneralUtil.htmlEncode((String)this.value) + " : " + GeneralUtil.htmlEncode((String)this.setting));
    }

    public void setUnsubscribeTokenManager(SingleUseUnsubscribeTokenManager tokenManager) {
        this.unsubscribeTokenManager = tokenManager;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return this.username;
    }

    private static enum SCHEDULE {
        daily,
        weekly,
        hourly;

    }
}

