/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.accessmode.ReadOnlyAccessBlocked
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 */
package com.atlassian.confluence.mail.notification.actions;

import com.atlassian.confluence.api.service.accessmode.ReadOnlyAccessBlocked;
import com.atlassian.confluence.mail.notification.Notification;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.security.access.annotations.RequiresAnyConfluenceAccess;
import com.atlassian.confluence.spaces.actions.AbstractSpaceAction;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;

@RequiresAnyConfluenceAccess
@ReadOnlyAccessBlocked
public class RemoveSpaceNotificationAction
extends AbstractSpaceAction {
    private ContentTypeEnum contentType;

    @PermittedMethods(value={HttpMethod.GET, HttpMethod.POST})
    public String execute() throws Exception {
        Notification notif = this.notificationManager.getNotificationByUserAndSpaceAndType(this.getAuthenticatedUser(), this.getSpace(), this.contentType);
        if (notif != null) {
            this.notificationManager.removeNotification(notif);
        }
        return "success";
    }

    @Override
    public void validate() {
        if (this.getAuthenticatedUser() == null) {
            this.addActionError(this.getText("no.anonymous.notifications"));
        }
    }

    public void setContentType(String contentType) {
        this.contentType = ContentTypeEnum.getByRepresentation(contentType);
    }
}

