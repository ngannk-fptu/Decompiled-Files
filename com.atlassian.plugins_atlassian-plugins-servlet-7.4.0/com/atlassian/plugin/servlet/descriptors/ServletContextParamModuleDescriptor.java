/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.descriptors.CannotDisable
 *  com.atlassian.plugin.module.ModuleFactory
 *  com.atlassian.plugin.util.validation.ValidationPattern
 *  com.atlassian.plugin.util.validation.ValidationPattern$RuleTest
 *  javax.annotation.Nonnull
 *  org.dom4j.Element
 */
package com.atlassian.plugin.servlet.descriptors;

import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.descriptors.CannotDisable;
import com.atlassian.plugin.module.ModuleFactory;
import com.atlassian.plugin.util.validation.ValidationPattern;
import javax.annotation.Nonnull;
import org.dom4j.Element;

@CannotDisable
public class ServletContextParamModuleDescriptor
extends AbstractModuleDescriptor<Void> {
    private String paramName;
    private String paramValue;

    public ServletContextParamModuleDescriptor() {
        super(ModuleFactory.LEGACY_MODULE_FACTORY);
    }

    public void init(@Nonnull Plugin plugin, @Nonnull Element element) {
        super.init(plugin, element);
        this.paramName = element.elementTextTrim("param-name");
        this.paramValue = element.elementTextTrim("param-value");
    }

    protected void provideValidationRules(ValidationPattern pattern) {
        super.provideValidationRules(pattern);
        pattern.rule(new ValidationPattern.RuleTest[]{ValidationPattern.test((String)"param-name").withError("Parameter name is required"), ValidationPattern.test((String)"param-value").withError("Parameter value is required")});
    }

    public String getParamName() {
        return this.paramName;
    }

    public String getParamValue() {
        return this.paramValue;
    }

    public Void getModule() {
        return null;
    }
}

