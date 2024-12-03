/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.user.UserProfile
 */
package com.atlassian.ratelimiting.internal.user.keyprovider;

import com.atlassian.ratelimiting.user.keyprovider.UserKeyProvider;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import java.util.Optional;

public class SalUserKeyProvider
implements UserKeyProvider {
    private final UserManager userManager;

    public SalUserKeyProvider(UserManager userManager) {
        this.userManager = userManager;
    }

    @Override
    public Optional<UserKey> apply(String username) {
        return Optional.ofNullable(this.userManager.getUserProfile(username)).map(UserProfile::getUserKey);
    }
}

