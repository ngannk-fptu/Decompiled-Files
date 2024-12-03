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
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import org.dom4j.Element;

public class DefaultGroupsModuleDescriptor
extends AbstractModuleDescriptor<List<String>> {
    private static final String GROUP = "group";
    private List<String> groups;

    public DefaultGroupsModuleDescriptor(ModuleFactory moduleFactory) {
        super(moduleFactory);
    }

    protected void provideValidationRules(ValidationPattern pattern) {
        super.provideValidationRules(pattern);
        pattern.rule(new ValidationPattern.RuleTest[]{ValidationPattern.test((String)GROUP).withError("Must specify at least one nested 'group' element, for example: <group>jira-users</group>")});
    }

    public void init(@Nonnull Plugin plugin, @Nonnull Element element) throws PluginParseException {
        super.init(plugin, element);
        this.groups = new ArrayList<String>();
        for (Element groupElement : element.elements(GROUP)) {
            this.groups.add(groupElement.getText());
        }
    }

    public List<String> getModule() {
        return this.groups;
    }
}

