/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.avatar.AvatarOwner
 *  com.atlassian.user.User
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.user.avatar;

import com.atlassian.plugins.avatar.AvatarOwner;
import com.atlassian.user.User;
import org.checkerframework.checker.nullness.qual.Nullable;

public class ConfluenceAvatarOwner
implements AvatarOwner<User> {
    private final User user;

    public ConfluenceAvatarOwner(@Nullable User user) {
        this.user = user;
    }

    public String getIdentifier() {
        return this.user != null ? this.user.getEmail() : null;
    }

    public boolean useUnknownAvatar() {
        return false;
    }

    public @Nullable User get() {
        return this.user;
    }
}

