/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.web.WebInterfaceManager
 *  com.atlassian.plugin.web.descriptors.DefaultWebItemModuleDescriptor
 *  com.atlassian.plugin.web.descriptors.WebFragmentModuleDescriptor
 *  com.atlassian.plugin.web.descriptors.WebItemModuleDescriptor
 *  com.atlassian.plugin.web.model.WebIcon
 *  com.atlassian.plugin.web.model.WebLink
 *  com.atlassian.spring.container.ContainerManager
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugin.descriptor.web.descriptors;

import com.atlassian.confluence.plugin.ConfluencePluginUtils;
import com.atlassian.confluence.plugin.descriptor.web.descriptors.ConfluenceAbstractWebFragmentModuleDescriptor;
import com.atlassian.confluence.plugin.descriptor.web.model.ConfluenceWebIcon;
import com.atlassian.confluence.plugin.descriptor.web.model.ConfluenceWebLink;
import com.atlassian.confluence.plugin.descriptor.web.model.SettableWebLink;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.web.WebInterfaceManager;
import com.atlassian.plugin.web.descriptors.DefaultWebItemModuleDescriptor;
import com.atlassian.plugin.web.descriptors.WebFragmentModuleDescriptor;
import com.atlassian.plugin.web.descriptors.WebItemModuleDescriptor;
import com.atlassian.plugin.web.model.WebIcon;
import com.atlassian.plugin.web.model.WebLink;
import com.atlassian.spring.container.ContainerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfluenceWebItemModuleDescriptor
extends ConfluenceAbstractWebFragmentModuleDescriptor
implements WebItemModuleDescriptor {
    private static final Logger log = LoggerFactory.getLogger(ConfluenceWebItemModuleDescriptor.class);

    public ConfluenceWebItemModuleDescriptor() {
        super((WebFragmentModuleDescriptor)new DefaultWebItemModuleDescriptor((WebInterfaceManager)ContainerManager.getComponent((String)"webInterfaceManager")));
    }

    public String getSection() {
        return this.getModuleDescriptor().getSection();
    }

    public ConfluenceWebLink getLink() {
        WebLink link = ((WebItemModuleDescriptor)this.getDecoratedDescriptor()).getLink();
        if (link == null) {
            return null;
        }
        if (this.getModuleClass() == null || this.getModuleClass().equals(Void.class)) {
            return new ConfluenceWebLink(link);
        }
        Plugin plugin = this.getPlugin();
        Object customWebLink = ConfluencePluginUtils.instantiatePluginModule(plugin, this.getModuleClass());
        if (!(customWebLink instanceof SettableWebLink)) {
            log.error("The class " + this.getModuleClass().getName() + " is not a SettableWebLink.");
            return null;
        }
        ((SettableWebLink)customWebLink).setLink(link);
        return (ConfluenceWebLink)customWebLink;
    }

    public ConfluenceWebIcon getIcon() {
        WebIcon webIcon = this.getModuleDescriptor().getIcon();
        if (webIcon == null) {
            return null;
        }
        return new ConfluenceWebIcon(webIcon);
    }

    public String getStyleClass() {
        return this.getModuleDescriptor().getStyleClass();
    }

    public String getEntryPoint() {
        return this.getModuleDescriptor().getEntryPoint();
    }

    private WebItemModuleDescriptor getModuleDescriptor() {
        return (WebItemModuleDescriptor)this.getDecoratedDescriptor();
    }

    public String toString() {
        return this.getSection() + "/" + this.getKey();
    }
}

