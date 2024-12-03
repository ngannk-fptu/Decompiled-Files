/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bitbucket.auth.Authentication
 *  com.atlassian.bitbucket.permission.PermissionVote
 *  com.atlassian.bitbucket.permission.PermissionVoter
 *  com.atlassian.bitbucket.permission.PermissionVoterProvider
 *  com.atlassian.oauth2.scopes.api.Permission
 *  com.atlassian.oauth2.scopes.api.ScopesRequestCache
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.oauth2.scopes.request.bitbucket;

import com.atlassian.bitbucket.auth.Authentication;
import com.atlassian.bitbucket.permission.PermissionVote;
import com.atlassian.bitbucket.permission.PermissionVoter;
import com.atlassian.bitbucket.permission.PermissionVoterProvider;
import com.atlassian.oauth2.scopes.api.Permission;
import com.atlassian.oauth2.scopes.api.ScopesRequestCache;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BitbucketScopesPermissionVoterProvider
implements PermissionVoterProvider {
    private final ScopesRequestCache scopesRequestCache;

    public BitbucketScopesPermissionVoterProvider(ScopesRequestCache scopesRequestCache) {
        this.scopesRequestCache = scopesRequestCache;
    }

    @Nullable
    public PermissionVoter create(@Nonnull Authentication authentication) {
        return permissionCheck -> {
            if (this.scopesRequestCache.hasPermission(Permission.permission((String)permissionCheck.getPermission().name()))) {
                return PermissionVote.ABSTAIN;
            }
            return PermissionVote.VETO;
        };
    }
}

