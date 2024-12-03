/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 */
package com.atlassian.confluence.api.service.web;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.web.WebItemView;
import com.atlassian.confluence.api.model.web.WebPanelView;
import com.atlassian.confluence.api.model.web.WebSectionView;
import java.util.Collection;
import java.util.Map;

@ExperimentalApi
public interface WebView {
    public Iterable<WebItemView> getItemsForSection(String var1, Map<String, Object> var2);

    public Iterable<WebSectionView> getSectionsForLocation(String var1, Map<String, Object> var2);

    public Iterable<WebSectionView> getSectionsForLocations(Collection<String> var1, Map<String, Object> var2);

    public Iterable<WebPanelView> getPanelsForLocation(String var1, Map<String, Object> var2);

    public Iterable<WebPanelView> getPanelsForLocations(Collection<String> var1, Map<String, Object> var2);

    public Map<String, Object> getWebPanelVelocityContext();
}

