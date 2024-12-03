/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.mywork.model;

import java.util.Date;
import java.util.List;

public class NotificationFilter {
    private Date fromCreatedDate;
    private Date toCreatedDate;
    private List<Long> pageIds;
    private List<Long> notificationIds;
    private List<String> actions;
    private String appId;
    private String userKey;

    private NotificationFilter(Builder builder) {
        this.fromCreatedDate = builder.fromCreatedDate;
        this.toCreatedDate = builder.toCreatedDate;
        this.pageIds = builder.pageIds;
        this.notificationIds = builder.notificationIds;
        this.actions = builder.actions;
        this.userKey = builder.userKey;
        this.appId = builder.appId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Date getFromCreatedDate() {
        return this.fromCreatedDate;
    }

    public Date getToCreatedDate() {
        return this.toCreatedDate;
    }

    public List<Long> getPageIds() {
        return this.pageIds;
    }

    public List<Long> getNotificationIds() {
        return this.notificationIds;
    }

    public List<String> getActions() {
        return this.actions;
    }

    public String getUserKey() {
        return this.userKey;
    }

    public String getAppId() {
        return this.appId;
    }

    public static final class Builder {
        private Date fromCreatedDate;
        private Date toCreatedDate;
        private List<Long> pageIds;
        private List<Long> notificationIds;
        private List<String> actions;
        private String userKey;
        private String appId;

        public Builder fromCreatedDate(Date fromCreatedDate) {
            this.fromCreatedDate = fromCreatedDate != null ? new Date(fromCreatedDate.getTime()) : null;
            return this;
        }

        public Builder toCreatedDate(Date toCreatedDate) {
            this.toCreatedDate = toCreatedDate != null ? new Date(toCreatedDate.getTime()) : null;
            return this;
        }

        public Builder pageIds(List<Long> pageIds) {
            this.pageIds = pageIds;
            return this;
        }

        public Builder notificationIds(List<Long> notificationIds) {
            this.notificationIds = notificationIds;
            return this;
        }

        public Builder actions(List<String> actions) {
            this.actions = actions;
            return this;
        }

        public Builder userKey(String userKey) {
            this.userKey = userKey;
            return this;
        }

        public Builder appId(String appId) {
            this.appId = appId;
            return this;
        }

        public NotificationFilter build() {
            return new NotificationFilter(this);
        }
    }
}

