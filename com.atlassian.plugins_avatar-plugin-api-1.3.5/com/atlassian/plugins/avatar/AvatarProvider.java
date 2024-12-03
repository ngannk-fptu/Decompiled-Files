/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 */
package com.atlassian.plugins.avatar;

import com.atlassian.plugins.avatar.Avatar;
import com.atlassian.plugins.avatar.AvatarOwner;
import com.google.common.base.Function;

public interface AvatarProvider<T, I> {
    public Avatar getAvatar(String var1, int var2);

    public Avatar getAvatar(AvatarOwner<T> var1, int var2);

    public Avatar getAvatarById(I var1, int var2);

    public Avatar getAvatar(AvatarOwner<T> var1, Function<AvatarOwner<T>, Avatar> var2, int var3);
}

