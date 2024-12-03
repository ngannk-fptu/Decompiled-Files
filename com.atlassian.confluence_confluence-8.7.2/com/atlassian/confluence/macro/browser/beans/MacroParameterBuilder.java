/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.macro.browser.beans;

import com.atlassian.confluence.macro.browser.beans.MacroParameter;
import com.atlassian.confluence.macro.browser.beans.MacroParameterType;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class MacroParameterBuilder {
    private String pluginKey;
    private String macroName;
    private String name;
    private MacroParameterType type;
    private String defaultValue;
    private boolean required;
    private boolean multiple;
    private Set<String> aliases = Collections.emptySet();
    private List<String> enumValues = Collections.emptyList();
    private boolean hidden = false;

    public MacroParameter build() {
        MacroParameter parameter = new MacroParameter(this.pluginKey, this.macroName, this.name, this.type, this.required, this.multiple, this.defaultValue, this.hidden);
        for (String alias : this.aliases) {
            parameter.addAlias(alias);
        }
        for (String enumValue : this.enumValues) {
            parameter.addEnumValue(enumValue);
        }
        return parameter;
    }

    public static MacroParameterBuilder builder() {
        return new MacroParameterBuilder();
    }

    public MacroParameterBuilder setPluginKey(String pluginKey) {
        this.pluginKey = pluginKey;
        return this;
    }

    public MacroParameterBuilder setMacroName(String macroName) {
        this.macroName = macroName;
        return this;
    }

    public MacroParameterBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public MacroParameterBuilder setType(MacroParameterType type) {
        this.type = type;
        return this;
    }

    public MacroParameterBuilder setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    public MacroParameterBuilder setRequired(boolean required) {
        this.required = required;
        return this;
    }

    public MacroParameterBuilder setMultiple(boolean multiple) {
        this.multiple = multiple;
        return this;
    }

    public MacroParameterBuilder setAliases(Set<String> aliases) {
        this.aliases = aliases;
        return this;
    }

    public MacroParameterBuilder setEnumValues(List<String> enumValues) {
        this.enumValues = enumValues;
        return this;
    }

    public MacroParameterBuilder setHidden(boolean hidden) {
        this.hidden = hidden;
        return this;
    }

    @Deprecated
    public MacroParameterBuilder setDocumentationUrl(String documentationUrl) {
        return this;
    }
}

