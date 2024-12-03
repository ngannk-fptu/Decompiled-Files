/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.soy.renderer.JsExpression
 *  com.atlassian.soy.renderer.SoyClientFunction
 *  com.atlassian.soy.renderer.SoyServerFunction
 *  com.google.common.collect.ImmutableSet
 */
package com.atlassian.plugins.navlink.util;

import com.atlassian.sal.api.user.UserManager;
import com.atlassian.soy.renderer.JsExpression;
import com.atlassian.soy.renderer.SoyClientFunction;
import com.atlassian.soy.renderer.SoyServerFunction;
import com.google.common.collect.ImmutableSet;
import java.util.Set;

public class IsUserAdminFunction
implements SoyServerFunction<Boolean>,
SoyClientFunction {
    private UserManager userManager;

    public IsUserAdminFunction(UserManager userManager) {
        this.userManager = userManager;
    }

    public String getName() {
        return "isUserAdmin";
    }

    public JsExpression generate(JsExpression ... args) {
        return new JsExpression("false");
    }

    public Boolean apply(Object ... args) {
        return this.userManager.isAdmin(this.userManager.getRemoteUsername());
    }

    public Set<Integer> validArgSizes() {
        return ImmutableSet.of((Object)0);
    }
}

