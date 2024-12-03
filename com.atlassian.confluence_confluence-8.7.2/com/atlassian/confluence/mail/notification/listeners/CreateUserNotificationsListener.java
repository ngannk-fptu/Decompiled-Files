/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.bandana.BandanaManager
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.mail.server.MailServerManager
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.confluence.mail.notification.listeners;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.event.events.user.AdminAddedUserEvent;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.SignupManager;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.event.api.EventListener;
import com.atlassian.mail.server.MailServerManager;
import org.springframework.beans.factory.annotation.Qualifier;

@Deprecated
public class CreateUserNotificationsListener {
    private final UserAccessor userAccessor;
    private final BandanaManager bandanaManager;
    private final MailServerManager mailServerManager;
    private final SignupManager signupManager;

    public CreateUserNotificationsListener(@Qualifier(value="userAccessor") UserAccessor userAccessor, BandanaManager bandanaManager, MailServerManager mailServerManager, SignupManager signupManager) {
        this.userAccessor = userAccessor;
        this.bandanaManager = bandanaManager;
        this.mailServerManager = mailServerManager;
        this.signupManager = signupManager;
    }

    @EventListener
    public void newUserCreated(AdminAddedUserEvent addedUserEvent) {
        if (this.mailServerManager.isDefaultSMTPMailServerDefined() && this.shouldSendEmail()) {
            ConfluenceUser user = this.userAccessor.getUserByName(addedUserEvent.getUser().getName());
            this.signupManager.sendWelcomeEmail(user);
        }
    }

    private boolean shouldSendEmail() {
        Boolean sendEmailSetting = (Boolean)this.bandanaManager.getValue((BandanaContext)ConfluenceBandanaContext.GLOBAL_CONTEXT, "sendEmail");
        return sendEmailSetting == null ? true : sendEmailSetting;
    }
}

