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

import com.atlassian.plugins.navlink.consumer.menu.services.MenuService;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.soy.renderer.JsExpression;
import com.atlassian.soy.renderer.SoyClientFunction;
import com.atlassian.soy.renderer.SoyServerFunction;
import com.google.common.collect.ImmutableSet;
import java.util.Set;

public class ShowAppSwitcherFunction
implements SoyServerFunction<Boolean>,
SoyClientFunction {
    private final MenuService menuService;
    private final UserManager userManager;

    public ShowAppSwitcherFunction(MenuService menuService, UserManager userManager) {
        this.menuService = menuService;
        this.userManager = userManager;
    }

    public String getName() {
        return "showAppSwitcher";
    }

    public JsExpression generate(JsExpression ... args) {
        return this.alwaysRenderAppSwitcherOnClientSide();
    }

    private JsExpression alwaysRenderAppSwitcherOnClientSide() {
        return new JsExpression("true");
    }

    public Boolean apply(Object ... args) {
        return this.menuService.isAppSwitcherVisibleForUser(this.userManager.getRemoteUsername());
    }

    public Set<Integer> validArgSizes() {
        return ImmutableSet.of((Object)0);
    }
}

