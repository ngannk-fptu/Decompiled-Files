/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.macro.browser;

import com.atlassian.confluence.macro.browser.MacroMetadataProvider;
import com.atlassian.confluence.macro.browser.beans.MacroBody;
import com.atlassian.confluence.macro.browser.beans.MacroFormDetails;
import com.atlassian.confluence.macro.browser.beans.MacroIcon;
import com.atlassian.confluence.macro.browser.beans.MacroMetadata;
import com.atlassian.confluence.macro.browser.beans.MacroMetadataBuilder;
import com.atlassian.confluence.macro.browser.beans.MacroSummary;
import com.atlassian.confluence.renderer.UserMacroConfig;
import com.atlassian.confluence.renderer.UserMacroLibrary;
import com.atlassian.confluence.util.HtmlUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserMacroMetadataProvider
implements MacroMetadataProvider {
    private static final Logger log = LoggerFactory.getLogger(UserMacroMetadataProvider.class);
    private UserMacroLibrary userMacroLibrary;
    public static final String USER_MACRO_DUMMY_PLUGIN_KEY = "_-user-macro-_";

    @Override
    public Collection<MacroMetadata> getData() {
        return this.extractMetadata(this.userMacroLibrary.getMacros());
    }

    @Override
    public Collection<MacroSummary> getSummaries() {
        ArrayList<MacroSummary> summaries = new ArrayList<MacroSummary>();
        for (MacroMetadata metadata : this.getData()) {
            summaries.add(metadata.extractMacroSummary());
        }
        return summaries;
    }

    @Override
    public MacroMetadata getByMacroName(String macroName) {
        if (this.userMacroLibrary.hasMacro(macroName)) {
            return this.extractMetadataFromConfig(this.userMacroLibrary.getMacro(macroName));
        }
        return null;
    }

    @Override
    public MacroMetadata getByMacroNameAndId(String macroName, String alternateId) {
        if (StringUtils.isNotBlank((CharSequence)alternateId)) {
            return null;
        }
        return this.getByMacroName(macroName);
    }

    private Collection<MacroMetadata> extractMetadata(Map<String, UserMacroConfig> userMacroConfigs) {
        HashSet<MacroMetadata> macroMetadata = new HashSet<MacroMetadata>();
        for (UserMacroConfig userMacroConfig : userMacroConfigs.values()) {
            MacroMetadata metadata = this.extractMetadataFromConfig(userMacroConfig);
            if (metadata == null) continue;
            macroMetadata.add(metadata);
        }
        return macroMetadata;
    }

    private MacroMetadata extractMetadataFromConfig(UserMacroConfig userMacroConfig) {
        if (userMacroConfig.getParameters() == null) {
            return null;
        }
        String macroName = userMacroConfig.getName();
        String location = userMacroConfig.getIconLocation();
        String description = userMacroConfig.getDescription();
        Set<String> categories = userMacroConfig.getCategories();
        String docUrl = userMacroConfig.getDocumentationUrl();
        String title = userMacroConfig.getTitle();
        boolean hidden = userMacroConfig.isHidden();
        MacroIcon icon = null;
        if (!StringUtils.isBlank((CharSequence)location)) {
            icon = new MacroIcon(HtmlUtil.htmlEncode(location), !location.startsWith("http"));
        }
        MacroFormDetails formDetails = MacroFormDetails.builder().macroName(macroName).documentationUrl(docUrl).parameters(userMacroConfig.getParameters()).build();
        if (StringUtils.isBlank((CharSequence)title)) {
            title = macroName;
        }
        if (userMacroConfig.isHasBody()) {
            formDetails.setBody(new MacroBody(USER_MACRO_DUMMY_PLUGIN_KEY, macroName));
        }
        MacroMetadataBuilder builder = MacroMetadata.builder().setMacroName(macroName).setPluginKey(USER_MACRO_DUMMY_PLUGIN_KEY).setTitle(HtmlUtil.htmlEncode(title)).setIcon(icon).setDescription(HtmlUtil.htmlEncode(description)).setCategories(categories).setHidden(hidden).setFormDetails(formDetails);
        return builder.build();
    }

    public void setUserMacroLibrary(UserMacroLibrary userMacroLibrary) {
        this.userMacroLibrary = userMacroLibrary;
    }
}

