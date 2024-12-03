/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.user.EntityException
 *  com.atlassian.user.UserManager
 *  javax.annotation.Nonnull
 *  org.jetbrains.annotations.NotNull
 */
package com.atlassian.oauth2.provider.core.user;

import com.atlassian.oauth2.provider.core.user.ActiveUserNotFoundException;
import com.atlassian.oauth2.provider.core.user.ProductUserProvider;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.user.EntityException;
import com.atlassian.user.UserManager;
import java.security.Principal;
import java.util.Optional;
import javax.annotation.Nonnull;
import org.jetbrains.annotations.NotNull;

public class RefappUserProvider
implements ProductUserProvider {
    private final UserManager userManager;

    public RefappUserProvider(UserManager userManager) {
        this.userManager = userManager;
    }

    @Override
    @NotNull
    public Optional<? extends Principal> getActiveUserByKey(@NotNull UserKey userkey) {
        try {
            return Optional.of(this.userManager.getUser(userkey.getStringValue()));
        }
        catch (EntityException e) {
            return Optional.empty();
        }
    }

    @Override
    public boolean isUserDeleted(@NotNull String userKey) throws ActiveUserNotFoundException {
        return !this.getActiveUserByKey(new UserKey(userKey)).isPresent();
    }

    @Override
    @NotNull
    public Optional<UserKey> getKeyForUsername(@NotNull String username) {
        return Optional.of(new UserKey(username));
    }

    @Override
    @Nonnull
    public Optional<String> getUsernameForKey(@Nonnull UserKey userKey) {
        return Optional.of(userKey.getStringValue());
    }
}

