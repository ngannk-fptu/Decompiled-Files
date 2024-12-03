/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.ApplicationConfiguration
 */
package com.atlassian.confluence.impl.filestore;

import com.atlassian.config.ApplicationConfiguration;
import com.atlassian.confluence.cluster.ClusterConfigurationUtils;
import com.atlassian.confluence.impl.filestore.AbstractFileStoreFactory;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public final class ApplicationConfigurationFileStoreFactory
extends AbstractFileStoreFactory {
    private final ApplicationConfiguration appConfig;

    ApplicationConfigurationFileStoreFactory(ApplicationConfiguration appConfig) {
        this.appConfig = Objects.requireNonNull(appConfig);
    }

    @Override
    protected Path getConfluenceHomePath() {
        if (ClusterConfigurationUtils.isClusterHomeConfigured(this.appConfig)) {
            return this.getSharedHomePath();
        }
        return this.getLocalHomePath();
    }

    @Override
    protected Path getLocalHomePath() {
        return Paths.get(Objects.requireNonNull(this.appConfig.getApplicationHome(), "local home has not been configured"), new String[0]);
    }

    @Override
    protected Path getSharedHomePath() {
        return ClusterConfigurationUtils.getSharedHome(this.appConfig).toPath();
    }
}

