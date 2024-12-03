/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.event.events.admin.MailServerDeleteEvent
 *  com.atlassian.confluence.user.SignupManager
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.mail.server.MailServerManager
 */
package com.atlassian.confluence.plugins.easyuser.notifications;

import com.atlassian.confluence.event.events.admin.MailServerDeleteEvent;
import com.atlassian.confluence.user.SignupManager;
import com.atlassian.event.api.EventListener;
import com.atlassian.mail.server.MailServerManager;

public class MailServerDeletedEventListener {
    private final SignupManager signupManager;
    private final MailServerManager mailServerManager;

    public MailServerDeletedEventListener(SignupManager signupManager, MailServerManager mailServerManager) {
        this.signupManager = signupManager;
        this.mailServerManager = mailServerManager;
    }

    @EventListener
    public void mailServerDeleted(MailServerDeleteEvent event) {
        if (!this.mailServerManager.isDefaultSMTPMailServerDefined() && this.signupManager.isDomainRestrictedSignupEnabled()) {
            this.signupManager.setPrivateSignupMode();
        }
    }
}

