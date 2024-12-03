/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  com.atlassian.config.ConfigurationException
 *  com.atlassian.confluence.core.ConfluenceSidManager
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.mail.InboundMailServer
 *  com.atlassian.mail.Email
 *  com.atlassian.mail.server.MailServer
 *  com.google.common.hash.Hashing
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.RandomStringUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.emailgateway.api;

import com.atlassian.annotations.PublicApi;
import com.atlassian.config.ConfigurationException;
import com.atlassian.confluence.core.ConfluenceSidManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.mail.InboundMailServer;
import com.atlassian.confluence.plugins.emailgateway.api.InboundMailServerManager;
import com.atlassian.mail.Email;
import com.atlassian.mail.server.MailServer;
import com.google.common.hash.Hashing;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PublicApi
public class NotificationEmailHelper {
    private static final Logger LOG = LoggerFactory.getLogger(NotificationEmailHelper.class);
    private final ConfluenceSidManager confluenceSidManager;
    private final InboundMailServerManager inboundMailServerManager;

    public NotificationEmailHelper(ConfluenceSidManager confluenceSidManager, InboundMailServerManager inboundMailServerManager) {
        this.confluenceSidManager = Objects.requireNonNull(confluenceSidManager);
        this.inboundMailServerManager = Objects.requireNonNull(inboundMailServerManager);
    }

    @Nullable
    public ContentEntityObject extractTargetContentFromEmailReply(Map<String, List<String>> headers, ContentFinder contentFinder) {
        Long contentId = this.extractContentIdFromReferencesHeader(headers);
        return contentId != null ? contentFinder.findContentById(contentId) : null;
    }

    @Nullable
    private Long extractContentIdFromReferencesHeader(Map<String, List<String>> headers) {
        List<String> referenceHeaders = headers.get("References");
        if (referenceHeaders == null) {
            return null;
        }
        for (String referenceHeader : referenceHeaders) {
            Optional<Long> first;
            String[] potentialMessageIds = StringUtils.split((String)referenceHeader, (char)' ');
            if (potentialMessageIds == null || !(first = Arrays.stream(potentialMessageIds).map(this::extractContentId).filter(Objects::nonNull).findFirst()).isPresent()) continue;
            return first.get();
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

    @Nullable
    private Long extractContentId(String messageId) {
        try {
            Pattern pattern = Pattern.compile("<(?<contentId>\\d+)@" + Pattern.quote(this.getInstanceHash()) + ">$");
            Matcher matcher = pattern.matcher(messageId.trim());
            return matcher.matches() ? Long.valueOf(matcher.group("contentId")) : null;
        }
        catch (ConfigurationException cex) {
            LOG.warn("Instance configuration error: [{}]", (Object)cex.getMessage(), (Object)cex);
            return null;
        }
    }

    private String getInstanceHash() throws ConfigurationException {
        return Hashing.sha256().hashUnencodedChars((CharSequence)this.confluenceSidManager.getSid()).toString();
    }

    private String getMessageIdSuffix() {
        String replyToAddress = this.getEmailReplyToAddress();
        return replyToAddress != null ? replyToAddress.substring(replyToAddress.lastIndexOf("@")) : "@confluence.localhost";
    }

    private String getEmailReplyToAddress() {
        MailServer mailServer = this.inboundMailServerManager.getMailServer();
        if (mailServer instanceof InboundMailServer) {
            return ((InboundMailServer)mailServer).getToAddress();
        }
        return null;
    }

    public static interface ContentFinder {
        public ContentEntityObject findContentById(long var1);
    }
}

