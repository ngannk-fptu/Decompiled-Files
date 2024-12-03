/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.plugin.factories.PluginFactory
 *  com.atlassian.plugin.loaders.DirectoryScanner
 *  com.atlassian.plugin.loaders.PluginLoader
 *  com.atlassian.plugin.loaders.ScanningPluginLoader
 *  com.atlassian.plugin.loaders.classloading.Scanner
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.FactoryBean
 */
package com.atlassian.confluence.plugin;

import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.factories.PluginFactory;
import com.atlassian.plugin.loaders.DirectoryScanner;
import com.atlassian.plugin.loaders.PluginLoader;
import com.atlassian.plugin.loaders.ScanningPluginLoader;
import com.atlassian.plugin.loaders.classloading.Scanner;
import java.io.File;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;

public class DirectoryScanningPluginFactory
implements FactoryBean {
    private static final Logger log = LoggerFactory.getLogger(DirectoryScanningPluginFactory.class);
    private static final String PLUGIN_SCAN_DIR = "atlassian.confluence.plugin.scan.directory";
    private final ScanningPluginLoader scanningPluginLoader;

    public DirectoryScanningPluginFactory(List<PluginFactory> pluginFactories, PluginEventManager eventManager) {
        String directoryName = System.getProperty(PLUGIN_SCAN_DIR);
        if (directoryName == null) {
            log.debug("The {} property is not defined. Will proceed with normal Plugin loading.", (Object)PLUGIN_SCAN_DIR);
            this.scanningPluginLoader = null;
            return;
        }
        File directoryFile = new File(directoryName);
        if (!directoryFile.isDirectory() && !directoryFile.canRead()) {
            log.error("Please check that the directory {} exists and contains the plugins to be loaded.", (Object)directoryFile.getAbsolutePath());
            this.scanningPluginLoader = null;
            return;
        }
        DirectoryScanner scanner = new DirectoryScanner(directoryFile);
        this.scanningPluginLoader = new ScanningPluginLoader((Scanner)scanner, pluginFactories, eventManager);
    }

    public Object getObject() {
        return this.scanningPluginLoader;
    }

    public Class getObjectType() {
        return PluginLoader.class;
    }

    public boolean isSingleton() {
        return true;
    }
}

