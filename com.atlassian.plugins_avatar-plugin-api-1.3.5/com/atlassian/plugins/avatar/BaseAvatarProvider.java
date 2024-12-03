/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 */
package com.atlassian.plugins.avatar;

import com.atlassian.plugins.avatar.Avatar;
import com.atlassian.plugins.avatar.AvatarOwner;
import com.atlassian.plugins.avatar.AvatarProvider;
import com.google.common.base.Function;

public abstract class BaseAvatarProvider<T, I>
implements AvatarProvider<T, I> {
    @Override
    public Avatar getAvatar(AvatarOwner<T> avatarOwner, int size) {
        return this.getAvatar(avatarOwner.getIdentifier(), size);
    }

    @Override
    public Avatar getAvatarById(I avatarId, int size) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Avatar getAvatar(AvatarOwner<T> owner, Function<AvatarOwner<T>, Avatar> fallbackFunction, int size) {
        return this.getAvatar(owner, size);
    }
}

