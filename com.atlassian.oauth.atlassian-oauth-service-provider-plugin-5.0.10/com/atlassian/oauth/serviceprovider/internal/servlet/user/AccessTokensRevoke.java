/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth.serviceprovider.ServiceProviderToken
 *  com.atlassian.oauth.serviceprovider.ServiceProviderTokenStore
 *  com.atlassian.sal.api.user.UserManager
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.oauth.serviceprovider.internal.servlet.user;

import com.atlassian.oauth.serviceprovider.ServiceProviderToken;
import com.atlassian.oauth.serviceprovider.ServiceProviderTokenStore;
import com.atlassian.sal.api.user.UserManager;
import java.util.Objects;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;

public class AccessTokensRevoke {
    private final UserManager userManager;
    private final ServiceProviderTokenStore store;

    public AccessTokensRevoke(UserManager userManager, ServiceProviderTokenStore store) {
        this.userManager = Objects.requireNonNull(userManager, "userManager");
        this.store = Objects.requireNonNull(store, "store");
    }

    public Optional<Integer> revoke(HttpServletRequest request) {
        String username = this.userManager.getRemoteUsername(request);
        if (username == null) {
            return Optional.of(401);
        }
        String tokenParam = request.getParameter("token");
        if (tokenParam == null) {
            return Optional.of(400);
        }
        ServiceProviderToken token = this.store.get(tokenParam);
        if (token == null) {
            return Optional.empty();
        }
        if (!username.equals(token.getUser().getName())) {
            return Optional.of(401);
        }
        this.store.removeAndNotify(tokenParam);
        return Optional.empty();
    }
}

