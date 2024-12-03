/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.spi.ActiveObjectsPluginConfiguration
 *  com.google.common.base.Preconditions
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.activeobjects.internal;

import com.atlassian.activeobjects.internal.DatabaseConfiguration;
import com.atlassian.activeobjects.spi.ActiveObjectsPluginConfiguration;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultDatabaseConfiguration
implements DatabaseConfiguration {
    private static final String DEFAULT_BASE_DIR = "data/plugins/activeobjects";
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final ActiveObjectsPluginConfiguration pluginConfiguration;

    public DefaultDatabaseConfiguration(ActiveObjectsPluginConfiguration pluginConfiguration) {
        this.pluginConfiguration = (ActiveObjectsPluginConfiguration)Preconditions.checkNotNull((Object)pluginConfiguration);
    }

    @Override
    public String getBaseDirectory() {
        try {
            return this.pluginConfiguration.getDatabaseBaseDirectory();
        }
        catch (RuntimeException e) {
            if (e.getClass().getSimpleName().equals("ServiceUnavailableException")) {
                this.log.debug("Active objects plugin configuration service not present, so using default base directory <{}>", (Object)DEFAULT_BASE_DIR);
                return DEFAULT_BASE_DIR;
            }
            throw e;
        }
    }
}

