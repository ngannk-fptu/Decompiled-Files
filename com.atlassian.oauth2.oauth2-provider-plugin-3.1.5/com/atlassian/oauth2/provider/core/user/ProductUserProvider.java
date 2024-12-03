/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 *  javax.annotation.Nonnull
 */
package com.atlassian.oauth2.provider.core.user;

import com.atlassian.oauth2.provider.core.user.ActiveUserNotFoundException;
import com.atlassian.sal.api.user.UserKey;
import java.security.Principal;
import java.util.Optional;
import javax.annotation.Nonnull;

public interface ProductUserProvider {
    @Nonnull
    public Optional<? extends Principal> getActiveUserByKey(@Nonnull UserKey var1) throws ActiveUserNotFoundException;

    public boolean isUserDeleted(@Nonnull String var1) throws ActiveUserNotFoundException;

    @Nonnull
    public Optional<UserKey> getKeyForUsername(@Nonnull String var1);

    @Nonnull
    public Optional<String> getUsernameForKey(@Nonnull UserKey var1);
}

