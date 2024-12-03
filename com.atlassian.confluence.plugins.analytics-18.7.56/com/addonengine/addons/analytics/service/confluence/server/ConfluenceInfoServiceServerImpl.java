/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.cluster.ClusterManager
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  javax.inject.Named
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.addonengine.addons.analytics.service.confluence.server;

import com.addonengine.addons.analytics.service.confluence.ConfluenceInfoService;
import com.atlassian.confluence.cluster.ClusterManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import javax.inject.Named;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;

@Named
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u0011\b\u0007\u0012\b\b\u0001\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\b\u0010\u0005\u001a\u00020\u0006H\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0007"}, d2={"Lcom/addonengine/addons/analytics/service/confluence/server/ConfluenceInfoServiceServerImpl;", "Lcom/addonengine/addons/analytics/service/confluence/ConfluenceInfoService;", "clusterManager", "Lcom/atlassian/confluence/cluster/ClusterManager;", "(Lcom/atlassian/confluence/cluster/ClusterManager;)V", "isDataCenter", "", "analytics"})
public final class ConfluenceInfoServiceServerImpl
implements ConfluenceInfoService {
    @NotNull
    private final ClusterManager clusterManager;

    @Autowired
    public ConfluenceInfoServiceServerImpl(@ComponentImport @NotNull ClusterManager clusterManager) {
        Intrinsics.checkNotNullParameter((Object)clusterManager, (String)"clusterManager");
        this.clusterManager = clusterManager;
    }

    @Override
    public boolean isDataCenter() {
        return this.clusterManager.isClustered();
    }
}

