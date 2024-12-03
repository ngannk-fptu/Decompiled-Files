/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.spring.container.LazyComponentReference
 *  com.atlassian.util.concurrent.Supplier
 *  com.opensymphony.xwork2.ActionInvocation
 *  com.opensymphony.xwork2.config.entities.ActionConfig
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.http.HttpSession
 *  org.apache.struts2.ServletActionContext
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.security.websudo;

import com.atlassian.confluence.core.ConfluenceSystemProperties;
import com.atlassian.confluence.security.websudo.WebSudoManager;
import com.atlassian.confluence.setup.struts.AbstractAwareInterceptor;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.xwork.StrutsActionHelper;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.spring.container.LazyComponentReference;
import com.atlassian.util.concurrent.Supplier;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import java.lang.reflect.Method;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated(forRemoval=true)
public class WebSudoInterceptor
extends AbstractAwareInterceptor {
    private static final Logger log = LoggerFactory.getLogger(WebSudoInterceptor.class);
    private final Supplier<WebSudoManager> webSudoManagerSupplier = new LazyComponentReference("webSudoManager");

    @Override
    public String intercept(ActionInvocation actionInvocation) throws Exception {
        ActionConfig actionConfig;
        Method actionMethod;
        if (this.skipWebSudoCheck()) {
            log.debug("web sudo check is skipped");
            return actionInvocation.invoke();
        }
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpSession session = request.getSession(false);
        WebSudoManager webSudoManager = this.getWebSudoManager();
        String requestURI = request.getServletPath();
        String pathInfo = request.getPathInfo();
        String queryString = request.getQueryString();
        boolean hasWebSudoSession = webSudoManager.hasValidSession(session);
        Class<?> actionClass = actionInvocation.getAction().getClass();
        if (!webSudoManager.matches(requestURI, actionClass, actionMethod = StrutsActionHelper.getActionClassMethod(actionClass, (actionConfig = actionInvocation.getProxy().getConfig()).getMethodName()))) {
            log.debug("web sudo check not required for {}.{}", actionClass, (Object)actionMethod);
            return actionInvocation.invoke();
        }
        HttpServletResponse response = ServletActionContext.getResponse();
        if (hasWebSudoSession) {
            log.debug("valid websudo session found: resetting and proceeding");
            webSudoManager.startSession(request, response);
            return actionInvocation.invoke();
        }
        log.debug("expired or missing websudo session: redirecting");
        String destination = requestURI + (null != pathInfo ? pathInfo : "") + (String)(null != queryString ? "?" + queryString : "");
        actionInvocation.getStack().push(Map.of("destination", destination));
        webSudoManager.invalidateSession(request, response);
        return "websudorequired";
    }

    boolean skipWebSudoCheck() {
        return ConfluenceSystemProperties.isDevMode() || !ContainerManager.isContainerSetup() || !GeneralUtil.isSetupComplete() || !this.getWebSudoManager().isEnabled();
    }

    WebSudoManager getWebSudoManager() {
        return (WebSudoManager)this.webSudoManagerSupplier.get();
    }
}

