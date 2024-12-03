/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.user.User
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.user;

import com.atlassian.confluence.internal.relations.RelatableEntity;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.user.User;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface ConfluenceUser
extends User,
RelatableEntity {
    public UserKey getKey();

    default public @Nullable String getLowerName() {
        throw new UnsupportedOperationException(this.getClass().getName() + " does not implement ConfluenceUser#getLowerName");
    }
}

