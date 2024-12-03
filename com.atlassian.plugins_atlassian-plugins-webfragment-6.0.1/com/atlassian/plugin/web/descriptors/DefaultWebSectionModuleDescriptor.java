/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.web.WebInterfaceManager
 *  com.atlassian.plugin.web.descriptors.WebSectionModuleDescriptor
 *  org.dom4j.Element
 */
package com.atlassian.plugin.web.descriptors;

import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.WebInterfaceManager;
import com.atlassian.plugin.web.descriptors.AbstractWebFragmentModuleDescriptor;
import com.atlassian.plugin.web.descriptors.WebSectionModuleDescriptor;
import org.dom4j.Element;

public class DefaultWebSectionModuleDescriptor
extends AbstractWebFragmentModuleDescriptor<Void>
implements WebSectionModuleDescriptor {
    private String location;

    public DefaultWebSectionModuleDescriptor(WebInterfaceManager webInterfaceManager) {
        super(webInterfaceManager);
    }

    public DefaultWebSectionModuleDescriptor() {
    }

    @Override
    public void init(Plugin plugin, Element element) throws PluginParseException {
        super.init(plugin, element);
        this.location = element.attributeValue("location");
    }

    public String getLocation() {
        return this.location;
    }

    public Class<Void> getModuleClass() {
        return Void.class;
    }

    public Void getModule() {
        return null;
    }
}

