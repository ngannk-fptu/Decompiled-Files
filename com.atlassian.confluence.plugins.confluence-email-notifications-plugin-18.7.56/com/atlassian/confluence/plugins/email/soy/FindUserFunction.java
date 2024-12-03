/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.notifications.NotificationUserService
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.fugue.Option
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.soy.renderer.SoyServerFunction
 *  com.atlassian.user.User
 *  com.google.common.collect.ImmutableSet
 */
package com.atlassian.confluence.plugins.email.soy;

import com.atlassian.confluence.notifications.NotificationUserService;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.fugue.Maybe;
import com.atlassian.fugue.Option;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.soy.renderer.SoyServerFunction;
import com.atlassian.user.User;
import com.google.common.collect.ImmutableSet;
import java.util.Set;

public final class FindUserFunction
implements SoyServerFunction<User> {
    private static final String FUNCTION_NAME = "findUser";
    private static final ImmutableSet<Integer> ARG_SIZES = ImmutableSet.of((Object)1);
    private final NotificationUserService userService;

    public FindUserFunction(NotificationUserService userService) {
        this.userService = userService;
    }

    public User apply(Object ... objects) {
        Object arg = objects[0];
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        if (arg instanceof UserKey) {
            return this.userService.findUserForKey((User)currentUser, (Maybe)Option.some((Object)((UserKey)arg)));
        }
        if (arg instanceof String) {
            return this.userService.findUserForName((User)currentUser, (Maybe)Option.some((Object)((String)arg)));
        }
        return this.userService.getAnonymousUser((User)currentUser);
    }

    public String getName() {
        return FUNCTION_NAME;
    }

    public Set<Integer> validArgSizes() {
        return ARG_SIZES;
    }
}

