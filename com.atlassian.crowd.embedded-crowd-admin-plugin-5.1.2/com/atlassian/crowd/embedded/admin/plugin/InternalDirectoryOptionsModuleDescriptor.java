/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.module.ModuleFactory
 *  com.atlassian.plugin.util.validation.ValidationPattern
 *  com.atlassian.plugin.util.validation.ValidationPattern$RuleTest
 *  javax.annotation.Nonnull
 *  org.dom4j.Element
 */
package com.atlassian.crowd.embedded.admin.plugin;

import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;
import com.atlassian.plugin.util.validation.ValidationPattern;
import javax.annotation.Nonnull;
import org.dom4j.Element;

public class InternalDirectoryOptionsModuleDescriptor
extends AbstractModuleDescriptor<Void> {
    private boolean editable;

    public InternalDirectoryOptionsModuleDescriptor(ModuleFactory moduleFactory) {
        super(moduleFactory);
    }

    protected void provideValidationRules(ValidationPattern pattern) {
        super.provideValidationRules(pattern);
        pattern.rule(new ValidationPattern.RuleTest[]{ValidationPattern.test((String)"@editable").withError("Must specify attribute editable with value 'true' or 'false'.")});
    }

    public void init(@Nonnull Plugin plugin, @Nonnull Element element) throws PluginParseException {
        super.init(plugin, element);
        this.editable = Boolean.valueOf(element.attributeValue("editable"));
    }

    public Void getModule() {
        return null;
    }

    public boolean isEditable() {
        return this.editable;
    }
}

