/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.spring.container.ContainerContext
 *  com.atlassian.spring.container.ContainerManager
 *  com.opensymphony.xwork2.Action
 *  com.opensymphony.xwork2.ActionInvocation
 *  com.opensymphony.xwork2.interceptor.Interceptor
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.confluence.core;

import com.atlassian.confluence.setup.SetupContext;
import com.atlassian.spring.container.ContainerContext;
import com.atlassian.spring.container.ContainerManager;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public final class ConfluenceAutowireInterceptor
implements Interceptor {
    public void destroy() {
    }

    public void init() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String intercept(ActionInvocation actionInvocation) throws Exception {
        Action action = (Action)actionInvocation.getAction();
        if (SetupContext.isAvailable()) {
            SetupContext.get().getBeanFactory().autowireBeanProperties((Object)action, 1, false);
        } else {
            ContainerContext containerContext = ContainerManager.getInstance().getContainerContext();
            containerContext.autowireComponent((Object)action);
        }
        if (action instanceof InitializingBean) {
            ((InitializingBean)action).afterPropertiesSet();
        }
        try {
            String string = actionInvocation.invoke();
            return string;
        }
        finally {
            if (action instanceof DisposableBean) {
                ((DisposableBean)action).destroy();
            }
        }
    }
}

