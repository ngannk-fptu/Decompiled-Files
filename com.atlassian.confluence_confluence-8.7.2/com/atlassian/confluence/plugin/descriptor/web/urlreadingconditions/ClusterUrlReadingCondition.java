/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.webresource.QueryParams
 *  com.atlassian.plugin.webresource.condition.UrlReadingCondition
 *  com.atlassian.plugin.webresource.url.UrlBuilder
 */
package com.atlassian.confluence.plugin.descriptor.web.urlreadingconditions;

import com.atlassian.confluence.cluster.ClusterManager;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.webresource.QueryParams;
import com.atlassian.plugin.webresource.condition.UrlReadingCondition;
import com.atlassian.plugin.webresource.url.UrlBuilder;
import java.util.Map;

public class ClusterUrlReadingCondition
implements UrlReadingCondition {
    private static final String CLUSTER_MODE_QUERY_PARAM = "isclustered";
    private final ClusterManager clusterManager;

    public ClusterUrlReadingCondition(ClusterManager clusterManager) {
        this.clusterManager = clusterManager;
    }

    public void init(Map<String, String> stringStringMap) throws PluginParseException {
    }

    public void addToUrl(UrlBuilder urlBuilder) {
        if (this.clusterManager.isClustered()) {
            urlBuilder.addToQueryString(CLUSTER_MODE_QUERY_PARAM, String.valueOf(true));
        }
    }

    public boolean shouldDisplay(QueryParams queryParams) {
        return Boolean.valueOf(queryParams.get(CLUSTER_MODE_QUERY_PARAM));
    }
}

