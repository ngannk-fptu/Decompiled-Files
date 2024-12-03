/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.web.WebFragmentHelper
 *  com.atlassian.plugin.web.WebInterfaceManager
 *  com.atlassian.plugin.web.descriptors.WebItemModuleDescriptor
 *  com.atlassian.plugin.web.descriptors.WebPanelModuleDescriptor
 *  com.atlassian.plugin.web.descriptors.WebSectionModuleDescriptor
 *  com.atlassian.plugin.web.model.WebPanel
 */
package com.atlassian.confluence.impl.plugin.web.readonly;

import com.atlassian.confluence.impl.plugin.web.readonly.ReadOnlyWebFragmentHelper;
import com.atlassian.confluence.impl.plugin.web.readonly.ReadOnlyWebItemModuleDescriptor;
import com.atlassian.confluence.impl.plugin.web.readonly.ReadOnlyWebPanelModuleDescriptor;
import com.atlassian.confluence.impl.plugin.web.readonly.ReadOnlyWebSectionModuleDescriptor;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.plugin.web.WebFragmentHelper;
import com.atlassian.plugin.web.WebInterfaceManager;
import com.atlassian.plugin.web.descriptors.WebItemModuleDescriptor;
import com.atlassian.plugin.web.descriptors.WebPanelModuleDescriptor;
import com.atlassian.plugin.web.descriptors.WebSectionModuleDescriptor;
import com.atlassian.plugin.web.model.WebPanel;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReadOnlyWebInterfaceManager
implements WebInterfaceManager {
    private final WebInterfaceManager delegate;

    public ReadOnlyWebInterfaceManager(WebInterfaceManager delegate) {
        this.delegate = delegate;
    }

    public boolean hasSectionsForLocation(String s) {
        return this.delegate.hasSectionsForLocation(s);
    }

    public List<WebSectionModuleDescriptor> getSections(String s) {
        return this.delegate.getSections(s).stream().map(ReadOnlyWebSectionModuleDescriptor::new).collect(Collectors.toList());
    }

    public List<WebSectionModuleDescriptor> getDisplayableSections(String s, Map<String, Object> map) {
        return this.delegate.getDisplayableSections(s, map).stream().map(ReadOnlyWebSectionModuleDescriptor::new).collect(Collectors.toList());
    }

    public List<WebItemModuleDescriptor> getItems(String s) {
        return this.delegate.getItems(s).stream().map(ReadOnlyWebItemModuleDescriptor::new).collect(Collectors.toList());
    }

    public List<WebItemModuleDescriptor> getDisplayableItems(String s, Map<String, Object> map) {
        return this.delegate.getDisplayableItems(s, map).stream().map(ReadOnlyWebItemModuleDescriptor::new).collect(Collectors.toList());
    }

    public List<WebPanel> getWebPanels(String s) {
        return this.delegate.getWebPanels(s);
    }

    public List<WebPanel> getDisplayableWebPanels(String s, Map<String, Object> map) {
        return this.delegate.getDisplayableWebPanels(s, map);
    }

    public List<WebPanelModuleDescriptor> getWebPanelDescriptors(String s) {
        return this.delegate.getWebPanelDescriptors(s).stream().map(ReadOnlyWebPanelModuleDescriptor::new).collect(Collectors.toList());
    }

    public List<WebPanelModuleDescriptor> getDisplayableWebPanelDescriptors(String s, Map<String, Object> map) {
        return this.delegate.getDisplayableWebPanelDescriptors(s, map).stream().map(ReadOnlyWebPanelModuleDescriptor::new).collect(Collectors.toList());
    }

    public void refresh() {
        throw new UnsupportedOperationException();
    }

    public WebFragmentHelper getWebFragmentHelper() {
        return GeneralUtil.applyIfNonNull(this.delegate.getWebFragmentHelper(), ReadOnlyWebFragmentHelper::new);
    }
}

