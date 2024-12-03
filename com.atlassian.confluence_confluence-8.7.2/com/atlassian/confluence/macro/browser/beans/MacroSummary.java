/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.macro.browser.beans;

import com.atlassian.confluence.macro.browser.beans.MacroIcon;
import com.atlassian.confluence.macro.browser.beans.MacroMetadata;
import com.atlassian.confluence.macro.browser.beans.MacroMetadataBuilder;
import com.atlassian.confluence.macro.browser.beans.MacroPropertyPanelButton;
import com.atlassian.confluence.util.i18n.Message;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;

public class MacroSummary {
    private final String macroName;
    private final String pluginKey;
    private final MacroIcon icon;
    private final String title;
    private final String description;
    private final Set<String> aliases;
    private final Set<String> categories;
    private final boolean isBodyDeprecated;
    private final boolean hidden;
    private final String alternateId;
    private final boolean hasRequiredParameters;
    private final boolean isAlwaysShowConfig;
    private final List<MacroPropertyPanelButton> buttons;
    private final String gadgetUrl;

    MacroSummary(MacroMetadataBuilder builder) {
        this.macroName = builder.getMacroName();
        this.pluginKey = builder.getPluginKey();
        this.icon = builder.getIcon();
        this.aliases = builder.getAliases();
        this.categories = builder.getCategories();
        this.isBodyDeprecated = builder.isBodyDeprecated();
        this.hidden = builder.isHidden();
        this.buttons = builder.getButtons();
        this.alternateId = builder.getAlternateId();
        this.hasRequiredParameters = MacroMetadata.hasRequiredParameters(builder.getFormDetails());
        this.isAlwaysShowConfig = builder.isAlwaysShowConfig();
        this.gadgetUrl = MacroMetadata.gadgetUrlFor(builder.getMacroName(), builder.getFormDetails());
        String macroTitle = builder.getTitle();
        this.title = macroTitle == null || macroTitle.equals(this.macroName) ? this.pluginKey + "." + this.macroName + ".label" : macroTitle;
        String macroDescription = builder.getDescription();
        this.description = StringUtils.isBlank((CharSequence)macroDescription) ? this.pluginKey + "." + this.macroName + ".desc" : macroDescription;
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

    public Message getTitle() {
        return Message.getInstance(this.title);
    }

    public Message getDescription() {
        return Message.getInstance(this.description);
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

    public String getAlternateId() {
        return this.alternateId;
    }

    public List<MacroPropertyPanelButton> getButtons() {
        return this.buttons;
    }

    public boolean isAnyParameterRequired() {
        return this.hasRequiredParameters;
    }

    public boolean isAlwaysShowConfig() {
        return this.isAlwaysShowConfig;
    }

    public String getGadgetUrl() {
        return this.gadgetUrl;
    }
}

