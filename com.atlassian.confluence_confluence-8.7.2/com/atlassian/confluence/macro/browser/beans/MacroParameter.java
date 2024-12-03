/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.atlassian.confluence.macro.browser.beans;

import com.atlassian.confluence.macro.browser.beans.MacroParameterType;
import com.atlassian.confluence.util.i18n.Message;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class MacroParameter
implements Serializable {
    private static final long serialVersionUID = 7689853671270300288L;
    public static final String DELIMITER_OPTION = "delimiter";
    public static final String DELIMITER_DEFAULT = ",";
    private String pluginKey;
    private String macroName;
    private String name;
    private MacroParameterType type;
    private String defaultValue;
    private boolean required;
    private boolean multiple;
    private Set<String> aliases;
    private List<String> enumValues;
    @SuppressFBWarnings(justification="enumToI18nKeyMapping field needs to be transient")
    private transient Map<String, Message> enumToI18nKeyMapping;
    private Properties options;
    private boolean hidden;
    private String displayName;
    private String description;

    public MacroParameter(String pluginKey, String macroName, String name, MacroParameterType type, boolean required, boolean multiple, String defaultValue, boolean hidden) {
        this.pluginKey = pluginKey;
        this.macroName = macroName;
        this.name = name;
        this.type = type;
        this.required = required;
        this.multiple = multiple;
        this.hidden = hidden;
        this.aliases = new HashSet<String>();
        this.enumValues = new ArrayList<String>();
        this.enumToI18nKeyMapping = new HashMap<String, Message>();
        this.options = new Properties();
        this.defaultValue = type == MacroParameterType.BOOLEAN ? Boolean.valueOf(defaultValue).toString() : defaultValue;
    }

    public boolean isHidden() {
        return this.hidden;
    }

    public void addAlias(String alias) {
        this.aliases.add(alias);
    }

    public void addEnumValue(String value) {
        if (this.type == MacroParameterType.ENUM) {
            this.enumValues.add(value);
            this.enumToI18nKeyMapping.put(value, this.getMessageForValue(value));
        }
    }

    public void addOptions(Map<String, String> options) {
        this.options.putAll(options);
    }

    public void addOption(String key, String value) {
        this.options.setProperty(key, value);
    }

    public String getName() {
        return this.name;
    }

    public MacroParameterType getType() {
        return this.type;
    }

    public String getDefaultValue() {
        return this.defaultValue;
    }

    public boolean isRequired() {
        return this.required;
    }

    public boolean isMultiple() {
        return this.multiple;
    }

    public Set<String> getAliases() {
        return this.aliases;
    }

    public List<String> getEnumValues() {
        if (this.type == MacroParameterType.ENUM) {
            return this.enumValues;
        }
        return null;
    }

    @Deprecated
    public Map<String, Message> getEnumMapValueName() {
        return this.getEnumToI18nKeyMapping();
    }

    public Map<String, Message> getEnumToI18nKeyMapping() {
        if (this.type == MacroParameterType.ENUM) {
            return this.enumToI18nKeyMapping;
        }
        return Collections.emptyMap();
    }

    public Properties getOptions() {
        return this.options;
    }

    public Message getDisplayName() {
        if (this.displayName != null) {
            return Message.getInstance(this.displayName);
        }
        return Message.getInstance(this.pluginKey + "." + this.macroName + ".param." + this.name + ".label");
    }

    public Message getDescription() {
        if (this.description != null) {
            return Message.getInstance(this.description);
        }
        return Message.getInstance(this.pluginKey + "." + this.macroName + ".param." + this.name + ".desc");
    }

    public String toString() {
        return this.name + (this.required ? " : required " : " : ") + this.type;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private Message getMessageForValue(String value) {
        return Message.getInstance(this.pluginKey + "." + this.macroName + ".param." + this.name + "." + value + ".desc");
    }
}

