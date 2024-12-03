/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.confluence.plugins.analytics.jobs.api.PeriodicEvent
 */
package com.atlassian.confluence.plugins.periodic.event;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.confluence.plugins.analytics.jobs.api.PeriodicEvent;
import java.util.Map;

@EventName(value="confluenceDailyStatistics")
class ConfluenceDailyStatisticsAnalyticsEvent
implements PeriodicEvent {
    private final boolean isDcLicensed;
    private final int clusterServerNodes;
    private final long uptime;
    private final int databaseClusterNodes;
    private final int allPages;
    private final int currentPages;
    private final int draftPages;
    private final int pagesWithUnpublishedChanges;
    private final long deletedPages;
    private final int allBlogs;
    private final int currentBlogs;
    private final int draftBlogs;
    private final int blogsWithUnpublishedChanges;
    private final int deletedBlogs;
    private final int personalSpaces;
    private final int globalSpaces;
    private final int allComments;
    private final int allAttachments;
    private final int currentAttachments;
    private final long allAttachmentsFileSize;
    private final long currentAttachmentsFileSize;
    private final long deletedAttachmentsFileSize;
    private final int registeredUsers;
    private final int maxUsers;
    private final int allUsers;
    private final int allGroups;
    private final int allMemberships;
    private final long systemAddOns;
    private final long userInstalledAddOns;
    private final int unsyncedUserCount;
    private final Map<Long, Map<String, Object>> userDirectory;
    private final boolean afcEnabled;

    ConfluenceDailyStatisticsAnalyticsEvent(Builder builder) {
        this.isDcLicensed = builder.isDcLicensed;
        this.clusterServerNodes = builder.clusterServerNodes;
        this.uptime = builder.uptime;
        this.databaseClusterNodes = builder.databaseClusterNodes;
        this.allPages = builder.allPages;
        this.currentPages = builder.currentPages;
        this.draftPages = builder.draftPages;
        this.pagesWithUnpublishedChanges = builder.pagesWithUnpublishedChanges;
        this.deletedPages = builder.deletedPages;
        this.allBlogs = builder.allBlogs;
        this.currentBlogs = builder.currentBlogs;
        this.draftBlogs = builder.draftBlogs;
        this.blogsWithUnpublishedChanges = builder.blogsWithUnpublishedChanges;
        this.deletedBlogs = builder.deletedBlogs;
        this.personalSpaces = builder.personalSpaces;
        this.globalSpaces = builder.globalSpaces;
        this.allComments = builder.allComments;
        this.allAttachments = builder.allAttachments;
        this.currentAttachments = builder.currentAttachments;
        this.allAttachmentsFileSize = builder.allAttachmentsFileSize;
        this.currentAttachmentsFileSize = builder.currentAttachmentsFileSize;
        this.deletedAttachmentsFileSize = builder.deletedAttachmentsFileSize;
        this.registeredUsers = builder.registeredUsers;
        this.maxUsers = builder.maxUsers;
        this.allUsers = builder.allUsers;
        this.allGroups = builder.allGroups;
        this.allMemberships = builder.allMemberships;
        this.systemAddOns = builder.systemAddOns;
        this.userInstalledAddOns = builder.userInstalledAddOns;
        this.userDirectory = builder.userDirectory;
        this.unsyncedUserCount = builder.unsyncedUserCount;
        this.afcEnabled = builder.afcEnabled;
    }

    public static Builder builder() {
        return new Builder();
    }

    public boolean getIsDcLicensed() {
        return this.isDcLicensed;
    }

    public int getClusterServerNodes() {
        return this.clusterServerNodes;
    }

    public long getUptime() {
        return this.uptime;
    }

    public int getDatabaseClusterNodes() {
        return this.databaseClusterNodes;
    }

    public int getCurrentPages() {
        return this.currentPages;
    }

    public int getDraftPages() {
        return this.draftPages;
    }

    public int getPagesWithUnpublishedChanges() {
        return this.pagesWithUnpublishedChanges;
    }

    public int getCurrentBlogs() {
        return this.currentBlogs;
    }

    public int getDraftBlogs() {
        return this.draftBlogs;
    }

    public int getBlogsWithUnpublishedChanges() {
        return this.blogsWithUnpublishedChanges;
    }

    public int getAllPages() {
        return this.allPages;
    }

    public int getAllBlogs() {
        return this.allBlogs;
    }

    public long getDeletedPages() {
        return this.deletedPages;
    }

    public long getDeletedBlogs() {
        return this.deletedBlogs;
    }

    public int getPersonalSpaces() {
        return this.personalSpaces;
    }

    public int getGlobalSpaces() {
        return this.globalSpaces;
    }

    public int getAllComments() {
        return this.allComments;
    }

    public int getAllAttachments() {
        return this.allAttachments;
    }

    public int getCurrentAttachments() {
        return this.currentAttachments;
    }

    public long getAllAttachmentsFileSize() {
        return this.allAttachmentsFileSize;
    }

    public long getCurrentAttachmentsFileSize() {
        return this.currentAttachmentsFileSize;
    }

    public long getDeletedAttachmentsFileSize() {
        return this.deletedAttachmentsFileSize;
    }

    public int getRegisteredUsers() {
        return this.registeredUsers;
    }

    public int getMaxUsers() {
        return this.maxUsers;
    }

    public int getAllUsers() {
        return this.allUsers;
    }

    public int getAllGroups() {
        return this.allGroups;
    }

    public int getAllMemberships() {
        return this.allMemberships;
    }

    public long getSystemAddOns() {
        return this.systemAddOns;
    }

    public long getUserInstalledAddOns() {
        return this.userInstalledAddOns;
    }

    public Map<Long, Map<String, Object>> getUserDirectory() {
        return this.userDirectory;
    }

    public int getUnsyncedUserCount() {
        return this.unsyncedUserCount;
    }

    public boolean getAfcEnabled() {
        return this.afcEnabled;
    }

    public static class Builder {
        private boolean isDcLicensed;
        private int clusterServerNodes;
        private long uptime;
        private int databaseClusterNodes;
        private int allPages;
        private int currentPages;
        private int draftPages;
        private int pagesWithUnpublishedChanges;
        private int deletedPages;
        private int allBlogs;
        private int currentBlogs;
        private int draftBlogs;
        private int blogsWithUnpublishedChanges;
        private int deletedBlogs;
        private int globalSpaces;
        private int personalSpaces;
        private int allComments;
        private int allAttachments;
        private int currentAttachments;
        private long allAttachmentsFileSize;
        private long currentAttachmentsFileSize;
        private long deletedAttachmentsFileSize;
        private int registeredUsers;
        private int maxUsers;
        private int allUsers;
        private int allGroups;
        private int allMemberships;
        private long systemAddOns;
        private long userInstalledAddOns;
        public int unsyncedUserCount;
        private Map<Long, Map<String, Object>> userDirectory;
        private boolean afcEnabled;

        private Builder() {
        }

        public Builder isDcLicensed(boolean isDcLicensed) {
            this.isDcLicensed = isDcLicensed;
            return this;
        }

        public Builder clusterServerNodes(int clusterServerNodes) {
            this.clusterServerNodes = clusterServerNodes;
            return this;
        }

        public Builder uptime(long uptime) {
            this.uptime = uptime;
            return this;
        }

        public Builder databaseClusterNodes(int databaseClusterNodes) {
            this.databaseClusterNodes = databaseClusterNodes;
            return this;
        }

        public Builder allPages(int allPages) {
            this.allPages = allPages;
            return this;
        }

        public Builder allBlogs(int allBlogs) {
            this.allBlogs = allBlogs;
            return this;
        }

        public Builder currentPages(int currentPages) {
            this.currentPages = currentPages;
            return this;
        }

        public Builder deletedPages(int deletedPages) {
            this.deletedPages = deletedPages;
            return this;
        }

        public Builder deletedBlogs(int deletedBlogs) {
            this.deletedBlogs = deletedBlogs;
            return this;
        }

        public Builder currentBlogs(int currentBlogs) {
            this.currentBlogs = currentBlogs;
            return this;
        }

        public Builder draftPages(int draftPages) {
            this.draftPages = draftPages;
            return this;
        }

        public Builder draftBlogs(int draftBlogs) {
            this.draftBlogs = draftBlogs;
            return this;
        }

        public Builder pagesWithUnpublishedChanges(int pagesWithUnpublishedChanges) {
            this.pagesWithUnpublishedChanges = pagesWithUnpublishedChanges;
            return this;
        }

        public Builder blogsWithUnpublishedChanges(int blogsWithUnpublishedChanges) {
            this.blogsWithUnpublishedChanges = blogsWithUnpublishedChanges;
            return this;
        }

        public Builder personalSpaces(int personalSpaces) {
            this.personalSpaces = personalSpaces;
            return this;
        }

        public Builder globalSpaces(int globalSpaces) {
            this.globalSpaces = globalSpaces;
            return this;
        }

        public Builder allComments(int allComments) {
            this.allComments = allComments;
            return this;
        }

        public Builder allAttachments(int allAttachments) {
            this.allAttachments = allAttachments;
            return this;
        }

        public Builder currentAttachments(int currentAttachments) {
            this.currentAttachments = currentAttachments;
            return this;
        }

        public Builder allAttachmentsFileSize(long allAttachmentsFileSize) {
            this.allAttachmentsFileSize = allAttachmentsFileSize;
            return this;
        }

        public Builder currentAttachmentsFileSize(long currentAttachmentsFileSize) {
            this.currentAttachmentsFileSize = currentAttachmentsFileSize;
            return this;
        }

        public Builder deletedAttachmentsFileSize(long deletedAttachmentsFileSize) {
            this.deletedAttachmentsFileSize = deletedAttachmentsFileSize;
            return this;
        }

        public Builder registeredUsers(int registeredUsers) {
            this.registeredUsers = registeredUsers;
            return this;
        }

        public Builder maxUsers(int maxUsers) {
            this.maxUsers = maxUsers;
            return this;
        }

        public Builder allUsers(int allUsers) {
            this.allUsers = allUsers;
            return this;
        }

        public Builder allGroups(int allGroups) {
            this.allGroups = allGroups;
            return this;
        }

        public Builder allMemberships(int allMemberships) {
            this.allMemberships = allMemberships;
            return this;
        }

        public Builder systemAddOns(long systemAddOns) {
            this.systemAddOns = systemAddOns;
            return this;
        }

        public Builder userInstalledAddOns(long userInstalledAddOns) {
            this.userInstalledAddOns = userInstalledAddOns;
            return this;
        }

        public Builder userDirectory(Map<Long, Map<String, Object>> userDirectory) {
            this.userDirectory = userDirectory;
            return this;
        }

        public Builder unsyncedUserCount(int unsyncedUserCount) {
            this.unsyncedUserCount = unsyncedUserCount;
            return this;
        }

        public Builder afcEnabled(boolean afcEnabled) {
            this.afcEnabled = afcEnabled;
            return this;
        }

        public ConfluenceDailyStatisticsAnalyticsEvent build() {
            return new ConfluenceDailyStatisticsAnalyticsEvent(this);
        }
    }
}

