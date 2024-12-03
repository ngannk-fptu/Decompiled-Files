/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.sharepage.api;

import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;

public class ShareEvent {
    private final Long entityId;
    private final Long contextualPageId;
    private final String contentType;
    private final Set<String> users;
    private final Set<String> originalRequestEmails;
    private final Map<String, Set<String>> emails;
    private final Set<String> groupNames;
    private final String note;
    private final String senderUsername;

    public ShareEvent(String senderUsername, Long entityId, Long contextualPageId, String contentType, String note, Set<String> users, Set<String> originalRequestEmails, Map<String, Set<String>> emails, Set<String> groupNames) {
        this.entityId = entityId;
        this.contextualPageId = contextualPageId;
        this.contentType = contentType;
        this.senderUsername = senderUsername;
        this.users = users;
        this.originalRequestEmails = originalRequestEmails;
        this.emails = emails;
        this.groupNames = groupNames;
        this.note = note;
    }

    public String getSenderUsername() {
        return this.senderUsername;
    }

    public Set<String> getUsers() {
        return this.users;
    }

    public Set<String> getEmails() {
        return this.emails.keySet();
    }

    public Map<String, Set<String>> getEmailsWithGroups() {
        return this.emails;
    }

    public Set<String> getRequestEmails() {
        return this.originalRequestEmails;
    }

    public String getNote() {
        return this.note;
    }

    public Set<String> getGroupNames() {
        return this.groupNames;
    }

    public int getNumberOfUsers() {
        return this.users.size();
    }

    public int getNumberOfEmails() {
        return this.emails.size();
    }

    public int getNumberOfGroups() {
        return this.groupNames.size();
    }

    public Long getEntityId() {
        return this.entityId;
    }

    public boolean isIncludesMessage() {
        return StringUtils.isNotEmpty((CharSequence)this.getNote());
    }

    public String getContentType() {
        return this.contentType;
    }

    public Long getContextualPageId() {
        return this.contextualPageId;
    }
}

