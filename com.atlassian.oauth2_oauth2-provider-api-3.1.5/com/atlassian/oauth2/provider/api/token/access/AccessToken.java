/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth2.scopes.api.Scope
 *  javax.annotation.Nonnull
 */
package com.atlassian.oauth2.provider.api.token.access;

import com.atlassian.oauth2.scopes.api.Scope;
import javax.annotation.Nonnull;

public interface AccessToken {
    @Nonnull
    public String getId();

    @Nonnull
    public String getClientId();

    @Nonnull
    public String getUserKey();

    @Nonnull
    public String getAuthorizationCode();

    @Nonnull
    public Long getAuthorizationDate();

    @Nonnull
    public Long getCreatedAt();

    public Long getLastAccessed();

    @Nonnull
    public Scope getScope();
}

