/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.CrowdService
 *  com.atlassian.crowd.embedded.api.User
 *  com.atlassian.user.User
 *  javax.annotation.Nullable
 */
package com.atlassian.confluence.user.crowd;

import com.atlassian.confluence.user.ConfluenceUserImpl;
import com.atlassian.crowd.embedded.api.CrowdService;
import com.atlassian.crowd.embedded.api.User;
import javax.annotation.Nullable;

public final class CrowdUserConversionHelper {
    private final CrowdService crowdService;

    public CrowdUserConversionHelper(CrowdService crowdService) {
        this.crowdService = crowdService;
    }

    @Nullable
    public User toCrowdUser(@Nullable com.atlassian.user.User legacyUser) {
        if (legacyUser == null) {
            return null;
        }
        com.atlassian.user.User user = legacyUser instanceof ConfluenceUserImpl ? ((ConfluenceUserImpl)legacyUser).getBackingUser() : legacyUser;
        return user instanceof User ? (User)user : this.crowdService.getUser(legacyUser.getName());
    }
}

