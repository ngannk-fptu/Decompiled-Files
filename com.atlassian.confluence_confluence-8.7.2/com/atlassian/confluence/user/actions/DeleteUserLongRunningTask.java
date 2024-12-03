/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 */
package com.atlassian.confluence.user.actions;

import com.atlassian.confluence.event.events.analytics.UserRemoveDoneAnalyticsEvent;
import com.atlassian.confluence.internal.user.UserAccessorInternal;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.longrunning.ConfluenceAbstractLongRunningTask;
import com.atlassian.event.api.EventPublisher;
import java.util.Objects;

public class DeleteUserLongRunningTask
extends ConfluenceAbstractLongRunningTask {
    private final I18NBean i18NBean;
    private final SettingsManager settingsManager;
    private final UserAccessorInternal userAccessor;
    private final ConfluenceUser user;
    private final EventPublisher eventPublisher;

    public DeleteUserLongRunningTask(I18NBean i18NBean, SettingsManager settingsManager, UserAccessorInternal userAccessor, ConfluenceUser user, EventPublisher eventPublisher) {
        this.i18NBean = Objects.requireNonNull(i18NBean);
        this.settingsManager = Objects.requireNonNull(settingsManager);
        this.userAccessor = Objects.requireNonNull(userAccessor);
        this.user = Objects.requireNonNull(user);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
    }

    @Override
    protected void runInternal() {
        try {
            boolean isCrowdManaged;
            this.progress.setPercentage(0);
            boolean isUnsynced = this.userAccessor.isUnsyncedUser(this.user);
            boolean isDeleted = this.userAccessor.isDeletedUser(this.user);
            boolean bl = isCrowdManaged = !isUnsynced && !isDeleted;
            if (isCrowdManaged) {
                this.progress.setStatus(this.i18NBean.getText("user.delete.progress.deactivate"));
                this.userAccessor.deactivateUser(this.user);
            }
            this.progress.setPercentage(15);
            this.progress.setStatus(this.i18NBean.getText("user.delete.progress.delete"));
            this.userAccessor.removeUser(this.user);
            this.progress.setPercentage(100);
            this.progress.setCompletedSuccessfully(true);
            this.progress.setStatus(this.getDeleteTaskFinishedMessage(isCrowdManaged));
            this.eventPublisher.publish((Object)new UserRemoveDoneAnalyticsEvent(isUnsynced));
        }
        catch (RuntimeException e) {
            log.error("Error during user deletion", (Throwable)e);
            this.progress.setStatus(this.i18NBean.getText("user.delete.progress.general.error", new String[]{e.getMessage()}));
            this.progress.setCompletedSuccessfully(false);
        }
    }

    private String getDeleteTaskFinishedMessage(boolean isCrowdManaged) {
        String backToViewText;
        String deleteFinished = this.i18NBean.getText("user.delete.progress.finished");
        Object backToViewUrl = this.settingsManager.getGlobalSettings().getBaseUrl();
        if (!isCrowdManaged) {
            backToViewUrl = (String)backToViewUrl + "/admin/users/showallunsyncedusers.action";
            backToViewText = this.i18NBean.getText("back.to.unsynced.from.directory");
        } else {
            backToViewUrl = (String)backToViewUrl + "/admin/users/showallusers.action";
            backToViewText = this.i18NBean.getText("return.to.user.browser");
        }
        return String.format("%s <a href=\"%s\">%s</a>", deleteFinished, backToViewUrl, backToViewText);
    }

    public String getName() {
        return this.i18NBean.getText("title.remove.user", new String[]{HtmlUtil.htmlEncode(this.user.getName())});
    }
}

