/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.AtlassianCoreException
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 */
package com.atlassian.confluence.user.actions;

import com.atlassian.confluence.core.FormAware;
import com.atlassian.confluence.user.actions.AbstractUserProfileAction;
import com.atlassian.confluence.util.HTMLPairType;
import com.atlassian.core.AtlassianCoreException;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import java.util.ArrayList;
import java.util.List;

public class EditEmailSettingsAction
extends AbstractUserProfileAction
implements FormAware {
    private boolean notifyByEmail = false;
    private boolean isSiteBlogWatchForUser;
    private boolean isWatchingNetwork;
    private boolean notifyForMyOwnActions = false;
    private boolean watchMyOwnContent = false;
    private boolean showDiffInEmailNotifications = false;
    private boolean showFullContentInEmailNotifications = false;
    private boolean notifyOnNewFollowers;
    private String mimeType;
    private boolean isReceiveRecommendedEmail = false;

    public String execute() throws Exception {
        this.updateUser();
        return "success";
    }

    @PermittedMethods(value={HttpMethod.GET, HttpMethod.POST})
    public String doInput() throws Exception {
        return "input";
    }

    public void setShowDiffInEmailNotifications(boolean showDiffInEmailNotifications) {
        this.showDiffInEmailNotifications = showDiffInEmailNotifications;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public void setNotifyForMyOwnActions(boolean notifyForMyOwnActions) {
        this.notifyForMyOwnActions = notifyForMyOwnActions;
    }

    public void setWatchMyOwnContent(boolean watchMyOwnContent) {
        this.watchMyOwnContent = watchMyOwnContent;
    }

    public void setNotifyByEmail(boolean notifyByEmail) {
        this.notifyByEmail = notifyByEmail;
    }

    public void setSiteBlogWatchForUser(boolean siteBlogWatchForUser) {
        this.isSiteBlogWatchForUser = siteBlogWatchForUser;
    }

    private void updateUser() throws AtlassianCoreException {
        if (this.notifyByEmail) {
            this.notificationManager.addDailyReportNotification(this.getAuthenticatedUser());
        } else {
            this.notificationManager.removeDailyReportNotification(this.getAuthenticatedUser());
        }
        this.notificationManager.setSiteBlogNotificationForUser(this.getAuthenticatedUser(), this.isSiteBlogWatchForUser);
        this.notificationManager.setNetworkNotificationForUser(this.getAuthenticatedUser(), this.isWatchingNetwork);
        this.getUserPreferences().setBoolean("confluence.prefs.notify.for.my.own.actions", this.notifyForMyOwnActions);
        this.getUserPreferences().setBoolean("confluence.prefs.watch.my.own.content", this.watchMyOwnContent);
        this.getUserPreferences().setBoolean("confluence.prefs.email.show.diff", this.showDiffInEmailNotifications);
        this.getUserPreferences().setString("confluence.prefs.email.mimetype", this.getMimeType());
        this.getUserPreferences().setBoolean("confluence.prefs.notify.on.new.followers", this.notifyOnNewFollowers);
        this.getUserPreferences().setBoolean("confluence.prefs.daily.summary.receive.updates.set", true);
        this.getUserPreferences().setBoolean("confluence.prefs.daily.summary.receive.updates", this.isReceiveRecommendedEmail);
    }

    @Override
    public boolean isPermitted() {
        return this.getUsername() != null && super.isPermitted();
    }

    @Override
    public boolean isEditMode() {
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

    public List<HTMLPairType> getMimeTypes() {
        ArrayList<HTMLPairType> mimeTypes = new ArrayList<HTMLPairType>();
        mimeTypes.add(new HTMLPairType(this.getText("mimetypes.HTML"), "text/html"));
        mimeTypes.add(new HTMLPairType(this.getText("mimetypes.Text"), "text/plain"));
        return mimeTypes;
    }

    public boolean isNotifyByEmail() {
        this.notifyByEmail = this.notificationManager.getDailyReportNotificationForUser(this.getAuthenticatedUser()) != null;
        return this.notifyByEmail;
    }

    public boolean isSiteBlogWatchForUser() {
        this.isSiteBlogWatchForUser = this.notificationManager.getSiteBlogNotificationForUser(this.getAuthenticatedUser()) != null;
        return this.isSiteBlogWatchForUser;
    }

    public boolean isWatchingNetwork() {
        this.isWatchingNetwork = this.notificationManager.getNetworkNotificationForUser(this.getAuthenticatedUser()) != null;
        return this.isWatchingNetwork;
    }

    public void setWatchingNetwork(boolean watchingNetwork) {
        this.isWatchingNetwork = watchingNetwork;
    }

    public boolean isNotifyForMyOwnActions() {
        return this.getUserPreferences().getBoolean("confluence.prefs.notify.for.my.own.actions");
    }

    public boolean isWatchMyOwnContent() {
        return this.getUserPreferences().getBoolean("confluence.prefs.watch.my.own.content");
    }

    public boolean isShowDiffInEmailNotifications() {
        return this.getUserPreferences().getBoolean("confluence.prefs.email.show.diff");
    }

    public boolean isNotifyOnNewFollowers() {
        return this.getUserPreferences().getBoolean("confluence.prefs.notify.on.new.followers");
    }

    public void setNotifyOnNewFollowers(boolean notifyOnNewFollowers) {
        this.notifyOnNewFollowers = notifyOnNewFollowers;
    }

    public boolean isReceiveRecommendedEmail() {
        this.isReceiveRecommendedEmail = this.getUserPreferences().getBoolean("confluence.prefs.daily.summary.receive.updates");
        return this.isReceiveRecommendedEmail;
    }

    public void setReceiveRecommendedEmail(boolean setIt) {
        this.isReceiveRecommendedEmail = setIt;
    }
}

