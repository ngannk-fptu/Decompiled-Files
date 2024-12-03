/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.Plugin
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.ImmutableSet$Builder
 *  org.apache.commons.io.IOUtils
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.plugin.metadata;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.metadata.PluginMetadata;
import com.atlassian.plugin.metadata.RequiredPluginProvider;
import com.google.common.collect.ImmutableSet;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

public class ClasspathFilePluginMetadata
implements PluginMetadata,
RequiredPluginProvider {
    static final String APPLICATION_PROVIDED_PLUGINS_FILENAME = "application-provided-plugins.txt";
    static final String APPLICATION_REQUIRED_PLUGINS_FILENAME = "application-required-plugins.txt";
    static final String APPLICATION_REQUIRED_MODULES_FILENAME = "application-required-modules.txt";
    private final Set<String> providedPluginKeys;
    private final Set<String> requiredPluginKeys;
    private final Set<String> requiredModuleKeys;
    private final ClassLoader classLoader;

    public ClasspathFilePluginMetadata() {
        this(ClasspathFilePluginMetadata.class.getClassLoader());
    }

    ClasspathFilePluginMetadata(ClassLoader classLoader) {
        this.classLoader = classLoader;
        this.providedPluginKeys = this.getStringsFromFile(APPLICATION_PROVIDED_PLUGINS_FILENAME);
        this.requiredPluginKeys = this.getStringsFromFile(APPLICATION_REQUIRED_PLUGINS_FILENAME);
        this.requiredModuleKeys = this.getStringsFromFile(APPLICATION_REQUIRED_MODULES_FILENAME);
    }

    @Override
    public boolean applicationProvided(Plugin plugin) {
        return this.providedPluginKeys.contains(plugin.getKey());
    }

    @Override
    public boolean required(Plugin plugin) {
        return this.requiredPluginKeys.contains(plugin.getKey());
    }

    @Override
    public boolean required(ModuleDescriptor<?> module) {
        return this.requiredModuleKeys.contains(module.getCompleteKey());
    }

    @Override
    public Set<String> getRequiredPluginKeys() {
        return this.requiredPluginKeys;
    }

    @Override
    public Set<String> getRequiredModuleKeys() {
        return this.requiredModuleKeys;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private Set<String> getStringsFromFile(String fileName) {
        ImmutableSet.Builder stringsFromFiles = ImmutableSet.builder();
        Collection<InputStream> fileInputStreams = this.getInputStreamsForFilename(fileName);
        try {
            for (InputStream fileInputStream : fileInputStreams) {
                if (fileInputStream == null) continue;
                try {
                    List lines = IOUtils.readLines((InputStream)fileInputStream);
                    for (String line : lines) {
                        String processedLine = this.processedLine(line);
                        if (processedLine == null) continue;
                        stringsFromFiles.add((Object)processedLine.intern());
                    }
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                    return stringsFromFiles.build();
                }
            }
        }
        finally {
            for (InputStream fileInputStream : fileInputStreams) {
                IOUtils.closeQuietly((InputStream)fileInputStream);
            }
        }
    }

    private String processedLine(String rawLine) {
        if (rawLine == null) {
            return null;
        }
        String trimmedLine = rawLine.trim();
        if (StringUtils.isBlank((CharSequence)trimmedLine)) {
            return null;
        }
        if (trimmedLine.startsWith("#")) {
            return null;
        }
        return trimmedLine;
    }

    Collection<InputStream> getInputStreamsForFilename(String fileName) {
        ArrayList<InputStream> inputStreams = new ArrayList<InputStream>();
        Class<ClasspathFilePluginMetadata> clazz = ClasspathFilePluginMetadata.class;
        String resourceName = clazz.getPackage().getName().replace(".", "/") + "/" + fileName;
        try {
            Enumeration<URL> urlEnumeration = this.classLoader.getResources(resourceName);
            while (urlEnumeration.hasMoreElements()) {
                inputStreams.add(urlEnumeration.nextElement().openStream());
            }
        }
        catch (IOException e) {
            for (InputStream inputStream : inputStreams) {
                IOUtils.closeQuietly((InputStream)inputStream);
            }
            throw new RuntimeException(e);
        }
        return inputStreams;
    }
}

