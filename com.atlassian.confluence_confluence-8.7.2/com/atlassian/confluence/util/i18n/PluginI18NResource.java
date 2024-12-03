/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.elements.ResourceDescriptor
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.util.i18n;

import com.atlassian.confluence.util.i18n.AbstractI18NResource;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.elements.ResourceDescriptor;
import java.io.InputStream;
import org.apache.commons.lang3.StringUtils;

public class PluginI18NResource
extends AbstractI18NResource {
    private final Plugin plugin;
    private final ResourceDescriptor resourceDescriptor;

    public PluginI18NResource(Plugin plugin, ResourceDescriptor resourceDescriptor) {
        this.plugin = plugin;
        this.resourceDescriptor = resourceDescriptor;
    }

    @Override
    protected InputStream getPropertyResourceAsStream(String locale) {
        InputStream resource = this.plugin.getResourceAsStream(this.getResourcePath(locale, true));
        if (resource == null) {
            resource = this.plugin.getResourceAsStream(this.getResourcePath(locale, false));
        }
        return resource;
    }

    private String getResourcePath(String locale, boolean replaceDotWithSlash) {
        String resourceLocation = replaceDotWithSlash ? this.resourceDescriptor.getLocation().replaceAll("\\.", "/") : this.resourceDescriptor.getLocation();
        return resourceLocation + (String)(StringUtils.isBlank((CharSequence)locale) ? "" : "_" + locale) + ".properties";
    }
}

