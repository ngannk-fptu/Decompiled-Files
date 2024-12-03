/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.spi.ActiveObjectsPluginConfiguration
 *  com.google.common.base.Preconditions
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.activeobjects.config.internal;

import com.atlassian.activeobjects.spi.ActiveObjectsPluginConfiguration;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DefaultActiveObjectsPluginConfiguration
implements ActiveObjectsPluginConfiguration {
    private static final String DEFAULT_BASE_DIR = "data/plugins/activeobjects";
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ActiveObjectsPluginConfiguration delegate;

    public DefaultActiveObjectsPluginConfiguration(ActiveObjectsPluginConfiguration delegate) {
        this.delegate = (ActiveObjectsPluginConfiguration)Preconditions.checkNotNull((Object)delegate);
    }

    public String getDatabaseBaseDirectory() {
        try {
            return this.delegate.getDatabaseBaseDirectory();
        }
        catch (RuntimeException e) {
            if (e.getClass().getSimpleName().equals("ServiceUnavailableException")) {
                this.logger.debug("Active objects plugin configuration service not present, so using default base directory <{}>", (Object)DEFAULT_BASE_DIR);
                return DEFAULT_BASE_DIR;
            }
            throw e;
        }
    }
}

