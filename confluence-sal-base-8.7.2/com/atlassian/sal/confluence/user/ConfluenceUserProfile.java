/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.links.linktypes.UserProfileLink
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.actions.ProfilePictureInfo
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.sal.api.user.UserProfile
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.sal.confluence.user;

import com.atlassian.confluence.links.linktypes.UserProfileLink;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.actions.ProfilePictureInfo;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserProfile;
import java.net.URI;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class ConfluenceUserProfile
implements UserProfile {
    private static final int PICTURE_MAX_SIZE = 48;
    private final ConfluenceUser user;
    private final UserKey userKey;
    private final ProfilePictureInfo profilePictureInfo;

    public ConfluenceUserProfile(ConfluenceUser user, ProfilePictureInfo profilePictureInfo) {
        this.user = user;
        this.userKey = user.getKey();
        this.profilePictureInfo = profilePictureInfo;
    }

    public @Nullable UserKey getUserKey() {
        return this.userKey;
    }

    public String getUsername() {
        return this.user.getName();
    }

    public String getFullName() {
        return this.user.getFullName();
    }

    public String getEmail() {
        return this.user.getEmail();
    }

    public URI getProfilePictureUri(int width, int height) {
        if (width > 48 || height > 48) {
            return null;
        }
        return this.getProfilePictureUri();
    }

    public URI getProfilePictureUri() {
        return this.profilePictureInfo.isDefault() ? null : URI.create(this.profilePictureInfo.getDownloadPath());
    }

    public URI getProfilePageUri() {
        return URI.create(UserProfileLink.getLinkPath((String)this.getUsername()));
    }
}

