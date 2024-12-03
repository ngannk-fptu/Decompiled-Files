/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.xwork2.ObjectFactory
 *  com.opensymphony.xwork2.inject.Container
 *  com.opensymphony.xwork2.inject.Inject
 *  com.opensymphony.xwork2.util.ValueStack
 *  javax.servlet.ServletContext
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.struts2.views.velocity.VelocityManager
 *  org.apache.velocity.app.VelocityEngine
 *  org.apache.velocity.context.Context
 *  org.apache.velocity.tools.ToolManager
 */
package com.atlassian.confluence.setup.struts;

import com.atlassian.confluence.setup.struts.ConfluenceVelocityManager;
import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ValueStack;
import java.util.Properties;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts2.views.velocity.VelocityManager;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.tools.ToolManager;

public class SingletonVelocityManager
extends VelocityManager {
    @Inject
    public void setObjectFactory(ObjectFactory fac) {
        LazyHolder.DELEGATE.setObjectFactory(fac);
    }

    @Inject
    public void setContainer(Container container) {
        LazyHolder.DELEGATE.setContainer(container);
    }

    public VelocityEngine getVelocityEngine() {
        return LazyHolder.DELEGATE.getVelocityEngine();
    }

    public Context createContext(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return LazyHolder.DELEGATE.createContext(stack, req, res);
    }

    public synchronized void init(ServletContext context) {
        LazyHolder.DELEGATE.init(context);
    }

    public Properties loadConfiguration(ServletContext context) {
        return LazyHolder.DELEGATE.loadConfiguration(context);
    }

    @Inject(value="struts.velocity.configfile")
    public void setCustomConfigFile(String val) {
        LazyHolder.DELEGATE.setCustomConfigFile(val);
    }

    @Inject(value="struts.velocity.toolboxlocation")
    public void setToolBoxLocation(String toolboxLocation) {
        LazyHolder.DELEGATE.setToolBoxLocation(toolboxLocation);
    }

    public ToolManager getToolboxManager() {
        return LazyHolder.DELEGATE.getToolboxManager();
    }

    @Inject(value="struts.velocity.contexts")
    public void setChainedContexts(String contexts) {
        LazyHolder.DELEGATE.setChainedContexts(contexts);
    }

    public Properties getVelocityProperties() {
        return LazyHolder.DELEGATE.getVelocityProperties();
    }

    public void setVelocityProperties(Properties velocityProperties) {
        LazyHolder.DELEGATE.setVelocityProperties(velocityProperties);
    }

    private static class LazyHolder {
        public static final VelocityManager DELEGATE = new ConfluenceVelocityManager();

        private LazyHolder() {
        }
    }
}

