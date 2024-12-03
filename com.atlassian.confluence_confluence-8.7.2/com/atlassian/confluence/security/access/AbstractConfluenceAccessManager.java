/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.security.access;

import com.atlassian.confluence.security.PermissionCheckExemptions;
import com.atlassian.confluence.security.access.AccessStatus;
import com.atlassian.confluence.security.access.AccessStatusImpl;
import com.atlassian.confluence.security.access.ConfluenceAccessManager;
import com.atlassian.user.User;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

abstract class AbstractConfluenceAccessManager
implements ConfluenceAccessManager {
    protected final PermissionCheckExemptions permissionCheckExemptions;

    AbstractConfluenceAccessManager(PermissionCheckExemptions permissionCheckExemptions) {
        this.permissionCheckExemptions = permissionCheckExemptions;
    }

    @Override
    public final @NonNull AccessStatus getUserAccessStatus(@Nullable User user) {
        if (this.permissionCheckExemptions.isExempt(user)) {
            if (user != null) {
                return AccessStatusImpl.LICENSED_ACCESS;
            }
            return AccessStatusImpl.ANONYMOUS_ACCESS;
        }
        return this.getUserAccessStatusNoExemptions(user);
    }
}

