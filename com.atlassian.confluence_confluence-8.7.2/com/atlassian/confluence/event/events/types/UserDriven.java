/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.event.events.types;

import com.atlassian.user.User;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface UserDriven {
    public @Nullable User getOriginatingUser();
}

