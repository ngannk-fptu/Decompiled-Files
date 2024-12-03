/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.filters.ServletContextThreadLocal
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.web.Condition
 *  com.atlassian.plugin.web.ContextProvider
 *  com.atlassian.plugin.web.WebFragmentHelper
 *  com.atlassian.plugin.web.conditions.ConditionLoadingException
 *  com.atlassian.spring.container.ContainerManager
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugin.descriptor.web;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.plugin.ConfluencePluginUtils;
import com.atlassian.confluence.renderer.radeox.macros.MacroUtils;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.themes.ThemeHelper;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.confluence.util.i18n.UserLocaleAware;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import com.atlassian.core.filters.ServletContextThreadLocal;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.web.Condition;
import com.atlassian.plugin.web.ContextProvider;
import com.atlassian.plugin.web.WebFragmentHelper;
import com.atlassian.plugin.web.conditions.ConditionLoadingException;
import com.atlassian.spring.container.ContainerManager;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfluenceWebFragmentHelper
implements WebFragmentHelper,
UserLocaleAware {
    private static final Logger log = LoggerFactory.getLogger(ConfluenceWebFragmentHelper.class);
    private I18NBeanFactory i18NBeanFactory;

    public Object loadComponent(String className, Plugin plugin) throws ConditionLoadingException {
        Class cls = this.loadConditionClass(className, plugin);
        try {
            return ConfluencePluginUtils.instantiatePluginModule(plugin, cls);
        }
        catch (Exception e) {
            throw new ConditionLoadingException((Throwable)e);
        }
    }

    public Condition loadCondition(String className, Plugin plugin) throws ConditionLoadingException {
        if (className.startsWith("com.atlassian.confluence.plugin.descriptor.web.conditions")) {
            Class cls = this.loadConditionClass(className, plugin);
            return (Condition)ContainerManager.getInstance().getContainerContext().createComponent(cls);
        }
        return (Condition)this.loadComponent(className, plugin);
    }

    private Class loadConditionClass(String className, Plugin plugin) throws ConditionLoadingException {
        try {
            return plugin.loadClass(className, this.getClass());
        }
        catch (ClassNotFoundException e) {
            throw new ConditionLoadingException((Throwable)e);
        }
    }

    public ContextProvider loadContextProvider(String className, Plugin plugin) throws ConditionLoadingException {
        return (ContextProvider)this.loadComponent(className, plugin);
    }

    public String getI18nValue(String key, List arguments, Map context) {
        return this.i18NBeanFactory.getI18NBean().getText(key, arguments);
    }

    public String renderVelocityFragment(String fragment, Map context) {
        if (!this.needToRender(fragment)) {
            return fragment;
        }
        return VelocityUtils.getRenderedContent(fragment, this.getContext(context));
    }

    private Map getContext(Map context) {
        Space space = context != null && context.get("space") instanceof Space ? (Space)context.get("space") : null;
        Map<String, Object> result = MacroUtils.defaultVelocityContext(space);
        if (context != null) {
            result.putAll(context);
        }
        if (!result.containsKey("helper")) {
            ThemeHelper helper = null;
            try {
                if (ServletContextThreadLocal.getRequest() != null) {
                    helper = (ThemeHelper)ServletContextThreadLocal.getRequest().getAttribute("view.page.action.helper");
                }
            }
            catch (RuntimeException e) {
                log.error("Error reading helper from request attributes", (Throwable)e);
            }
            if (helper == null) {
                ConfluenceActionSupport action = (ConfluenceActionSupport)result.get("action");
                helper = action.getHelper();
            }
            result.put("helper", helper);
        }
        return result;
    }

    private boolean needToRender(String velocity) {
        return velocity != null && (velocity.contains("$") || velocity.contains("#"));
    }

    @Override
    public void setI18NBeanFactory(I18NBeanFactory i18NBeanFactory) {
        this.i18NBeanFactory = i18NBeanFactory;
    }
}

