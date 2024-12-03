/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.module.ModuleFactory
 *  com.google.common.base.Strings
 *  org.dom4j.Element
 */
package com.atlassian.confluence.impl.macro.schema;

import com.atlassian.confluence.macro.xhtml.MacroMigration;
import com.atlassian.confluence.plugin.descriptor.DefaultFactoryModuleDescriptor;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.module.ModuleFactory;
import com.google.common.base.Strings;
import org.dom4j.Element;

public class MacroSchemaMigratorModuleDescriptor
extends DefaultFactoryModuleDescriptor<MacroMigration> {
    private String macroName;
    private int schemaVersion;

    public MacroSchemaMigratorModuleDescriptor(ModuleFactory moduleFactory) {
        super(moduleFactory);
    }

    @Override
    public void init(Plugin plugin, Element element) throws PluginParseException {
        super.init(plugin, element);
        this.macroName = element.attributeValue("macro");
        if (Strings.isNullOrEmpty((String)this.macroName)) {
            throw new PluginParseException("Macro name is required for macro-schema-migrator with key : " + this.getKey());
        }
        try {
            this.schemaVersion = Integer.parseInt(element.attributeValue("version"));
        }
        catch (NumberFormatException ex) {
            throw new PluginParseException("Could not parse version as int for macro-schema-migrator for macro : " + this.macroName, (Throwable)ex);
        }
    }

    @Override
    public MacroMigration getModule() {
        return (MacroMigration)super.getModuleFromProvider();
    }

    public int getSchemaVersion() {
        return this.schemaVersion;
    }

    public String getMacroName() {
        return this.macroName;
    }
}

