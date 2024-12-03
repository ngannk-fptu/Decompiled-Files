/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.spring.container.LazyComponentReference
 *  com.atlassian.util.concurrent.Supplier
 *  com.google.common.collect.ImmutableSet
 *  com.opensymphony.xwork2.ActionInvocation
 *  com.opensymphony.xwork2.interceptor.AbstractInterceptor
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.struts2.ServletActionContext
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.xwork;

import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.spring.container.LazyComponentReference;
import com.atlassian.util.concurrent.Supplier;
import com.google.common.collect.ImmutableSet;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SetupIncompleteInterceptor
extends AbstractInterceptor {
    private static final Logger log = LoggerFactory.getLogger(SetupIncompleteInterceptor.class);
    private final Supplier<BootstrapManager> bootstrapManagerSupplier = new LazyComponentReference("bootstrapManager");
    private static final Set<String> noRedirectPrefixes = ImmutableSet.of((Object)"/bootstrap", (Object)"/setup");
    private static final Set<String> silentRedirects = ImmutableSet.of((Object)"", (Object)"index.action");

    public String intercept(ActionInvocation invocation) throws Exception {
        HttpServletRequest request = ServletActionContext.getRequest();
        if (request == null || StringUtils.isBlank((CharSequence)request.getServletPath()) || this.shouldPassThrough(request.getServletPath())) {
            return invocation.invoke();
        }
        if (!this.isSetupComplete()) {
            String servletPath = request.getServletPath();
            if (!silentRedirects.contains(servletPath)) {
                log.warn("Redirecting request to current setup step: {}", (Object)HtmlUtil.htmlEncode(servletPath));
            }
            return "notsetup";
        }
        return invocation.invoke();
    }

    private boolean shouldPassThrough(String servletPath) {
        String actionNameSpace = this.getNamespaceFromServletPath(servletPath);
        return noRedirectPrefixes.stream().anyMatch(actionNameSpace::startsWith) || "/fourohfour.action".equals(servletPath);
    }

    private boolean isSetupComplete() {
        return ContainerManager.isContainerSetup() && ((BootstrapManager)this.bootstrapManagerSupplier.get()).isSetupComplete();
    }

    private String getNamespaceFromServletPath(String servletPath) {
        servletPath = servletPath.substring(0, servletPath.lastIndexOf("/"));
        return servletPath;
    }
}

