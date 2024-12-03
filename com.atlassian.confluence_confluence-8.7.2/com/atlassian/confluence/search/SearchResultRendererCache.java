/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginAccessor
 */
package com.atlassian.confluence.search;

import com.atlassian.confluence.plugin.SearchResultRenderer;
import com.atlassian.plugin.PluginAccessor;
import java.util.Collections;
import java.util.List;

public class SearchResultRendererCache {
    private volatile List<SearchResultRenderer> searchReslultRenderers = null;
    private PluginAccessor pluginAccessor;

    public List<SearchResultRenderer> getSearchReslultRenderers() {
        if (this.searchReslultRenderers == null) {
            this.searchReslultRenderers = Collections.unmodifiableList(this.pluginAccessor.getEnabledModulesByClass(SearchResultRenderer.class));
        }
        return this.searchReslultRenderers;
    }

    public void updateCache(boolean updateIfNull) {
        if (this.searchReslultRenderers != null || updateIfNull) {
            this.searchReslultRenderers = Collections.unmodifiableList(this.pluginAccessor.getEnabledModulesByClass(SearchResultRenderer.class));
        }
    }

    public void setPluginAccessor(PluginAccessor plugAccessor) {
        this.pluginAccessor = plugAccessor;
    }
}

