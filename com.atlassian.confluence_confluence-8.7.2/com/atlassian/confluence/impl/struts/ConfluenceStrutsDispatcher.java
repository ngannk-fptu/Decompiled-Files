/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.xwork2.ActionProxy
 *  com.opensymphony.xwork2.config.Configuration
 *  com.opensymphony.xwork2.config.ConfigurationManager
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.struts2.dispatcher.Dispatcher
 *  org.apache.struts2.dispatcher.mapper.ActionMapping
 */
package com.atlassian.confluence.impl.struts;

import com.atlassian.confluence.plugin.descriptor.web.DefaultWebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.WebInterface;
import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.setup.struts.PluginAwareXWorkConfiguration;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationManager;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts2.dispatcher.Dispatcher;
import org.apache.struts2.dispatcher.mapper.ActionMapping;

public class ConfluenceStrutsDispatcher
extends Dispatcher {
    private final ThreadLocal<ActionProxy> actionProxy = new ThreadLocal();
    private static final AtomicReference<ConfluenceStrutsDispatcher> staticRef = new AtomicReference();

    public ConfluenceStrutsDispatcher(ServletContext servletContext, Map<String, String> initParams) {
        super(servletContext, initParams);
    }

    public void init() {
        super.init();
        if (!staticRef.compareAndSet(null, this)) {
            throw new IllegalStateException("ConfluenceStrutsDispatcher cannot be initialized twice");
        }
    }

    static ConfluenceStrutsDispatcher get() {
        return staticRef.get();
    }

    protected ConfigurationManager createConfigurationManager(String name) {
        return new ConfigurationManager(name){

            protected Configuration createConfiguration(String beanName) {
                return new PluginAwareXWorkConfiguration(beanName);
            }
        };
    }

    public void serviceAction(HttpServletRequest request, HttpServletResponse response, ActionMapping mapping) throws ServletException {
        super.serviceAction(request, response, mapping);
        if (this.actionProxy.get() != null) {
            this.setWebInterfaceContext(this.actionProxy.get().getAction(), request);
        }
        this.actionProxy.remove();
    }

    protected ActionProxy prepareActionProxy(Map<String, Object> extraContext, String actionNamespace, String actionName, String actionMethod) {
        this.actionProxy.set(super.prepareActionProxy(extraContext, actionNamespace, actionName, actionMethod));
        return this.actionProxy.get();
    }

    private void setWebInterfaceContext(Object action, HttpServletRequest request) {
        WebInterfaceContext webInterfaceContext;
        if (!(action instanceof WebInterface)) {
            return;
        }
        WebInterface webInterface = (WebInterface)action;
        WebInterfaceContext existentWebInterfaceContext = (WebInterfaceContext)request.getAttribute("atlas.webInterfaceContext");
        if (existentWebInterfaceContext != null) {
            webInterfaceContext = webInterface.getWebInterfaceContext() instanceof DefaultWebInterfaceContext ? (DefaultWebInterfaceContext)webInterface.getWebInterfaceContext() : DefaultWebInterfaceContext.copyOf(webInterface.getWebInterfaceContext());
            webInterfaceContext = ((DefaultWebInterfaceContext)webInterfaceContext).putAllMissing(existentWebInterfaceContext);
        } else {
            webInterfaceContext = webInterface.getWebInterfaceContext();
        }
        request.setAttribute("atlas.webInterfaceContext", (Object)webInterfaceContext);
    }
}

