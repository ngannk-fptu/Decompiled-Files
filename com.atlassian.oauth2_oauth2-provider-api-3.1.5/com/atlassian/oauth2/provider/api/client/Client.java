/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth2.scopes.api.Scope
 *  javax.annotation.Nonnull
 */
package com.atlassian.oauth2.provider.api.client;

import com.atlassian.oauth2.scopes.api.Scope;
import java.util.List;
import javax.annotation.Nonnull;

public interface Client {
    @Nonnull
    public String getId();

    @Nonnull
    public String getClientId();

    @Nonnull
    public String getClientSecret();

    @Nonnull
    public String getName();

    @Nonnull
    public List<String> getRedirects();

    @Nonnull
    public String getUserKey();

    @Nonnull
    public Scope getScope();
}

