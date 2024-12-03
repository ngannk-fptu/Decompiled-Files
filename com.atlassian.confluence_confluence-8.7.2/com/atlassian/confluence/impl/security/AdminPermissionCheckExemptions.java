/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.CrowdService
 *  com.atlassian.user.User
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.impl.security;

import com.atlassian.confluence.internal.security.ThreadLocalPermissionsCacheInternal;
import com.atlassian.confluence.security.PermissionCheckExemptions;
import com.atlassian.confluence.user.DisabledUserManager;
import com.atlassian.crowd.embedded.api.CrowdService;
import com.atlassian.user.User;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class AdminPermissionCheckExemptions
implements PermissionCheckExemptions {
    private final CrowdService crowdService;
    private final DisabledUserManager disabledUserManager;

    public AdminPermissionCheckExemptions(DisabledUserManager disabledUserManager, CrowdService crowdService) {
        this.disabledUserManager = disabledUserManager;
        this.crowdService = crowdService;
    }

    @Override
    public boolean isExempt(User user) {
        if (ThreadLocalPermissionsCacheInternal.hasTemporaryPermissionExemption()) {
            return true;
        }
        if (user == null) {
            return false;
        }
        return this.isExemptViaAdminGroupMembership(user);
    }

    private boolean isExemptViaAdminGroupMembership(@NonNull User user) {
        Boolean isExempt = ThreadLocalPermissionsCacheInternal.hasPermissionExemption(user);
        if (isExempt == null) {
            isExempt = !this.disabledUserManager.isDisabled(user) && this.isMemberOfAdministratorsGroup(user);
            ThreadLocalPermissionsCacheInternal.cachePermissionExemption(user, isExempt);
        }
        return isExempt;
    }

    private boolean isMemberOfAdministratorsGroup(@NonNull User user) {
        return this.crowdService.isUserMemberOfGroup(user.getName(), "confluence-administrators");
    }
}

