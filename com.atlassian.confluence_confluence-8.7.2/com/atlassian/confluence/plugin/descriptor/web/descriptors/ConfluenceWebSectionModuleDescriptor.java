/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.web.WebInterfaceManager
 *  com.atlassian.plugin.web.descriptors.DefaultWebSectionModuleDescriptor
 *  com.atlassian.plugin.web.descriptors.WebFragmentModuleDescriptor
 *  com.atlassian.plugin.web.descriptors.WebSectionModuleDescriptor
 *  com.atlassian.spring.container.ContainerManager
 *  org.dom4j.Element
 */
package com.atlassian.confluence.plugin.descriptor.web.descriptors;

import com.atlassian.confluence.plugin.descriptor.web.descriptors.ConfluenceAbstractWebFragmentModuleDescriptor;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.WebInterfaceManager;
import com.atlassian.plugin.web.descriptors.DefaultWebSectionModuleDescriptor;
import com.atlassian.plugin.web.descriptors.WebFragmentModuleDescriptor;
import com.atlassian.plugin.web.descriptors.WebSectionModuleDescriptor;
import com.atlassian.spring.container.ContainerManager;
import org.dom4j.Element;

public class ConfluenceWebSectionModuleDescriptor
extends ConfluenceAbstractWebFragmentModuleDescriptor
implements WebSectionModuleDescriptor {
    private boolean hideSingleWebItem = false;

    public ConfluenceWebSectionModuleDescriptor() {
        super((WebFragmentModuleDescriptor)new DefaultWebSectionModuleDescriptor((WebInterfaceManager)ContainerManager.getComponent((String)"webInterfaceManager")));
    }

    public void init(Plugin plugin, Element element) throws PluginParseException {
        super.init(plugin, element);
        this.hideSingleWebItem = Boolean.parseBoolean(element.attributeValue("hideSingleWebItem"));
    }

    public String getLocation() {
        return ((WebSectionModuleDescriptor)this.getDecoratedDescriptor()).getLocation();
    }

    public boolean hideSingleWebItem() {
        return this.hideSingleWebItem;
    }
}

