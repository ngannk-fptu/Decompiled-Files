/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.notifications.SystemUser
 *  com.atlassian.soy.renderer.SoyServerFunction
 *  com.google.common.collect.ImmutableSet
 */
package com.atlassian.confluence.plugins.email.soy;

import com.atlassian.confluence.notifications.SystemUser;
import com.atlassian.soy.renderer.SoyServerFunction;
import com.google.common.collect.ImmutableSet;
import java.util.Set;

public class IsSystemAccountFunction
implements SoyServerFunction<Boolean> {
    public Boolean apply(Object ... objects) {
        return objects[0] instanceof SystemUser;
    }

    public String getName() {
        return "isSystemAccount";
    }

    public Set<Integer> validArgSizes() {
        return ImmutableSet.of((Object)1);
    }
}

