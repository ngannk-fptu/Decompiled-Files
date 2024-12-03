/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.soy.renderer.SoyServerFunction
 *  com.google.common.collect.ImmutableSet
 */
package com.atlassian.confluence.plugins.email.soy;

import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.soy.renderer.SoyServerFunction;
import com.google.common.collect.ImmutableSet;
import java.util.Set;

public class UserFullNameFunction
implements SoyServerFunction<String> {
    private static final ImmutableSet<Integer> ARG_SIZES = ImmutableSet.of((Object)1);
    private final UserAccessor userAccessor;

    public UserFullNameFunction(UserAccessor userAccessor) {
        this.userAccessor = userAccessor;
    }

    public String apply(Object ... args) {
        Object arg = args[0];
        if (arg instanceof UserKey) {
            ConfluenceUser user = this.userAccessor.getUserByKey((UserKey)arg);
            return user.getFullName();
        }
        this.checkType(arg);
        return "";
    }

    private void checkType(Object arg) {
        if (arg != null) {
            throw new IllegalArgumentException("argument 0 is not of type 'UserKey' in 'userFullName' soy function: " + arg.getClass().getName());
        }
    }

    public String getName() {
        return "userFullName";
    }

    public Set<Integer> validArgSizes() {
        return ARG_SIZES;
    }
}

