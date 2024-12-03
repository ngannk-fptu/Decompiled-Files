/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.web;

import com.atlassian.plugin.web.WebFragmentHelper;
import com.atlassian.plugin.web.descriptors.WebItemModuleDescriptor;
import com.atlassian.plugin.web.descriptors.WebPanelModuleDescriptor;
import com.atlassian.plugin.web.descriptors.WebSectionModuleDescriptor;
import com.atlassian.plugin.web.model.WebPanel;
import java.util.List;
import java.util.Map;

public interface WebInterfaceManager {
    public boolean hasSectionsForLocation(String var1);

    public List<WebSectionModuleDescriptor> getSections(String var1);

    public List<WebSectionModuleDescriptor> getDisplayableSections(String var1, Map<String, Object> var2);

    public List<WebItemModuleDescriptor> getItems(String var1);

    public List<WebItemModuleDescriptor> getDisplayableItems(String var1, Map<String, Object> var2);

    public List<WebPanel> getWebPanels(String var1);

    public List<WebPanel> getDisplayableWebPanels(String var1, Map<String, Object> var2);

    public List<WebPanelModuleDescriptor> getWebPanelDescriptors(String var1);

    public List<WebPanelModuleDescriptor> getDisplayableWebPanelDescriptors(String var1, Map<String, Object> var2);

    public void refresh();

    public WebFragmentHelper getWebFragmentHelper();
}

