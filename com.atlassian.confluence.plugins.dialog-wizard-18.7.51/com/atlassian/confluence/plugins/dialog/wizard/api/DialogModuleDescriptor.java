/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.module.ModuleFactory
 *  javax.annotation.Nonnull
 *  org.dom4j.Element
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.dialog.wizard.api;

import com.atlassian.confluence.plugins.dialog.wizard.api.Dialog;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;
import javax.annotation.Nonnull;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DialogModuleDescriptor
extends AbstractModuleDescriptor<Dialog> {
    private static final Logger log = LoggerFactory.getLogger(DialogModuleDescriptor.class);
    private Dialog dialog;

    public DialogModuleDescriptor(ModuleFactory moduleFactory) {
        super(moduleFactory);
    }

    public void init(@Nonnull Plugin plugin, @Nonnull Element element) throws PluginParseException {
        super.init(plugin, element);
        String widthString = element.attributeValue("width");
        String heightString = element.attributeValue("height");
        int width = 600;
        int height = 480;
        try {
            width = Integer.parseInt(widthString);
            height = Integer.parseInt(heightString);
        }
        catch (NumberFormatException e) {
            log.error("Could not parse width and height for dialog: " + widthString + ", " + heightString);
        }
        this.dialog = new Dialog(element.attributeValue("id"), element.attributeValue("title-key"), width, height, element.attributeValue("class-names"));
    }

    public Dialog getModule() {
        return this.dialog;
    }
}

