/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.web.descriptors.WebItemModuleDescriptor
 *  com.atlassian.plugin.web.descriptors.WebSectionModuleDescriptor
 */
package com.atlassian.confluence.web;

import com.atlassian.confluence.plugin.descriptor.web.ConfluenceWebInterfaceManager;
import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.web.WebMenu;
import com.atlassian.confluence.web.WebMenuManager;
import com.atlassian.plugin.web.descriptors.WebItemModuleDescriptor;
import com.atlassian.plugin.web.descriptors.WebSectionModuleDescriptor;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ConfluenceWebMenuManager
implements WebMenuManager {
    protected ConfluenceWebInterfaceManager webInterfaceManager;

    @Override
    public WebMenu getMenu(String id, String menuKey, WebInterfaceContext context) {
        return this.getMenu(id, Collections.singleton(menuKey), context);
    }

    @Override
    public WebMenu getMenu(String id, Collection<String> menuKeys, WebInterfaceContext context) {
        WebMenu menu = new WebMenu(id);
        for (String menuKey : menuKeys) {
            menu.addSection("leading", null, null, this.getLeadingItems(menuKey, context));
            for (WebSectionModuleDescriptor section : this.getSections(menuKey, context)) {
                String label = section.getWebLabel() == null ? "" : section.getWebLabel().getKey();
                String ariaLabel = section.getParams() == null ? "" : (String)section.getParams().get("ariaLabelKey");
                menu.addSection(section.getKey(), label, ariaLabel, this.getSectionItems(menuKey, section.getKey(), context));
            }
            menu.addSection("trailing", "", null, this.getTrailingItems(menuKey, context));
        }
        return menu;
    }

    protected List<WebItemModuleDescriptor> getSectionItems(String menuKey, String sectionKey, WebInterfaceContext context) {
        return this.webInterfaceManager.getDisplayableItems(menuKey + "/" + sectionKey, context);
    }

    protected List<WebSectionModuleDescriptor> getSections(String menuKey, WebInterfaceContext context) {
        return this.webInterfaceManager.getDisplayableSections(menuKey, context);
    }

    protected List<WebItemModuleDescriptor> getTrailingItems(String menuKey, WebInterfaceContext context) {
        return Collections.emptyList();
    }

    protected List<WebItemModuleDescriptor> getLeadingItems(String menuKey, WebInterfaceContext context) {
        return this.webInterfaceManager.getDisplayableItems(menuKey, context);
    }

    public void setWebInterfaceManager(ConfluenceWebInterfaceManager webInterfaceManager) {
        this.webInterfaceManager = webInterfaceManager;
    }
}

