/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.user.UserProfile
 */
package com.atlassian.plugins.rest.module.security;

import com.atlassian.plugins.rest.common.security.AuthenticationContext;
import com.atlassian.plugins.rest.module.servlet.ServletUtils;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import java.security.Principal;
import java.util.Objects;

public class SalAuthenticationContext
implements AuthenticationContext {
    private final UserManager userManager;

    public SalAuthenticationContext(UserManager userManager) {
        this.userManager = Objects.requireNonNull(userManager);
    }

    @Override
    public Principal getPrincipal() {
        UserProfile userProfile = this.getUserProfile();
        return userProfile != null ? new SalPrincipal(userProfile) : null;
    }

    @Override
    public boolean isAuthenticated() {
        return this.getUserProfile() != null;
    }

    private UserProfile getUserProfile() {
        return this.userManager.getRemoteUser(ServletUtils.getHttpServletRequest());
    }

    private static class SalPrincipal
    implements Principal {
        private final UserProfile userProfile;

        SalPrincipal(UserProfile userProfile) {
            this.userProfile = Objects.requireNonNull(userProfile, "userProfile");
        }

        @Override
        public String getName() {
            return this.userProfile.getUsername();
        }

        @Override
        public int hashCode() {
            return this.userProfile.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof SalPrincipal && ((SalPrincipal)obj).userProfile.equals(this.userProfile);
        }

        @Override
        public String toString() {
            return this.userProfile.getUsername();
        }
    }
}

