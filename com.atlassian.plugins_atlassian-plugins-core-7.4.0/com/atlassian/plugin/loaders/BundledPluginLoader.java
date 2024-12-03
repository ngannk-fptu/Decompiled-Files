/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.ReferenceMode
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.plugin.factories.PluginFactory
 *  com.atlassian.plugin.loaders.classloading.DeploymentUnit
 *  com.google.common.base.Preconditions
 *  org.apache.commons.io.FileUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.loaders;

import com.atlassian.plugin.DefaultPluginArtifactFactory;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginInternal;
import com.atlassian.plugin.ReferenceMode;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.factories.PluginFactory;
import com.atlassian.plugin.loaders.DirectoryScanner;
import com.atlassian.plugin.loaders.FileListScanner;
import com.atlassian.plugin.loaders.ScanningPluginLoader;
import com.atlassian.plugin.loaders.classloading.DeploymentUnit;
import com.atlassian.plugin.loaders.classloading.EmptyScanner;
import com.atlassian.plugin.loaders.classloading.ForwardingScanner;
import com.atlassian.plugin.loaders.classloading.Scanner;
import com.google.common.base.Preconditions;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BundledPluginLoader
extends ScanningPluginLoader {
    private static final Logger log = LoggerFactory.getLogger(BundledPluginLoader.class);

    public static String getListSuffix() {
        return ".list";
    }

    private BundledPluginLoader(Scanner scanner, List<PluginFactory> pluginFactories, PluginEventManager eventManager) {
        super(new NonRemovingScanner(scanner), pluginFactories, new DefaultPluginArtifactFactory(ReferenceMode.PERMIT_REFERENCE), eventManager);
    }

    public BundledPluginLoader(File source, List<PluginFactory> pluginFactories, PluginEventManager eventManager) {
        this(BundledPluginLoader.buildSourceScanner(source), pluginFactories, eventManager);
    }

    public BundledPluginLoader(URL zipUrl, File pluginPath, List<PluginFactory> pluginFactories, PluginEventManager eventManager) {
        this(BundledPluginLoader.buildZipScanner(zipUrl, pluginPath), pluginFactories, eventManager);
    }

    @Override
    protected Plugin postProcess(Plugin plugin) {
        if (plugin instanceof PluginInternal) {
            ((PluginInternal)plugin).setBundledPlugin(true);
        } else {
            log.warn("unable to set bundled attribute on plugin '{}' as it is of class {}", (Object)plugin, (Object)plugin.getClass().getCanonicalName());
        }
        return plugin;
    }

    private static Scanner buildScannerCommon(File file) {
        if (file.isDirectory()) {
            return new DirectoryScanner(file);
        }
        if (file.isFile() && file.getName().endsWith(BundledPluginLoader.getListSuffix())) {
            List<File> files = BundledPluginLoader.readListFile(file);
            return new FileListScanner(files);
        }
        return null;
    }

    private static Scanner buildSourceScanner(File source) {
        Preconditions.checkNotNull((Object)source, (Object)"Source must not be null");
        Scanner scanner = BundledPluginLoader.buildScannerCommon(source);
        if (null == scanner) {
            log.error("Cannot build a scanner for source '{}'", (Object)source);
            return new EmptyScanner();
        }
        return scanner;
    }

    private static Scanner buildZipScanner(URL url, File pluginPath) {
        Preconditions.checkArgument((null != url ? 1 : 0) != 0, (Object)"Bundled plugins url cannot be null");
        Scanner scanner = null;
        File file = FileUtils.toFile((URL)url);
        if (null != file) {
            scanner = BundledPluginLoader.buildScannerCommon(file);
        }
        if (null == scanner) {
            com.atlassian.plugin.util.FileUtils.conditionallyExtractZipFile(url, pluginPath);
            scanner = new DirectoryScanner(pluginPath);
        }
        return scanner;
    }

    private static List<File> readListFile(File file) {
        try {
            List<String> fnames = Files.readAllLines(file.toPath());
            ArrayList<File> files = new ArrayList<File>();
            for (String fname : fnames) {
                files.add(new File(fname));
            }
            return files;
        }
        catch (IOException e) {
            throw new IllegalStateException("Unable to read list from " + file, e);
        }
    }

    private static class NonRemovingScanner
    extends ForwardingScanner {
        NonRemovingScanner(Scanner scanner) {
            super(scanner);
        }

        @Override
        public void remove(DeploymentUnit unit) {
        }
    }
}

