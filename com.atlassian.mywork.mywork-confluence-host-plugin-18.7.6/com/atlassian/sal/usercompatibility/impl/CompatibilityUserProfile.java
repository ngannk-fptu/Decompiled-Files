/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserProfile
 *  com.google.common.base.Preconditions
 */
package com.atlassian.sal.usercompatibility.impl;

import com.atlassian.sal.usercompatibility.CompatibilityUserUtilAccessException;
import com.atlassian.sal.usercompatibility.UserKey;
import com.atlassian.sal.usercompatibility.UserKeys;
import com.atlassian.sal.usercompatibility.UserProfile;
import com.google.common.base.Preconditions;
import java.lang.reflect.Method;
import java.net.URI;

class CompatibilityUserProfile
implements UserProfile {
    private final com.atlassian.sal.api.user.UserProfile userProfile;

    CompatibilityUserProfile(com.atlassian.sal.api.user.UserProfile userProfile) {
        this.userProfile = (com.atlassian.sal.api.user.UserProfile)Preconditions.checkNotNull((Object)userProfile, (Object)"userProfile");
    }

    @Override
    public UserKey getUserKey() {
        if (UserKeys.isUserKeyImplemented()) {
            Class<com.atlassian.sal.api.user.UserProfile> userProfileClass = com.atlassian.sal.api.user.UserProfile.class;
            try {
                Method method = userProfileClass.getMethod("getUserKey", new Class[0]);
                return new UserKey(method.invoke((Object)this.userProfile, new Object[0]).toString());
            }
            catch (Exception e) {
                throw new CompatibilityUserUtilAccessException(e);
            }
        }
        return UserKeys.getUserKeyFrom(this.userProfile.getUsername());
    }

    @Override
    public String getUsername() {
        return this.userProfile.getUsername();
    }

    @Override
    public String getFullName() {
        return this.userProfile.getFullName();
    }

    @Override
    public String getEmail() {
        return this.userProfile.getEmail();
    }

    @Override
    public URI getProfilePictureUri(int width, int height) {
        return this.userProfile.getProfilePictureUri(width, height);
    }

    @Override
    public URI getProfilePictureUri() {
        return this.userProfile.getProfilePictureUri();
    }

    @Override
    public URI getProfilePageUri() {
        return this.userProfile.getProfilePageUri();
    }
}

