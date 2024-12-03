/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.actions.ProfilePictureInfo
 *  com.atlassian.user.User
 *  com.google.common.collect.ImmutableList
 *  org.codehaus.jackson.annotate.JsonAutoDetect
 *  org.codehaus.jackson.map.annotate.JsonSerialize
 */
package com.atlassian.confluence.plugins.macros.dashboard.recentupdates;

import com.atlassian.confluence.plugins.macros.dashboard.recentupdates.RecentUpdate;
import com.atlassian.confluence.plugins.macros.dashboard.recentupdates.rest.serialisers.ProfilePictureInfoSerialiser;
import com.atlassian.confluence.plugins.macros.dashboard.recentupdates.rest.serialisers.UserSerialiser;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.actions.ProfilePictureInfo;
import com.atlassian.user.User;
import com.google.common.collect.ImmutableList;
import java.util.LinkedList;
import java.util.List;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonAutoDetect
public class RecentUpdateGroup {
    private User modifier;
    private final ProfilePictureInfo profilePictureInfo;
    private final boolean canViewProfile;
    private LinkedList<RecentUpdate> changeSets;

    public RecentUpdateGroup(User modifier, ProfilePictureInfo profilePictureInfo, boolean canViewProfile) {
        this.modifier = modifier;
        this.profilePictureInfo = profilePictureInfo;
        this.canViewProfile = canViewProfile;
        this.changeSets = new LinkedList();
    }

    public void add(RecentUpdate recentUpdate) {
        this.changeSets.add(recentUpdate);
    }

    @JsonSerialize(using=UserSerialiser.class)
    public User getModifier() {
        return this.modifier;
    }

    @JsonSerialize(using=ProfilePictureInfoSerialiser.class)
    public ProfilePictureInfo getProfilePictureInfo() {
        return this.profilePictureInfo;
    }

    public boolean isCurrentUser() {
        String loggedInUser = AuthenticatedUserThreadLocal.getUsername();
        if (this.modifier == null) {
            return loggedInUser == null;
        }
        return this.modifier.getName().equals(loggedInUser);
    }

    public List<RecentUpdate> getRecentUpdates() {
        return ImmutableList.copyOf(this.changeSets);
    }

    public boolean getCanViewProfile() {
        return this.canViewProfile;
    }
}

