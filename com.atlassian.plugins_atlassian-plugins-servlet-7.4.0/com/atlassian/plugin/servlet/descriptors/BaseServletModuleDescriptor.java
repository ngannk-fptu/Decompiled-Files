/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.RequirePermission
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.module.ModuleFactory
 *  com.atlassian.plugin.util.validation.ValidationPattern
 *  com.atlassian.plugin.util.validation.ValidationPattern$RuleTest
 *  javax.annotation.Nonnull
 *  org.dom4j.Element
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.servlet.descriptors;

import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.RequirePermission;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;
import com.atlassian.plugin.util.validation.ValidationPattern;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequirePermission(value={"execute_java"})
public abstract class BaseServletModuleDescriptor<T>
extends AbstractModuleDescriptor<T> {
    protected static final Logger log = LoggerFactory.getLogger(BaseServletModuleDescriptor.class);
    private List<String> paths;
    private Map<String, String> initParams;
    private boolean asyncSupported = this.getDefaultAsyncSupported();

    public BaseServletModuleDescriptor(ModuleFactory moduleCreator) {
        super(moduleCreator);
    }

    public void init(@Nonnull Plugin plugin, @Nonnull Element element) {
        super.init(plugin, element);
        this.checkPermissions();
        List urlPatterns = element.elements("url-pattern");
        this.paths = new ArrayList<String>(urlPatterns.size());
        for (Object urlPattern : urlPatterns) {
            this.paths.add(urlPattern.getTextTrim());
        }
        this.initParams = new HashMap<String, String>();
        List paramsList = element.elements("init-param");
        for (Element initParamEl : paramsList) {
            Element paramNameEl = initParamEl.element("param-name");
            Element paramValueEl = initParamEl.element("param-value");
            this.initParams.put(paramNameEl.getTextTrim(), paramValueEl.getTextTrim());
        }
        Element async = element.element("async-supported");
        if (async != null) {
            this.asyncSupported = Boolean.parseBoolean(async.getTextTrim());
        }
    }

    protected void provideValidationRules(ValidationPattern pattern) {
        super.provideValidationRules(pattern);
        pattern.rule(new ValidationPattern.RuleTest[]{ValidationPattern.test((String)"url-pattern").withError("There must be at least one path specified")}).rule("init-param", new ValidationPattern.RuleTest[]{ValidationPattern.test((String)"param-name").withError("Parameter name is required"), ValidationPattern.test((String)"param-value").withError("Parameter value is required")});
    }

    public List<String> getPaths() {
        return this.paths;
    }

    public Map<String, String> getInitParams() {
        return this.initParams;
    }

    public boolean isAsyncSupported() {
        return this.asyncSupported;
    }

    protected boolean getDefaultAsyncSupported() {
        return false;
    }
}

