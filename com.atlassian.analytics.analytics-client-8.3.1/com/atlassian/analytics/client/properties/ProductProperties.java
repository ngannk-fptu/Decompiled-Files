/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.analytics.client.properties;

import com.atlassian.analytics.client.AnalyticsMd5Hasher;
import com.atlassian.analytics.client.ServerIdProvider;
import com.atlassian.analytics.client.cluster.ClusterInformationProvider;
import com.atlassian.analytics.client.sen.SenProvider;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;

public class ProductProperties {
    private final String sen;
    private final String serverId;
    private final String currentNodeId;

    public ProductProperties(SenProvider senProvider, ServerIdProvider serverIdProvider, ClusterInformationProvider clusterInformationProvider) {
        Objects.requireNonNull(senProvider, "The sen provider is mandatory.");
        Objects.requireNonNull(serverIdProvider, "The server id provider is mandatory.");
        Objects.requireNonNull(clusterInformationProvider, "The information provider is mandatory.");
        this.sen = senProvider.getSen().orElse("");
        this.serverId = serverIdProvider.getServerId();
        this.currentNodeId = clusterInformationProvider.getCurrentNodeId();
    }

    private String getNodeIdSuffix() {
        if (StringUtils.isBlank((CharSequence)this.currentNodeId)) {
            return "";
        }
        return "." + AnalyticsMd5Hasher.md5Hex(this.currentNodeId);
    }

    public String getUniqueServerId() {
        return AnalyticsMd5Hasher.md5Hex(StringUtils.defaultString((String)this.serverId) + this.sen) + this.getNodeIdSuffix();
    }
}

