/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.module.ModuleFactory
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.dom4j.Element
 */
package com.atlassian.confluence.plugin.descriptor.xhtml;

import com.atlassian.confluence.macro.xhtml.MacroMigration;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.dom4j.Element;

public class MacroMigrationModuleDescriptor
extends AbstractModuleDescriptor<MacroMigration> {
    private static final String MACRO_NAME = "macro-name";
    private MacroMigration macroMigration;
    private String macroName;

    public MacroMigrationModuleDescriptor(ModuleFactory moduleFactory) {
        super(moduleFactory);
    }

    public void init(@NonNull Plugin plugin, @NonNull Element element) throws PluginParseException {
        super.init(plugin, element);
        this.macroName = element.attributeValue(MACRO_NAME);
        if (this.macroName == null) {
            throw new PluginParseException("macro-name is a required attribute of the macro-migrator tag");
        }
    }

    public void enabled() {
        super.enabled();
        this.macroMigration = (MacroMigration)this.moduleFactory.createModule(this.moduleClassName, (ModuleDescriptor)this);
    }

    public MacroMigration getModule() {
        return this.macroMigration;
    }

    public String getMacroName() {
        return this.macroName;
    }
}

