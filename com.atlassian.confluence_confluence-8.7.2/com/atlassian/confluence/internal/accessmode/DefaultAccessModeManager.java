/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.ApplicationConfiguration
 *  com.atlassian.config.ConfigurationException
 *  com.atlassian.confluence.api.model.accessmode.AccessMode
 */
package com.atlassian.confluence.internal.accessmode;

import com.atlassian.config.ApplicationConfiguration;
import com.atlassian.config.ConfigurationException;
import com.atlassian.confluence.api.model.accessmode.AccessMode;
import com.atlassian.confluence.impl.cluster.ClusterConfigurationHelperInternal;
import com.atlassian.confluence.internal.accessmode.AccessModeManager;
import com.atlassian.confluence.internal.accessmode.ThreadLocalReadOnlyAccessCacheInternal;
import java.util.Optional;

public class DefaultAccessModeManager
implements AccessModeManager {
    private final ApplicationConfiguration applicationConfig;
    private final ClusterConfigurationHelperInternal clusterConfigurationHelperInternal;

    public DefaultAccessModeManager(ApplicationConfiguration applicationConfig, ClusterConfigurationHelperInternal clusterConfigurationHelperInternal) {
        this.applicationConfig = applicationConfig;
        this.clusterConfigurationHelperInternal = clusterConfigurationHelperInternal;
    }

    @Override
    public AccessMode getAccessMode() {
        String accessModeName = (String)this.applicationConfig.getProperty((Object)"access.mode");
        return accessModeName != null ? AccessMode.valueOf((String)accessModeName) : AccessMode.READ_WRITE;
    }

    @Override
    public void updateAccessMode(AccessMode accessMode) throws ConfigurationException {
        Optional<Object> sharedAccessMode;
        this.applicationConfig.setProperty((Object)"access.mode", (Object)accessMode.name());
        this.applicationConfig.save();
        if (!(!this.clusterConfigurationHelperInternal.isClusteredInstance() || (sharedAccessMode = this.clusterConfigurationHelperInternal.getSharedProperty("access.mode")).isPresent() && sharedAccessMode.get().equals(accessMode.name()))) {
            this.clusterConfigurationHelperInternal.saveSharedProperty("access.mode", accessMode.name());
        }
    }

    @Override
    public boolean isReadOnlyAccessModeEnabled() {
        return this.getAccessMode().equals((Object)AccessMode.READ_ONLY);
    }

    @Override
    public boolean shouldEnforceReadOnlyAccess() {
        return !ThreadLocalReadOnlyAccessCacheInternal.hasReadOnlyAccessExemption() && this.isReadOnlyAccessModeEnabled();
    }
}

