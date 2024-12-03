/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.search.service.RecentUpdateQueryParameters
 *  com.atlassian.renderer.RenderContext
 */
package com.atlassian.confluence.plugins.macros.dashboard.recentupdates.tabs;

import com.atlassian.confluence.plugins.macros.dashboard.recentupdates.RecentlyUpdatedMacroParams;
import com.atlassian.confluence.plugins.macros.dashboard.recentupdates.RecentlyUpdatedMacroRequestParams;
import com.atlassian.confluence.search.service.RecentUpdateQueryParameters;
import com.atlassian.renderer.RenderContext;
import java.util.Map;

public interface RecentlyUpdatedMacroTab {
    public String getName();

    public String getDisplayName();

    public String getNoContentMessage();

    public boolean shouldDisplay(RenderContext var1);

    public RecentUpdateQueryParameters getQueryParameters(RecentlyUpdatedMacroParams var1, RecentlyUpdatedMacroRequestParams var2, RenderContext var3);

    public Map<String, Object> getRenderContext(RecentlyUpdatedMacroRequestParams var1, RenderContext var2);

    public String getUrl();
}

