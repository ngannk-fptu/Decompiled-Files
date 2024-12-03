/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.spring.container.ContainerManager
 *  com.opensymphony.xwork2.ActionInvocation
 *  com.opensymphony.xwork2.interceptor.Interceptor
 */
package com.atlassian.confluence.setup.actions;

import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.spring.container.ContainerManager;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;

public class SetupCheckInterceptor
implements Interceptor {
    public static final String ALREADY_SETUP = "alreadysetup";

    public void destroy() {
    }

    public void init() {
    }

    public String intercept(ActionInvocation actionInvocation) throws Exception {
        if (GeneralUtil.isSetupComplete() && ContainerManager.isContainerSetup()) {
            return ALREADY_SETUP;
        }
        return actionInvocation.invoke();
    }
}

