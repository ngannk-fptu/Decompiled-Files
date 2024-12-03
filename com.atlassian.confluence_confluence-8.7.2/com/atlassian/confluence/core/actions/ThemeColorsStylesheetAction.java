/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.elements.ResourceLocation
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.core.actions;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.themes.ColorSchemeBean;
import com.atlassian.confluence.themes.ColourScheme;
import com.atlassian.confluence.themes.ColourSchemeManager;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.elements.ResourceLocation;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThemeColorsStylesheetAction
extends ConfluenceActionSupport {
    private static final Logger log = LoggerFactory.getLogger(ThemeColorsStylesheetAction.class);
    private String completeModuleKey;
    private String stylesheetName;
    private String stylesheetLocation;
    private PluginAccessor pluginAccessor;
    private ColorSchemeBean colorScheme;
    private ColourSchemeManager colourSchemeManager;
    private String spaceKey;

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() throws Exception {
        ResourceLocation resource = this.getStylesheetResource(this.completeModuleKey, this.stylesheetName);
        if (resource == null) {
            log.warn("Couldn't find matching stylesheet resource for completeModuleKey: [" + this.completeModuleKey + "], stylesheetName: [" + this.stylesheetName + "]");
            return "error";
        }
        this.colorScheme = new ColorSchemeBean(this.getActiveColorScheme(this.spaceKey));
        this.stylesheetLocation = this.prependSlashIfMissing(resource.getLocation());
        return "success";
    }

    private ResourceLocation getStylesheetResource(String completeModuleKey, String stylesheetName) {
        ModuleDescriptor module = this.pluginAccessor.getEnabledPluginModule(completeModuleKey);
        if (module == null) {
            return null;
        }
        ResourceLocation location = module.getResourceLocation("download", stylesheetName);
        if (location != null) {
            return location;
        }
        return module.getResourceLocation("stylesheet", stylesheetName);
    }

    public String getStylesheetLocation() {
        return this.stylesheetLocation;
    }

    @Override
    public void setPluginAccessor(PluginAccessor pluginAccessor) {
        this.pluginAccessor = pluginAccessor;
    }

    public void setCompleteModuleKey(String completeModuleKey) {
        this.completeModuleKey = completeModuleKey;
    }

    public void setStylesheetName(String stylesheetName) {
        this.stylesheetName = stylesheetName;
    }

    private String prependSlashIfMissing(String text) {
        if (text == null) {
            return null;
        }
        if (text.startsWith("/")) {
            return text;
        }
        return "/" + text;
    }

    public ColorSchemeBean getColorScheme() {
        return this.colorScheme;
    }

    private ColourScheme getActiveColorScheme(String spaceKey) {
        if (StringUtils.isNotEmpty((CharSequence)spaceKey)) {
            return this.colourSchemeManager.getSpaceColourScheme(spaceKey);
        }
        return this.colourSchemeManager.getGlobalColourScheme();
    }

    public void setColourSchemeManager(ColourSchemeManager colourSchemeManager) {
        this.colourSchemeManager = colourSchemeManager;
    }

    public void setSpaceKey(String spaceKey) {
        this.spaceKey = spaceKey;
    }
}

