/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.bandana.BandanaManager
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  com.google.common.collect.ImmutableMap
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.user.actions;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.core.FormAware;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.confluence.user.actions.AbstractUserProfileAction;
import com.atlassian.confluence.util.HTMLPairType;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ViewMyEmailSettingsAction
extends AbstractUserProfileAction
implements FormAware {
    private static final Logger log = LoggerFactory.getLogger(ViewMyEmailSettingsAction.class);
    private boolean editMode;
    private String mimeType;
    private String undoSetting;
    private String undoPreviousValue;
    private String undoMsgValue;
    private Map<String, String> undoActionMap = ImmutableMap.builder().put((Object)"subscribe-to-recommended", (Object)"/plugins/dailysummary/settings.action?setting=subscribe-to-recommended").put((Object)"switch-schedule", (Object)"/plugins/dailysummary/settings.action?setting=switch-schedule").build();
    private Map<String, String> undoMessageMap = ImmutableMap.builder().put((Object)"subscribe-to-recommended", (Object)"daily.summary.setting.undo.subscribe.message").put((Object)"switch-schedule", (Object)"daily.summary.setting.undo.schedulechange.message").build();
    public static final String RECOMMENDED_UPDATES_DEFAULT_ENABLED_KEY = "atl.confluence.plugins.confluence-daily-summary-email:admin.defaultEnabled";
    private BandanaManager bandanaManager;

    @PermittedMethods(value={HttpMethod.GET})
    public String doView() throws Exception {
        this.editMode = false;
        return "success";
    }

    public String doEdit() throws Exception {
        this.editMode = true;
        return "input";
    }

    public boolean isNotifyForMyOwnActions() {
        if (this.getUserPreferences() != null) {
            return this.getUserPreferences().getBoolean("confluence.prefs.notify.for.my.own.actions");
        }
        return false;
    }

    public boolean isWatchMyOwnContent() {
        if (this.getUserPreferences() != null) {
            return this.getUserPreferences().getBoolean("confluence.prefs.watch.my.own.content");
        }
        return false;
    }

    public boolean isShowDiffInEmailNotifications() {
        if (this.getUserPreferences() != null) {
            return this.getUserPreferences().getBoolean("confluence.prefs.email.show.diff");
        }
        return false;
    }

    public boolean isNotifyByEmail() {
        if (this.getAuthenticatedUser() != null) {
            return this.notificationManager.getDailyReportNotificationForUser(this.getAuthenticatedUser()) != null;
        }
        return false;
    }

    public boolean isSiteBlogWatchForUser() {
        if (this.getAuthenticatedUser() != null) {
            return this.notificationManager.getSiteBlogNotificationForUser(this.getAuthenticatedUser()) != null;
        }
        return false;
    }

    public boolean isWatchingNetwork() {
        if (this.getAuthenticatedUser() != null) {
            return this.notificationManager.getNetworkNotificationForUser(this.getAuthenticatedUser()) != null;
        }
        return false;
    }

    public boolean isNotifyOnNewFollowers() {
        if (this.getUserPreferences() != null) {
            return this.getUserPreferences().getBoolean("confluence.prefs.notify.on.new.followers");
        }
        return false;
    }

    public boolean isReceiveRecommendedEmail() {
        if (this.getUserPreferences() != null && this.getUserPreferences().getBoolean("confluence.prefs.daily.summary.receive.updates.set")) {
            return this.getUserPreferences().getBoolean("confluence.prefs.daily.summary.receive.updates");
        }
        String strVal = (String)this.bandanaManager.getValue((BandanaContext)ConfluenceBandanaContext.GLOBAL_CONTEXT, RECOMMENDED_UPDATES_DEFAULT_ENABLED_KEY);
        if (strVal != null) {
            return Boolean.parseBoolean(strVal);
        }
        return true;
    }

    public String getMimeType() {
        if (this.mimeType == null && this.getUserPreferences() != null) {
            this.mimeType = this.getUserPreferences().getString("confluence.prefs.email.mimetype");
        }
        if (this.mimeType == null || this.mimeType.equals("html")) {
            this.mimeType = "text/html";
        }
        return this.mimeType;
    }

    public String getMimeTypeUserFriendly() {
        try {
            if (this.getMimeType() == null || this.getMimeType().equals("text/html")) {
                return this.getText("mimetypes.HTML");
            }
            return this.getText("mimetypes.Text");
        }
        catch (RuntimeException e) {
            log.error("Unable to look up user's preferred mime type. User = " + this.getUser() + ", " + e.toString(), (Throwable)e);
            throw e;
        }
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public List<HTMLPairType> getMimeTypes() {
        ArrayList<HTMLPairType> mimeTypes = new ArrayList<HTMLPairType>();
        mimeTypes.add(new HTMLPairType(this.getText("mimetypes.HTML"), "text/html"));
        mimeTypes.add(new HTMLPairType(this.getText("mimetypes.Text"), "text/plain"));
        return mimeTypes;
    }

    @Override
    public boolean isEditMode() {
        return this.editMode;
    }

    public boolean isUndoSettingsNeeded() {
        return !StringUtils.isBlank((CharSequence)this.undoSetting) && !StringUtils.isBlank((CharSequence)this.getUndoPreviousValue()) && this.getActionForUndoSetting() != null;
    }

    public void setUndoSetting(String setting) {
        this.undoSetting = setting;
    }

    public String getUndoSetting() {
        return this.undoSetting;
    }

    public String getUndoPreviousValue() {
        return this.undoPreviousValue;
    }

    public void setUndoPreviousValue(String previousValue) {
        this.undoPreviousValue = previousValue;
    }

    public void setUndoMsgValue(String value) {
        this.undoMsgValue = value;
    }

    public String getUndoMsgValue() {
        return this.undoMsgValue;
    }

    public String getActionForUndoSetting() {
        return this.undoActionMap.get(this.getUndoSetting());
    }

    public String getUndoConfirmationLabel() {
        String i18nPrevValue = this.getI18n().getText(this.undoMessageMap.get(this.undoSetting) + "." + this.getUndoMsgValue());
        if (i18nPrevValue == null) {
            i18nPrevValue = this.undoMsgValue;
        }
        return this.getI18n().getText(this.undoMessageMap.get(this.undoSetting), new Object[]{HtmlUtil.htmlEncode(i18nPrevValue)});
    }

    public void setBandanaManager(BandanaManager bandanaManager) {
        this.bandanaManager = bandanaManager;
    }
}

