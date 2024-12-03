/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.jira.auditing.AssociatedItem
 *  com.atlassian.jira.auditing.AssociatedItem$Type
 *  com.atlassian.sal.api.user.UserProfile
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.ratelimiting.internal.jira.audit;

import com.atlassian.jira.auditing.AssociatedItem;
import com.atlassian.ratelimiting.internal.user.AnonymousUserProfile;
import com.atlassian.sal.api.user.UserProfile;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AffectedUser
implements AssociatedItem {
    private final UserProfile userProfile;

    public AffectedUser(UserProfile userProfile) {
        this.userProfile = userProfile;
    }

    @Nonnull
    public String getObjectName() {
        return this.userProfile.getUsername();
    }

    @Nullable
    public String getObjectId() {
        return AnonymousUserProfile.isAnonymousRepresentativeUser(this.userProfile.getUserKey()) ? this.userProfile.getFullName() : this.userProfile.getUserKey().getStringValue();
    }

    @Nullable
    public String getParentName() {
        return null;
    }

    @Nullable
    public String getParentId() {
        return null;
    }

    @Nonnull
    public AssociatedItem.Type getObjectType() {
        return AssociatedItem.Type.USER;
    }
}

