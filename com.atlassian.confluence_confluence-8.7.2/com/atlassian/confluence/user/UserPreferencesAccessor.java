/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.user;

import com.atlassian.confluence.user.ConfluenceUserPreferences;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.user.User;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly=true)
public interface UserPreferencesAccessor {
    @Nonnull
    public ConfluenceUserPreferences getConfluenceUserPreferences(@Nullable User var1);

    @Deprecated(since="8.0", forRemoval=true)
    public static UserPreferencesAccessor forUserAccessor(Supplier<UserAccessor> userAccessorRef) {
        return user -> ((UserAccessor)userAccessorRef.get()).getConfluenceUserPreferences(user);
    }
}

