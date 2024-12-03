/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationType
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.module.ModuleFactory
 *  com.atlassian.plugin.util.validation.ValidationPattern
 *  com.atlassian.plugin.util.validation.ValidationPattern$RuleTest
 *  org.dom4j.Element
 */
package com.atlassian.applinks.core.plugin;

import com.atlassian.applinks.api.ApplicationType;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;
import com.atlassian.plugin.util.validation.ValidationPattern;
import java.util.ArrayList;
import java.util.Objects;
import org.dom4j.Element;

public class AbstractAppLinksTypeModuleDescriptor<T>
extends AbstractModuleDescriptor<T> {
    private Iterable<String> interfaces;

    public AbstractAppLinksTypeModuleDescriptor(ModuleFactory moduleFactory) {
        super(moduleFactory);
    }

    protected void provideValidationRules(ValidationPattern pattern) {
        super.provideValidationRules(pattern);
        pattern.rule(new ValidationPattern.RuleTest[]{ValidationPattern.test((String)"@class").withError("No " + ApplicationType.class.getSimpleName() + "  class specified.")});
    }

    public void init(Plugin plugin, Element element) throws PluginParseException {
        Objects.requireNonNull(plugin, "plugin can't be null");
        Objects.requireNonNull(element, "element can't be null");
        super.init(plugin, element);
        ArrayList<String> interfaces = new ArrayList<String>();
        for (Element child : element.elements("interface")) {
            interfaces.add(child.getTextTrim());
        }
        if (element.attributeValue("interface") != null) {
            interfaces.add(element.attributeValue("interface"));
        }
        this.interfaces = interfaces;
    }

    public Iterable<String> getInterfaces() {
        return this.interfaces;
    }

    public T getModule() {
        return (T)this.moduleFactory.createModule(this.moduleClassName, (ModuleDescriptor)this);
    }
}

