/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.retention;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.ConfluenceUser;
import org.checkerframework.checker.nullness.qual.Nullable;

@Internal
public interface RetentionPolicyPermissionManager {
    public boolean canViewGlobalPolicy(@Nullable ConfluenceUser var1);

    public boolean canEditGlobalPolicy(@Nullable ConfluenceUser var1);

    public boolean canViewSpacePolicy(@Nullable ConfluenceUser var1, Space var2);

    public boolean canEditSpacePolicy(@Nullable ConfluenceUser var1, Space var2);
}

