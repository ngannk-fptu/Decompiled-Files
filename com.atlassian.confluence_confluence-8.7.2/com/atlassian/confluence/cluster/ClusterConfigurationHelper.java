/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.cluster;

import com.atlassian.confluence.cluster.ClusterException;
import com.atlassian.confluence.cluster.ClusterJoinConfig;
import com.atlassian.confluence.impl.setup.BootstrapDatabaseAccessor;
import java.io.File;
import java.net.NetworkInterface;
import java.util.List;
import java.util.Optional;

public interface ClusterConfigurationHelper {
    public boolean isClusteredInstance();

    public boolean isClusterHomeConfigured();

    public void createCluster(String var1, File var2, String var3, ClusterJoinConfig var4) throws ClusterException;

    public void bootstrapCluster(BootstrapDatabaseAccessor.BootstrapDatabaseData var1) throws ClusterException;

    public List<NetworkInterface> getClusterableInterfaces();

    public Optional<File> sharedHome();

    public Optional<ClusterJoinConfig> joinConfig();
}

