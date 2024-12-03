/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.util.concurrent.Supplier
 */
package com.atlassian.plugins.avatar;

import com.atlassian.util.concurrent.Supplier;

public interface AvatarOwner<T>
extends Supplier<T> {
    public String getIdentifier();

    public boolean useUnknownAvatar();

    public T get();
}

