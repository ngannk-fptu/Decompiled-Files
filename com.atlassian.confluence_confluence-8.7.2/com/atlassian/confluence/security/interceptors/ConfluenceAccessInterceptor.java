/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.spring.container.LazyComponentReference
 *  com.atlassian.util.concurrent.Supplier
 *  com.opensymphony.xwork2.ActionInvocation
 */
package com.atlassian.confluence.security.interceptors;

import com.atlassian.confluence.impl.security.access.ActionAccessChecker;
import com.atlassian.confluence.setup.struts.AbstractAwareInterceptor;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.spring.container.LazyComponentReference;
import com.atlassian.util.concurrent.Supplier;
import com.opensymphony.xwork2.ActionInvocation;

public class ConfluenceAccessInterceptor
extends AbstractAwareInterceptor {
    private final Supplier<ActionAccessChecker> actionAccessChecker = new LazyComponentReference("actionAccessChecker");

    @Override
    public String intercept(ActionInvocation actionInvocation) throws Exception {
        if (ContainerManager.isContainerSetup() && !this.isAccessPermitted(actionInvocation)) {
            return "notpermitted";
        }
        return actionInvocation.invoke();
    }

    private boolean isAccessPermitted(ActionInvocation actionInvocation) {
        return ((ActionAccessChecker)this.actionAccessChecker.get()).isAccessPermitted(actionInvocation.getAction(), actionInvocation.getProxy().getConfig().getMethodName());
    }
}

