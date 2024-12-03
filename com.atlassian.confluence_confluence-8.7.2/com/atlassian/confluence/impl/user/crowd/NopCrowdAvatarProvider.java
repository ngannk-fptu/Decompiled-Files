/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.manager.avatar.AvatarProvider
 *  com.atlassian.crowd.model.user.User
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.impl.user.crowd;

import com.atlassian.crowd.manager.avatar.AvatarProvider;
import com.atlassian.crowd.model.user.User;
import java.net.URI;
import org.checkerframework.checker.nullness.qual.Nullable;

public class NopCrowdAvatarProvider
implements AvatarProvider {
    public @Nullable URI getUserAvatar(User user, int sizeHint) {
        return null;
    }

    public @Nullable URI getHostedUserAvatarUrl(long applicationId, String username, int sizeHint) {
        return null;
    }
}

