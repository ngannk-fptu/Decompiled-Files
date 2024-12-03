/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.user.UserProfile
 *  org.apache.commons.lang3.StringUtils
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.stp.security;

import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import java.util.Objects;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

public class UserService {
    private final UserManager userManager;

    @Autowired
    public UserService(UserManager userManager) {
        this.userManager = Objects.requireNonNull(userManager);
    }

    public Optional<String> getUsername() {
        return Optional.ofNullable(this.userManager.getRemoteUsername());
    }

    public Optional<String> getUserEmail() {
        return this.getUsername().filter(StringUtils::isNotBlank).map(arg_0 -> ((UserManager)this.userManager).getUserProfile(arg_0)).filter(Objects::nonNull).map(UserProfile::getEmail);
    }
}

