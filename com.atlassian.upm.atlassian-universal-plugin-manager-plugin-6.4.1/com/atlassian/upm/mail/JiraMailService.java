/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.jira.config.properties.ApplicationProperties
 *  com.atlassian.jira.notification.NotificationRecipient
 *  com.atlassian.jira.user.ApplicationUser
 *  com.atlassian.jira.user.util.UserManager
 *  com.atlassian.mail.queue.MailQueue
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.upm.mail;

import com.atlassian.jira.config.properties.ApplicationProperties;
import com.atlassian.jira.notification.NotificationRecipient;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.mail.queue.MailQueue;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.mail.AbstractAtlassianMailService;
import com.atlassian.upm.mail.UpmEmail;
import java.util.Objects;

public class JiraMailService
extends AbstractAtlassianMailService {
    static final String JIRA_MAIL_SEND_DISABLED = "jira.mail.send.disabled";
    private final MailQueue mailQueue;
    private final ApplicationProperties applicationProperties;
    private final UserManager userManager;

    public JiraMailService(MailQueue mailQueue, ApplicationProperties applicationProperties, UserManager userManager) {
        this.mailQueue = Objects.requireNonNull(mailQueue, "mailQueue");
        this.applicationProperties = Objects.requireNonNull(applicationProperties, "applicationProperties");
        this.userManager = Objects.requireNonNull(userManager, "userManager");
    }

    @Override
    public boolean isDisabled() {
        return super.isDisabled() || this.applicationProperties.getOption(JIRA_MAIL_SEND_DISABLED);
    }

    @Override
    public void sendMail(UpmEmail email) {
        this.mailQueue.addItem(this.createMailQueueItem(email));
    }

    @Override
    public UpmEmail.Format getUserEmailFormatPreference(UserKey userKey) {
        ApplicationUser applicationUser = this.userManager.getUserByKey(userKey.getStringValue());
        if (applicationUser != null && "text".equals(new NotificationRecipient(applicationUser).getFormat())) {
            return UpmEmail.Format.TEXT;
        }
        return UpmEmail.Format.HTML;
    }

    @Override
    public Option<String> getInstanceName() {
        return Option.option(this.applicationProperties.getText("jira.title"));
    }
}

