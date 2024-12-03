/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.soy.renderer.SoyServerFunction
 *  com.atlassian.user.User
 *  com.google.common.collect.ImmutableSet
 */
package com.atlassian.confluence.plugins.soy;

import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.soy.renderer.SoyServerFunction;
import com.atlassian.user.User;
import com.google.common.collect.ImmutableSet;
import java.util.Set;

public class GetUserByNameFunction
implements SoyServerFunction<User> {
    private static final ImmutableSet<Integer> VALID_ARGUMENT_SIZES = ImmutableSet.of((Object)1);
    private final UserAccessor userAccessor;

    public GetUserByNameFunction(UserAccessor userAccessor) {
        this.userAccessor = userAccessor;
    }

    public String getName() {
        return "getUserByName";
    }

    public User apply(Object ... args) {
        return this.userAccessor.getUserByName((String)args[0]);
    }

    public Set<Integer> validArgSizes() {
        return VALID_ARGUMENT_SIZES;
    }
}

