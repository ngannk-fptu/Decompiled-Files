/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.macro.browser.beans;

import com.atlassian.confluence.macro.browser.beans.MacroFormDetails;
import com.atlassian.confluence.macro.browser.beans.MacroIcon;
import com.atlassian.confluence.macro.browser.beans.MacroMetadataBuilder;
import com.atlassian.confluence.macro.browser.beans.MacroParameter;
import com.atlassian.confluence.macro.browser.beans.MacroPropertyPanelButton;
import com.atlassian.confluence.macro.browser.beans.MacroSummary;
import com.atlassian.confluence.util.i18n.Message;
import java.util.List;
import java.util.Set;

public class MacroMetadata {
    private final MacroSummary macroSummary;
    private final MacroFormDetails formDetails;

    public static MacroMetadataBuilder builder() {
        return new MacroMetadataBuilder();
    }

    protected MacroMetadata(MacroMetadataBuilder builder) {
        this.macroSummary = new MacroSummary(builder);
        this.formDetails = builder.getFormDetails();
    }

    static boolean hasRequiredParameters(MacroFormDetails formDetails) {
        return formDetails != null && formDetails.hasRequiredParameters();
    }

    static String gadgetUrlFor(String macroName, MacroFormDetails formDetails) {
        if (!"gadget".equals(macroName)) {
            return null;
        }
        for (MacroParameter macroParameter : formDetails.getParameters()) {
            if (!"url".equals(macroParameter.getName())) continue;
            return macroParameter.getDefaultValue();
        }
        return null;
    }

    public String getMacroName() {
        return this.macroSummary.getMacroName();
    }

    public String getPluginKey() {
        return this.macroSummary.getPluginKey();
    }

    public MacroIcon getIcon() {
        return this.macroSummary.getIcon();
    }

    public Message getTitle() {
        return this.macroSummary.getTitle();
    }

    public Message getDescription() {
        return this.macroSummary.getDescription();
    }

    public Set<String> getAliases() {
        return this.macroSummary.getAliases();
    }

    public Set<String> getCategories() {
        return this.macroSummary.getCategories();
    }

    public boolean isBodyDeprecated() {
        return this.macroSummary.isBodyDeprecated();
    }

    public boolean isHidden() {
        return this.macroSummary.isHidden();
    }

    public String toString() {
        return this.macroSummary.getPluginKey() + ":" + this.macroSummary.getMacroName();
    }

    public String getAlternateId() {
        return this.macroSummary.getAlternateId();
    }

    public MacroFormDetails getFormDetails() {
        return this.formDetails;
    }

    public List<MacroPropertyPanelButton> getButtons() {
        return this.macroSummary.getButtons();
    }

    public MacroSummary extractMacroSummary() {
        return this.macroSummary;
    }
}

