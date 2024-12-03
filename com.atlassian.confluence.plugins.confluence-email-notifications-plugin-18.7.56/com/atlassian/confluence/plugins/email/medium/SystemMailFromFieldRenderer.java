/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserProfile
 *  com.atlassian.user.User
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.confluence.plugins.email.medium;

import com.atlassian.sal.api.user.UserProfile;
import com.atlassian.user.User;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface SystemMailFromFieldRenderer {
    public String renderFromField(@Nullable UserProfile var1, @Nonnull User var2);
}

