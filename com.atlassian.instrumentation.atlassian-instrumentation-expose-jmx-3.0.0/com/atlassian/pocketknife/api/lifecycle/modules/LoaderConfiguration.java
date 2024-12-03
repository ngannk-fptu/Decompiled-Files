/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 */
package com.atlassian.pocketknife.api.lifecycle.modules;

import com.atlassian.plugin.Plugin;
import com.atlassian.pocketknife.api.lifecycle.modules.ResourceLoader;
import java.io.InputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class LoaderConfiguration {
    private final Plugin plugin;
    private final List<String> pathsToAuxAtlassianPluginXMLs;
    private ResourceLoader resourceLoader;
    private boolean failOnDuplicateKey;

    public LoaderConfiguration(Plugin plugin) {
        if (plugin == null) {
            throw new NullPointerException("Plugin has not been specified");
        }
        this.plugin = plugin;
        this.pathsToAuxAtlassianPluginXMLs = new LinkedList<String>();
        this.failOnDuplicateKey = true;
        this.resourceLoader = new DefaultResourceLoader();
    }

    public Plugin getPlugin() {
        return this.plugin;
    }

    public ResourceLoader getResourceLoader() {
        return this.resourceLoader;
    }

    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public boolean isFailOnDuplicateKey() {
        return this.failOnDuplicateKey;
    }

    public void setFailOnDuplicateKey(boolean failOnDuplicateKey) {
        this.failOnDuplicateKey = failOnDuplicateKey;
    }

    public List<String> getPathsToAuxAtlassianPluginXMLs() {
        return this.pathsToAuxAtlassianPluginXMLs;
    }

    public void addPathsToAuxAtlassianPluginXMLs(List<String> pathsToAuxAtlassianPluginXMLs) {
        this.pathsToAuxAtlassianPluginXMLs.addAll(pathsToAuxAtlassianPluginXMLs);
    }

    public void addPathsToAuxAtlassianPluginXMLs(String ... pathsToAuxAtlassianPluginXMLs) {
        this.pathsToAuxAtlassianPluginXMLs.addAll(Arrays.asList(pathsToAuxAtlassianPluginXMLs));
    }

    class DefaultResourceLoader
    implements ResourceLoader {
        DefaultResourceLoader() {
        }

        @Override
        public InputStream getResourceAsStream(String resourceName) {
            return LoaderConfiguration.this.plugin.getClassLoader().getResourceAsStream(resourceName);
        }
    }
}

