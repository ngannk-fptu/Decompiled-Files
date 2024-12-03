/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.sal.api.user;

import com.atlassian.sal.api.user.UserKey;
import java.net.URI;

public interface UserProfile {
    public UserKey getUserKey();

    public String getUsername();

    public String getFullName();

    public String getEmail();

    public URI getProfilePictureUri(int var1, int var2);

    public URI getProfilePictureUri();

    public URI getProfilePageUri();
}

