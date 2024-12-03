/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.elements.ResourceDescriptor
 */
package com.atlassian.confluence.util.i18n;

import com.atlassian.confluence.util.i18n.ResourceBundleI18NResource;
import com.atlassian.confluence.util.i18n.UTF8Control;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.elements.ResourceDescriptor;
import java.util.ResourceBundle;

public class HelpPathsI18NResource
extends ResourceBundleI18NResource {
    private final Plugin plugin;
    private final ResourceDescriptor resourceDescriptor;

    public HelpPathsI18NResource(Plugin plugin, ResourceDescriptor resourceDescriptor) {
        this.plugin = plugin;
        this.resourceDescriptor = resourceDescriptor;
    }

    @Override
    protected String getLocation() {
        return this.resourceDescriptor.getLocation();
    }

    @Override
    protected ClassLoader getClassLoader() {
        return this.plugin.getClassLoader();
    }

    @Override
    protected ResourceBundle.Control getControl() {
        return new UTF8Control();
    }
}

