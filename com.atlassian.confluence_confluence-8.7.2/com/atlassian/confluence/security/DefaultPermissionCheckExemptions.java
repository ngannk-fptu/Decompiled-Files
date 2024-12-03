/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.CrowdService
 *  com.atlassian.user.User
 *  com.google.common.base.Supplier
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.security;

import com.atlassian.confluence.internal.security.ThreadLocalPermissionsCacheInternal;
import com.atlassian.confluence.security.PermissionCheckExemptions;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.crowd.embedded.api.CrowdService;
import com.atlassian.user.User;
import com.google.common.base.Supplier;
import org.checkerframework.checker.nullness.qual.NonNull;

@Deprecated
public final class DefaultPermissionCheckExemptions
implements PermissionCheckExemptions {
    private final Supplier<UserAccessor> userAccessorRef = () -> userAccessor;
    private final CrowdService crowdService;

    public DefaultPermissionCheckExemptions(UserAccessor userAccessor, CrowdService crowdService) {
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
            isExempt = !this.getUserAccessor().isDeactivated(user) && this.isMemberOfAdministratorsGroup(user);
            ThreadLocalPermissionsCacheInternal.cachePermissionExemption(user, isExempt);
        }
        return isExempt;
    }

    private boolean isMemberOfAdministratorsGroup(@NonNull User user) {
        return this.crowdService.isUserMemberOfGroup(user.getName(), "confluence-administrators");
    }

    private UserAccessor getUserAccessor() {
        return (UserAccessor)this.userAccessorRef.get();
    }
}

