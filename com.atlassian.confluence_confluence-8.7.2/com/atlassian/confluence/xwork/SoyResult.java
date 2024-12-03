/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.util.profiling.Ticker
 *  com.atlassian.util.profiling.Timers
 *  com.opensymphony.xwork2.Action
 *  com.opensymphony.xwork2.ActionInvocation
 *  com.opensymphony.xwork2.Result
 *  com.opensymphony.xwork2.config.entities.ActionConfig
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.xwork;

import com.atlassian.confluence.plugin.struts.PluginAwareActionConfig;
import com.atlassian.confluence.renderer.template.TemplateRenderer;
import com.atlassian.confluence.xwork.ActionViewData;
import com.atlassian.confluence.xwork.ActionViewDataMappings;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.util.profiling.Ticker;
import com.atlassian.util.profiling.Timers;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.Result;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SoyResult
implements Result {
    private static final Logger log = LoggerFactory.getLogger(SoyResult.class);
    private String completeModuleKey;
    private String template;
    private String contentType = "text/html";
    private String encoding = "utf-8";
    private String location;
    private TemplateRenderer templateRenderer;

    public void execute(ActionInvocation invocation) throws Exception {
        HttpServletResponse response = (HttpServletResponse)invocation.getInvocationContext().get("com.opensymphony.xwork2.dispatcher.HttpServletResponse");
        if (StringUtils.isNotBlank((CharSequence)this.location)) {
            this.completeModuleKey = StringUtils.substringBefore((String)this.location, (String)"/");
            this.template = StringUtils.substringAfter((String)this.location, (String)"/");
        }
        if (StringUtils.isBlank((CharSequence)this.completeModuleKey)) {
            throw new IllegalArgumentException("completeModuleKey is required");
        }
        if (StringUtils.isBlank((CharSequence)this.template)) {
            throw new IllegalArgumentException("template is required");
        }
        if (this.completeModuleKey.startsWith(":")) {
            ActionConfig actionConfig = invocation.getProxy().getConfig();
            if (!(actionConfig instanceof PluginAwareActionConfig)) {
                throw new IllegalArgumentException("Relative location found but could not resolve it since proxy is not instance of PluginAwareActionConfig");
            }
            this.completeModuleKey = ((PluginAwareActionConfig)actionConfig).getPlugin().getKey() + this.completeModuleKey;
        }
        response.setContentType(this.contentType + "; charset=" + this.encoding);
        PrintWriter writer = response.getWriter();
        try (Ticker ignored = Timers.start((String)("SoyResult: execute(): template: " + this.template));){
            Map<String, Object> data = SoyResult.getData((Action)invocation.getAction(), false);
            Map<String, Object> injectedData = SoyResult.getData((Action)invocation.getAction(), true);
            this.getTemplateRenderer().renderTo(writer, this.completeModuleKey, this.template + ".soy", data, injectedData);
        }
        writer.flush();
    }

    static Map<String, Object> getData(Action action, boolean injected) {
        BeanInfo beanInfo;
        HashMap<String, Object> data = new HashMap<String, Object>();
        try {
            beanInfo = Introspector.getBeanInfo(action.getClass());
        }
        catch (IntrospectionException e) {
            log.debug("Error retrieving javabean properties from class: " + action.getClass(), (Throwable)e);
            return data;
        }
        for (PropertyDescriptor propertyDescriptor : beanInfo.getPropertyDescriptors()) {
            Object value;
            Method method = propertyDescriptor.getReadMethod();
            if (method == null || !method.isAnnotationPresent(ActionViewData.class) && !method.isAnnotationPresent(ActionViewDataMappings.class)) continue;
            String key = propertyDescriptor.getName();
            try {
                value = method.invoke((Object)action, new Object[0]);
            }
            catch (IllegalAccessException | InvocationTargetException e) {
                log.debug("Error retrieving value for method: " + method + ". Skipping.");
                continue;
            }
            if (method.isAnnotationPresent(ActionViewData.class) && method.getAnnotation(ActionViewData.class).injected() == injected) {
                data.put(key, value);
                continue;
            }
            if (!method.isAnnotationPresent(ActionViewDataMappings.class) || method.getAnnotation(ActionViewDataMappings.class).injected() != injected) continue;
            if (!(value instanceof Map)) {
                log.debug("Method return type must be Map. Method: " + method);
                continue;
            }
            Map map = (Map)value;
            for (Map.Entry entry : map.entrySet()) {
                if (!(entry.getKey() instanceof String)) {
                    log.debug("Skipping non-string keys.");
                    continue;
                }
                data.put((String)entry.getKey(), entry.getValue());
            }
        }
        return data;
    }

    private TemplateRenderer getTemplateRenderer() {
        if (this.templateRenderer == null) {
            this.templateRenderer = (TemplateRenderer)ContainerManager.getComponent((String)"templateRenderer", TemplateRenderer.class);
        }
        return this.templateRenderer;
    }

    public String getCompleteModuleKey() {
        return this.completeModuleKey;
    }

    public void setCompleteModuleKey(String completeModuleKey) {
        this.completeModuleKey = completeModuleKey;
    }

    public String getTemplate() {
        return this.template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getContentType() {
        return this.contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getEncoding() {
        return this.encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }
}

