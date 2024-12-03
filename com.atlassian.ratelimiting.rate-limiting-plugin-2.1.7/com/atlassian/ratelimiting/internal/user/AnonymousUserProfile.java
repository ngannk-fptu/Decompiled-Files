/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.sal.api.user.UserProfile
 */
package com.atlassian.ratelimiting.internal.user;

import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserProfile;
import java.net.URI;

public final class AnonymousUserProfile
implements UserProfile {
    public static final UserKey ANONYMOUS_USER_KEY = new UserKey("rate_limiting_anonymous_user-67d5362d-3b2f-4531-9039-5f041bdd402a");

    public static boolean isAnonymousRepresentativeUser(UserKey userKey) {
        return ANONYMOUS_USER_KEY.equals((Object)userKey);
    }

    static boolean isAnonymousRepresentativeUser(String username) {
        return ANONYMOUS_USER_KEY.getStringValue().equals(username);
    }

    public UserKey getUserKey() {
        return ANONYMOUS_USER_KEY;
    }

    public String getUsername() {
        return ANONYMOUS_USER_KEY.getStringValue();
    }

    public String getFullName() {
        return "Anonymous";
    }

    public String getEmail() {
        return "";
    }

    public URI getProfilePictureUri(int width, int height) {
        return null;
    }

    public URI getProfilePictureUri() {
        return null;
    }

    public URI getProfilePageUri() {
        return null;
    }
}

