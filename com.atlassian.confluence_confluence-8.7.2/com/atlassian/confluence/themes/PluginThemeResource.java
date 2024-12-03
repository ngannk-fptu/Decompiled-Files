/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.elements.ResourceDescriptor
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.themes;

import com.atlassian.confluence.themes.ThemeResource;
import com.atlassian.plugin.elements.ResourceDescriptor;
import org.apache.commons.lang3.StringUtils;

public class PluginThemeResource
implements ThemeResource {
    private static final String IEONLY_PARAM = "ieonly";
    private final String location;
    private final String completeModuleKey;
    private final String name;
    private final boolean isIeOnly;
    private final ThemeResource.Type type;

    private PluginThemeResource(String completeModuleKey, ResourceDescriptor resourceDescriptor, ThemeResource.Type type) {
        this.type = type;
        this.completeModuleKey = completeModuleKey;
        this.name = resourceDescriptor.getName();
        this.location = resourceDescriptor.getLocation();
        this.isIeOnly = StringUtils.equalsIgnoreCase((CharSequence)resourceDescriptor.getParameter(IEONLY_PARAM), (CharSequence)"true");
    }

    public static PluginThemeResource css(String completeModuleKey, ResourceDescriptor resourceDescriptor) {
        return new PluginThemeResource(completeModuleKey, resourceDescriptor, ThemeResource.Type.CSS);
    }

    public static PluginThemeResource javascript(String completeModuleKey, ResourceDescriptor resourceDescriptor) {
        return new PluginThemeResource(completeModuleKey, resourceDescriptor, ThemeResource.Type.JAVSCRIPT);
    }

    @Override
    public ThemeResource.Type getType() {
        return this.type;
    }

    @Override
    public String getLocation() {
        return this.location;
    }

    @Override
    public String getCompleteModuleKey() {
        return this.completeModuleKey;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public String toString() {
        return "[ completeModuleKey='" + this.completeModuleKey + "', name='" + this.name + "', location='" + this.location + "' ]";
    }

    @Override
    public boolean isIeOnly() {
        return this.isIeOnly;
    }
}

