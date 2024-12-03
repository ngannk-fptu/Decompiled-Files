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
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.spaces.actions.AbstractSpaceAction;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;

@ReadOnlyAccessBlocked
public class AddSpaceNotificationAction
extends AbstractSpaceAction {
    private ContentTypeEnum type;

    @PermittedMethods(value={HttpMethod.GET, HttpMethod.POST})
    public String execute() throws Exception {
        this.notificationManager.addSpaceNotification(this.getAuthenticatedUser(), this.getSpace(), this.type);
        return "success";
    }

    @Override
    public void validate() {
        if (this.getAuthenticatedUser() == null) {
            this.addActionError(this.getText("no.anonymous.notifications"));
        }
    }

    public void setContentType(String type) {
        this.type = ContentTypeEnum.getByRepresentation(type);
    }
}

