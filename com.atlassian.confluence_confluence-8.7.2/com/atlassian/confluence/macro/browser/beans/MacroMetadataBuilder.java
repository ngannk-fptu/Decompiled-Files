/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.macro.browser.beans;

import com.atlassian.confluence.macro.browser.beans.MacroFormDetails;
import com.atlassian.confluence.macro.browser.beans.MacroIcon;
import com.atlassian.confluence.macro.browser.beans.MacroMetadata;
import com.atlassian.confluence.macro.browser.beans.MacroPropertyPanelButton;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class MacroMetadataBuilder {
    private String macroName;
    private String pluginKey;
    private MacroIcon icon;
    private String title;
    private boolean isAlwaysShowConfig = false;
    private String description;
    private Set<String> aliases = Collections.emptySet();
    private Set<String> categories = Collections.emptySet();
    private boolean isBodyDeprecated;
    private boolean hidden;
    private boolean showDefaultParameterInPlaceholder;
    private MacroFormDetails formDetails;
    private String alternateId;
    private List<MacroPropertyPanelButton> buttons = Collections.emptyList();

    @Deprecated
    public MacroMetadataBuilder() {
    }

    public MacroMetadata build() {
        return new MacroMetadata(this);
    }

    public MacroMetadataBuilder setMacroName(String macroName) {
        this.macroName = macroName;
        return this;
    }

    public MacroMetadataBuilder setPluginKey(String pluginKey) {
        this.pluginKey = pluginKey;
        return this;
    }

    public MacroMetadataBuilder setIcon(MacroIcon icon) {
        this.icon = icon;
        return this;
    }

    public MacroMetadataBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    public MacroMetadataBuilder setAlwaysShowConfig(boolean isAlwaysShowConfig) {
        this.isAlwaysShowConfig = isAlwaysShowConfig;
        return this;
    }

    public MacroMetadataBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public MacroMetadataBuilder setAliases(Set<String> aliases) {
        this.aliases = aliases;
        return this;
    }

    public MacroMetadataBuilder setCategories(Set<String> categories) {
        this.categories = categories;
        return this;
    }

    public MacroMetadataBuilder setBodyDeprecated(boolean bodyDeprecated) {
        this.isBodyDeprecated = bodyDeprecated;
        return this;
    }

    public MacroMetadataBuilder setHidden(boolean hidden) {
        this.hidden = hidden;
        return this;
    }

    public MacroMetadataBuilder setFormDetails(MacroFormDetails formDetails) {
        this.formDetails = formDetails;
        return this;
    }

    public MacroMetadataBuilder setAlternateId(String alternateId) {
        this.alternateId = alternateId;
        return this;
    }

    public MacroMetadataBuilder setButtons(List<MacroPropertyPanelButton> buttons) {
        this.buttons = buttons;
        return this;
    }

    public MacroMetadataBuilder setShowDefaultParameterInPlaceholder(boolean showDefaultParameterInPlaceholder) {
        this.showDefaultParameterInPlaceholder = showDefaultParameterInPlaceholder;
        return this;
    }

    public static MacroMetadataBuilder builder() {
        return new MacroMetadataBuilder();
    }

    public String getMacroName() {
        return this.macroName;
    }

    public String getPluginKey() {
        return this.pluginKey;
    }

    public MacroIcon getIcon() {
        return this.icon;
    }

    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return this.description;
    }

    public Set<String> getAliases() {
        return this.aliases;
    }

    public Set<String> getCategories() {
        return this.categories;
    }

    public boolean isBodyDeprecated() {
        return this.isBodyDeprecated;
    }

    public boolean isHidden() {
        return this.hidden;
    }

    public MacroFormDetails getFormDetails() {
        return this.formDetails;
    }

    public String getAlternateId() {
        return this.alternateId;
    }

    public List<MacroPropertyPanelButton> getButtons() {
        return this.buttons;
    }

    public boolean isAlwaysShowConfig() {
        return this.isAlwaysShowConfig;
    }
}

