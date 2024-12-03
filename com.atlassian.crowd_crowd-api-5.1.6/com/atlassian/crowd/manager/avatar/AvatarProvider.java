/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.user.User
 *  javax.annotation.Nullable
 */
package com.atlassian.crowd.manager.avatar;

import com.atlassian.crowd.model.user.User;
import java.net.URI;
import javax.annotation.Nullable;

public interface AvatarProvider {
    public static final AvatarProvider NULL_PROVIDER = new AvatarProvider(){

        @Override
        @Nullable
        public URI getUserAvatar(User user, int sizeHint) {
            return null;
        }

        @Override
        @Nullable
        public URI getHostedUserAvatarUrl(long applicationId, String username, int sizeHint) {
            return null;
        }
    };

    @Nullable
    public URI getUserAvatar(User var1, int var2);

    @Nullable
    public URI getHostedUserAvatarUrl(long var1, String var3, int var4);
}

