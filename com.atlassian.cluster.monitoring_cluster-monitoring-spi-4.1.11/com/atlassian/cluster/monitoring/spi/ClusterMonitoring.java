/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.annotations.PublicSpi
 *  com.atlassian.plugin.ModuleCompleteKey
 *  io.atlassian.fugue.Either
 *  javax.annotation.Nonnull
 */
package com.atlassian.cluster.monitoring.spi;

import com.atlassian.annotations.Internal;
import com.atlassian.annotations.PublicSpi;
import com.atlassian.cluster.monitoring.spi.model.MonitoringError;
import com.atlassian.cluster.monitoring.spi.model.NodeIdentifier;
import com.atlassian.cluster.monitoring.spi.model.NodeInformation;
import com.atlassian.cluster.monitoring.spi.model.Table;
import com.atlassian.plugin.ModuleCompleteKey;
import io.atlassian.fugue.Either;
import java.util.List;
import javax.annotation.Nonnull;

@Internal
@PublicSpi
public interface ClusterMonitoring {
    @Nonnull
    public Either<MonitoringError, List<NodeInformation>> getNodes();

    @Nonnull
    public Either<MonitoringError, NodeIdentifier> getCurrentNode();

    @Nonnull
    public Either<MonitoringError, Table> getData(ModuleCompleteKey var1, NodeIdentifier var2);

    public boolean isAvailable();

    public boolean isDataCenterLicensed();

    public boolean enableClustering();

    public boolean isClusterSetupEnabled();
}

