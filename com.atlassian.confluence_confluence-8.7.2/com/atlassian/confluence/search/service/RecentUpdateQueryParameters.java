/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.service;

import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.user.ConfluenceUser;
import java.util.EnumSet;
import java.util.Set;

public class RecentUpdateQueryParameters {
    private final EnumSet<ContentTypeEnum> contentTypes;
    private final Set<ConfluenceUser> followingUsers;
    private final Set<String> labels;
    private final Set<String> spaceKeys;

    public RecentUpdateQueryParameters(Set<ConfluenceUser> followingUsers, Set<String> labels, Set<String> spaceKeys, EnumSet<ContentTypeEnum> contentTypes) {
        this.followingUsers = followingUsers;
        this.labels = labels;
        this.spaceKeys = spaceKeys;
        this.contentTypes = contentTypes;
    }

    public Set<ConfluenceUser> getFollowingUsers() {
        return this.followingUsers;
    }

    public Set<String> getLabels() {
        return this.labels;
    }

    public Set<String> getSpaceKeys() {
        return this.spaceKeys;
    }

    public EnumSet<ContentTypeEnum> getContentTypes() {
        return this.contentTypes;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        RecentUpdateQueryParameters that = (RecentUpdateQueryParameters)o;
        if (this.contentTypes != null ? !this.contentTypes.equals(that.contentTypes) : that.contentTypes != null) {
            return false;
        }
        if (this.labels != null ? !this.labels.equals(that.labels) : that.labels != null) {
            return false;
        }
        if (this.spaceKeys != null ? !this.spaceKeys.equals(that.spaceKeys) : that.spaceKeys != null) {
            return false;
        }
        return !(this.followingUsers != null ? !this.followingUsers.equals(that.followingUsers) : that.followingUsers != null);
    }

    public int hashCode() {
        int result = this.contentTypes != null ? this.contentTypes.hashCode() : 0;
        result = 31 * result + (this.followingUsers != null ? this.followingUsers.hashCode() : 0);
        result = 31 * result + (this.labels != null ? this.labels.hashCode() : 0);
        result = 31 * result + (this.spaceKeys != null ? this.spaceKeys.hashCode() : 0);
        return result;
    }
}

