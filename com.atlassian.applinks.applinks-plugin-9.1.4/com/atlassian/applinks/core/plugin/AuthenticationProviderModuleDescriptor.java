/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.spi.auth.AuthenticationProviderPluginModule
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

import com.atlassian.applinks.core.plugin.DescriptorWeightAttributeParser;
import com.atlassian.applinks.spi.auth.AuthenticationProviderPluginModule;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;
import com.atlassian.plugin.util.validation.ValidationPattern;
import java.util.Comparator;
import java.util.Objects;
import org.dom4j.Element;

public class AuthenticationProviderModuleDescriptor
extends AbstractModuleDescriptor<AuthenticationProviderPluginModule> {
    public static Comparator<AuthenticationProviderModuleDescriptor> BY_WEIGHT = new Comparator<AuthenticationProviderModuleDescriptor>(){

        @Override
        public int compare(AuthenticationProviderModuleDescriptor o1, AuthenticationProviderModuleDescriptor o2) {
            return o1.getWeight() - o2.getWeight();
        }
    };
    private int weight;

    public AuthenticationProviderModuleDescriptor(ModuleFactory moduleFactory) {
        super(moduleFactory);
    }

    public void init(Plugin plugin, Element element) throws PluginParseException {
        Objects.requireNonNull(plugin, "plugin can't be null");
        Objects.requireNonNull(element, "element can't be null");
        super.init(plugin, element);
        this.weight = DescriptorWeightAttributeParser.getWeight(element);
    }

    protected void provideValidationRules(ValidationPattern pattern) {
        super.provideValidationRules(pattern);
        pattern.rule(new ValidationPattern.RuleTest[]{ValidationPattern.test((String)"@class").withError("No AuthenticationProviderPluginModule implementation class specified."), ValidationPattern.test((String)"@i18n-name-key").withError("No i18n-name-key specified.")});
    }

    public AuthenticationProviderPluginModule getModule() {
        return (AuthenticationProviderPluginModule)this.moduleFactory.createModule(this.moduleClassName, (ModuleDescriptor)this);
    }

    public int getWeight() {
        return this.weight;
    }
}

