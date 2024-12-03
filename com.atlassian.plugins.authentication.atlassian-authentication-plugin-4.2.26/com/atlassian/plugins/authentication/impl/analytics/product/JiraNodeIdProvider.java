/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.jira.cluster.ClusterInfo
 *  com.atlassian.plugin.spring.scanner.annotation.component.JiraComponent
 *  com.atlassian.plugin.spring.scanner.annotation.imports.JiraImport
 *  javax.inject.Inject
 */
package com.atlassian.plugins.authentication.impl.analytics.product;

import com.atlassian.jira.cluster.ClusterInfo;
import com.atlassian.plugin.spring.scanner.annotation.component.JiraComponent;
import com.atlassian.plugin.spring.scanner.annotation.imports.JiraImport;
import com.atlassian.plugins.authentication.impl.analytics.NodeIdProvider;
import java.util.Optional;
import javax.inject.Inject;

@JiraComponent
public class JiraNodeIdProvider
implements NodeIdProvider {
    private final ClusterInfo clusterInfo;

    @Inject
    public JiraNodeIdProvider(@JiraImport ClusterInfo clusterInfo) {
        this.clusterInfo = clusterInfo;
    }

    @Override
    public String getNodeId() {
        return Optional.ofNullable(this.clusterInfo.getNodeId()).orElse("NOT_CLUSTERED");
    }
}

