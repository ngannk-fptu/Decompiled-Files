/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.mail.Email
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.user.User
 *  org.apache.commons.lang3.StringUtils
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.mail.template;

import com.atlassian.confluence.mail.template.AbstractMailNotificationQueueItem;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.mail.Email;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.user.User;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.Nullable;

public class MailNotificationQueueItem
extends AbstractMailNotificationQueueItem {
    public static final String TEMPLATES_LOCATION = "/templates/email/";
    private static final String TEMPLATES_WEB_RESOURCE = "com.atlassian.confluence.plugins.confluence-email-resources:notification-templates";
    private static UserAccessor userAccessor;
    private String userName;
    private String mimeType = "text/plain";

    public static MailNotificationQueueItem createFromTemplateFile(User user, String templateFileName, String subject) {
        String templateLocation = MailNotificationQueueItem.getDefaultTemplateLocation(user, templateFileName);
        return MailNotificationQueueItem.createFromTemplateFileAndLocation(user, templateLocation, templateFileName, subject);
    }

    public static MailNotificationQueueItem createFromTemplateFileAndLocation(User user, String templateLocation, String templateFileName, String subject) {
        return new MailNotificationQueueItem(user, templateLocation, templateFileName, subject);
    }

    public static MailNotificationQueueItem createFromTemplateContent(User user, String templateContent, String subject) {
        return new MailNotificationQueueItem(user, templateContent, subject);
    }

    protected MailNotificationQueueItem(User user, String templateLocation, String templateFileName, String subject) {
        super(templateLocation, templateFileName);
        this.setSubject(subject);
        this.checkUser(user, subject);
        this.userName = user.getName();
        this.dateQueued = new Date();
        if (StringUtils.isNotEmpty((CharSequence)MailNotificationQueueItem.getMimeTypeForUser(user))) {
            this.mimeType = MailNotificationQueueItem.getMimeTypeForUser(user);
        }
    }

    protected MailNotificationQueueItem(User user, String templateContent, String subject) {
        super(templateContent);
        this.setSubject(subject);
        this.checkUser(user, subject);
        this.userName = user.getName();
        this.dateQueued = new Date();
        if (StringUtils.isNotEmpty((CharSequence)MailNotificationQueueItem.getMimeTypeForUser(user))) {
            this.mimeType = MailNotificationQueueItem.getMimeTypeForUser(user);
        }
    }

    private void checkUser(User user, String subject) {
        if (user == null) {
            throw new IllegalArgumentException("null user for mail notification item with subject " + subject);
        }
        if (user.getEmail() == null) {
            throw new IllegalArgumentException("user " + user.getName() + " has null email address for mail notification item with subject " + subject);
        }
    }

    protected String getMimeType() {
        return this.mimeType;
    }

    @Override
    protected Email createMailObject() {
        String messageBody = this.getRenderedContent();
        Email mail = null;
        ConfluenceUser user = MailNotificationQueueItem.getUserAccessor().getUserByName(this.userName);
        if (user != null) {
            mail = new Email(user.getEmail());
            mail.setEncoding("UTF-8");
            mail.setSubject(this.getSubject());
            mail.setBody(messageBody);
            mail.setMimeType(this.mimeType);
            this.setLastError(null);
        }
        return mail;
    }

    protected static String getMimeTypeForUser(User user) {
        return "text/html";
    }

    protected static String getDefaultTemplateLocation(@Nullable User user, @Nullable String templateFileName) {
        if (templateFileName == null || templateFileName.endsWith(".vm")) {
            return "/templates/email/html/";
        }
        return TEMPLATES_WEB_RESOURCE;
    }

    @Deprecated
    public static UserAccessor getUserAccessor() {
        if (userAccessor == null) {
            userAccessor = (UserAccessor)ContainerManager.getComponent((String)"userAccessor");
        }
        return userAccessor;
    }
}

