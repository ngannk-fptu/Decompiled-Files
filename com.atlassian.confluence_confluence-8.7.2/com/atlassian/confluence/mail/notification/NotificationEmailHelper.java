/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.mail.Email
 *  com.atlassian.mail.MailFactory
 *  com.atlassian.mail.server.MailServerManager
 *  com.google.common.base.Preconditions
 *  org.apache.commons.lang3.RandomStringUtils
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.mail.notification;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.mail.ConfluencePopMailServer;
import com.atlassian.mail.Email;
import com.atlassian.mail.MailFactory;
import com.atlassian.mail.server.MailServerManager;
import com.google.common.base.Preconditions;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

@Deprecated
public class NotificationEmailHelper {
    private final MailServerManager mailServerManager;

    public NotificationEmailHelper(MailServerManager mailServerManager) {
        this.mailServerManager = (MailServerManager)Preconditions.checkNotNull((Object)mailServerManager);
    }

    public ContentEntityObject extractTargetContentFromEmailReply(Map<String, List<String>> headers, ContentFinder contentFinder) {
        Long contentId = this.extractContentIdFromReferencesHeader(headers);
        if (contentId != null) {
            return contentFinder.findContentById(contentId);
        }
        return null;
    }

    private Long extractContentIdFromReferencesHeader(Map<String, List<String>> headers) {
        List<String> referenceHeaders = headers.get("References");
        if (referenceHeaders == null) {
            return null;
        }
        for (String referenceHeader : referenceHeaders) {
            String lastMessageId;
            Long contentId;
            String[] potentialMessageIds = StringUtils.split((String)referenceHeader, (char)' ');
            if (potentialMessageIds == null || potentialMessageIds.length <= 0 || (contentId = this.extractContentId(lastMessageId = potentialMessageIds[potentialMessageIds.length - 1])) == null) continue;
            return contentId;
        }
        return null;
    }

    public void populateTrackingHeaders(Email mail, Long contentId) {
        mail.setMessageId(this.generateEmailMessageId(contentId));
        mail.setReplyTo(this.getEmailReplyToAddress());
    }

    private String generateEmailMessageId(Long contentId) {
        return String.format("contentId-%s-%s%s", contentId, RandomStringUtils.randomNumeric((int)10), this.getMessageIdSuffix());
    }

    private Long extractContentId(String messageId) {
        Pattern pattern = Pattern.compile("^<contentId-(\\d+)-\\d+" + Pattern.quote(this.getMessageIdSuffix()) + ">$");
        Matcher matcher = pattern.matcher(messageId);
        if (matcher.matches()) {
            String contentIdStr = matcher.group(1);
            return Long.valueOf(contentIdStr);
        }
        return null;
    }

    private String getMessageIdSuffix() {
        String replyToAddress = this.getEmailReplyToAddress();
        if (replyToAddress != null) {
            return replyToAddress.substring(replyToAddress.lastIndexOf("@"));
        }
        return "@confluence.localhost";
    }

    @Deprecated
    public static NotificationEmailHelper newNotificationEmailHelper() {
        return new NotificationEmailHelper(MailFactory.getServerManager());
    }

    private String getEmailReplyToAddress() {
        ConfluencePopMailServer popMailServer = (ConfluencePopMailServer)this.mailServerManager.getDefaultPopMailServer();
        if (popMailServer != null) {
            return popMailServer.getToAddress();
        }
        return null;
    }

    public static interface ContentFinder {
        public ContentEntityObject findContentById(long var1);
    }
}

