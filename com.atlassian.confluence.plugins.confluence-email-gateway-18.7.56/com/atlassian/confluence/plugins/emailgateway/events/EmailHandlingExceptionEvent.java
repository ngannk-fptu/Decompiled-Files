/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.event.notification.NotificationEnabledEvent
 */
package com.atlassian.confluence.plugins.emailgateway.events;

import com.atlassian.confluence.api.model.event.notification.NotificationEnabledEvent;

public class EmailHandlingExceptionEvent
implements NotificationEnabledEvent {
    private String emailAddress;
    private String emailSubject;
    private boolean createPageError;
    private boolean attachmentError;
    private boolean readOnlyModeError;

    public EmailHandlingExceptionEvent(String emailAddress, String emailSubject, boolean createPageError, boolean attachmentError, boolean readOnlyModeError) {
        this.emailAddress = emailAddress;
        this.emailSubject = emailSubject;
        this.createPageError = createPageError;
        this.attachmentError = attachmentError;
        this.readOnlyModeError = readOnlyModeError;
    }

    public String getEmailAddress() {
        return this.emailAddress;
    }

    public String getEmailSubject() {
        return this.emailSubject;
    }

    public boolean isCreatePageError() {
        return this.createPageError;
    }

    public boolean isAttachmentError() {
        return this.attachmentError;
    }

    public boolean isReadOnlyModeError() {
        return this.readOnlyModeError;
    }

    public boolean isSuppressNotifications() {
        return false;
    }
}

