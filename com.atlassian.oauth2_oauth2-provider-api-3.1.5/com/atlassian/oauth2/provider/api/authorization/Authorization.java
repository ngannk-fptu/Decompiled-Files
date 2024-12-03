/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth2.scopes.api.Scope
 *  javax.annotation.Nonnull
 */
package com.atlassian.oauth2.provider.api.authorization;

import com.atlassian.oauth2.provider.api.pkce.CodeChallengeMethod;
import com.atlassian.oauth2.scopes.api.Scope;
import javax.annotation.Nonnull;

public interface Authorization {
    @Nonnull
    public String getAuthorizationCode();

    @Nonnull
    public String getClientId();

    @Nonnull
    public String getRedirectUri();

    @Nonnull
    public String getUserKey();

    @Nonnull
    public Long getCreatedAt();

    @Nonnull
    public Scope getScope();

    public CodeChallengeMethod getCodeChallengeMethod();

    public String getCodeChallenge();
}

