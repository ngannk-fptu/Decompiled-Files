/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.spring.container.ContainerManager
 *  com.google.common.base.Supplier
 *  com.google.common.base.Suppliers
 *  com.opensymphony.xwork2.ActionInvocation
 *  com.opensymphony.xwork2.interceptor.Interceptor
 *  javax.servlet.ServletRequest
 *  org.apache.struts2.ServletActionContext
 */
package com.atlassian.confluence.themes;

import com.atlassian.confluence.impl.themes.ThemeContextRequestHelper;
import com.atlassian.spring.container.ContainerManager;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;
import javax.servlet.ServletRequest;
import org.apache.struts2.ServletActionContext;

public class ThemeContextInterceptor
implements Interceptor {
    private final Supplier<ThemeContextRequestHelper> helper = Suppliers.memoize(() -> (ThemeContextRequestHelper)ContainerManager.getComponent((String)"themeContextRequestHelper"));

    public void destroy() {
    }

    public void init() {
    }

    public String intercept(ActionInvocation actionInvocation) throws Exception {
        if (ContainerManager.isContainerSetup()) {
            ((ThemeContextRequestHelper)this.helper.get()).initThemeContext(actionInvocation.getAction(), (ServletRequest)ServletActionContext.getRequest());
        }
        return actionInvocation.invoke();
    }
}

