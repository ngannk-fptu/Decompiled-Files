/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.impl.IdentifierUtils
 *  com.google.common.base.Function
 *  com.google.common.base.Functions
 *  com.google.common.collect.Iterables
 */
package com.atlassian.crowd.model.user;

import com.atlassian.crowd.embedded.impl.IdentifierUtils;
import com.atlassian.crowd.model.user.User;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.Iterables;
import java.security.Principal;

public final class Users {
    @Deprecated
    public static final Function<User, String> NAME_FUNCTION = Principal::getName;
    @Deprecated
    public static final Function<User, String> LOWER_NAME_FUNCTION = Functions.compose((Function)IdentifierUtils.TO_LOWER_CASE, NAME_FUNCTION);

    private Users() {
    }

    public static Iterable<String> namesOf(Iterable<? extends User> users) {
        return Iterables.transform(users, NAME_FUNCTION);
    }
}

