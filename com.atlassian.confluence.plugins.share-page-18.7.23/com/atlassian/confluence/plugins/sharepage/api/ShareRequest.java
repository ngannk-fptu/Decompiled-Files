/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.ContentType
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.plugins.sharepage.api;

import com.atlassian.confluence.api.model.content.ContentType;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class ShareRequest {
    private final Set<String> users;
    private final Set<String> emails;
    private final Set<String> groups;
    private final Long entityId;
    private final Long contextualPageId;
    private final String note;
    private final ContentType entityType;

    @JsonCreator
    public ShareRequest(@JsonProperty(value="users") Set<String> users, @JsonProperty(value="emails") Set<String> emails, @JsonProperty(value="groups") Set<String> groups, @JsonProperty(value="entityId") Long entityId, @JsonProperty(value="contextualPageId") Long contextualPageId, @JsonProperty(value="note") String note, @JsonProperty(value="entityType") String entityTypeValue) {
        this.users = users != null ? users : Collections.emptySet();
        this.emails = emails != null ? emails : Collections.emptySet();
        this.groups = groups != null ? groups : Collections.emptySet();
        this.entityId = entityId;
        this.contextualPageId = contextualPageId;
        this.note = note;
        this.entityType = ContentType.valueOf((String)Objects.requireNonNull(entityTypeValue));
    }

    public Set<String> getUsers() {
        return this.users;
    }

    public String getNote() {
        return this.note;
    }

    public Long getEntityId() {
        return this.entityId;
    }

    public Set<String> getEmails() {
        return this.emails;
    }

    public Set<String> getGroups() {
        return this.groups;
    }

    public ContentType getEntityType() {
        return this.entityType;
    }

    public Long getContextualPageId() {
        return this.contextualPageId;
    }

    public static class ShareRequestBuilder {
        private Set<String> users;
        private Set<String> emails;
        private Set<String> groups;
        private Long entityId;
        private Long contextualPageId;
        private String note;
        private String entityTypeValue;

        public ShareRequestBuilder setUsers(Set<String> users) {
            this.users = users;
            return this;
        }

        public ShareRequestBuilder setEmails(Set<String> emails) {
            this.emails = emails;
            return this;
        }

        public ShareRequestBuilder setGroups(Set<String> groups) {
            this.groups = groups;
            return this;
        }

        public ShareRequestBuilder setEntityId(Long entityId) {
            this.entityId = Objects.requireNonNull(entityId);
            return this;
        }

        public ShareRequestBuilder setContextualPageId(Long contextualPageId) {
            this.contextualPageId = contextualPageId;
            return this;
        }

        public ShareRequestBuilder setNote(String note) {
            this.note = note;
            return this;
        }

        public ShareRequestBuilder setEntityTypeValue(String entityTypeValue) {
            this.entityTypeValue = Objects.requireNonNull(entityTypeValue);
            return this;
        }

        public ShareRequest build() {
            return new ShareRequest(this.users, this.emails, this.groups, this.entityId, this.contextualPageId, this.note, this.entityTypeValue);
        }
    }
}

