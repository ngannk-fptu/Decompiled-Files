/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 */
package com.atlassian.confluence.impl.cluster;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.cluster.ClusterConfigurationHelper;
import java.util.Optional;

@Internal
public interface ClusterConfigurationHelperInternal
extends ClusterConfigurationHelper {
    public void createSharedHome();

    public void saveSetupConfigIntoSharedHome();

    public void populateExistingClusterSetupConfig();

    public void createClusterConfig();

    public void saveSharedProperty(Object var1, Object var2);

    public Optional<Object> getSharedProperty(Object var1);

    public void saveSharedBuildNumber(String var1);

    public Optional<String> getSharedBuildNumber();
}

