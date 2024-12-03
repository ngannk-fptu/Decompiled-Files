/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.avatar.Avatar
 *  com.atlassian.plugins.avatar.AvatarOwner
 *  com.atlassian.plugins.avatar.BaseAvatarProvider
 *  com.google.common.base.Function
 */
package com.atlassian.confluence.plugins.avatar;

import com.atlassian.plugins.avatar.Avatar;
import com.atlassian.plugins.avatar.AvatarOwner;
import com.atlassian.plugins.avatar.BaseAvatarProvider;
import com.google.common.base.Function;

public class ConfluenceAvatarProvider<T, I>
extends BaseAvatarProvider<T, I> {
    public Avatar getAvatar(String email, int size) {
        throw new UnsupportedOperationException("Not supported in Confluence");
    }

    public Avatar getAvatar(AvatarOwner<T> owner, Function<AvatarOwner<T>, Avatar> fallbackFunction, int size) {
        return (Avatar)fallbackFunction.apply(owner);
    }
}

