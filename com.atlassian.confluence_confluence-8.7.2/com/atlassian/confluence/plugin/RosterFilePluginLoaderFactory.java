/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.plugin.factories.PluginFactory
 *  com.atlassian.plugin.loaders.PluginLoader
 *  com.atlassian.plugin.loaders.RosterFilePluginLoader
 *  com.google.common.base.Strings
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.FactoryBean
 */
package com.atlassian.confluence.plugin;

import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.factories.PluginFactory;
import com.atlassian.plugin.loaders.PluginLoader;
import com.atlassian.plugin.loaders.RosterFilePluginLoader;
import com.google.common.base.Strings;
import java.io.File;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;

public class RosterFilePluginLoaderFactory
implements FactoryBean {
    private static final Logger log = LoggerFactory.getLogger(RosterFilePluginLoaderFactory.class);
    private static final String ROSTER_FILE_PROPERTY = "atlassian.confluence.plugin.roster.file";
    private final RosterFilePluginLoader rosterFilePluginLoader;

    public RosterFilePluginLoaderFactory(List<PluginFactory> pluginFactories, PluginEventManager eventManager) {
        String rosterFileName = System.getProperty(ROSTER_FILE_PROPERTY);
        RosterFilePluginLoader rosterFilePluginLoader = null;
        if (!Strings.isNullOrEmpty((String)rosterFileName)) {
            try {
                File rosterFile = new File(rosterFileName);
                rosterFilePluginLoader = new RosterFilePluginLoader(rosterFile, pluginFactories, eventManager);
            }
            catch (RuntimeException runtime) {
                log.error("Cannot create RosterFilePluginLoader for '{}' : {}", (Object)rosterFileName, (Object)runtime.getMessage());
                log.debug("Stack trace for RosterFilePluginLoader failure:", (Throwable)runtime);
            }
        } else {
            log.debug("No roster file specified - property '{}' is not defined.", (Object)ROSTER_FILE_PROPERTY);
        }
        this.rosterFilePluginLoader = rosterFilePluginLoader;
    }

    public Object getObject() {
        return this.rosterFilePluginLoader;
    }

    public Class getObjectType() {
        return PluginLoader.class;
    }

    public boolean isSingleton() {
        return true;
    }
}

